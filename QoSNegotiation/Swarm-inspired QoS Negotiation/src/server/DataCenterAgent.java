package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import client.ClientAgent;

import PSO.Particle;

import negotiation.GlobalVars;
import negotiation.Offer;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;

public class DataCenterAgent extends Agent{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3575535954133085186L;
	private DataCenter dataCenter;
	private PrintWriter file;
	
	public void setFile(PrintWriter file){
		this.file = file;
		
	}
	
	int i=0;
	@Override
	protected void setup(){
		
		float[] min = new float[Particle.nrResources];
		float[] max = new float[Particle.nrResources];
		
		min[0] = 30;  min[1] = 1; min[2] = 1000; min[3] = 50;
		max[0] = 60; max[1] = 9; max[2] = 5000; max[3] = 800;
		
		System.out.println("Data center agent - READY! ");
		dataCenter = new DataCenter(6, max, min);
		
		 addBehaviour(new ReceiveMessageDCBehaviour(this));
	}
	
    @SuppressWarnings("deprecation")
	public void sendAcceptOfferMessage() {
        ACLMessage msg = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
        msg.setContent("I accept your offer");
        msg.addReceiver(new AID(GlobalVars.CLIENT_NAME + "@" + this.getContainerController().getPlatformName()));
        
        
        file.close();
        ClientAgent.fitnessOfertePrimite.close();
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
    
    public ArrayList<Offer> computeCounterOffer(Offer clientOffer) {
    	return (ArrayList<Offer>) dataCenter.sendCounterOffers(clientOffer);
    }
    
    @SuppressWarnings("deprecation")
	public void sendMessage(ArrayList<Offer> offer) {
        
    	ACLMessage msg = new ACLMessage(ACLMessage.INFORM_REF);
        try {
            msg.setContentObject( offer);
            System.out.println("Offer from Server:"+offer.toString());
			file.println("Offer from Server:" + offer.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        msg.addReceiver(new AID(GlobalVars.CLIENT_NAME + "@" + this.getContainerController().getPlatformName()));
        this.send(msg);
    }
    
    public boolean checkAcceptOffer(Offer offer) {
    	return dataCenter.acceptOffer(offer);
    }

}