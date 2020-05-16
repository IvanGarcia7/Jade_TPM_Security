/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2000 CSELT S.p.A. 

GNU Lesser General Public License

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, 
version 2.1 of the License. 

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
*****************************************************************/
 
 // Created: Jun 8, 2004
 
package jade.core.migration;

import java.security.PrivateKey;
import java.util.*;

import jade.content.ContentManager;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.basic.Action;
import jade.content.Concept;
import jade.core.AID;
import jade.core.Agent;
import jade.core.AgentContainer;
import jade.core.SecureCloud.SecureCAConfirmation;
import jade.core.SecureTPM.Agencia;
import jade.core.SecureTPM.Pair;
import jade.core.ServiceException;
import jade.core.migration.ontology.MigrationOntology;
import jade.core.migration.ontology.MobileAgentDescription;
import jade.core.migration.ontology.MobileAgentLanguage;
import jade.core.migration.ontology.MobileAgentProfile;
import jade.core.migration.ontology.MobileAgentProtocol;
import jade.core.migration.ontology.MobileAgentSystem;
import jade.core.migration.ontology.MoveAction;
import jade.core.migration.ontology.PowerupAction;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.SimpleAchieveREResponder;
import jade.util.Logger;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Description here
 * 
 * @author <a href="mailto:Joan.Ametller@uab.es">Joan Ametller Esquerra</a>
 * @author Carles Garrigues
 * @author <a href="mailto:Jordi.Cucurull@uab.es">Jordi Cucurull Juan</a>
 * 
 */
public class AMSResponder extends SimpleAchieveREResponder {

	public static final String ERR_BAD_LANGUAGE = "Received agent is programmed by an unsupported language: ";	
	public static final String ERR_BAD_LANGUAGE_FORMAT = "Received agent has an invalid format: ";
	public static final String ERR_BAD_LANGUAGE_VERSION = "Received agent with incompatible code version: ";
	public static final String ERR_BAD_PLATFORM = "Received agent comes from an unsupported platform: ";
	public static final String ERR_BAD_PLATFORM_VERSION = "Received agent comes from an incompatible platform release: ";
	public static final String ERR_BAD_PROTOCOL = "Received message belongs to an unsupported mobility protocol: ";
	public static final String ERR_BAD_PROTOCOL_VERSION = "Received message belongs to an unsupported mobility protocol release. This is version: ";
	public static final String ERR_MSG_WITHOUT_CODE = "Received message without agent code.";
	public static final String ERR_MSG_WITHOUT_DATA = "Received message without agent data.";
	public static final String ERR_MSG_WITHOUT_AID = "Received message without agent identification.";
	public static final String ERR_MSG_BAD_FORMED = "Received malformed message.";

	private AgentContainer mycontainer;
	
  public AMSResponder(Agent a, MessageTemplate mt, int timeout, AgentContainer _myContainer){
	super(a,mt);
	mycontainer = _myContainer;
	a.getContentManager().registerLanguage(new SLCodec(),
      FIPANames.ContentLanguage.FIPA_SL0);
	a.getContentManager().registerOntology(MigrationOntology.getInstance());
    _cm = a.getContentManager();
    
    //Get current java version.
    String[] majorMinorVersion = 
  		System.getProperty(InterPlatformMobilityService.JAVA_SYSTEM_PROPERTY_CLASS_VERSION).split("\\.");

    languageMajorVersion = (new Integer(majorMinorVersion[0])).intValue();
    languageMinorVersion = (new Integer(majorMinorVersion[1])).intValue();
    
    _agentsPrePowerUp = new HashMap();
    _prePowerUpTimeout = timeout;
  }

  private boolean isLanguageSupportedVersion(String major, String minor) {
  	
  	int receivedMajor = (new Integer(major)).intValue();
  	int receivedMinor = (new Integer(minor)).intValue();
	
  	if (receivedMajor>languageMajorVersion) return false;
	
	if (receivedMajor==languageMajorVersion) {
		if (receivedMinor>languageMinorVersion) return false;
	}
	
	if (receivedMajor<InterPlatformMobilityService.MIN_SUPPORTED_LANGUAGE_MAJOR_VERSION) 
		return false;
	if (receivedMajor==InterPlatformMobilityService.MIN_SUPPORTED_LANGUAGE_MAJOR_VERSION) {
		if (receivedMinor<InterPlatformMobilityService.MIN_SUPPORTED_LANGUAGE_MINOR_VERSION)
			return false;
	}
  	
	return true;
  }
  
