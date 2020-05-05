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

import java.util.Vector;

import jade.lang.acl.ACLMessage;
import jade.proto.SimpleAchieveREInitiator;
import jade.util.Logger;
import jade.core.Agent;
import jade.core.AID;
import jade.core.PlatformID;
import jade.content.ContentManager;
import jade.core.migration.ontology.MobileAgentDescription;
import jade.core.migration.ontology.MobileAgentLanguage;
import jade.core.migration.ontology.MobileAgentProfile;
import jade.core.migration.ontology.MobileAgentProtocol;
import jade.core.migration.ontology.MobileAgentSystem;
import org.apache.commons.codec.binary.Base64;
import jade.core.migration.ontology.MoveAction;
import jade.domain.FIPANames;
import jade.content.lang.sl.SLCodec;
import jade.core.migration.ontology.MigrationOntology;
import jade.content.onto.basic.Action;
import jade.core.ServiceException;
import jade.core.migration.ontology.PowerupAction;
/**
 * Description here
 * 
 * @author <a href="mailto:Joan.Ametller@uab.es">Joan Ametller Esquerra</a>
 * @author Carles Garrigues
 * @author <a href="mailto:Jordi.Cucurull@uab.es">Jordi Cucurull Juan</a>
 * 
 */
public class AMSInitiator extends SimpleAchieveREInitiator{
	
  public AMSInitiator(Agent a, ACLMessage msg, byte[] instance, byte[] jar, PlatformID dest, AID name){
    super(a, msg);
    _jar = jar;
    _instance = instance;
    _name = name;
    _dest = dest;
    _request = msg;
    _cm = a.getContentManager();
    _cm.registerLanguage(new SLCodec(),
                FIPANames.ContentLanguage.FIPA_SL0);
    _cm.registerOntology(MigrationOntology.getInstance());

  }
  
  /**
   * Method to create and fullfill a MobileAgentDescription object.
   * @param instance Agent instance.
   * @param jar Agent code.
   * @param name Agent AID.
   * @return MobileAgentDescription object.
   */
  private MobileAgentDescription createMAD(byte[] instance, byte[] jar, AID name) {
  	
  	if (logger.isLoggable(Logger.FINE))
			logger.log(Logger.FINE,
							"AMSInitiator: Filling mobile agent ontology.");
  	
  	//Get minor and major version of Java Platform.
  	String[] majorMinorVersion = 
  		System.getProperty(InterPlatformMobilityService.JAVA_SYSTEM_PROPERTY_CLASS_VERSION).split("\\.");
  	
  	//Fill and create MobileAgentLanguage object.
  	MobileAgentLanguage mal = new MobileAgentLanguage();
  	mal.setName(InterPlatformMobilityService.LANGUAGE_PROPERTY_NAME);
  	mal.setFormat(InterPlatformMobilityService.LANGUAGE_PROPERTY_FORMAT);
  	mal.setMajorVersion(majorMinorVersion[0]);
  	mal.setMinorVersion(majorMinorVersion[1]);
  	
  	//Fill and create MobileAgentSystem object.
  	MobileAgentSystem mas = new MobileAgentSystem();
  	mas.setName(InterPlatformMobilityService.PLATFORM_PROPERTY_NAME);
  	mas.setMajorVersion(InterPlatformMobilityService.PLATFORM_PROPERTY_MAJOR_VERSION);
  	mas.setMinorVersion(InterPlatformMobilityService.PLATFORM_PROPERTY_MINOR_VERSION);
  	
  	//Fill and create MobileAgentProtocol object.
  	MobileAgentProtocol map = new MobileAgentProtocol();
  	map.setName(InterPlatformMobilityService.PROTOCOL_PROPERTY_NAME);
  	map.setMajorVersion(InterPlatformMobilityService.PROTOCOL_PROPERTY_MAJOR_VERSION);
  	map.setMinorVersion(InterPlatformMobilityService.PROTOCOL_PROPERTY_MINOR_VERSION);
  	
  	//Join last definitions in MobileAgentProfile.
  	MobileAgentProfile mapf = new MobileAgentProfile();
  	mapf.setLanguage(mal);
  	mapf.setSystem(mas);
  	mapf.setProtocol(map);
  	
  	//Add instance, code and properties of the mobile agent in
  	//the MobileAgentDescription.
  	MobileAgentDescription mad = new MobileAgentDescription();
    mad.setData(new String(Base64.encodeBase64(_instance)));
    mad.setCode(new String(Base64.encodeBase64(_jar)));
    mad.setName(name);
    mad.setAgentProfile(mapf);
  	
  	return mad;
  }
  
  public ACLMessage prepareRequest(ACLMessage acl){
    
  	if (logger.isLoggable(Logger.FINE))
			logger.log(Logger.FINE,
							"AMSInitiator: Preparing request message.");
  	
  	_request.addReceiver(_dest.getAmsAID());
    _cm = myAgent.getContentManager();
    
    //Create and fullfill mobile agent ontology.
    MobileAgentDescription mad = createMAD(_instance, _jar, _name);
    
    MoveAction move = new MoveAction();
    move.setMobileAgentDescription(mad);
    _request.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
    _request.setOntology(MigrationOntology.getInstance().getName());
    Action act = new Action();
    act.setAction(move);
    act.setActor(myAgent.getAID());
    
    try{
      _cm.fillContent(_request, act);
    }
    catch(Exception e){
    	if (logger.isLoggable(Logger.WARNING))
			logger.log(Logger.WARNING,
							"AMSInitiator: Error filling message content.");
     return null;
    }
    _jar = null;
    _instance = null;
    return _request;
  }

