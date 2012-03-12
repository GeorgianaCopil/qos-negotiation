package server;

import java.util.HashMap;
import java.util.Map;
import PSO.PSOS;
import PSO.Particle;

import negotiation.Offer;

public class Server {

	private PSOS psoAlgorithm;
	private int swarmSize;
	Fitness fitness;
	int i = 0;
	private long totalTime;
	private long availableTime;
	private float scale = 0;
	private int offerNumber;
	private float scale_threshold = 0;
	private float threshold_lower, threshold_upper;
	private static final int NUMBER_OF_VALUES_FOR_SCALING = 5;

	// utilitatile pentru ofertele primite de la client
	private HashMap<Integer, Float> oppositeAgentOfferUtilities;
	// ofertele agentului advers
	private HashMap<Integer, Offer> oppositeAgentOffers;
	// contra ofertele trimise de server
	private HashMap<Integer, Offer> counterOffers;

	private Float[] maxCompromise;

	public Server(float[] minValues, float[] maxValues, Float[] weights,
			int size) {


		offerNumber = 0;

		psoAlgorithm = new PSOS();

		float gBestValues[] = new float[Particle.nrResources];

		for (int i = 0; i < 3; i++)
			gBestValues[i] = minValues[i];
		gBestValues[3] = maxValues[3];

		psoAlgorithm.initializeSwarm(size, minValues, maxValues, gBestValues);
		this.swarmSize = size;
		this.fitness = new Fitness();

		fitness.setMax(maxValues);
		fitness.setMin(minValues);
		fitness.setWeight(weights);

		oppositeAgentOfferUtilities = new HashMap<Integer, Float>();
		oppositeAgentOffers = new HashMap<Integer, Offer>();
		counterOffers = new HashMap<Integer, Offer>();

	}

	/**
	 * calculeaza impactul avut de oferta clientului asupra particulelor
	 * selectate din swarm
	 * 
	 * @param timeLeft
	 *            timpul ramas pntru negociere
	 * @return factorul de compromis
	 */
	private float[] computeCompromise(int timeLeft) {

		float[] maxCompromise = new float[Particle.nrResources];

		maxCompromise = new float[Particle.nrResources];

		maxCompromise[0] = 0.512f;
		maxCompromise[1] = 0.5496f;
		maxCompromise[2] = 0.5115f;
		maxCompromise[3] = -0.513f;

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
				scale = scale + 0;

		}

		scale = scale + offerNumber / 4;

		// random point in functie de oferta celulialt, sa creasca odata cu
		// crestera compromisului celuilalt

		for (int i = 0; i < Particle.nrResources; i++) {

			randomPoint[i] = scale;
			compromise[i] = maxCompromise[i] * randomPoint[i]
					/ (randomPoint[i] + 50);

		}

		return compromise;
	}

	/**
	 * calculeaza numarul de particule afectate de oferta clientului
	 * 
	 * @return numarul de particule afectate de oferta clientului
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
	 * calculeaza contra-oferta serverului
	 * 
	 * @param clientOffer
	 *            oferta din partea clinetului
	 * @param timeLeft
	 *            timpul ramas pentru negociere
	 * @return contra-oferta serverului
	 */
	public Offer computeOffer(Offer clientOffer, int timeLeft) {

		float[] counterDistance = new float[Particle.nrResources];

		// se calculeaza timpul ramas pentru negociere
		availableTime = totalTime - System.currentTimeMillis();

		fitness.rateOffer(clientOffer);
		oppositeAgentOfferUtilities.put(new Integer(offerNumber),
				clientOffer.getFitness());
		oppositeAgentOffers.put(new Integer(offerNumber), clientOffer);

		offerNumber++;

		counterDistance[0] = clientOffer.getHdd();
		counterDistance[1] = clientOffer.getCpu();
		counterDistance[2] = clientOffer.getMemory();
		counterDistance[3] = clientOffer.getCost();

		Map<Integer, Particle> alteredParticles = psoAlgorithm.alterDistance(
				psoAlgorithm.selectParticles(numberOfAlteredParticles()),
				computeCompromise(timeLeft), counterDistance);

		Particle selectedParticle = psoAlgorithm
				.selectParticle(alteredParticles);

		Offer serverOffer = new Offer();
		serverOffer.setHdd(selectedParticle.getDistance()[0]);
		serverOffer.setCpu(selectedParticle.getDistance()[1]);
		serverOffer.setMemory(selectedParticle.getDistance()[2]);
		serverOffer.setCost(selectedParticle.getDistance()[3]);

		fitness.rateOffer(serverOffer);

		counterOffers.put(new Integer(offerNumber), serverOffer);

//		 if(availableTime < 0)
//		 return null;

		return serverOffer;
	}

	/**
	 * actualizeaza valorile parametrilor din swarm
	 * 
	 * @param time
	 *            timpul ramas pentru negociere
	 */
	public void updateSwarm() {

		int c1 = 2, c2 = 2;
		float W = 0.6f;

		psoAlgorithm.alterParticles(psoAlgorithm.getParticles(), c1, c2, W);

	}

	/**
	 * returneaza true daca se accepta oferta, false altfel
	 * 
	 * @param clientOffer
	 *            oferta clientului
	 * @param timeLeft
	 *            timpul ramas pentru negociere
	 * @return true daca se accepta oferta, false altfel
	 */
	public boolean acceptOffer(Offer clientOffer, int timeLeft) {

		fitness.rateOffer(clientOffer);
		float threshold;
		
		scale_threshold +=  (totalTime - availableTime)*10;
		threshold = threshold_upper - (threshold_upper - threshold_lower)* (scale_threshold/(scale_threshold+10));
		
		if (clientOffer.getFitness() < (1-threshold)) {
			return true;
		}
		return false;
	}

	/**
	 * se "scoate" serverul din negociere
	 * 
	 * @throws Throwable
	 */
	public void exitNegotiation() throws Throwable {

		this.finalize();
	}

	public void setThreshold(float threshold_lower, float threshold_upper) {
		this.threshold_lower = threshold_lower;
		this.threshold_upper = threshold_upper;
	}

	public long getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(long totalTime) {
		this.totalTime = System.currentTimeMillis() + totalTime;
	}

	public void setMaxCompromise(Float[] maxComprimise) {
		this.maxCompromise = maxComprimise;
	}

	public Float[] getMaxCompromise() {
		return maxCompromise;
	}

	public HashMap<Integer, Offer> getOppositeAgentOffers() {
		return oppositeAgentOffers;
	}

	public void setOppositeAgentOffers(
			HashMap<Integer, Offer> oppositeAgentOffers) {
		this.oppositeAgentOffers = oppositeAgentOffers;
	}

	public HashMap<Integer, Offer> getCounterOffers() {
		return counterOffers;
	}

	public void setCounterOffers(HashMap<Integer, Offer> counterOffers) {
		this.counterOffers = counterOffers;
	}

}
