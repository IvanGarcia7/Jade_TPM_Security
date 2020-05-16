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

// Created: Jun 7, 2004
package jade.core.migration;

import jade.core.AID;
import jade.core.Agent;
import jade.core.AgentState;
import jade.core.BaseService;
import jade.core.ContainerID;
import jade.core.Filter;
import jade.core.HorizontalCommand;
import jade.core.LifeCycle;
import jade.core.MainContainer;
import jade.core.PlatformID;
import jade.core.SecureAgent.SecureAgentPlatform;
import jade.core.SecureCloud.SecureCAConfirmation;
import jade.core.SecureCloud.SecureCAPlatform;
import jade.core.SecureTPM.Agencia;
import jade.core.SecureTPM.Pair;
import jade.core.UnreachableException;
import jade.core.VerticalCommand;
import jade.core.GenericCommand;
import jade.core.Node;
import jade.core.ServiceException;
import jade.core.Sink;
import jade.core.Service;
import jade.core.ServiceHelper;
import jade.core.ServiceFinder;
import jade.core.IMTPException;
import jade.core.NotFoundException;
import jade.core.mobility.AgentMobilityHelper;
import jade.core.mobility.AgentMobilityService;
import jade.core.Profile;
import jade.core.AgentContainer;
import jade.core.ProfileException;
import java.io.ObjectOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.ObjectStreamClass;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.*;
import java.util.Map.Entry;
import java.util.jar.JarOutputStream;
import java.util.jar.JarEntry;
import java.util.logging.Level;

import jade.core.management.AgentManagementService;
import jade.core.management.AgentManagementSlice;
import jade.core.management.CodeLocator;
import jade.core.management.CodeLocatorListener;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import jade.core.management.JarClassLoader;
import jade.core.messaging.MessagingSlice;
import jade.core.NodeDescriptor;
import jade.core.migration.analysis.ClassAnalysisLibrary;
import jade.core.migration.code.JarManager;
import jade.core.migration.ontology.MigrationOntology;

import jade.security.Credentials;
import jade.security.JADEPrincipal;
import jade.security.JADESecurityException;
import jade.util.Logger;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


/**
 * Description here
 *
 * @author <a href="mailto:Joan.Ametller@uab.es">Joan Ametller Esquerra </a>
 * @author Carles Garrigues
 * @author <a href="mailto:Jordi.Cucurull@uab.es">Jordi Cucurull Juan</a>
 *
 */
public class InterPlatformMobilityService extends BaseService {

	public static final String DEFAULT_PROTOCOL = "default-protocol";

	public static final String MIGRATION_OK = "migration-ok";

	public static final String MAIN_CONTAINER = "Main-Container";

	public static final String MESSAGE_RESPONSE_TIMEOUT = "30000";

	public static final String MESSAGE_RESPONSE_TIMEOUT_RESPONDER = "60000";

	public static final String JAR_PATH = "jade_core_migration_InterPlatformMobilityService_JarPath";

	private static final String[] OWNED_COMMANDS = new String[] {
			InterPlatformMobilityHelper.INFORM_MIGRATION_RESULT,
			InterPlatformMobilityHelper.INFORM_MIGRATED,
			InterPlatformMobilityHelper.LAUNCH_AGENT,
			InterPlatformMobilityHelper.REMOVE_PREPOWERUP_AGENT};

	public static final String JAVA_SYSTEM_PROPERTY_CLASS_VERSION = "java.class.version";
	public static final String LANGUAGE_PROPERTY_NAME = "java";
	public static final String LANGUAGE_PROPERTY_FORMAT = "bytecode";
	public static final String PROTOCOL_PROPERTY_NAME = "simple-mobility-protocol";
	public static final String PROTOCOL_PROPERTY_MAJOR_VERSION = "1";
	public static final String PROTOCOL_PROPERTY_MINOR_VERSION = "0";
	public static final String PLATFORM_PROPERTY_NAME = "jade";
	public static final String PLATFORM_PROPERTY_MAJOR_VERSION = "3";
	public static final String PLATFORM_PROPERTY_MINOR_VERSION = "4";
	public static final int MIN_SUPPORTED_LANGUAGE_MAJOR_VERSION = 47;
	public static final int MIN_SUPPORTED_LANGUAGE_MINOR_VERSION = 0;
	public static final int MAX_SUPPORTED_PROTOCOL_MAJOR_VERSION = 1;
	public static final int MAX_SUPPORTED_PROTOCOL_MINOR_VERSION = 65536;
	public static final int MIN_SUPPORTED_PROTOCOL_MAJOR_VERSION = 0;
	public static final int MIN_SUPPORTED_PROTOCOL_MINOR_VERSION = 0;

	public static final String MIGRATION_TIMEOUT = "jade_core_migration_IPMS_migration_timeout";

	public static final String MIGRATION_TIMEOUT_RESPONDER = "jade_core_migration_IPMS_migration_timeout_responder";

