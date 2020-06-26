package jade.core.GUI;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jade.core.AID;
import jade.core.Agent;
import jade.core.PlatformID;
import jade.core.SecureAgent.SecureAgentPlatform;
import jade.core.SecureTPM.Pair;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;


public class CAAgent extends SecureAgentPlatform implements Serializable {


	// private static final long serialVersionUID = 91162339198848092L;
	public List<Pair<PlatformID,String>> hops;
	int index = 0;
	private String DATA = "HOPS:";
	private AID[] searchAgents;
	private List<AID> AvailablesellerAgents = new ArrayList<AID>();
	private transient AgentGui myGUI;
	private String service;
	private int temporal;


	public void setup() {
		if (!DATA.isEmpty()) {
			//targetBookTitle = (String) args[0];
			System.out.println("Data is "+DATA);
			// ADD A NEW BEHAVIOUR TO PROCESS A NEW REQUEST IN EVERY HOP.
			myGUI = new AgentGuiImpl(this);
			myGUI.show();
			//addBehaviour(new ThreeStepBehaviour());
		} else {
			//DATA IS NOT DEFINED
			System.out.println("DATA IS NOT DEFINED");
			doDelete();
		}
	}




	public void afterMove() {

		System.out.println(hops.size());
		System.out.println(index);

		if(!hops.get(index).getValue().equalsIgnoreCase("")) {
			AvailablesellerAgents.clear();
			addBehaviour(new ThreeStepBehaviour());
			service = hops.get(index).getValue();
			temporal=index;
		}

		index++;

		if(index>=hops.size()){
			System.out.println("END MIGRATION");
			System.out.println(DATA);
		}else{
			System.out.println(DATA);

			PlatformID newDestination = hops.get(index).getKey();
			doSecureMigration(newDestination);
		}


	}

	public void setHops(List<Pair<PlatformID,String>> hopsList){
		hops = hopsList;
	}

	public class ThreeStepBehaviour extends Behaviour {
		private int step = 0;
		public void action() {

			System.out.println("Trying to process "+DATA);

			//SEARCH THE AVAILABLE AGENTS IN THE DF
			DFAgentDescription template = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			//sd.setType("ImageProcessing");
			System.out.println(hops.get(temporal).getValue());

			sd.setType(hops.get(temporal).getValue());
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

					cfp.setConversationId(hops.get(temporal).getValue());
					cfp.setReplyWith("cfp"+System.currentTimeMillis()); // Unique value
					myAgent.send(cfp);

					mt = MessageTemplate.and(MessageTemplate.MatchConversationId(hops.get(temporal).getValue()),
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
					order.setConversationId(hops.get(temporal).getValue());
					order.setReplyWith("order"+System.currentTimeMillis());
					myAgent.send(order);

					mt = MessageTemplate.and(MessageTemplate.MatchConversationId(hops.get(temporal).getValue()),
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
							System.out.println("**************************");
							DATA = reply.getContent();
							System.out.println("**************************");

						}else {
							System.out.println("REQUEST FAILED: AGENT IS NOT AVAILABLE.");
							block();
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




}






