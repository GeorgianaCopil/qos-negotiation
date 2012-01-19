package client;

import java.io.IOException;
import java.util.HashMap;

import negotiation.GlobalVars;
import negotiation.Offer;
import PSO.Particle;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;

public class ClientSubswarmAgent extends Agent{
	
private static final long serialVersionUID = -8831012499667090679L;
	
	private ClientSubswarm client;
	@Override
	protected void setup(){
		
		float[] min = new float[Particle.nrResources];
		float[] max = new float[Particle.nrResources];
		
		min[0] = 30;  min[1] = 1; min[2] = 1000; min[3] = 50;
		max[0] = 60; max[1] = 9; max[2] = 5000; max[3] = 800;
		
		int noServers = 6;
		int subSwarmSize = 30;
		
		System.out.println("The client agent - READY! ");
		client = new ClientSubswarm(min, max, subSwarmSize, noServers);
		addBehaviour(new ReceiveMessageClientBehaviour(this));
		startNegotiation();
	}
	
    public ClientSubswarm getClient() {
		return client;
	}
    
    @SuppressWarnings("deprecation")
	private void startNegotiation(){
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM_REF);
        try {
        	//TODO
            Offer offer = new Offer();
            offer.setHdd(60);
            offer.setCpu(9);
            offer.setMemory(5000);
            offer.setCost(3000);
            msg.setContentObject(offer );
            System.out.println("Offer from Client:"+offer.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        msg.addReceiver(new AID(GlobalVars.SERVICE_CENTER_NAME + "@" + this.getContainerController().getPlatformName()));
        this.send(msg);
    }
    

	
	 @SuppressWarnings("deprecation")
	public void sendRefuseOfferMessage() {
	        ACLMessage msg = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
	        msg.setContent("I despise your offer. Negotiation over! ");
	        msg.addReceiver(new AID(GlobalVars.SERVICE_CENTER_NAME + "@" + this.getContainerController().getPlatformName()));
	        this.send(msg);
	    }

	@SuppressWarnings("deprecation")
	public void sendAcceptOfferMessage() {
        ACLMessage msg = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
        msg.setContent("I accept your offer");
        msg.addReceiver(new AID(GlobalVars.SERVICE_CENTER_NAME + "@" + this.getContainerController().getPlatformName()));
        this.send(msg);
    }
	
	 @SuppressWarnings("deprecation")
	public void sendMessage(HashMap<Integer, Offer> counterOffer) {
	        ACLMessage msg = new ACLMessage(ACLMessage.INFORM_REF);
	        try {
	            msg.setContentObject( counterOffer);
	            System.out.println("Offer from Client:"+counterOffer.toString());
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        msg.addReceiver(new AID(GlobalVars.SERVICE_CENTER_NAME + "@" + this.getContainerController().getPlatformName()));
	        this.send(msg);
	    }

}