	public void init(AgentContainer ac, Profile p) throws ProfileException {
		super.init(ac, p);

		_myContainer = ac;
		_profile = p;
		try {

			try {
				_migrationTimeout = Integer.parseInt(p.getParameter(MIGRATION_TIMEOUT, MESSAGE_RESPONSE_TIMEOUT));
			} catch (NumberFormatException nte) {
				_migrationTimeout = Integer.parseInt(MESSAGE_RESPONSE_TIMEOUT);
				if (logger.isLoggable(Logger.WARNING))
					logger.log(Logger.WARNING, "Incorrect passed timeout value, setting to default value: "+ MESSAGE_RESPONSE_TIMEOUT);
			}

			try {
				_migrationTimeoutResponder = Integer.parseInt(p.getParameter(MIGRATION_TIMEOUT_RESPONDER, MESSAGE_RESPONSE_TIMEOUT_RESPONDER));
			} catch (NumberFormatException nte) {
				_migrationTimeoutResponder = Integer.parseInt(MESSAGE_RESPONSE_TIMEOUT_RESPONDER);
				if (logger.isLoggable(Logger.WARNING))
					logger.log(Logger.WARNING, "Incorrect passed timeout value, setting to default value: "+ MESSAGE_RESPONSE_TIMEOUT_RESPONDER);
			}
			if (_migrationTimeout!=Integer.parseInt(MESSAGE_RESPONSE_TIMEOUT)) {
				if (logger.isLoggable(Logger.WARNING))
					logger.log(Logger.WARNING, "Migration timeout redefined to: "+ _migrationTimeout);
			}
			if (_migrationTimeoutResponder!=Integer.parseInt(MESSAGE_RESPONSE_TIMEOUT_RESPONDER)) {
				if (logger.isLoggable(Logger.WARNING))
					logger.log(Logger.WARNING, "Migration timeout responder redefined to: "+ _migrationTimeoutResponder);
			}

			if ((_jarPath = p.getParameter(JAR_PATH, "")).equals(
					"")) {

				if (logger.isLoggable(Logger.WARNING))
					logger.log(Logger.WARNING,
							"AGENTS_PATH property not set, using "
									+ System.getProperty("user.dir")
									+ File.separator + "jarManager" + File.separator
									+ " directory for jar agents");

				_jarPath = System.getProperty("user.dir")
						+ File.separator + "jarManager" + File.separator;

				// Create the directory if not exists.
				File f = new File(_jarPath);
				if (!f.exists()) f.mkdir();

			}

			// Create the JarManager.
			_jarManager = new JarManager(_jarPath);

		} catch (Exception e) {
			if (logger.isLoggable(Logger.SEVERE))
				logger.log(Logger.SEVERE, "Error initializing inter-platform migration service: "+ e);
		}

		if (logger.isLoggable(Logger.INFO))
			logger.log(Logger.INFO, getClass().getName() + " initialized");

	}


	public String getName() {
		return InterPlatformMobilityHelper.NAME;
	}

	public Slice getLocalSlice() {
		return _localSlice;
	}

	public ServiceHelper getHelper(Agent a) {
		return new InterPlatformMigrationHelperImpl();
	}

	public Class getHorizontalInterface() {
		return InterPlatformMobilitySlice.class;
	}

	public Filter getCommandFilter(boolean direction) {
		if (direction == Filter.OUTGOING) {
			return _outFilter;
		} else return null;
	}

	public Sink getCommandSink(boolean side) {
		if (side == Sink.COMMAND_SOURCE) {
			return _senderSink;
		} else {
			return _receiverSink;
		}
	}

	public String[] getOwnedCommands() {
		return OWNED_COMMANDS;
	}

	/*	public String getAgentCodeSite(AID aid) {
            return getLocator().getAgentCodeLocation(aid);
        }
    */
	private class CommandSourceSink implements Sink {

		public void consume(VerticalCommand cmd) {
			if (cmd.getName().equals(
					InterPlatformMobilityHelper.INFORM_MIGRATED))
				handleInformMigrated(cmd);
			else if (cmd.getName().equals(
					InterPlatformMobilityHelper.INFORM_MIGRATION_RESULT))
				handleInformMigrationResult(cmd);
			else if (cmd.getName().equals(
					InterPlatformMobilityHelper.LAUNCH_AGENT))
				launchAgent(cmd);
			else if (cmd.getName().equals(
					InterPlatformMobilityHelper.REMOVE_PREPOWERUP_AGENT))
				removePrePoweredUpAgent(cmd);
		}

