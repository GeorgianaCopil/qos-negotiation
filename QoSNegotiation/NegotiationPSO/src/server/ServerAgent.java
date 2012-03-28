package server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
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

public class ServerAgent extends Agent implements Negotiation {

	private static final long serialVersionUID = -5658278153108475219L;

	// pso
	private PSO psoAlgorithm;
	private int swarmSize;
	
	// fitness
	private Fitness fitness;

	// caractersistici resurse
	// valori maxime, minime, ponderea fiecarei resurse, compromisul maxim
	// pentru fiecare resursa
	private float[] minValues;
	private float[] maxValues;
	private float[] weight;
	private float[] maxCompromise;

	// goal-ul agentului
	private float[] goal;

	// timpul pentru negociere
	private long availableTime;
	private long totalTime;
	private int offerNumber;

	// pragul pentru care este acceptata o oferta
	private float scale_threshold = 0;
	private float threshold_lower, threshold_upper;

	// calcul compromis
	private static final int NUMBER_OF_VALUES_FOR_SCALING = 5;
	private float scale = 0;

	// salvarea ofertelor schimbate
	private HashMap<Integer, Float> oppositeAgentOfferUtilities;
	private HashMap<Integer, Offer> oppositeAgentOffers;
	private HashMap<Integer, Offer> counterOffers;

	@Override
	public boolean acceptOffer(Offer offer) {

		fitness.rateOffer(offer);
		float threshold;

		scale_threshold += (totalTime - availableTime) * 10;
		threshold = threshold_upper - (threshold_upper - threshold_lower)
				* (scale_threshold / (scale_threshold + 10));

		if (offer.getFitness() >  threshold) {
			return true;
		}
		return false;
	}

	@Override
	public Offer computeCounterOffer(Offer offer) {
		
		float[] counterDistance = new float[Particle.nrResources];

		// se calculeaza timpul ramas pentru negociere
		availableTime = totalTime - System.currentTimeMillis();

		fitness.rateOffer(offer);
		oppositeAgentOfferUtilities.put(new Integer(offerNumber),
				offer.getFitness());
		oppositeAgentOffers.put(new Integer(offerNumber), offer);

		offerNumber++;

		counterDistance = offer.getResources();
		
		Map<Integer, Particle> alteredParticles = psoAlgorithm.alterDistance(
				psoAlgorithm.selectParticles(numberOfAlteredParticles()),
				computeCompromise(), counterDistance);
		
	
		
		Particle selectedParticle = psoAlgorithm
				.averageParticle(alteredParticles);

		Offer serverOffer = new Offer();

		serverOffer.setResources(selectedParticle.getDistance());

		fitness.rateOffer(serverOffer);

		counterOffers.put(new Integer(offerNumber), serverOffer);
	
		if (availableTime < 0)
			return null;
		
		return serverOffer;
	}

	@Override
	public void waitForOffer() {

		int c1 = 2, c2 = 2;
		float W = 0.6f;

		psoAlgorithm.alterParticles(psoAlgorithm.getPopulation(), c1, c2, W);

	}

	// ------------------------------------------------------------------------------------------------------------
	// AGENT JADE
	// ------------------------------------------------------------------------------------------------------------

