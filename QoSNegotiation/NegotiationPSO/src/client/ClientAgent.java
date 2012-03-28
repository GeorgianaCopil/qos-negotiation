package client;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Properties;
import pso.PSO;
import pso.Particle;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import negotiation.Fitness;
import negotiation.Negotiation;
import negotiation.Offer;
import negotiation_broker.GlobalVars;

public class ClientAgent extends Agent implements Negotiation {

	private PSO psoAlgorithm;

	private float[] maxValues;
	private float[] minValues;
	private float[] goal;
	private float[] weight;

	private long totalTime;
	private long availableTime;
	private float scale = 0;
	private int offerNumber;

	private Fitness fitness;

	// gestionarea pragului peste care se accepta o oferta
	private float threshold_lower, threshold_upper;
	private float scale_threshold = 0;

	// salvarea ofertelor schimbate
	private HashMap<Integer, Offer> oppositeAgentOffers;
	private HashMap<Integer, Offer> counterOffers;
	private HashMap<Integer, Float> oppositeAgentUtilities;

	// algoritmul pso
	private int swarmSize;

	// compormisul pentru fiecare oferta
	private static final int NUMBER_OF_VALUES_FOR_SCALING = 4;
	private float[] maxCompromise;

	private static final long serialVersionUID = -8724467133204924653L;

	@Override
	public boolean acceptOffer(Offer offer) {

		fitness.rateOffer(offer);
		float threshold;

		scale_threshold += (totalTime - availableTime) * 10;
		threshold = threshold_upper - (threshold_upper - threshold_lower)
				* (scale_threshold / (scale_threshold + 10));

		if (offer.getFitness() > threshold) {
		
			return true;
		}
		return false;
	}

	@Override
	public Offer computeCounterOffer(Offer offer) {

		Offer clientOffer = new Offer();

		// se calculeaza timpul ramas pentru negociere
		availableTime = totalTime - System.currentTimeMillis();

		if (availableTime < 0)
			return null;

		fitness.rateOffer(offer);
		oppositeAgentOffers.put(offerNumber, offer);
		oppositeAgentUtilities.put(offerNumber, offer.getFitness());

		// s-a mai schimbat o oferta intre client si Data Center
		offerNumber++;

		int size = computeNoAffectedParticles();

		HashMap<Integer, Particle> selectedParticles;

		float[] counterDistance = new float[Particle.nrResources];
		float[] resources = offer.getResources();

		selectedParticles = (HashMap<Integer, Particle>) psoAlgorithm
				.selectParticles(size);

		for (int i = 0; i < counterDistance.length; i++)
			counterDistance[i] = resources[i];

		float[] compromise = computeCompromise();

		psoAlgorithm.alterDistance(selectedParticles, compromise,
				counterDistance).values();

		Particle selectedParticle = psoAlgorithm
				.averageParticle(selectedParticles);

		clientOffer.setResources(selectedParticle.getDistance());

		fitness.rateOffer(clientOffer);
		counterOffers.put(offerNumber, clientOffer);

		return clientOffer;
	}

	@Override
	public void waitForOffer() {

		int c1 = 2, c2 = 2;
		float W = 0.75f;

		psoAlgorithm.alterParticles(psoAlgorithm.getPopulation(), c1, c2, W);
	}

	// SETAREA PARAMETRILOR

	/**
	 * calculeaza numarul de particule care sunt afectate de oferta agentului
	 * advers aceasta valoare depinde de timpul ramas pentru negociere
	 */
	public int computeNoAffectedParticles() {

		if (availableTime / totalTime < 0.25)
			return (int) (swarmSize * 0.25);
		if (availableTime / totalTime < 0.5)
			return (int) (swarmSize * 0.20);
		if (availableTime / totalTime < 0.75)
			return (int) (swarmSize * 0.15);

		return (int) (swarmSize * 0.05);
	}