		//Serializes the agent and transfers its instance to Main-Container
		private void handleInformMigrated(VerticalCommand cmd) {
			if (logger.isLoggable(Logger.FINE))
				logger.log(Logger.FINE,"Source-Sink: handleInformMigrated invoked");

			Object[] params = cmd.getParams();
			PlatformID where = (PlatformID)params[1];
			AID name = (AID)params[0];

			Agent agent = _myContainer.acquireLocalAgent(name);

			try{
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				ObjectOutputStream encoder = new ObjectOutputStream(out);
				encoder.writeObject(agent);
				if (logger.isLoggable(Logger.FINE))
					logger.log(Logger.FINE, "Source-Sink: handleInformMigrated: Agent serialized");
				byte[] instance = out.toByteArray();

				//Get code source container of agent.
				AgentMobilityService ams = (AgentMobilityService) myFinder.findService(AgentMobilityService.NAME);
				String codesrc = ams.getClassSite(agent);
				if (codesrc == null) {
					codesrc = _myContainer.getID().getName();
				}

				InterPlatformMobilitySlice mainSlice =
						(InterPlatformMobilitySlice)getSlice(MAIN_SLICE);
				mainSlice.transferInstance(instance, name, agent.getClass().getName(), codesrc, where);
				if (logger.isLoggable(Logger.FINE))
					logger.log(Logger.FINE, "Source-Sink: handleInformMigrated: Instance transfered to Main-Container");

			}catch(Exception e){
				if (logger.isLoggable(Logger.SEVERE))
					logger.log(Logger.SEVERE, "Source-Sink: handleInformMigrated: error while migrating: "+ e);
				cmd.setReturnValue(e);
			}
			_myContainer.releaseLocalAgent(name);
		}
		// Add an entry to CodeLocator table
		// Create agent
		private void launchAgent(VerticalCommand cmd) {
			if (logger.isLoggable(Logger.FINE))
				logger.log(Logger.FINE, "Source-Sink: launchAgent invoked");
			Object[] params = cmd.getParams();
			byte[] jar = (byte[]) params[0];
			byte[] instance = (byte[]) params[1];
			AID name = (AID) params[2];
			JarClassLoader loader = null;
			Deserializer des = null;

			try {
				//Register jar in JarManager and deserialize agent instance
				String jarfile = _jarManager.registerAgent(name, jar);
				File file = new File(jarfile);
				loader = new JarClassLoader(file, getClass()
						.getClassLoader());

				// Register ClassLoader to CodeLocator.
				getLocator().registerAgent(name, loader);
				des = new Deserializer(new ByteArrayInputStream(
						instance), loader);
				Agent agent = (Agent) des.readObject();
				des.close();

				if (logger.isLoggable(Logger.FINE))
					logger.log(Logger.FINE,
							"Source-Sink: launchAgent: Agent de-serialized");

				AID agentID = new AID(name.getName(), AID.ISGUID);
				NodeDescriptor myNodeDescriptor = _myContainer
						.getNodeDescriptor();

				//Create agent and inform the platform
				_myContainer.initAgent(agentID, agent, myNodeDescriptor
						.getOwnerPrincipal(), myNodeDescriptor
						.getOwnerCredentials());
				if (logger.isLoggable(Logger.FINE))
					logger.log(Logger.FINE,
							"Source-Sink: launchAgent: Agent created");
			} catch (Exception e) {
				if (logger.isLoggable(Logger.WARNING))
					logger.log(Logger.WARNING,
							"Source-Sink: launchAgent: Error launching agent: "+e);

				//Close Deserializer.
				try {
					if (des!=null) des.close();
				} catch (IOException ioe) {
					if (logger.isLoggable(Logger.WARNING))
						logger.log(Logger.WARNING,
								"Source-Sink: launchAgent: Error closing file descriptors: "+ioe);
				}

				//Remove agent from CodeLocator if the migration cannot be completed.
				getLocator().removeAgent(name);

				//Return error result.
				cmd.setReturnValue(e);
			}
		}

		// Remove an agent that is not powered up.
		private void removePrePoweredUpAgent(VerticalCommand cmd) {
			if (logger.isLoggable(Logger.FINE))
				logger.log(Logger.FINE, "Source-Sink: removePrePoweredUpAgent invoked");
			Object[] params = cmd.getParams();
			AID name = (AID) params[0];

			try {

				AID agentID = new AID(name.getName(), AID.ISGUID);

				//Remove agent from the platform
				_myContainer.getMain().deadAgent(agentID, false);

				//Remove agent references.
				getLocator().removeAgent(agentID);

				if (logger.isLoggable(Logger.FINE))
					logger.log(Logger.FINE,
							"Source-Sink: removePrePoweredUpAgent: Agent removed");
			} catch (Exception e) {
				if (logger.isLoggable(Logger.WARNING))
					logger.log(Logger.WARNING,
							"Source-Sink: removePrePoweredUpAgent: Error removing agent: "+e);
				cmd.setReturnValue(e);
			}
		}