	@Override
	protected void setup() {

		Properties properties = new Properties();

		try {
			properties.load(new FileInputStream(
					"configuration files/pso_server.properties"));
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

		// timpul disponibil pentru negociere
		Long time = new Long(properties.getProperty("negotiation.time"));

		// valorile maxime pentru fiecare resursa
		String max_values = properties.getProperty("negotiation.resource_max");

		// valorile minime pentru fiecare resursa
		String min_values = properties.getProperty("negotiation.resource_min");
		
		// valorile pentru goal
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

		// dimensiunea swarmului
		swarmSize = new Integer(
				properties.getProperty("algorithm.pso.swarm_size"));

		// compromisul maxim pentru fiecare resursa
		String max_compromise_values = properties
				.getProperty("algorithm.pso.max_compromise");

		// ponderile pentru fiecare resursa
		String weight_values = properties
				.getProperty("negotiation.resource_weight");

		// determinam valoarea maxima a compromisului pentru fiecare resursa
		temp = max_compromise_values.split(delimiter);
		for (int i = 0; i < temp.length; i++)
			maxCompromise[i] = new Float(temp[i]);
		
		// determinam valorile pentru goal
		temp = goal_values.split(delimiter);
		for (int i = 0; i < temp.length; i++)
			goal[i] = new Float(temp[i]);

		// pragul pentru care e acceptata o oferta pentru fiecare server
		// implicat in negociere
		String threshold_lower_value = properties
				.getProperty("negotiation.lower_accepted");
		String threshold_upper_value = properties
				.getProperty("negotiation.upper_accepted");
		// determinam valorile pargului peste care e acceptata o oferta de
		// server
		threshold_lower = new Float(threshold_lower_value);
		threshold_upper = new Float(threshold_upper_value);

		// determinam ponderile pentru fiecare resursa
		temp = weight_values.split(delimiter);
		for (int i = 0; i < temp.length; i++)
			weight[i] = new Float(temp[i]);

		totalTime = System.currentTimeMillis() + time;
		availableTime = totalTime - System.currentTimeMillis();

		fitness = new Fitness();
		fitness.setMaxValues(maxValues);
		fitness.setMinValues(minValues);
		fitness.setResourceWeight(weight);
		fitness.setGoal(goal);
		
		psoAlgorithm = new PSO(minValues, maxValues);
		psoAlgorithm.initializeGBest(goal);
		psoAlgorithm.initializeSwarm(swarmSize, minValues, maxValues, goal);
		psoAlgorithm.setResourceWeights(weight);
		
		oppositeAgentOffers = new HashMap<Integer, Offer>();
		oppositeAgentOfferUtilities = new HashMap<Integer, Float>();
		counterOffers = new HashMap<Integer, Offer>();
		
		addBehaviour(new ReceiveMessageBehaviour(this));
		
		System.out.println("Server agent - READY! ");

		//addBehaviour(new ReceiveMessageBehaviour(this));
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

	@SuppressWarnings("deprecation")
	public void sendMessage(Offer offer) {

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

	// ------------------------------------------------------------------------------------------------------------
	// AJUSTAREA PARAMETRILOR
	// ------------------------------------------------------------------------------------------------------------

	/**
	 * calculeaza numarul de particule afectate de oferta contra-agentului
	 * 
	 * @return numarul de particule afectate de oferta contra-agentului
	 */
	private int numberOfAlteredParticles() {

		if (availableTime / totalTime < 0.25)
			return (int) (swarmSize * 0.25);
		if (availableTime / totalTime < 0.5)
			return (int) (swarmSize * 0.20);
		if (availableTime / totalTime < 0.75)
			return (int) (swarmSize * 0.15);

		return (int) (swarmSize * 0.05);
	}

	/**
	 * calculeaza impactul avut de oferta clientului asupra particulelor
	 * selectate din swarm
	 * 
	 * @param timeLeft
	 *            timpul ramas pntru negociere
	 * @return factorul de compromis
	 */
	private float[] computeCompromise() {

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
			comparisonUtility = oppositeAgentOfferUtilities.get(offerNumber
					- NUMBER_OF_VALUES_FOR_SCALING + 1);

			for (int i = NUMBER_OF_VALUES_FOR_SCALING + 2; i >= 1; i--)
				if (oppositeAgentOfferUtilities.get(offerNumber - i) > comparisonUtility)
					compromisedOffers++;

			if (compromisedOffers / NUMBER_OF_VALUES_FOR_SCALING > 0.75)
				scale = scale + offerNumber * 10;
			else if (compromisedOffers / NUMBER_OF_VALUES_FOR_SCALING > 0.5)
				scale = scale + offerNumber * 5;
			else if (compromisedOffers / NUMBER_OF_VALUES_FOR_SCALING > 0.25)
				scale = (float) (scale + offerNumber * 2.5);
			else
				scale = scale + 10;

		}

		scale = scale + 10;

		// random point in functie de oferta celulialt, sa creasca odata cu
		// crestera compromisului celuilalt

		for (int i = 0; i < Particle.nrResources; i++) {

			randomPoint[i] = scale;
			compromise[i] = maxCompromise[i] * randomPoint[i]
					/ (randomPoint[i] + 50);

		}

		return compromise;
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

		int iteration = 1;

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
