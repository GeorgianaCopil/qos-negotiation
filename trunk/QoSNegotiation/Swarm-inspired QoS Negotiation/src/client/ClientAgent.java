package client;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import PSO.Particle;
import negotiation.GlobalVars;
import negotiation.Offer;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;

public class ClientAgent extends Agent {

	private static final long serialVersionUID = -8831012499667090679L;

	private Client client;
	float[] min = new float[Particle.nrResources];
	float[] max = new float[Particle.nrResources];
	

	//TODO tipul de algoritm utilizat
	//TODO tipul functiei de fitness

	@Override
	protected void setup() {

		
		float[] max_compromise = new float[Particle.nrResources];
		float[] weight = new float[Particle.nrResources];

		// loading configuration properties
		
		Properties properties = new Properties();
		
		
		try {
			properties.load(new FileInputStream("configuration files/pso_client.properties"));
		} catch (FileNotFoundException e) {
			
			System.err.println("Configuration file not found!!");
			e.printStackTrace();
			
		} catch (IOException e) {

			System.err.println("IO exception!!!");
			e.printStackTrace();
		}
		
		//determinam timpul pentru negociere
		Integer time = new Integer(properties.getProperty("negotiation.time"));
		//determinam pragul peste care e acceptata o oferta
		
		Float threshold_upper = new Float(properties.getProperty("negotiation.accepted_upper"));
		Float threshold_lower = new Float(properties.getProperty("negotiation.accepted_lower"));
		
		//determinam dimensiunea swarmului
		Integer swarmSize = new Integer(properties.getProperty("algorithm.pso.swarm_size"));
		
		String max_values = properties.getProperty("negotiation.resource_max");
		String min_values = properties.getProperty("negotiation.resource_min");
		String max_compromise_values = properties.getProperty("algorithm.pso.max_compromise");
		String weight_values = properties.getProperty("negotiation.resource_weight");
		
		
		String[] temp;
		String delimiter = " ";
		
		//determinam maximul pentru fiecare resursa
		temp = max_values.split(delimiter);
		 for(int i =0; i < temp.length ; i++)
		    max[i] = new Float(temp[i]);
		 
		//determinam minimul pentru fiecare resursa
		 temp = min_values.split(delimiter);
		 for(int i =0; i < temp.length ; i++)
			    min[i] = new Float(temp[i]);
		 
		 //determinam valoarea maxima a compromisului pentru fiecare resursa
		 temp = max_compromise_values.split(delimiter);
		 for(int i =0; i < temp.length ; i++)
			    max_compromise[i] = new Float(temp[i]);
		 
		 temp = weight_values.split(delimiter);
		 for(int i = 0; i< temp.length; i++)
			 weight[i] = new Float(temp[i]);

		System.out.println("The client agent - READY! ");
		
		client = new Client(time, min, max, weight);
		
		client.setMaxCompromise(max_compromise);
		client.setSwarmSize(swarmSize);
		client.setThreshold(threshold_lower, threshold_upper);
		client.initializePopulation();
		
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

			Offer offer = new Offer();
			
			offer.setHdd(max[0]);
			offer.setCpu(max[1]);
			offer.setMemory(max[2]);
			offer.setCost(min[3]);
			
			msg.setContentObject(offer);
			System.out.println("Offer from Client:" + offer.toString());
			client.updateSwarm();
			
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
		printNegotiationResults("client");
		msg.setContent("I accept your offer");
		msg.addReceiver(new AID(GlobalVars.SERVICE_CENTER_NAME + "@"
				+ this.getContainerController().getPlatformName()));
		
		this.send(msg);
	}

	@SuppressWarnings("deprecation")
	public void sendMessage(Offer offer) {
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM_REF);
		try {
			msg.setContentObject(offer);
			System.out.println("Offer number: " + client.getOfferNumber());
			System.out.println("Offer from Client:" + offer.toString());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		msg.addReceiver(new AID(GlobalVars.SERVICE_CENTER_NAME + "@"
				+ this.getContainerController().getPlatformName()));
		this.send(msg);
	}
	
	public void printNegotiationResults(String filename){
		client.printNegotiationResults(filename);
	}

}
