package client;

import java.util.logging.Level;
import java.util.logging.Logger;

import negotiation.Offer;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

public class ReceiveMessageBehaviour extends CyclicBehaviour {

	private static final long serialVersionUID = -7309051465448728382L;
	
	private ClientAgent clientAgent;
	
	public ReceiveMessageBehaviour(Agent agent){
		
		clientAgent = (ClientAgent) agent;
		
	}

	@Override
	public void action() {
		
		ACLMessage message = clientAgent.receive();

		if (message == null) {
			return;
		}

		switch (message.getPerformative()) {
		case ACLMessage.INFORM_REF: {
			try {
				
				Offer offer = (Offer) message
						.getContentObject();
				if (clientAgent.acceptOffer(offer)){
					System.err.println("Client: Offer accepted ");
					clientAgent.sendAcceptOfferMessage();
					
				}
				else {
					Offer counterOffer = clientAgent.computeCounterOffer(offer);
					if (counterOffer == null){
						System.out.println("aici");
						clientAgent.sendRefuseOfferMessage();
					}
					else {
						
						clientAgent.sendMessage(counterOffer);
						clientAgent.waitForOffer();
						
					}
				}
			} catch (UnreadableException ex) {
				Logger.getLogger(ReceiveMessageBehaviour.class.getName())
						.log(Level.SEVERE, null, ex);
			}
		}
			break;
		case ACLMessage.ACCEPT_PROPOSAL: {
			System.out.println("Client: WoooHooo!");
		}
			break;
		case ACLMessage.REJECT_PROPOSAL: {
			System.out.println("Client: Sorry.");
		}
			break;
		}

		
	}

}