		private void handleInformMigrationResult(VerticalCommand cmd) {
			if (logger.isLoggable(Logger.FINE))
				logger.log(Logger.FINE,
						"Source-Sink: handleInformMigrationResult invoked");
			Object[] params = cmd.getParams();
			AID name = (AID) params[0];
			String result = (String) params[1];

			try {
				MainContainer impl = _myContainer.getMain();
				ContainerID instanceCid = impl.getContainerID(name);

				//Send the result of the migration to the container where the
				// agent lives
				InterPlatformMobilitySlice instanceSlice = (InterPlatformMobilitySlice) getSlice(instanceCid
						.getName());
				try {
					if (logger.isLoggable(Logger.FINE))
						logger.log(Logger.FINE,
								"Source-Sink: handleInformMigrationResult: sending result to "
										+ instanceCid.getName());
					instanceSlice.informMigrationResult(name, result);
				} catch (IMTPException imtpe) {
					// Try to get a newer slice and repeat...
					instanceSlice = (InterPlatformMobilitySlice) getFreshSlice(instanceCid
							.getName());
					instanceSlice.informMigrationResult(name, result);
				}

				/*if (result.equals(MIGRATION_OK)) {

					//remove agent from platform
					if (logger.isLoggable(Logger.FINE))
						logger
								.log(Logger.FINE,
										"Source-Sink: handleInformMigrationResult removing agent from platform");

					_myContainer.getMain().deadAgent(name, false);

				}*/
			} catch (Exception e) {
				if (logger.isLoggable(Logger.WARNING))
					logger.log(Logger.WARNING,
							"Source-Sink: Error sending migration result: Possible agent duplication" + e);
			}
		}

	}

	private class CommandTargetSink implements Sink {

		public void consume(VerticalCommand cmd) {
			if (cmd.getName().equals(
					InterPlatformMobilityHelper.DELETE_AGENT_REFERENCES))
				handleDeleteAgentReferences(cmd);
		}

		private void handleDeleteAgentReferences(VerticalCommand cmd) {
			Object[] params = cmd.getParams();
			AID name = (AID) params[0];
			try {

				_myContainer.removeLocalAgent(name);

				//Remove agent references whether agent have his code on
				//his living container.
				/*if (getLocator().isRegistered(name)) {
					if (logger.isLoggable(Logger.FINE))
						logger.log(Logger.FINE,
								"TargetSink: handleDeleteAgentReferences: Deleting agent references: "
										+ name.getName());
					getLocator().removeAgent(name);
				} else {

					//In case of agent do not has his code here, we obtain from mobility service
					//the code location and send a deleteAgentReferences command to it.
					AgentMobilityService ams = (AgentMobilityService) myFinder.findService(AgentMobilityService.NAME);
					Agent agent = _myContainer.acquireLocalAgent(name);
					String codeContainerName = ams.getClassSite(agent);
					_myContainer.releaseLocalAgent(name);
					InterPlatformMobilitySlice codeSlice = (InterPlatformMobilitySlice) getSlice(codeContainerName);
					_myContainer.removeLocalAgent(agent.getAID());
					try {
						codeSlice.deleteAgentReferences(name);
					} catch (IMTPException imtpe) {
						// Try to get a newer slice and repeat...
						codeSlice = (InterPlatformMobilitySlice) getFreshSlice(codeContainerName);
						codeSlice.deleteAgentReferences(name);
					}

				}*/
			} catch (Exception e) {
				if (logger.isLoggable(Logger.WARNING))
					logger
							.log(Logger.WARNING,
									"TargetSink: handleDeleteAgentReferences: Error deleting agent references");
			}
		}
	}

	private class CommandOutgoingFilter extends Filter {

