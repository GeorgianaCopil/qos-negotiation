package client;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import PSO.Particle;

import negotiation.GlobalVars;
import negotiation.Offer;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;

public class ClientAgent extends Agent {

	private static final long serialVersionUID = -8831012499667090679L;

	private Client client;
	private PrintWriter file;
	public static PrintWriter fitnessOfertePrimite;
	
	public void setFile(PrintWriter file){
		this.file = file;
	}

	@Override
	protected void setup() {

		float[] min = new float[Particle.nrResources];
		float[] max = new float[Particle.nrResources];

		try {
			fitnessOfertePrimite = new PrintWriter("fitnessOferteTrimise.txt");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		min[0] = 30;
		min[1] = 1;
		min[2] = 1000;
		min[3] = 50;
		max[0] = 60;
		max[1] = 9;
		max[2] = 5000;
		max[3] = 800;

		System.out.println("The client agent - READY! ");
		client = new Client(50000, min, max);
		addBehaviour(new ReceiveMessageClientBehaviour(this));
		startNegotiation();
	}

	public Client getClient() {
		return client;
	}

	@SuppressWarnings("deprecation")
	private void startNegotiation() {
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM_REF);
		try {
			// TODO
			Offer offer = new Offer();
			offer.setHdd(60);
			offer.setCpu(9);
			offer.setMemory(5000);
			offer.setCost(50);
			msg.setContentObject(offer);
			System.out.println("Offer from Client:" + offer.toString());
			client.updateSwarm();
			file.println("Offer number: " + client.getOfferNumber());
			file.println("Offer from Client:" + offer.toString());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		msg.addReceiver(new AID(GlobalVars.SERVICE_CENTER_NAME + "@"
				+ this.getContainerController().getPlatformName()));
		this.send(msg);
	}

	@SuppressWarnings("deprecation")
	public void sendRefuseOfferMessage() {
		ACLMessage msg = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
		msg.setContent("I despise your offer. Negotiation over! ");
		msg.addReceiver(new AID(GlobalVars.SERVICE_CENTER_NAME + "@"
				+ this.getContainerController().getPlatformName()));
		this.send(msg);
	}

	@SuppressWarnings("deprecation")
	public void sendAcceptOfferMessage() {
		ACLMessage msg = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
		msg.setContent("I accept your offer");
		msg.addReceiver(new AID(GlobalVars.SERVICE_CENTER_NAME + "@"
				+ this.getContainerController().getPlatformName()));
		file.close();
		
		fitnessOfertePrimite.close();
		this.send(msg);
	}

	@SuppressWarnings("deprecation")
	public void sendMessage(Offer offer) {
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM_REF);
		try {
			msg.setContentObject(offer);
			System.out.println("Offer number: " + client.getOfferNumber());
			System.out.println("Offer from Client:" + offer.toString());
			file.println("Offer number: " + client.getOfferNumber());
			file.println("Offer from Client:" + offer.toString());
			
			fitnessOfertePrimite.println(offer.getFitness());
		} catch (IOException e) {
			e.printStackTrace();
		}
		msg.addReceiver(new AID(GlobalVars.SERVICE_CENTER_NAME + "@"
				+ this.getContainerController().getPlatformName()));
		this.send(msg);
	}

}
