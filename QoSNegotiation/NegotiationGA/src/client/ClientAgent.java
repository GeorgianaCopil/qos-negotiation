package client;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Properties;

import genetic_algorithm.Chromosome;
import genetic_algorithm.GeneticAlgorithm;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import negotiation.Fitness;
import negotiation.Negotiation;
import negotiation.Offer;
import negotiation_broker.GlobalVars;

public class ClientAgent extends Agent implements Negotiation {

	private static final long serialVersionUID = 7603570134519005645L;
	private GeneticAlgorithm geneticAlgorithm;

	private int offersNo;

	// timpul negocierii
	private long totalTime;
	private long availableTime;

	// acceptare oferta
	private float thresholdUpper;
	private float thresholdLower;
	private float scaleThreshold;

	// utilitatea
	private Fitness fitness;

	// param alg. genetic
	private int populationSize;
	
	private float[] minValues;
	private float[] maxValues;
	private float[] resourceWeight;
	
	private float[] goal;
	
	// stocarea ofertelor
	private ArrayList<Offer> counterOffers;
	private ArrayList<Offer> oppositeAgentOffers;


	@Override
	public boolean acceptOffer(Offer offer) {

		float threshold;

		scaleThreshold += (totalTime - availableTime) * 10;

		threshold = thresholdUpper - (thresholdUpper - thresholdLower)
				* (scaleThreshold / (scaleThreshold + 5));

		fitness.rateOffer(offer);

		if (offer.getFitness() > threshold) {
			return true;

		}

		return false;
	}

	@Override
	public Offer computeCounterOffer(Offer offer) {

		offersNo++;

		availableTime = totalTime - System.currentTimeMillis();

		if (availableTime > 0) {

			Chromosome selectedChromosome = geneticAlgorithm.alterPopulation(
					offer.toChromosome(), numberOfAffectedIndividuals());

			Offer counterOffer = new Offer();
			counterOffer.setResources(selectedChromosome.getGenes());

			fitness.rateOffer(counterOffer);

			return counterOffer;
		}

		return null;
	}

	@Override
	public void waitForOffer() {

		for (int i = 0; i < computeNumberOfGenerations(); i++)
			geneticAlgorithm.evolve();

	}
	
	//#############################################################################################################
	//JADE AGENT
	//#############################################################################################################
	
	


	
	@Override
	protected void setup(){
		
		
		// loading configuration properties
		
		Properties properties = new Properties();
		
		
		try {
			properties.load(new FileInputStream("configuration files/ga_client.properties"));
		} catch (FileNotFoundException e) {
			
			System.err.println("Configuration file not found!!");
			e.printStackTrace();
			
		} catch (IOException e) {

			System.err.println("IO exception!!!");
			e.printStackTrace();
		}
		
		// timpul pentru negociere
		Integer time = new Integer(properties.getProperty("negotiation.time"));
		
		// nr de resurse
		Integer resourceNo = new Integer(properties.getProperty("negotiation.no_resources"));
		
		totalTime = System.currentTimeMillis() + time;
		availableTime = totalTime - System.currentTimeMillis();
		
		// pragul peste care e acceptata o oferta
		thresholdUpper = new Float(properties.getProperty("negotiation.accepted_upper"));
		thresholdLower = new Float(properties.getProperty("negotiation.accepted_lower"));
		
		// dimensiunea populatiei
		populationSize = new Integer(properties.getProperty("algorithm.ga.population_size"));
		
		String max_values = properties.getProperty("negotiation.resource_max");
		String min_values = properties.getProperty("negotiation.resource_min");
		String weight_values = properties.getProperty("negotiation.resource_weight");
		String goal_values = properties.getProperty("negotiation.goal");
		
		String[] temp;
		String delimiter = " ";
		
		maxValues = new float[resourceNo];
		minValues = new float[resourceNo];
		goal = new float[resourceNo];
		resourceWeight = new float[resourceNo];
		
		// maximul pentru fiecare resursa
		temp = max_values.split(delimiter);
		 for(int i = 0; i < temp.length ; i++)
		    maxValues[i] = new Float(temp[i]);
		 
		// minimul pentru fiecare resursa
		 temp = min_values.split(delimiter);
		 for(int i =0; i < temp.length ; i++)
			 minValues[i] = new Float(temp[i]);
		 
		 // ponderea pentru fiecare resursa
		 temp = weight_values.split(delimiter);
		 for(int i = 0; i< temp.length; i++)
			 resourceWeight[i] = new Float(temp[i]);

		 // goal-ul agentului
		 temp = goal_values.split(delimiter);
		 for(int i = 0; i < temp.length; i++)
			 goal[i] = new Float(temp[i]);
		
		initialize();
		
		oppositeAgentOffers = new ArrayList<Offer>();
		counterOffers = new ArrayList<Offer>();
		
		System.out.println("Client agent - READY! ");
		
		addBehaviour(new ReceiveMessageBehaviour(this));
		startNegotiation();
		
	}
	