		protected boolean accept(VerticalCommand cmd) {
			if (cmd.getName().equals(AgentMobilityHelper.INFORM_MOVED)) {
				Object[] params = cmd.getParams();
				if ((params[1] instanceof PlatformID)
						&& (params[0] instanceof AID)) {

					if (!testLocalMigration(cmd))
						handleInformMoved(cmd);
					return false;
				}
			}

			if (cmd.getName().equals(
					AgentManagementSlice.INFORM_STATE_CHANGED)) {
				Object[] params = cmd.getParams();
				AID aid = (AID) params[0];
				AgentState from = (AgentState) params[1];
				AgentState to = (AgentState) params[2];

				//Check for AMS creation to install AMSResponder behaviour.
				if ((from.getValue() == Agent.AP_INITIATED)&&
						(to.getValue() == Agent.AP_ACTIVE)) {

					if (aid.getLocalName().equals("ams")) {
						if (_profile.getBooleanProperty(Profile.MAIN, false)) {
							AID amsAID = new AID("ams", false);
							Agent ams = _myContainer.acquireLocalAgent(amsAID);
							MessageTemplate mt = MessageTemplate.and(MessageTemplate
									.MatchOntology(MigrationOntology.NAME), MessageTemplate
									.MatchPerformative(ACLMessage.REQUEST));
							ams.addBehaviour(new AMSResponder(ams, mt, _migrationTimeoutResponder, _myContainer));
							_myContainer.releaseLocalAgent(amsAID);
						}
					}
				}

				//If an agent dies eliminate all references to it.
				if ((from.getValue() == Agent.AP_ACTIVE)&&
						(to.getValue() == Agent.AP_DELETED)) {

					String codeContainerName = null;

					try {
						AgentMobilityService ams = (AgentMobilityService) myFinder.findService(AgentMobilityService.NAME);
						Agent agent = _myContainer.acquireLocalAgent(aid);
						codeContainerName = ams.getClassSite(agent);
						_myContainer.releaseLocalAgent(aid);
					} catch (ServiceException se) {
						if (logger.isLoggable(Logger.WARNING))
							logger.log(Logger.WARNING,
									"CommandOutgoingFilter: Error getting agent code location from MobilityService.");
					} catch (IMTPException imtpe) {
						if (logger.isLoggable(Logger.WARNING))
							logger.log(Logger.WARNING,
									"CommandOutgoingFilter: Error getting agent code location from MobilityService.");
					}

					if (codeContainerName == null) {
						codeContainerName = MAIN_CONTAINER;
					}

					try {
						InterPlatformMobilitySlice slice =
								(InterPlatformMobilitySlice) getSlice(codeContainerName);
						slice.deleteAgentReferences(aid);

					} catch (ServiceException se) {
						if (logger.isLoggable(Logger.WARNING))
							logger.log(Logger.WARNING,
									"CommandOutgoingFilter: Error getting Slice to delete agent references.");
					} catch (IMTPException imtpe) {
						if (logger.isLoggable(Logger.WARNING))
							logger.log(Logger.WARNING,
									"CommandOutgoingFilter: Error deleting agent references.");
					}


					return true;
				}

				if (from.getValue() != AgentMobilityService.AP_TRANSIT)
					return true;

				if (to.getValue() == AgentMobilityService.AP_GONE) {
				} else if (to.getValue() == Agent.AP_ACTIVE) {
				} else if (to.getValue() == Agent.AP_DELETED) {
				}
			}

			if (cmd.getName().equals(MessagingSlice.SET_PLATFORM_ADDRESSES)) {

				if (!_codeLocatorMonitored) {

					//Associate to CodeLocator events.
					getLocator().subscribeToEvents(new CodeLocatorMonitor());
					_codeLocatorMonitored = true;
				}
			}

			return true;
		}

		private boolean testLocalMigration(VerticalCommand vcmd) {

			if (logger.isLoggable(Logger.FINE))
				logger.log(Logger.FINE,"CommandOutgoingFilter: testLocalMigration invoked");

			Object[] params = vcmd.getParams();
			PlatformID where = (PlatformID)params[1];
			AID name = (AID)params[0];

			//Test for a migration to the same platform.
			if (where.getName().equals(_myContainer.getPlatformID())) {

				//Test whether this is the Main Container.
				if (_myContainer.getMain()!=null) {
					Agent agent = _myContainer.acquireLocalAgent(name);
					agent.restoreBufferedState();
					_myContainer.releaseLocalAgent(name);

				} else {
					//Moving agent to Main-Container of the local platform.
					try {
						Agent agent = _myContainer.acquireLocalAgent(name);
						AgentMobilityHelper amh = ((AgentMobilityHelper) agent.getHelper(AgentMobilityHelper.NAME));
						agent.restoreBufferedState();
						_myContainer.releaseLocalAgent(name);
						amh.move(new ContainerID(MAIN_CONTAINER,null));
					} catch (ServiceException se) {
						if (logger.isLoggable(Logger.SEVERE))
							logger.log(Logger.SEVERE, "CommandOutgoingFilter: testLocalMigration: error moving agent to Main Container of local platform");
					}
				}
				return true;
			} else return false;
		}

		private void handleInformMoved(VerticalCommand vcmd) {

			if (logger.isLoggable(Logger.FINE))
				logger.log(Logger.FINE,
						"Outgoing Filter: handleInformMoved invoked");
			Object[] params = vcmd.getParams();
			AID aid = (AID) params[0];
			PlatformID where = (PlatformID) params[1];
			GenericCommand cmd = new GenericCommand(
					InterPlatformMobilityHelper.INFORM_MIGRATED,
					InterPlatformMobilityHelper.NAME, null);
			cmd.addParam(aid);
			cmd.addParam(where);

			try {
				Object lastException = submit(cmd);
				// If an exception was thrown, we forward it as an IMTPException
				// so that
				// the mobility helper handles it and forwards it to
				// Agent.notifyNove
				if (lastException != null)
					vcmd
							.setReturnValue(new IMTPException(
									"Error while migrating",
									(Throwable) lastException));
			} catch (ServiceException se) {
				vcmd.setReturnValue(new IMTPException("Error while migrating",
						(Throwable) se));
			}
		}
	}

	private class ServiceComponent implements Service.Slice {