  private boolean isProtocolSupportedVersion(String major, String minor) {
  	
  	int receivedMajor = (new Integer(major)).intValue();
  	int receivedMinor = (new Integer(minor)).intValue();
	
  	if (receivedMajor>InterPlatformMobilityService.MAX_SUPPORTED_PROTOCOL_MAJOR_VERSION) return false;
	
	/*if (receivedMajor==InterPlatformMigrationService.MAX_SUPPORTED_PROTOCOL_MAJOR_VERSION) {
		if (receivedMinor>InterPlatformMigrationService.MAX_SUPPORTED_PROTOCOL_MINOR_VERSION) return false;
	}*/
	
	if (receivedMajor<InterPlatformMobilityService.MIN_SUPPORTED_PROTOCOL_MAJOR_VERSION) 
		return false;
	/*if (receivedMajor==InterPlatformMigrationService.MIN_SUPPORTED_PROTOCOL_MAJOR_VERSION) {
		if (receivedMinor<InterPlatformMigrationService.MIN_SUPPORTED_PROTOCOL_MINOR_VERSION)
			return false;
	}*/
  	
	return true;
  }
  
  private boolean checkMAD(MobileAgentDescription mad, ACLMessage reply) {
  	
  	try {
  		
  	  	if (logger.isLoggable(Logger.FINE))
  			logger.log(Logger.FINE,
  							"AMSResponder: Checking for mobile agent compatibility.");
  		
	  	//Get MobileAgentProfile.
  		MobileAgentProfile mapf = mad.getAgentProfile();
  		
  		//Check for language compatibility.
	  	MobileAgentLanguage mal = mapf.getLanguage();
	  	if (!mal.getName().equals(InterPlatformMobilityService.LANGUAGE_PROPERTY_NAME)) {
	  		reply.setPerformative(ACLMessage.FAILURE);
	  		reply.setContent(ERR_BAD_LANGUAGE + mal.getName());
	  		return false;
	  	}
	  	if (!mal.getFormat().equals(InterPlatformMobilityService.LANGUAGE_PROPERTY_FORMAT)) {
	  		reply.setPerformative(ACLMessage.FAILURE);
	  		reply.setContent(ERR_BAD_LANGUAGE_FORMAT + mal.getFormat());
	  		return false;
	  	}
	  	if (!isLanguageSupportedVersion(mal.getMajorVersion(),mal.getMinorVersion())) {
	  		reply.setPerformative(ACLMessage.FAILURE);
	  		reply.setContent(ERR_BAD_LANGUAGE_VERSION + mal.getMajorVersion() + "." 
	  				+ mal.getMinorVersion());
	  		return false;
	  	}
	  	
	  	//Check for agent platform compatibility.
	  	MobileAgentSystem mas = mapf.getSystem();
	  	if (!mas.getName().equals(InterPlatformMobilityService.PLATFORM_PROPERTY_NAME)) {
	  		reply.setPerformative(ACLMessage.FAILURE);
	  		reply.setContent(ERR_BAD_PLATFORM + mas.getName());
	  		return false;
	  	}
	  	if (!mas.getMajorVersion().equals(InterPlatformMobilityService.PLATFORM_PROPERTY_MAJOR_VERSION)) {
	  		reply.setPerformative(ACLMessage.FAILURE);
	  		reply.setContent(ERR_BAD_PLATFORM_VERSION + mas.getMajorVersion() + "." 
	  				+ mas.getMinorVersion());
	  		return false;
	  	}
	  	if (!mas.getMinorVersion().equals(InterPlatformMobilityService.PLATFORM_PROPERTY_MINOR_VERSION)) {
	  		reply.setPerformative(ACLMessage.FAILURE);
	  		reply.setContent(ERR_BAD_PLATFORM_VERSION + mas.getMajorVersion() + "." 
	  				+ mas.getMinorVersion());
	  		return false;
	  	}
	
	  	//Check for mobility protocol compatibility.
	  	MobileAgentProtocol map = mapf.getProtocol();
	  	if (!map.getName().equals(InterPlatformMobilityService.PROTOCOL_PROPERTY_NAME)) {
	  		reply.setPerformative(ACLMessage.FAILURE);
	  		reply.setContent(ERR_BAD_PROTOCOL + map.getName());
	  		return false;
	  	}
	  	if (!isProtocolSupportedVersion(map.getMajorVersion(),map.getMinorVersion())) {
	  		reply.setPerformative(ACLMessage.FAILURE);
	  		reply.setContent(ERR_BAD_PROTOCOL_VERSION + 
	  				InterPlatformMobilityService.PROTOCOL_PROPERTY_MAJOR_VERSION + 
					"." + InterPlatformMobilityService.PROTOCOL_PROPERTY_MINOR_VERSION);
	  		return false;
	  	}
	  	
	  	//Check for required fields in the "simple-mobility-protocol".
	  	if (mad.getCode()==null) {
	  		reply.setPerformative(ACLMessage.FAILURE);
	  		reply.setContent(ERR_MSG_WITHOUT_CODE);
	  		return false;
	  	}
	  	
	  	if (mad.getData()==null) {
	  		reply.setPerformative(ACLMessage.FAILURE);
	  		reply.setContent(ERR_MSG_WITHOUT_DATA);
	  		return false;
	  	}
	  	
	  	if (mad.getName()==null) {
	  		reply.setPerformative(ACLMessage.FAILURE);
	  		reply.setContent(ERR_MSG_WITHOUT_AID);
	  		return false;
	  	}
	  	
	  	
  	} catch (Exception e) {
  		reply.setPerformative(ACLMessage.FAILURE);
  		reply.setContent(ERR_MSG_BAD_FORMED);
  		return false;
  	}
  	
  	if (logger.isLoggable(Logger.FINE))
		logger.log(Logger.FINE,
						"AMSResponder: Mobile agent compatibility checked and granted.");
  	return true;
  	
  }
  
