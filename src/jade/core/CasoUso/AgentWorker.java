package jade.core.CasoUso;



import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;


public class AgentWorker extends Agent {

	public void setup(){

		//REGISTER INTO THE YELLOW PAGES
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("ImageProcessing");
		sd.setName("ImageWorker");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}

		//ADD THE BEHAVIOURS OF THE AGENT IMAGE PROCESSING
		addBehaviour(new ImageProcessingRequest());
		addBehaviour(new ImageProcessingResponse());

	}


	private class ImageProcessingRequest extends CyclicBehaviour {
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
			ACLMessage msg = myAgent.receive(mt);

			if (msg != null) {
				System.out.println("SENDIND THE OK CONFIRMATION");
				String title = msg.getContent();
				ACLMessage reply = msg.createReply();
				String Msg = "OK";
				//INFORM ABOUT THE STATUS OF THE AGENT
				if (title != null) {
					reply.setPerformative(ACLMessage.PROPOSE);
					reply.setContent(Msg);
				} else {
					reply.setPerformative(ACLMessage.REFUSE);
					reply.setContent("not-available");
				}
				myAgent.send(reply);
			}
			else {
				//WAIT FOR THE MESSAGE
				block();
			}
		}
	}

	private class ImageProcessingResponse extends CyclicBehaviour {
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
			ACLMessage msg = myAgent.receive(mt);

			if (msg != null) {
				String title = msg.getContent()+"Plataforma 2";
				ACLMessage reply = msg.createReply();
				//if (null) {
				reply.setPerformative(ACLMessage.INFORM);
				reply.setContent(title);
				System.out.println("DATA PROCESED SUCCESFULLY: "+title);
				myAgent.send(reply);
			}else {
				block();
			}


			//else {
			// The requested book has been sold to another buyer in the meanwhile .
			//	reply.setPerformative(ACLMessage.FAILURE);
			//	reply.setContent("not-available");
			//}
		}
	}

}