  protected void handleAgree(ACLMessage agree){
  	if (logger.isLoggable(Logger.FINE))
		logger.log(Logger.FINE,
						"AMSInitiator: Agree message received.");
  	//TODO: Tractar respostes AGREE
  }
  protected void handleRefuse(ACLMessage refuse){
  	if (logger.isLoggable(Logger.FINE))
		logger.log(Logger.FINE,
						"AMSInitiator: Refuse message received.");
  }
  
  protected void handleAllResponses(Vector responses) {
  	if (responses.size()==0) {
    	if (logger.isLoggable(Logger.WARNING))
			logger.log(Logger.WARNING,
							"AMSInitiator: Timeout expired.");
  		
  	    //get migration helper and call informMigrationResult
  	    try {
  	  	InterPlatformMobilityHelper help = 
  	      (InterPlatformMobilityHelper)myAgent.getHelper(
  	      InterPlatformMobilityHelper.NAME);
  	    help.informMigrationResult(new AID(_name.getName(), AID.ISGUID),
  	    		"Timeout to receive migration response has expired");
  	    } catch (ServiceException se) {	
  	    	if (logger.isLoggable(Logger.SEVERE))
  				logger.log(Logger.SEVERE,
  								"AMSInitiator: Error contacting migration service to notice failure.");
  	    }
  	}
  }

  protected void handleInform(ACLMessage inform){

  	if (logger.isLoggable(Logger.FINE))
		logger.log(Logger.FINE,
						"AMSInitiator: Inform message received.");
  	
  	//extract AID and migration result from message (soluci� temporal)
    String msg = inform.getContent();
    String name = msg.substring(0,msg.indexOf("#"));
    String result = msg.substring(msg.indexOf("#") + 1);
    try {
      //get migration helper and call informMigrationResult
      InterPlatformMobilityHelper help = 
        (InterPlatformMobilityHelper)myAgent.getHelper(
        InterPlatformMobilityHelper.NAME);
      help.informMigrationResult(new AID(name, AID.ISGUID),result);

      //if migration was OK, request agent power up
      if (result.equals(InterPlatformMobilityService.MIGRATION_OK)) {
        ACLMessage req = new ACLMessage(ACLMessage.REQUEST);
        req.addReceiver(inform.getSender());
        myAgent.addBehaviour(new PowerupRequest(myAgent,req,new AID(name, AID.ISGUID)));
      }
    }
    catch (ServiceException se) {
	    	if (logger.isLoggable(Logger.SEVERE))
  				logger.log(Logger.SEVERE,
  								"AMSInitiator: Error contacting migration service to inform migration result.");	
    }
  }
  protected void handleNotUnderstood(ACLMessage notUnderstood){
  	if (logger.isLoggable(Logger.FINE))
		logger.log(Logger.FINE,
						"AMSInitiator: Not understood message received.");
  }
  
  protected void handleOutOfSequence(ACLMessage outOfSequence) {
  	if (logger.isLoggable(Logger.FINE))
		logger.log(Logger.FINE,
						"AMSInitiator: Out of sequence message received.");
  }
  
  protected void handleFailure(ACLMessage failure){
  	if (logger.isLoggable(Logger.FINE))
		logger.log(Logger.FINE,
						"AMSInitiator: Failured message received.");
  	
    //get migration helper and call informMigrationResult
    try {
  	InterPlatformMobilityHelper help = 
      (InterPlatformMobilityHelper)myAgent.getHelper(
      InterPlatformMobilityHelper.NAME);
    help.informMigrationResult(new AID(_name.getName(), AID.ISGUID),failure.getContent());
    } catch (ServiceException se) {
    	if (logger.isLoggable(Logger.SEVERE))
				logger.log(Logger.SEVERE,
								"AMSInitiator: Error contacting migration service to notice failure.");
    }
  }
  
  private byte[] _jar;
  private byte[] _instance;
  private AID _name;
  private PlatformID _dest;
  private ACLMessage _request;
  private ContentManager _cm;
  
  
  private class PowerupRequest extends SimpleAchieveREInitiator{
    public PowerupRequest(Agent a, ACLMessage msg, AID name){
      super(a, msg);
      _name=name;
      _request = msg;
    }
    
    public ACLMessage prepareRequest(ACLMessage acl){
      PowerupAction pup = new PowerupAction();
      pup.setPowerUpAid(_name);
      _request.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
      _request.setOntology(MigrationOntology.getInstance().getName());
      Action act = new Action();
      act.setAction(pup);
      act.setActor(myAgent.getAID());
      try{
        myAgent.getContentManager().fillContent(_request, act);
      }
      catch(Exception e){
      	if (logger.isLoggable(Logger.SEVERE))
			logger.log(Logger.SEVERE,
							"AMSInitiator: Error filling message content.");
        return null;
      }
      return _request;      
    }

    protected void handleInform(ACLMessage inform){
      //TODO: Tractar informs
    }
    
    private AID _name;
    private ACLMessage _request;
  }
  
  private Logger logger = Logger.getMyLogger(getClass().getName());
  
}
