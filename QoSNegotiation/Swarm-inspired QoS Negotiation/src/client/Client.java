package client;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import PSO.PSO;
import PSO.Particle;
import negotiation.Offer;

public class Client {

	private PSO psoAlgorithm;
	private int swarmSize;
	private float[] maxValues;
	private float[] minValues;
	Fitness fitness;
	private long totalTime;
	private long availableTime;
	private int offerNumber;
	private float[] gBestValues;
	private float scale = 0.25f;
	private float threshold_lower, threshold_upper;
	private float scale_threshold = 0;
	float[] maxCompromise;

	// numarul de oferte schimbate dupa care se face ajustarea compromisului
	private static final int NUMBER_OF_VALUES_FOR_SCALING = 4;

	// ofertele agentului advers
	private HashMap<Integer, ArrayList<Offer>> oppositeAgentOffers;

	// utilitatile ofertelor trimise de agentul advers
	private HashMap<Integer, ArrayList<Float>> oppositeAgentOffersUtilities;

	// ofertele trimise
	private HashMap<Integer, Offer> counterOffers;

	public Client(long time, float[] minValues, float[] maxValues,
			float[] weight) {

		totalTime = System.currentTimeMillis() + time;
		availableTime = time;
		this.maxValues = maxValues;
		this.minValues = minValues;
		psoAlgorithm = new PSO();

		// initializare
		// valorile pentru gBest sunt: valoarea maxima posibila pentru resurse
		// si valoarea minima posibila pentru cost
		gBestValues = new float[Particle.nrResources];
		for (int i = 0; i < 3; i++)
			gBestValues[i] = maxValues[i];
		gBestValues[3] = minValues[3];

		offerNumber = 0;

		fitness = new Fitness();
		fitness.setMax(maxValues);
		fitness.setMin(minValues);
		fitness.setWeight(weight);

		oppositeAgentOffers = new HashMap<Integer, ArrayList<Offer>>();
		oppositeAgentOffersUtilities = new HashMap<Integer, ArrayList<Float>>();
		counterOffers = new HashMap<Integer, Offer>();

	}

	public void initializePopulation() {
		// se initializeaza popolatia
		psoAlgorithm.initializeSwarm(swarmSize, minValues, maxValues,
				gBestValues);
	}

	/**
	 * calculeaza impactul avut de oferta contra agentului, pentru fiecare
	 * resursa acest factor de compromis este determinat de numarul de oferte
	 * schimbat de agenti
	 */
	public float[] computeCompromise(int serverNumber) {

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
			comparisonUtility = oppositeAgentOffersUtilities.get(
					offerNumber - NUMBER_OF_VALUES_FOR_SCALING + 1).get(
					serverNumber);

			for (int i = NUMBER_OF_VALUES_FOR_SCALING + 2; i >= 1; i--)
				if (oppositeAgentOffersUtilities.get(offerNumber - i).get(
						serverNumber) > comparisonUtility)
					compromisedOffers++;

			if (compromisedOffers / NUMBER_OF_VALUES_FOR_SCALING > 0.75)
				scale = scale + offerNumber * 10;
			else if (compromisedOffers / NUMBER_OF_VALUES_FOR_SCALING > 0.5)
				scale = scale + offerNumber * 5;
			else if (compromisedOffers / NUMBER_OF_VALUES_FOR_SCALING > 0.25)
				scale = (float) (scale + offerNumber * 2.5);
			else
				scale = scale + 0;

		}

		scale = scale + offerNumber / 4;

		// random pointf in functie de oferta celulialt, sa creasca odata cu
		// crestera compromisului celuilalt

