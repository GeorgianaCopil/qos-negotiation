package server;

import java.util.logging.Level;
import java.util.logging.Logger;

import negotiation.Offer;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

public class ReceiveMessageBehaviour extends CyclicBehaviour {

	private static final long serialVersionUID = 3341093680683567258L;

	private ServerAgent serverAgent;
	
	public ReceiveMessageBehaviour(Agent agent){
		
		serverAgent = (ServerAgent) agent;
		
	}

	@Override
	public void action() {

		ACLMessage message = serverAgent.receive();

		if (message == null) {
			return;
		}

		switch (message.getPerformative()) {
		case ACLMessage.INFORM_REF: {
			try {

				
				Offer offer = (Offer) message.getContentObject();

				if (serverAgent.acceptOffer(offer)) {
					System.err.println("Server: Offer accepted ");
					serverAgent.sendAcceptOfferMessage();
					
				} else {
					
					
					Offer counterOffer = serverAgent.computeCounterOffer(offer);
					if (counterOffer == null)
						serverAgent.sendRefuseOfferMessage();
					else {

						serverAgent.sendMessage(counterOffer);
						serverAgent.waitForOffer();

					}
				}
			} catch (UnreadableException ex) {
				Logger.getLogger(ReceiveMessageBehaviour.class.getName()).log(
						Level.SEVERE, null, ex);
			}
		}
			break;
		case ACLMessage.ACCEPT_PROPOSAL: {
			serverAgent.printNegotiationResults("server");
			System.out.println("Server: WoooHooo!");
		}
			break;
		case ACLMessage.REJECT_PROPOSAL: {
			System.out.println("Server: Sorry.");
		}
			break;
		}

	}

}
