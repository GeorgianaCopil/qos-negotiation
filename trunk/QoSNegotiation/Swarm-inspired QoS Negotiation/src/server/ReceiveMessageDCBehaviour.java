package server;

import java.util.ArrayList;

import negotiation.Offer;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

public class ReceiveMessageDCBehaviour extends CyclicBehaviour {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public DataCenterAgent dataCenterAgent;

	public ReceiveMessageDCBehaviour(Agent dataCenterAgent) {
		this.dataCenterAgent = (DataCenterAgent) dataCenterAgent;
	}

	@Override
	public void action() {

		ACLMessage message = dataCenterAgent.receive();
		if (message == null) {
			return;
		}

		switch (message.getPerformative()) {
		case ACLMessage.INFORM_REF: {
			try {
				Offer offer = (Offer) message.getContentObject();

				if (dataCenterAgent.checkAcceptOffer(offer)) {
					System.err.println("ServiceCenterServer Offer accepted: "
							+ offer.toString());
					dataCenterAgent.sendAcceptOfferMessage();
				} else {
					ArrayList<Offer> offers = dataCenterAgent
							.computeCounterOffer(offer);
					dataCenterAgent.waitForOffer(10);
					if (offers.isEmpty()) {
						dataCenterAgent.sendRefuseOfferMessage();
					} else {

						dataCenterAgent.sendMessage(offers);
					}
				}
			} catch (UnreadableException ex) {
				ex.printStackTrace();
			}
		}
			break;
		case ACLMessage.ACCEPT_PROPOSAL: {
			System.out.println("ServiceCenter Server: WoooHooo!");
		}
			break;
		case ACLMessage.REJECT_PROPOSAL: {
			System.out.println("ServiceCenter Server: Sorry.");
		}
			break;
		}

	}

}