	public float[] computeCompromise() {

		// factorul de compromis va fi repr de punctele x de pe graficul
		// functiei a*x/(x+1), unde a este valoarea maxima pentru
		// factorul de compromis

		float[] randomPoint = new float[Particle.nrResources];
		float[] compromise = new float[Particle.nrResources];

		// numaram cate din ofertele agentului din intervalul
		// [offerNumber-NUMBER_OF_VALUES_FOR_SCALING+1, offerNumber]
		// sunt mai mari decat NUMBER_OF_VALUES_FOR_SCALING

		// comparisonUtility este utilitatea ofertei cu numarul
		// offerNumber-NUMBER_OF_VALUES_FOR_SCALING+1

		float comparisonUtility;

		// numarul de oferte pentru care agentul advers a redus compromisul
		// care sunt cuprinse in intervalul
		// [offerNumber-NUMBER_OF_VALUES_FOR_SCALING+1, offerNumber]
		int compromisedOffers = 0;

		if (offerNumber > NUMBER_OF_VALUES_FOR_SCALING
				&& offerNumber % NUMBER_OF_VALUES_FOR_SCALING == 0) {
			
			comparisonUtility = oppositeAgentUtilities.get(offerNumber
					- NUMBER_OF_VALUES_FOR_SCALING + 1);

			for (int i = NUMBER_OF_VALUES_FOR_SCALING + 2; i >= 1; i--)
				if (oppositeAgentUtilities.get(offerNumber - i) > comparisonUtility)
					compromisedOffers++;

			if (compromisedOffers / NUMBER_OF_VALUES_FOR_SCALING > 0.75)
				scale = scale + offerNumber * 10;
			else if (compromisedOffers / NUMBER_OF_VALUES_FOR_SCALING > 0.5)
				scale = scale + offerNumber * 5;
			else if (compromisedOffers / NUMBER_OF_VALUES_FOR_SCALING > 0.25)
				scale = (float) (scale + offerNumber * 2.5);
			else
				scale = scale + 5;

		}

		scale = scale + 10;

		// random pointf in functie de oferta celulialt, sa creasca odata cu
		// crestera compromisului celuilalt

		for (int i = 0; i < Particle.nrResources; i++) {

			randomPoint[i] = scale;
			compromise[i] = maxCompromise[i] * randomPoint[i]
					/ (randomPoint[i] + 50);

		}
		return compromise;
	}

	// AGENT JADE

	@Override
	protected void setup() {


		// loading configuration properties

		Properties properties = new Properties();

		try {
			properties.load(new FileInputStream(
					"configuration files/pso_client.properties"));
		} catch (FileNotFoundException e) {

			System.err.println("Configuration file not found!!");
			e.printStackTrace();

		} catch (IOException e) {

			System.err.println("IO exception!!!");
			e.printStackTrace();
		}
		
		Integer nrResources = new Integer(
				properties.getProperty("negotiation.no_resources"));

		minValues = new float[nrResources];
		maxValues = new float[nrResources];
		maxCompromise = new float[nrResources];
		goal = new float[nrResources];
		weight = new float[nrResources];

		// determinam timpul pentru negociere
		Integer time = new Integer(properties.getProperty("negotiation.time"));
		// determinam pragul peste care e acceptata o oferta

		threshold_upper = new Float(
				properties.getProperty("negotiation.accepted_upper"));
		threshold_lower = new Float(
				properties.getProperty("negotiation.accepted_lower"));

		// determinam dimensiunea swarmului
		swarmSize = new Integer(
				properties.getProperty("algorithm.pso.swarm_size"));

		String max_values = properties.getProperty("negotiation.resource_max");
		String min_values = properties.getProperty("negotiation.resource_min");
		String max_compromise_values = properties
				.getProperty("algorithm.pso.max_compromise");
		String weight_values = properties
				.getProperty("negotiation.resource_weight");
		String goal_values = properties.getProperty("negotiation.goal");

		String[] temp;
		String delimiter = " ";

		// determinam maximul pentru fiecare resursa
		temp = max_values.split(delimiter);
		for (int i = 0; i < temp.length; i++)
			maxValues[i] = new Float(temp[i]);

		// determinam minimul pentru fiecare resursa
		temp = min_values.split(delimiter);
		for (int i = 0; i < temp.length; i++)
			minValues[i] = new Float(temp[i]);

		// determinam valoarea maxima a compromisului pentru fiecare resursa
		temp = max_compromise_values.split(delimiter);
		for (int i = 0; i < temp.length; i++)
			maxCompromise[i] = new Float(temp[i]);

		temp = weight_values.split(delimiter);
		for (int i = 0; i < temp.length; i++)
			weight[i] = new Float(temp[i]);

		temp = goal_values.split(delimiter);
		for (int i = 0; i < temp.length; i++)
			goal[i] = new Float(temp[i]);

		totalTime = System.currentTimeMillis() + time;
		availableTime = totalTime - System.currentTimeMillis();
		
		oppositeAgentOffers = new HashMap<Integer, Offer>();
		oppositeAgentUtilities = new HashMap<Integer, Float>();
		counterOffers = new HashMap<Integer, Offer>();

		fitness = new Fitness();
		fitness.setMaxValues(maxValues);
		fitness.setMinValues(minValues);
		fitness.setResourceWeight(weight);
		fitness.setGoal(goal);

		psoAlgorithm = new PSO(minValues, maxValues);
		psoAlgorithm.initializeGBest(goal);
		psoAlgorithm.initializeSwarm(swarmSize, minValues, maxValues, goal);
		psoAlgorithm.setResourceWeights(weight);

		System.out.println("The client agent - READY! ");
		
		startNegotiation();
		addBehaviour(new ReceiveMessageBehaviour(this));
		
		

	}

