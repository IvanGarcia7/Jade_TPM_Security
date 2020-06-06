package jade.core.CasoUso;

import jade.core.Agent;
import jade.core.PlatformID;
import java.util.ArrayList;
import java.util.List;
import jade.core.AID;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;


public class AgentRastreator extends Agent {

	private String DATA = "hola";
	private AID[] searchAgents;
	private List<AID> AvailablesellerAgents = new ArrayList<AID>();
	private boolean proceed = true;


	protected void setup() {
		//Object[] args = getArguments();
		//args != null && args.length > 0
		if (!DATA.isEmpty()) {
			//targetBookTitle = (String) args[0];
			System.out.println("Data is "+DATA);
			// ADD A NEW BEHAVIOUR TO PROCESS A NEW REQUEST IN EVERY HOP.
			addBehaviour(new ThreeStepBehaviour());
		} else {
			//DATA IS NOT DEFINED
			System.out.println("DATA IS NOT DEFINED");
			doDelete();
		}
	}


	public class ThreeStepBehaviour extends Behaviour {
		private int step = 0;
		public void action() {

			System.out.println("Trying to process "+DATA);

			//SEARCH THE AVAILABLE AGENTS IN THE DF
			DFAgentDescription template = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setType("ImageProcessing");
			template.addServices(sd);

			try {
				DFAgentDescription[] result = DFService.search(myAgent, template);
				System.out.println("FOUND THE FOLLOWING IMAGE PROCESSING AGENTS:");

				searchAgents = new AID[result.length];
				for (int i = 0; i < result.length; ++i) {
					searchAgents[i] = result[i].getName();
					System.out.println(searchAgents[i].getName());
				}

			}
			catch (FIPAException e) {
				e.printStackTrace();
			}

			switch (step) {
				case 0:
					myAgent.addBehaviour(new RequestPerformer(0));
					step++;
					break;
				case 1:
					//myAgent.addBehaviour(new RequestPerformer(1));
					step++;
					break;
				case 2:
					//myAgent.addBehaviour(new RequestPerformer(2));
					step++;
					break;
			}
		}

		public boolean done() {
			return step == 3;
		}

	}


	private class RequestPerformer extends Behaviour {
		//AGENT AVAILABLE TO PROCESS THE REQUEST
		private AID selectedAgent;
		//NUNMBER OF AGENTS DETECTED
		private int counterAgents = 0;
		private MessageTemplate mt;
		private int step = 0;

		public RequestPerformer(int i) {
			//INITIALIZE THE PERFORMER IN THE STEP i
			step = i;
		}

		public void action() {
			switch (step) {
				case 0:
					System.out.println("INICIALIZING THE SERVICE WITH THE DATA "+DATA);
					// SEND A REQUEST TO ALL AVAILABLE AGENTS

					ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
					for (int i = 0; i < searchAgents.length; ++i) {
						cfp.addReceiver(searchAgents[i]);
					}
					cfp.setContent("REQUEST");
					cfp.setConversationId("ImageProcessing");
					cfp.setReplyWith("cfp"+System.currentTimeMillis()); // Unique value
					myAgent.send(cfp);

					mt = MessageTemplate.and(MessageTemplate.MatchConversationId("ImageProcessing"),
							MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
					step = 1;
					break;
				case 1:
					System.out.println("RECEIVE ALL PROPOSALS");

					ACLMessage reply = myAgent.receive(mt);
					if (reply != null) {
						if (reply.getPerformative() == ACLMessage.PROPOSE) {
							String status = reply.getContent();
							if (status.equalsIgnoreCase("OK")) {
								System.out.println(reply.getSender());
								AvailablesellerAgents.add(reply.getSender());
							}
						}
						counterAgents++;
						if (counterAgents >= searchAgents.length) {
							System.out.println("ho");
							//GET THE FIRST AGENT AVAILABLE
							selectedAgent = AvailablesellerAgents.get(0);
							//PROCEED WITH THE STEP 2 AFTER RECEIVE ALL REPLIES
							step = 2;
						}
					}
					else {
						block();
					}
					break;
				case 2:
					// SEND THE DATA TO THE SELECTEDAGENT CHOOSE PREVIOUSLY
					System.out.println("SENDIND THE DATA TO THE FIRST SERVICE AVAILABLE");
					ACLMessage order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
					order.addReceiver(selectedAgent);
					order.setContent(DATA);
					order.setConversationId("ImageProcessing");
					order.setReplyWith("order"+System.currentTimeMillis());
					myAgent.send(order);

					mt = MessageTemplate.and(MessageTemplate.MatchConversationId("ImageProcessing"),
							MessageTemplate.MatchInReplyTo(order.getReplyWith()));
					step = 3;
					break;
				case 3:
					//RECEIVE THE DATA PROCESSED
					reply = myAgent.receive(mt);
					if (reply != null) {
						if (reply.getPerformative() == ACLMessage.INFORM) {
							System.out.println(DATA+" SUCCESSFULLY PROCESED FROM AGENT "+reply.getSender().getName());
							System.out.println(reply.getContent());
							DATA = reply.getContent();
						}else {
							System.out.println("REQUEST FAILED: AGENT IS NOT AVAILABLE.");
							block();
						}

						if(proceed == true) {
							proceed = false;
							//MOVE IN A NEW PLATFORM
							AID remoteAMS = new AID("ams@192.168.0.110:1964/JADE", AID.ISGUID);
							remoteAMS.addAddresses("http://raspberrypi:31711/acc");
							PlatformID destination = new PlatformID(remoteAMS);
							doMove(destination);
						}
						step = 4;
					}else {
						block();
					}
					break;
			}
		}

		public boolean done() {
			return false;
		}
	}  // End of inner class RequestPerformer


	public void afterMove() {
		System.out.println("THE AGENT COMPLETE THE MIGRATION CORRECTLY");
		//CLEAR ALL THE VARIABLES USED PREVIUSLY AND START A NEW BEHAVIOUR
		AvailablesellerAgents.clear();
		addBehaviour(new ThreeStepBehaviour());
	}

}