		public VerticalCommand serve(HorizontalCommand cmd) {
			VerticalCommand result = null;

			if (cmd.getName().equals(
					InterPlatformMobilitySlice.H_TRANSFERINSTANCE)) {
				byte[] instance = (byte[]) cmd.getParams()[0];
				AID name = (AID) cmd.getParams()[1];
				String className = (String)cmd.getParams()[2];
				String codesrc = (String) cmd.getParams()[3];
				PlatformID where = (PlatformID) cmd.getParams()[4];

				cmd.setReturnValue(createAMSBehaviour(instance, name, className, codesrc,
						where));
			} else if (cmd.getName().equals(
					InterPlatformMobilitySlice.H_GETAGENTCODE)) {
				AID name = (AID) cmd.getParams()[0];
				String className = (String)cmd.getParams()[1];

				cmd.setReturnValue(handleGetAgentCode(name, className));
			} else if (cmd.getName().equals(
					InterPlatformMobilitySlice.H_INFORMMIGRATIONRESULT)) {
				AID name = (AID) cmd.getParams()[0];
				String res = (String) cmd.getParams()[1];
				result = handleInformMigrationResult(name, res);
			} else if (cmd.getName().equals(
					InterPlatformMobilitySlice.H_DELETEAGENTREFERENCES)) {
				GenericCommand gCmd = new GenericCommand(
						InterPlatformMobilityHelper.DELETE_AGENT_REFERENCES,
						InterPlatformMobilityHelper.NAME, null);
				AID name = (AID) cmd.getParams()[0];
				gCmd.addParam(name);

				result = gCmd;
			}
			return result;
		}

		private Object createAMSBehaviour(byte[] instance, AID name, String className,
										  String codesrc, PlatformID where) {

			try {
				// get agent code from jar
				//   first, look if the jar is in this container using CodeLocator
				// table
				//   else, look for it remotely (broadcast of getAgentCode
				// command)
				if (logger.isLoggable(Logger.FINE))
					logger.log(Logger.FINE,
							"ServiceComponent: createAMSBehaviour invoked");

				byte[] rawJar = null;
				String location = _jarManager.getAgentCodeLocation(name);
				if (location != null) {
					File jar = new File(location);
					rawJar = getJarByteArray(jar);
					if (logger.isLoggable(Logger.FINE))
						logger.log(Logger.FINE, "Agent Code in Main Container");
				} else {
					if (logger.isLoggable(Logger.FINE))
						logger.log(Logger.FINE, "Agent Code in "
								+ codesrc);
					Slice slice = getSlice(codesrc);

					rawJar = ((InterPlatformMobilitySlice) slice)
							.getAgentCode(name, className);
				}

				// create AMSInitiator behaviour and add it to ams
				if (rawJar != null) {
					AID amsAID = new AID("ams", false);
					Agent ams = _myContainer.acquireLocalAgent(amsAID);

					//Create the message and establish a timeout.
					ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
					msg.setReplyByDate(new Date(System.currentTimeMillis()
							+ _migrationTimeout));

					AMSInitiator b = new AMSInitiator(ams, msg, instance,
							rawJar, where, name, _myContainer);
					ams.addBehaviour(b);
					_myContainer.releaseLocalAgent(amsAID);
				} else {
					if (logger.isLoggable(Logger.SEVERE))
						logger
								.log(Logger.SEVERE,
										"ServiceComponent: createAMSBehaviour rawJar is Null");
					throw new ServiceException("Cannot get agent code");
				}

				return null;
			} catch (Exception e) {
				if (logger.isLoggable(Logger.SEVERE))
					logger
							.log(Logger.SEVERE,
									"ServiceComponent: createAMSBehaviour: error transfering instance");
				return e;
			}
		}

		// get jar file through CodeLocator table
		private byte[] handleGetAgentCode(AID name, String className) {
			//TODO: Trencar amb 2 metodes (obtenir codi per agents jar o per
			// agents normals)
			try {
				byte[] rawJar = null;
				String location = _jarManager.getAgentCodeLocation(name);

				if (logger.isLoggable(Logger.FINE))
					logger.log(Logger.FINE, "Location of agent's code: " + location);

				// Check if the JAR file is registered in the Jar manager.
				if (location != null) {
					File jar = new File(location);
					rawJar = getJarByteArray(jar);
					if (logger.isLoggable(Logger.FINE))
						logger.log(Logger.FINE,
								"ServiceCompo handleGetAgentCode jar returned, size: "
										+ rawJar.length);
					return rawJar;

					// If Jar is not registered in the Jar Manager...
				} else {
					if (getLocator().isRegistered(name)) {
						ClassLoader cl;
						if ((cl = getLocator().getAgentClassLoader(name)) instanceof JarClassLoader) {
							String sourceJarFile = ((JarClassLoader) cl).getJarFileName();
							String newJarFile = _jarManager.registerAgent(name, new File(sourceJarFile));
							File f = new File(newJarFile);
							getLocator().updateAgent(name, new JarClassLoader(f, cl.getParent()));

						} else {

							if (logger.isLoggable(Logger.FINE))
								logger
										.log(Logger.FINE,
												"ServiceCompo handleGetAgentCode jar doesn't exist but there is an alternative ClassLoader");

							// create jar agent
							String newJarFile = createJar(name, Class.forName(className));
							File f = new File(newJarFile);
							getLocator().updateAgent(name, new JarClassLoader(f, cl.getParent()));
						}

					} else {
						if (logger.isLoggable(Logger.FINE))
							logger
									.log(Logger.FINE,
											"ServiceCompo handleGetAgentCode jar doesn't exist");

						// create jar agent
						Class agentMainClass = Class.forName(className);
						String newJarFileName = createJar(name, agentMainClass);

						// Create classloader.
						File jarFile = new File(newJarFileName);
						JarClassLoader loader = new JarClassLoader(jarFile,
								agentMainClass.getClassLoader());
						getLocator().registerAgent(name, loader);
					}

					// load and return the new jar
					location = _jarManager.getAgentCodeLocation(name);
					if (location != null) {
						File jar = new File(location);
						rawJar = getJarByteArray(jar);
						if (logger.isLoggable(Logger.FINE))
							logger.log(Logger.FINE,
									"ServiceCompo handleGetAgentCode jar created and returned, size: "
											+ rawJar.length);
						return rawJar;
					} else {
						return null;
					}
				}
			} catch (Exception e) {
				// If any error occurs, return null as if code weren't in this
				// container
				if (logger.isLoggable(Logger.WARNING))
					logger
							.log(Logger.WARNING,
									"ServiceComponent: handleGetAgentCode error obtaining agent code");
				return null;
			}
		}