  protected ACLMessage prepareResponse(ACLMessage request) {
  	
  	if (logger.isLoggable(Logger.FINE))
		logger.log(Logger.FINE,
						"AMSResponder: Request message received.");
  	
  	Action action = null;
    ACLMessage reply = request.createReply();
    
    try{	
    InterPlatformMobilityHelper helper = 
        (InterPlatformMobilityHelper)myAgent.getHelper(
        InterPlatformMobilityHelper.NAME);
    
	    try{
	      action = (Action)_cm.extractContent(request);
	    } catch(Exception e){
	      if (logger.isLoggable(Logger.WARNING))
				logger.log(Logger.WARNING,
								"AMSResponder: Error extracting message content. Can't report error to source platform.");
	      return null;
	    }
	    if(action != null){
	      Concept act = action.getAction();
	      if(act instanceof MoveAction){
	    	  
	    	//remove incomming agents registered in the platform not initiated
	    	//through a determined period of time.
	    	removeAgentsNotPoweredUp(helper);
	    	  
	        MobileAgentDescription mad = ((MoveAction)act).getMobileAgentDescription();
	        //Check for mobile agent compatibility.
	        //TODO: Use Ontology to send AID.
	        reply.setOntology(null);
	        if (checkMAD(mad, reply)) {
		        byte[] jar = Base64.decodeBase64(mad.getCode().getBytes());
		        byte[] instance = Base64.decodeBase64(mad.getData().getBytes());
		        AID aid = mad.getName();
		        reply.setPerformative(ACLMessage.INFORM);
	
		        mad = null;
		        request = null;



				AID amsAID = new AID("ams", false);
				Agent ams = mycontainer.acquireLocalAgent(amsAID);

				//FETCH MY PRIVATE AND MY LIST OF WHITE PASS
				PrivateKey privateAMS = ams.getPrivateKey();

				System.out.println("THE PRIVATEKEY IS THE FOLLOWING");
				System.out.println(privateAMS);


				Pair<byte [], byte []> PairReceive = null;
				byte [] decryptedKey = null;
				try {
					PairReceive = (Pair<byte[], byte[]>) Agencia.deserialize(instance);
					byte[] OTP = PairReceive.getKey();
					byte[] content = PairReceive.getValue();
					decryptedKey = Agencia.decrypt(privateAMS, OTP);

					SecretKey originalKey = new SecretKeySpec(decryptedKey, 0, decryptedKey.length,
							"AES");
					Cipher aesCipher = Cipher.getInstance("AES");
					aesCipher.init(Cipher.DECRYPT_MODE, originalKey);
					byte[] byteObject = aesCipher.doFinal(content);
					Pair<String, byte[]> contentdecrypt = (Pair<String, byte[]>) Agencia.deserialize(byteObject);

					String challenge = contentdecrypt.getKey();

					//SEE IF I HAVE PERMISSION
					Map<String, SecureCAConfirmation> pass_request = ams.request_pass();

					System.out.println("*********************HOTSPOTS*****************************");
					Iterator it = pass_request.entrySet().iterator();
					while(it.hasNext()){
						Map.Entry pair = (Map.Entry)it.next();
						System.out.println(pair.getKey() + " = " + pair.getValue());
					}
					System.out.println("*********************HOTSPOTS*****************************");

					//See if the token is in the list
					SecureCAConfirmation myPacket = pass_request.get(challenge);

					Date time = myPacket.getTimestamp();

					Calendar c = Calendar.getInstance();
					Date timeChallenge = new Date(c.getTimeInMillis());

					//if (time.compareTo(timeChallenge) < 0) {
						System.out.println("THE REQUEST PASS THE TIMESTAMP CHALLENGUE");
						instance = contentdecrypt.getValue();
					//} else {
					//	instance = null;
					//}
				}catch(Exception e){
					e.printStackTrace();
				}


				mycontainer.releaseLocalAgent(amsAID);




				try{
		          
		          
		          //create agent without powering it up
		          String result = helper.launchIncommingAgent(jar,instance,aid);
		          
		          //add agent in the table which control for PrePowerUp agents.
		          _agentsPrePowerUp.put(aid,new Long(System.currentTimeMillis()));
		          
		          //return result
		          String msg = aid.getName() + "#" + result; //solucio temporal per passar l'AID
		          reply.setContent(msg);
		          
		        }
		        catch(ServiceException se){
		          reply.setContent(se.getMessage());
		        }
	        }
	      } else if(act instanceof PowerupAction){
	        AID agent = ((PowerupAction)act).getPowerUpAid();
	        reply.setPerformative(ACLMessage.INFORM);
	        try{
	          //Power up agent
	          helper.powerUpAgent(agent);
	          
	          //remove the agent of the table which control for PrePowerUp agents.
	          _agentsPrePowerUp.remove(agent);
	          
	          reply.setContent("Agent powered up");
	        }
	        catch(ServiceException se){
	          reply.setContent(se.getMessage());
	        }
	      }
	      return reply;
	    }
	    else return null;
  
  } catch (ServiceException se){
	  request = null;
      reply.setContent(se.getMessage());
      return reply;
  }
  
  }

  /**
   * Method to remove incomming agents registered into the platform
   * but not powered up during a fixed period of time.
   *
   */
  private void removeAgentsNotPoweredUp(InterPlatformMobilityHelper helper) {

	  AID agent;
	  LinkedList agentsToRemove = new LinkedList();
	  Map.Entry me;
	  long time;
	  Iterator it = _agentsPrePowerUp.entrySet().iterator();
	  while (it.hasNext()) {
		  me = (Map.Entry) it.next();
		  time = System.currentTimeMillis() - ((Long) me.getValue()).longValue();
		  if (time>_prePowerUpTimeout) agentsToRemove.add(me.getKey());
	  }
	  
	  try{
          
		  it = agentsToRemove.iterator();
		  while (it.hasNext()) {
			  agent = (AID) it.next();
			  helper.removePrePowerUpAgent(agent);
			  _agentsPrePowerUp.remove(agent);
			  
		  }
		  
        }
        catch(ServiceException se){
          System.out.println(se);
        }
	  
  }
  
  private Logger logger = Logger.getMyLogger(getClass().getName());
  private ContentManager _cm;
  private int languageMajorVersion;
  private int languageMinorVersion;
  private HashMap _agentsPrePowerUp;
  private int _prePowerUpTimeout;
  
}