	@SuppressWarnings("deprecation")
	public void startNegotiation(){
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM_REF);
		try {

			Offer offer = new Offer();
			
			offer.setResources(goal);
			
			msg.setContentObject(offer);
			System.out.println("Offer number: " + offersNo);
			System.out.println("Offer from Client:" + offer.toString());
			waitForOffer();
			
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
		
		this.send(msg);
	}

	@SuppressWarnings("deprecation")
	public void sendMessage(Offer offer) {
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM_REF);
		try {
			msg.setContentObject(offer);
			System.out.println("Offer number: " + offersNo);
			System.out.println("Offer from Client:" + offer.toString());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		msg.addReceiver(new AID(GlobalVars.SERVICE_CENTER_NAME + "@"
				+ this.getContainerController().getPlatformName()));
		this.send(msg);
	}

	// #############################################################################################################
	// PRIVATE METHODS
	// #############################################################################################################

	private int computeNumberOfGenerations() {

		return (int)Math.floor(1 - availableTime/totalTime)*10+1;
	}

	private int numberOfAffectedIndividuals() {
		
		if (availableTime / totalTime < 0.25)
			return (int) (populationSize * 0.25);
		if (availableTime / totalTime < 0.5)
			return (int) (populationSize * 0.20);
		if (availableTime / totalTime < 0.75)
			return (int) (populationSize * 0.15);

		return (int) (populationSize * 0.05);
	}
	

	private void initialize(){
		
		geneticAlgorithm = new GeneticAlgorithm(minValues, maxValues, resourceWeight, populationSize);
		geneticAlgorithm.setGoal(new Chromosome(goal));
		
		offersNo = 1;
		
		fitness = new Fitness();
		
		fitness.setGoal(goal);
		fitness.setResourceWeight(resourceWeight);
		fitness.setMaxValues(maxValues);
		fitness.setMinValues(minValues);
		
		
	}
	
	
	// ##############################################################################################################
	// PRINT RESULTS
	// ##############################################################################################################
	
	public void printNegotiationResults(String fileName) {

		PrintWriter result_file = null;
		PrintWriter fitness_file = null;

		try {
			result_file = new PrintWriter(fileName+".txt");
			fitness_file = new PrintWriter(fileName+"_fitness.txt");
		} catch (FileNotFoundException e) {
			System.err.println("Error creating file!");
			e.printStackTrace();
		}

		int iteration = 1;
		
		fitness_file.println("client - server");
		
		while (iteration < counterOffers.size()
				&& iteration < oppositeAgentOffers.size()) {

			result_file.println("Iteration " + iteration);
			fitness_file.append(new Integer(iteration).toString()+" ");
			if (iteration > 0) {

				result_file.println("Client "+ counterOffers.get(iteration).toString());
				fitness_file.append(new Float(oppositeAgentOffers.get(iteration).getFitness()).toString());
				fitness_file.append(" ");
				
				result_file.println("Server "
						+ counterOffers.get(iteration).toString());
				fitness_file.append(new Float(oppositeAgentOffers.get(iteration).getFitness()).toString());
				fitness_file.append(" ");
				
			
			}

		
			iteration++;

		}

		result_file.close();
		fitness_file.close();

	}

	

	// ##############################################################################################################
	// GETTERS AND SETTERS
	// ##############################################################################################################

	public float getThreshold_upper() {
		return thresholdUpper;
	}

	public void setThreshold_upper(float threshold_upper) {
		this.thresholdUpper = threshold_upper;
	}

	public float getThreshold_lower() {
		return thresholdLower;
	}

	public void setThreshold_lower(float threshold_lower) {
		this.thresholdLower = threshold_lower;
	}

	public int getPopulationSize() {
		return populationSize;
	}

	public void setPopulationSize(int populationSize) {
		this.populationSize = populationSize;
	}

	public float[] getMinValues() {
		return minValues;
	}

	public void setMinValues(float[] minValues) {
		this.minValues = minValues;
	}

	public float[] getMaxValues() {
		return maxValues;
	}

	public void setMaxValues(float[] maxValues) {
		this.maxValues = maxValues;
	}

	public float[] getGoal() {
		return goal;
	}

	public void setGoal(float[] goal) {
		this.goal = goal;
	}
	
}
