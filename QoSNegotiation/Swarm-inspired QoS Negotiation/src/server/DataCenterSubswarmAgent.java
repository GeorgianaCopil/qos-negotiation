package server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import negotiation.GlobalVars;
import negotiation.Offer;
import PSO.Particle;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;

public class DataCenterSubswarmAgent extends Agent{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private DataCenterSubswarm dataCenter;
	int i=0;
	@Override
	protected void setup(){
		
		float[] min = new float[Particle.nrResources];
		float[] max = new float[Particle.nrResources];
		
		min[0] = 30;  min[1] = 1; min[2] = 1000; min[3] = 50;
		max[0] = 60; max[1] = 9; max[2] = 5000; max[3] = 800;
		
		System.out.println("Data center agent - READY! ");
		dataCenter = new DataCenterSubswarm(1, max, min);
		
		 addBehaviour(new ReceiveMessageDCBehaviour(this));
	}
	
    @SuppressWarnings("deprecation")
	public void sendAcceptOfferMessage() {
        ACLMessage msg = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
        msg.setContent("I accept your offer");
        msg.addReceiver(new AID(GlobalVars.CLIENT_NAME + "@" + this.getContainerController().getPlatformName()));
        this.send(msg);
    }
    
    @SuppressWarnings("deprecation")
	public void sendRefuseOfferMessage() {
        ACLMessage msg = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
        msg.setContent("I despise your offer. Negotiation over! ");
        msg.addReceiver(new AID(GlobalVars.CLIENT_NAME + "@" + this.getContainerController().getPlatformName()));
        this.send(msg);
    }
    
    public void waitForOffer(int time) {
    	dataCenter.waitForOffer(time);
    }
    
    public Map<Integer, Offer> computeCounterOffer(HashMap<Integer, Offer> clientOffer) {
    	return  dataCenter.sendCounterOffers(clientOffer);
    }
    
    @SuppressWarnings("deprecation")
	public void sendMessage(ArrayList<Offer> offer) {
        
    	ACLMessage msg = new ACLMessage(ACLMessage.INFORM_REF);
        try {
            msg.setContentObject( offer);
            System.out.println("Offer from Server:"+offer.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        msg.addReceiver(new AID(GlobalVars.CLIENT_NAME + "@" + this.getContainerController().getPlatformName()));
        this.send(msg);
    }
    
    public boolean checkAcceptOffer(Map<Integer, Offer> offer) {
    	return dataCenter.acceptOffer(offer);
    }


}
