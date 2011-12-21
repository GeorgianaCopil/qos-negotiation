package client;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import negotiation.Offer;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

public class ReceiveMessageClientBehaviour extends CyclicBehaviour {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	ClientAgent clientAgent;

	public ReceiveMessageClientBehaviour(Agent agent) {
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
				
				@SuppressWarnings("unchecked")
				ArrayList<Offer> offers = (ArrayList<Offer>) message
						.getContentObject();
			
				if (clientAgent.getClient().acceptOffer(offers, 10))
					clientAgent.sendAcceptOfferMessage();
				else {
					Offer counterOffer = clientAgent.getClient().computeOffer(
							offers);
					if (counterOffer == null)
						clientAgent.sendRefuseOfferMessage();
					else {
						clientAgent.sendMessage(counterOffer);
						clientAgent.getClient().updateSwarm();
					}
				}
			} catch (UnreadableException ex) {
				Logger.getLogger(ReceiveMessageClientBehaviour.class.getName())
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