	@SuppressWarnings("deprecation")
	public void startNegotiation() {
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM_REF);
		try {

			Offer offer = new Offer();

			offer.setResources(goal);
		
			counterOffers.put(offerNumber, offer);

			msg.setContentObject(offer);
			System.out.println("Offer number: " + offerNumber);
			System.out.println("Offer from Client:" + offer.toString());
			waitForOffer();

		} catch (IOException e) {
			e.printStackTrace();
		}
		msg.addReceiver(new AID(GlobalVars.SERVER_NAME + "@"
				+ this.getContainerController().getPlatformName()));
		this.send(msg);
	}

	@SuppressWarnings("deprecation")
	public void sendRefuseOfferMessage() {
		ACLMessage msg = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
		msg.setContent("I despise your offer. Negotiation over! ");
		msg.addReceiver(new AID(GlobalVars.SERVER_NAME + "@"
				+ this.getContainerController().getPlatformName()));
		this.send(msg);
	}

	@SuppressWarnings("deprecation")
	public void sendAcceptOfferMessage() {
		ACLMessage msg = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
		printNegotiationResults("client");
		msg.setContent("I accept your offer");
		msg.addReceiver(new AID(GlobalVars.SERVER_NAME + "@"
				+ this.getContainerController().getPlatformName()));

		this.send(msg);
	}

	@SuppressWarnings("deprecation")
	public void sendMessage(Offer offer) {
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM_REF);
		try {
			msg.setContentObject(offer);
			System.out.println("Offer number: " + offerNumber);
			System.out.println("Offer from Client:" + offer.toString());

		} catch (IOException e) {
			e.printStackTrace();
		}
		msg.addReceiver(new AID(GlobalVars.SERVER_NAME + "@"
				+ this.getContainerController().getPlatformName()));
		this.send(msg);
	}

	// AFISAREA REZULTATELOR NEGOCIERII

	public void printNegotiationResults(String fileName) {

		PrintWriter result_file = null;
		PrintWriter fitness_file = null;

		try {
			result_file = new PrintWriter(fileName + ".txt");
			fitness_file = new PrintWriter(fileName + "_fitness.txt");
		} catch (FileNotFoundException e) {
			System.err.println("Error creating file!");
			e.printStackTrace();
		}

		int iteration = 0;

		fitness_file.println("client - server");

		while (iteration < counterOffers.size()
				&& iteration < oppositeAgentOffers.size()) {

			result_file.println("Iteration " + iteration);
			fitness_file.append(new Integer(iteration).toString() + " ");
			if (iteration > 0) {

				result_file.println("Client "
						+ counterOffers.get(iteration).toString());
				fitness_file.append(new Float(counterOffers.get(iteration)
						.getFitness()).toString());
				fitness_file.append(" ");

				result_file.println("Server "
						+ counterOffers.get(iteration).toString());
				fitness_file.append(new Float(oppositeAgentOffers
						.get(iteration).getFitness()).toString());
				fitness_file.println(" ");

			}

			iteration++;

		}

		result_file.close();
		fitness_file.close();

	}
}
