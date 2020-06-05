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
	
	private String targetBookTitle = "hola";
	private AID[] sellerAgents;
	private List<AID> AvailablesellerAgents = new ArrayList<AID>();
	private boolean hazlo = true;

	
	protected void setup() {
		//Object[] args = getArguments();
		//args != null && args.length > 0
		System.out.println("hu");
		if (!targetBookTitle.isEmpty()) {
			//targetBookTitle = (String) args[0];
			System.out.println("Data is "+targetBookTitle);
			// Add a TickerBehaviour that schedules a request to seller agents every minute
			addBehaviour(new ThreeStepBehaviour());
			
			
			
		} else {
			// Make the agent terminate
			System.out.println("No target book title specified");
			doDelete();
		}					
	}

	
	public class ThreeStepBehaviour extends Behaviour {
		private int step = 0;
		public void action() {
			System.out.println("Trying to process "+targetBookTitle);
			// Update the list of seller agents
			DFAgentDescription template = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setType("ImageProcessing");
			template.addServices(sd);
			try {
				DFAgentDescription[] result = DFService.search(myAgent, template); 
				System.out.println("Found the following seller agents:");
				sellerAgents = new AID[result.length];
				for (int i = 0; i < result.length; ++i) {
					sellerAgents[i] = result[i].getName();
					System.out.println(sellerAgents[i].getName());
				}
			}
			catch (FIPAException fe) {
				fe.printStackTrace();
			}
			
			switch (step) {
				case 0:
					// Perform the request
					myAgent.addBehaviour(new RequestPerformer(0));
					step++;
					break;
				case 1:
					// Perform the request
					//myAgent.addBehaviour(new RequestPerformer(1));
					step++;
					break;
				case 2:
					// perform operation Z
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
		private AID bestSeller; // The agent who provides the best offer 
		private int repliesCnt = 0; // The counter of replies from seller agents
		private MessageTemplate mt; // The template to receive replies
		private int step = 0;

		public RequestPerformer(int i) {
			// TODO Auto-generated constructor stub
			step = i;
		}

		public void action() {
			switch (step) {
			case 0:
				System.out.println("INICIALIZING THE SERVICE WITH THE DATA "+targetBookTitle);
				// Send the cfp to all sellers
				ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
				for (int i = 0; i < sellerAgents.length; ++i) {
					cfp.addReceiver(sellerAgents[i]);
				} 
				cfp.setContent(targetBookTitle);
				cfp.setConversationId("ImageProcessing");
				cfp.setReplyWith("cfp"+System.currentTimeMillis()); // Unique value
				myAgent.send(cfp);
				// Prepare the template to get proposals
				mt = MessageTemplate.and(MessageTemplate.MatchConversationId("ImageProcessing"),
						MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
				step = 1;
				break;
			case 1:
				System.out.println("RECEIVE ALL PROPOSALS");
				// Receive all proposals/refusals from seller agents
				ACLMessage reply = myAgent.receive(mt);
				if (reply != null) {
					// Reply received
					if (reply.getPerformative() == ACLMessage.PROPOSE) {
						// This is an offer 
						String status = reply.getContent();
						if (status.equalsIgnoreCase("OK")) {
							// This is the best offer at present
							System.out.println(reply.getSender());
							AvailablesellerAgents.add(reply.getSender());
						}
					}
					repliesCnt++;
					System.out.println(repliesCnt);
					System.out.println(sellerAgents.length);
					if (repliesCnt >= sellerAgents.length) {
						System.out.println("ho");
						bestSeller = AvailablesellerAgents.get(0);
						for (AID aid : AvailablesellerAgents) {
							System.out.println("*****");
							System.out.println(aid);
							System.out.println("*****");
						}
						// We received all replies
						step = 2; 
					}
				}
				else {
					block();
				}
				break;
			case 2:
				// Send the purchase order to the seller that provided the best offer
				System.out.println("SENDIND THE DATA TO THE FIRST SERVICE AVAILABLE");
				ACLMessage order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
				order.addReceiver(bestSeller);
				order.setContent(targetBookTitle);
				order.setConversationId("ImageProcessing");
				order.setReplyWith("order"+System.currentTimeMillis());
				myAgent.send(order);
				// Prepare the template to get the purchase order reply
				mt = MessageTemplate.and(MessageTemplate.MatchConversationId("ImageProcessing"),
						MessageTemplate.MatchInReplyTo(order.getReplyWith()));
				step = 3;
				break;
			case 3:      
				// Receive the purchase order reply
				reply = myAgent.receive(mt);
				if (reply != null) {
					// Purchase order reply received
					if (reply.getPerformative() == ACLMessage.INFORM) {
						// Purchase successful. We can terminate
						System.out.println(targetBookTitle+" successfully processed from agent "+reply.getSender().getName());
						System.out.println(reply.getContent());
						targetBookTitle = reply.getContent();
						
						
					}
					else {
						System.out.println("Attempt failed: requested book already sold.");
						block();
					}

					if(hazlo == true) {
						hazlo = false;
						AID remoteAMS = new AID("ams@192.168.0.110:1964/JADE", AID.ISGUID);
						remoteAMS.addAddresses("http://raspberrypi:31711/acc");
						//this.addBehaviour(new Beh1(this));
						PlatformID destination = new PlatformID(remoteAMS);
						doMove(destination);
						
		
					}
						step = 4;
					
					
					
				}
				else {
					block();
				}
				break;
			}        
		}

		public boolean done() {
			if (step == 2 && bestSeller == null) {
				System.out.println("Attempt failed: "+targetBookTitle+" not available for sale");
			}
			return ((step == 2 && bestSeller == null) || step == 4);
		}
	}  // End of inner class RequestPerformer
	
	
	public void afterMove() {
		System.out.println("aiuda");
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("hola");
		addBehaviour(new ThreeStepBehaviour());
	}
	
}