		for (int i = 0; i < Particle.nrResources; i++) {

			randomPoint[i] = scale;
			compromise[i] = maxCompromise[i] * randomPoint[i]
					/ (randomPoint[i] + 50);

		}
		return compromise;
	}

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

	/**
	 * calculeaza contra oferta pentru ofertele primite de la Data Center
	 * 
	 * @param DCOffers
	 *            ofertele primite de la Data Center
	 * @return contra ofera clientului
	 */

	public Offer computeOffer(ArrayList<Offer> DCOffers) {

		Offer clientOffer = new Offer();

		// se calculeaza timpul ramas pentru negociere
		availableTime = totalTime - System.currentTimeMillis();

		// se memoreaza oferta agentului advers
		Iterator<Offer> DCOfferIterator = DCOffers.iterator();

		// calculam utilitatea pentru ofertele agentului advers
		ArrayList<Float> utilities = new ArrayList<Float>();

		while (DCOfferIterator.hasNext()) {

			Offer currentOffer = DCOfferIterator.next();
			fitness.rateOffer(currentOffer);
			utilities.add(new Float(currentOffer.getFitness()));

		}

		oppositeAgentOffers.put(offerNumber, DCOffers);
		oppositeAgentOffersUtilities.put(offerNumber, utilities);

		// s-a mai schimbat o oferta intre client si Data Center
		offerNumber++;

		int size = computeNoAffectedParticles();

		HashMap<Integer, Particle> selectedParticles;// = (HashMap<Integer,
														// Particle>)
														// psoAlgorithm.selectParticles(size);
		HashMap<Integer, Particle> totalSelectedParticles = new HashMap<Integer, Particle>();
		// modifica distantele particulelor in functie de ofertele serverelor
		// din DC

		DCOfferIterator = DCOffers.iterator();
		int i = 0;
		while (DCOfferIterator.hasNext()) {

			float[] counterDistance = new float[Particle.nrResources];
			selectedParticles = (HashMap<Integer, Particle>) psoAlgorithm
					.selectParticles(size / DCOffers.size());
			Offer offer = DCOfferIterator.next();
			counterDistance[0] = offer.getHdd();
			counterDistance[1] = offer.getCpu();
			counterDistance[2] = offer.getMemory();
			counterDistance[3] = offer.getCost();

			float[] compromise = computeCompromise(i);

			psoAlgorithm.alterDistance(selectedParticles, compromise,
					counterDistance).values();
			totalSelectedParticles.putAll(selectedParticles);
			i++;
		}

		Particle selectedParticle = psoAlgorithm
				.averageParticle(totalSelectedParticles);

		clientOffer.setHdd(selectedParticle.getDistance()[0]);
		clientOffer.setCpu(selectedParticle.getDistance()[1]);
		clientOffer.setMemory(selectedParticle.getDistance()[2]);
		clientOffer.setCost(selectedParticle.getDistance()[3]);

		fitness.rateOffer(clientOffer);

		counterOffers.put(offerNumber, clientOffer);

		if (availableTime < 0)
			return null;

		return clientOffer;

	}

	/**
	 * returneaza true daca se accepta o oferta, false altfel
	 * 
	 * @param DCOffers
	 *            ofertele DC
	 * @param time
	 *            timpul ramas pentru negociere
	 * @return true daca se accepta o oferta, false altfel
	 */
	public boolean acceptOffer(ArrayList<Offer> DCOffers, int time) {

		Iterator<Offer> iterator = DCOffers.iterator();
		Offer serverOffer;
		float threshold;
		
		scale_threshold +=  (totalTime - availableTime)*10;

		threshold = threshold_upper - (threshold_upper - threshold_lower)* (scale_threshold/(scale_threshold+10));
		while (iterator.hasNext()) {

			serverOffer = iterator.next();

			fitness.rateOffer(serverOffer);

			if( offerNumber > 1)
				if (serverOffer.getFitness() > threshold) {
				return true;

			}
		}

		return false;
	}
	



	/**
	 * actualizeaza particulele din swarm dupa algoritmul clasic PSO
	 */
	public void updateSwarm() {

		int c1 = 2, c2 = 2;
		float W = 0.75f;

		psoAlgorithm.alterParticles(psoAlgorithm.getParticles(), c1, c2, W);

	}

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
		
		fitness_file.println("client/server");
		
		while (iteration < counterOffers.size()
				&& iteration < oppositeAgentOffers.size()) {

			result_file.println("iteratia " + iteration);
			fitness_file.append(new Integer(iteration).toString()+" ");
			if (iteration > 0) {

				result_file.println("Client "
						+ counterOffers.get(iteration).toString());
				fitness_file.append(new Float(counterOffers.get(iteration).getFitness()).toString());
				fitness_file.append(" ");
			}

			ArrayList<Offer> oppositeOffers = oppositeAgentOffers
					.get(iteration);
			Iterator<Offer> oppositeOffersIterator = oppositeOffers.iterator();

			Offer currentOffer;
			
			while (oppositeOffersIterator.hasNext()) {

				currentOffer = oppositeOffersIterator.next();
				result_file.println("Server "
						+ currentOffer.toString());
				fitness_file.println(new Float(currentOffer.getFitness()).toString());

			}

			iteration++;

		}

		result_file.close();
		fitness_file.close();

	}

	public void setMaxValues(float[] maxValues) {
		this.maxValues = maxValues;
	}

	public float[] getMaxValues() {
		return maxValues;
	}

	public void setMinValues(float[] minValues) {
		this.minValues = minValues;
	}

	public float[] getMinValues() {
		return minValues;
	}

	public int getOfferNumber() {
		return offerNumber;
	}

	public void setMaxCompromise(float[] maxCompromise) {
		this.maxCompromise = maxCompromise;
	}

	public void setSwarmSize(int swarmSize) {
		this.swarmSize = swarmSize;
	}

	public void setThreshold(float threshold_lower, float threshold_upper) {
		this.threshold_lower = threshold_lower;
		this.threshold_upper = threshold_upper;
	}

}
