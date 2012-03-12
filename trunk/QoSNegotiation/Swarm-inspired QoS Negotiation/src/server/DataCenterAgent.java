package server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import PSO.Particle;

import negotiation.GlobalVars;
import negotiation.Offer;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;

public class DataCenterAgent extends Agent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3575535954133085186L;
	private DataCenter dataCenter;


	float[] min = new float[Particle.nrResources];
	float[] max = new float[Particle.nrResources];

	@Override
	protected void setup() {

		// factorul de compromis pentru fiecare resursa
		Float[] max_compromise = new Float[Particle.nrResources];
		// pragul peste care o oferta e acceptata pentru server
		Float threshold_upper, threshold_lower;

		// ponderile pentru fiecare resursa
		Float[] weight;

		Properties properties = new Properties();

		try {
			properties.load(new FileInputStream(
					"configuration files/pso_data_center.properties"));
		} catch (FileNotFoundException e) {

			System.err.println("Configuration file not found!!");
			e.printStackTrace();

		} catch (IOException e) {

			System.err.println("IO exception!!!");
			e.printStackTrace();
		}

		// -----------------------------------------------------------------------------------------
		// configurare pentru data center

		// timpul disponibil pentru negociere
		Long time = new Long(properties.getProperty("negotiation.time"));

		// numarul de servere implicate in negociere din partea Data Center-ului
		Integer numberOfServers = new Integer(
				properties.getProperty("dc.number_of_servers"));

		// valorile maxime pentru fiecare resursa
		String max_values = properties.getProperty("negotiation.resource_max");

		// valorile minime pentru fiecare resursa
		String min_values = properties.getProperty("negotiation.resource_min");
		

		

		String[] temp;
		String delimiter = " ";

		// determinam maximul pentru fiecare resursa
		temp = max_values.split(delimiter);
		for (int i = 0; i < temp.length; i++)
			max[i] = new Float(temp[i]);

		// determinam minimul pentru fiecare resursa
		temp = min_values.split(delimiter);
		for (int i = 0; i < temp.length; i++)
			min[i] = new Float(temp[i]);

		// determinam fisierele de configurare pentru fiecare server
		String configuration_files = properties
				.getProperty("dc.server_config");
		String[] config = new String[temp.length];
		temp = configuration_files.split(delimiter);
		for (int i = 0; i < temp.length; i++){
			config[i] = temp[i];
		}

		// ----------------------------------------------------------------------------------------
		// configurare pentru servers

		HashMap<Integer, Integer> swarmSizeAll = new HashMap<Integer, Integer>();
		HashMap<Integer, Float[]> resourceWeightAll = new HashMap<Integer, Float[]>();
		HashMap<Integer, Float> thresholdLowerAll = new HashMap<Integer, Float>();
		HashMap<Integer, Float> thresholdUpperAll = new HashMap<Integer, Float>();
		HashMap<Integer, Float[]> maxCompromiseAll = new HashMap<Integer, Float[]>();
		
		int counter = 0;
		while (counter < numberOfServers) {
			
			try {
				System.out.println(config[counter]);
				properties.load(new FileInputStream("configuration files/"+config[counter]));
				//properties.load(new FileInputStream("configuration files/pso_server.properties"));
			} catch (FileNotFoundException e) {
				System.err.println("Configuration file not found!!");
				e.printStackTrace();
			} catch (IOException e) {
				System.err.println("IO exception!!");
				e.printStackTrace();
			}

			// dimensiunea swarmului
			Integer swarmSize = new Integer(
					properties.getProperty("algorithm.pso.swarm_size"));
			swarmSizeAll.put(counter, swarmSize);

			// compromisul maxim pentru fiecare resursa
			String max_compromise_values = properties
					.getProperty("algorithm.pso.max_compromise");
			

			// ponderile pentru fiecare resursa
			String weight_values = properties
					.getProperty("negotiation.resource_weight");

			// determinam valoarea maxima a compromisului pentru fiecare resursa
			temp = max_compromise_values.split(delimiter);
			for (int i = 0; i < temp.length; i++)
				max_compromise[i] = new Float(temp[i]);
			maxCompromiseAll.put(counter, max_compromise);
			
			// pragul pentru care e acceptata o oferta pentru fiecare server
			// implicat in negociere
			String threshold_lower_value = properties
					.getProperty("negotiation.lower_accepted");
			String threshold_upper_value = properties.getProperty("negotiation.upper_accepted");
			// determinam valorile pargului peste care e acceptata o oferta de server
			threshold_lower = new Float(threshold_lower_value);
			threshold_upper = new Float(threshold_upper_value);
			thresholdLowerAll.put(counter, threshold_lower);
			thresholdUpperAll.put(counter, threshold_upper);

			// determinam ponderile pentru fiecare resursa
			temp = weight_values.split(delimiter);
			weight = new Float[Particle.nrResources];
			for (int i = 0; i < temp.length; i++)
				weight[i] = new Float(temp[i]);
			resourceWeightAll.put(counter, weight);
			
			counter++;
			
		}
		
		System.out.println("Data center agent - READY! ");
		dataCenter = new DataCenter(time, numberOfServers, swarmSizeAll, max, min,
				thresholdLowerAll,thresholdUpperAll, resourceWeightAll, maxCompromiseAll);

		addBehaviour(new ReceiveMessageDCBehaviour(this));
	}

	@SuppressWarnings("deprecation")
	public void sendAcceptOfferMessage() {
		ACLMessage msg = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
		msg.setContent("I accept your offer");
		msg.addReceiver(new AID(GlobalVars.CLIENT_NAME + "@"
				+ this.getContainerController().getPlatformName()));
		
		this.send(msg);
	}

	@SuppressWarnings("deprecation")
	public void sendRefuseOfferMessage() {
		ACLMessage msg = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
		msg.setContent("I despise your offer. Negotiation over! ");
		msg.addReceiver(new AID(GlobalVars.CLIENT_NAME + "@"
				+ this.getContainerController().getPlatformName()));
		this.send(msg);
	}

	public void waitForOffer() {
		dataCenter.waitForOffer();
	}

	public ArrayList<Offer> computeCounterOffer(Offer clientOffer) {
		return (ArrayList<Offer>) dataCenter.sendCounterOffers(clientOffer);
	}

	@SuppressWarnings("deprecation")
	public void sendMessage(ArrayList<Offer> offer) {

		ACLMessage msg = new ACLMessage(ACLMessage.INFORM_REF);
		try {
			msg.setContentObject(offer);
			System.out.println("Offer from Server:" + offer.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		msg.addReceiver(new AID(GlobalVars.CLIENT_NAME + "@"
				+ this.getContainerController().getPlatformName()));
		this.send(msg);
	}

	public boolean checkAcceptOffer(Offer offer) {
		return dataCenter.acceptOffer(offer);
	}
	
	public void printNegotiationResults(String filename){
		dataCenter.printNegotiationResults(filename);
	}

}