		// This method treats H_INFORMMIGRATIONRESULT commands sent by
		// the Main-Container slice.
		private VerticalCommand handleInformMigrationResult(AID name, String res) {

			GenericCommand result = null;
			Agent instance = _myContainer.acquireLocalAgent(name);
			//AID lockName = instance.getAID();
			if (res.equals(MIGRATION_OK)) {
				/*
				// If everything was successfull, commit migration
				if (logger.isLoggable(Logger.FINE))
					logger
							.log(Logger.FINE,
									"ServiceComponent: handleInformMigrationResult: commit migration");
				//_myContainer.commitMigration(instance);
				try {
					//Cause the invocation of 'beforeMove()' and the
					// subsequent termination of the agent thread
					instance.changeStateTo(new LifeCycle(AgentMobilityService.AP_GONE) {
						public boolean alive() {
							return false;
						}
					});

					result = new GenericCommand(
							InterPlatformMobilityHelper.DELETE_AGENT_REFERENCES,
							InterPlatformMobilityHelper.NAME, null);
					result.addParam(instance.getAID());
				} catch (Exception e) {
					if(logger.isLoggable(Logger.SEVERE))
						logger.log(Logger.SEVERE, "Error changing agent's state");
				}*/
				instance.doDelete();
			} else {
				// If not, abortMigration
				if (logger.isLoggable(Logger.INFO))
					logger.log(Logger.INFO,
							"Migration failure: Aborting migration: "
									+ res);
				instance.restoreBufferedState();
			}
			_myContainer.releaseLocalAgent(name);
			return result;
		}

		public Node getNode() throws ServiceException {
			try {
				return InterPlatformMobilityService.this.getLocalNode();
			} catch (IMTPException imtpe) {
				throw new ServiceException(
						"Problem contacting the IMTP Manager", imtpe);
			}
		}

		public Service getService() {
			return InterPlatformMobilityService.this;
		}
	}

	//FIXME: Hauriem de tenir una classe wrapper per el codelocator
	// i ocultar la variable _locator per un millor disseny
	private CodeLocator getLocator(){
		try{
			if(_locator==null)
				return ((AgentManagementService)
						myFinder.findService(AgentManagementService.NAME)).getCodeLocator();
			else
				return _locator;
		}catch(Exception e){
			if (logger.isLoggable(Logger.SEVERE))
				logger.log(Logger.SEVERE,
						"Error accessing Agent Management Service Code locator. "
								+ e);
			return null;
			//FIXME: Excepci� dedicada.
		}
	}

	// Filter for outgoing commands
	private final Filter _outFilter = new CommandOutgoingFilter();

	// The local slice for this service
	private final ServiceComponent _localSlice = new ServiceComponent();

	// The command sink, source side
	private final CommandSourceSink _senderSink = new CommandSourceSink();

	// The command sink, target side
	private final CommandTargetSink _receiverSink = new CommandTargetSink();

	private Logger logger = Logger.getMyLogger(getClass().getName());

	private Profile _profile;

	private AgentContainer _myContainer;

	private CodeLocator _locator;

	private String _jarPath;

	private JarManager _jarManager;

	private boolean _codeLocatorMonitored = false;

	protected int _migrationTimeout;
	protected int _migrationTimeoutResponder;

	// The helper for this service (entry point for agents).
	private class InterPlatformMigrationHelperImpl implements
			InterPlatformMobilityHelper {

		public void init(Agent a) {
			_a = a;
		}

		public void powerUpAgent(AID name) throws ServiceException {
			try {
				if (logger.isLoggable(Logger.FINE))
					logger.log(Logger.FINE,
							"MigrationHelper: powerUpAgent invoked");
				_myContainer.powerUpLocalAgent(name);
			} catch (NotFoundException ne) {
				throw new ServiceException("Agent to power up not found", ne);
			}
		}

		// Called by the ams to inform migration result
		public void informMigrationResult(AID aid, String result)
				throws ServiceException {
			GenericCommand cmd = new GenericCommand(
					InterPlatformMobilityHelper.INFORM_MIGRATION_RESULT,
					InterPlatformMobilityHelper.NAME, null);
			cmd.addParam(aid);
			cmd.addParam(result);

			submit(cmd);
		}

		public String launchIncommingAgent(byte[] jar, byte[] instance, AID name)
				throws ServiceException {
			GenericCommand cmd = new GenericCommand(
					InterPlatformMobilityHelper.LAUNCH_AGENT,
					InterPlatformMobilityHelper.NAME, null);
			cmd.addParam(jar);
			cmd.addParam(instance);
			cmd.addParam(name);

			Object lastException = submit(cmd);
			if (lastException != null) {
				if (logger.isLoggable(Logger.SEVERE))
					logger
							.log(Logger.SEVERE,
									"MigrationHelper: launchIncommingAgent returns an exception");
				return ((Exception) lastException).toString();
			} else {
				if (logger.isLoggable(Logger.FINE))
					logger.log(Logger.FINE,
							"MigrationHelper: launchIncommingAgent returns "
									+ MIGRATION_OK);
				return MIGRATION_OK;
			}
		}

		public void removePrePowerUpAgent(AID name) throws ServiceException {
			GenericCommand cmd = new GenericCommand(
					InterPlatformMobilityHelper.REMOVE_PREPOWERUP_AGENT,
					InterPlatformMobilityHelper.NAME, null);
			cmd.addParam(name);

			submit(cmd);
		}

		//This method is currently not used
		public void informMigrated(PlatformID where, AID name)
				throws ServiceException {
			GenericCommand cmd = new GenericCommand(
					InterPlatformMobilityHelper.INFORM_MIGRATED,
					InterPlatformMobilityHelper.NAME, null);
			cmd.addParam(name);
			cmd.addParam(where);

			Object lastException = submit(cmd);
			if (lastException != null) {
				throw new ServiceException("Error while migrating",
						(Throwable) lastException);
			}
		}

		private Agent _a;
	};

	// Work-around for PJAVA compilation
	protected Service.Slice getFreshSlice(String name) throws ServiceException {
		return super.getFreshSlice(name);
	}

	private byte[] getJarByteArray(File f) throws FileNotFoundException,
			IOException {
		FileInputStream fis = new FileInputStream(f);
		int length = fis.available();
		byte[] bytes = new byte[length];
		fis.read(bytes, 0, length);
		fis.close();
		return bytes;
	}

	private String createJar(AID aid, Class c) {

		ClassAnalysisLibrary cal;

		try {

			//Create an instance of class dependece analizer.
			cal = new ClassAnalysisLibrary(c);

			//Define common classes on all platforms that have not being
			// searched.
			cal.addFilter("jade/");
			cal.addFilter("java/");
			cal.addFilter("javax/");
			cal.addFilter("sun/");
			cal.addFilter("com/sun/");
			cal.addFilter("COM/rsa/");

			// Register JAR agent in the JarManager.
			return _jarManager.registerAgent(aid, cal.createJar(true));

		} catch (ClassNotFoundException cnfe) {
			if (logger.isLoggable(Logger.SEVERE))
				logger
						.log(Logger.SEVERE,
								"Incomming Filter: handleRequestCreated Error accessing agent classes");
		} catch (IOException ioe) {
			if (logger.isLoggable(Logger.SEVERE))
				logger
						.log(Logger.SEVERE,
								"Incomming Filter: handleRequestCreated Unable to do jar agent ");
		} catch (Exception e) {
			if (logger.isLoggable(Logger.SEVERE))
				logger.log(Logger.SEVERE, " Error creating jar");
		}

		return null;
	}

	private class Deserializer extends ObjectInputStream {

		public Deserializer(InputStream is, JarClassLoader cl)
				throws IOException {
			super(is);
			_loader = cl;
		}

		protected Class resolveClass(ObjectStreamClass v) throws IOException,
				ClassNotFoundException {
			return Class.forName(v.getName(), false, _loader);
		}

		private JarClassLoader _loader;

	}

	private class CodeLocatorMonitor extends CodeLocatorListener {

		/**
		 * Method executed each time an agent present in the
		 * CodeLocator is cloned.
		 */
		public ClassLoader handleCloneAgent(AID oldName, AID newName, ClassLoader cl) {

			// The new agent is taken into account.
			_jarManager.cloneAgent(oldName, newName);

			// ClassLoader is not replicated because in case of JarClassLoader
			// is automatically done by Jade.
			return null;
		}

		/**
		 * Method executed each time an agent present in the
		 * CodeLocator is removed.
		 */
		public void handleRemoveAgent(AID name, ClassLoader cl) {
			_jarManager.removeAgentRef(name);
		}

	}

}