package client;

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
	//TODO
	private float scale = (float)0.25;

	// numarul de oferte schimbate dupa care se face ajustarea compromisului

	private static final int NUMBER_OF_VALUES_FOR_SCALING = 4;

	// structura in care se retin valorile ofertelor agentului advers
	private HashMap<Integer, ArrayList<Offer>> counterOffers;
	private HashMap<Integer, ArrayList<Float>> counterOffersUtilities;

	public Client(long time, float[] minValues, float[] maxValues) {

		totalTime = System.currentTimeMillis() + time;
		availableTime = time;
		this.maxValues = maxValues;
		this.minValues = minValues;
		this.swarmSize = 60;
		psoAlgorithm = new PSO();

		// initializare
		// valorile pentru gBest sunt: valoarea maxima posibila pentru resurse
		// si valoarea minima posibila pentru cost
		float gBestValues[] = new float[Particle.nrResources];
		for (int i = 0; i < 3; i++)
			gBestValues[i] = maxValues[i];
		gBestValues[3] = minValues[3];

		// se initializeaza popolatia
		psoAlgorithm.initializeSwarm(swarmSize, minValues, maxValues,
				gBestValues);

		offerNumber = 0;

		fitness = new Fitness();
		fitness.setMax(maxValues);
		fitness.setMin(minValues);

		// ofertele agentului advers
		counterOffers = new HashMap<Integer, ArrayList<Offer>>();
		// utilitatile ofertelor agentului advers
		counterOffersUtilities = new HashMap<Integer, ArrayList<Float>>();

	}

	/**
	 * calculeaza impactul avut de oferta contra agentului, pentru fiecare
	 * resursa acest factor de compromis este determinat de numarul de oferte
	 * schimbat de agenti
	 */
	public float[] computeCompromise(int serverNumber, float[] maxCompromise) {

		// factorul de compromis va fi repr de punctele x de pe graficul
		// functiei a*x/(x+1), unde a este valoarea maxima pentru
		// factorul de compromis

		maxCompromise = new float[Particle.nrResources];
		maxCompromise[0] = 0.412f;
		maxCompromise[1] = 0.3496f;
		maxCompromise[2] = 0.6115f;
		maxCompromise[3] = -0.0113f;

		float[] randomPoint = new float[Particle.nrResources];
		float[] compromise = new float[Particle.nrResources];

		// numaram cate din ofertele agentului din intervalul
		// [offerNumber-NUMBER_OF_VALUES_FOR_SCALING+1, offerNumber]
		// sunt mai mari decat NUMBER_OF_VALUES_FOR_SCALING

		// comparisonUtility este utilitatea ofertei cu numarul
		// offerNumber-NUMBER_OF_VALUES_FOR_SCALING+1

		float comparisonUtility;

		// numarul de oferte pentru care agentul advers a redus compromisul
		//care sunt cuprinse in intervalul [offerNumber-NUMBER_OF_VALUES_FOR_SCALING+1, offerNumber]
		int compromisedOffers = 0;

		if (offerNumber > NUMBER_OF_VALUES_FOR_SCALING
				&& offerNumber % NUMBER_OF_VALUES_FOR_SCALING == 0) {
			System.out.println("aici");
			comparisonUtility = counterOffersUtilities.get(
					offerNumber - NUMBER_OF_VALUES_FOR_SCALING + 1).get(
					serverNumber);

			for (int i = NUMBER_OF_VALUES_FOR_SCALING + 2; i >= 1; i--)
				if (counterOffersUtilities.get(offerNumber - i).get(
						serverNumber) < comparisonUtility)
					compromisedOffers++;

			// functie "treapta"
			//TODO rafinat
			
			if (compromisedOffers / NUMBER_OF_VALUES_FOR_SCALING > 0.75)
				scale = scale + offerNumber * 10;
			else if (compromisedOffers / NUMBER_OF_VALUES_FOR_SCALING > 0.5)
				scale = scale + offerNumber  * 5;
			else if (compromisedOffers / NUMBER_OF_VALUES_FOR_SCALING > 0.25)
				scale = (float) (scale + offerNumber  * 2.5);
			else
				scale = scale + 0;

		}
		
		//TODO de ce?
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

		// se memoreaza oferta agentului advers
		Iterator<Offer> DCOfferIterator = DCOffers.iterator();

		// calculam utilitatea pentru ofertele agentului advers
		ArrayList<Float> utilities = new ArrayList<Float>();

		while (DCOfferIterator.hasNext()) {

			Offer currentOffer = DCOfferIterator.next();
			utilities.add(new Float(currentOffer.getFitness()));

		}

		counterOffers.put(offerNumber, DCOffers);
		counterOffersUtilities.put(offerNumber, utilities);

		// s-a mai schimbat o oferta intre client si Data Center
		offerNumber++;

		// se calculeaza timpul ramas pentru negociere
		availableTime = totalTime - System.currentTimeMillis();

		int size = computeNoAffectedParticles();

		HashMap<Integer, Particle> selectedParticles;// = (HashMap<Integer, Particle>) psoAlgorithm.selectParticles(size);
		HashMap<Integer, Particle> totalSelectedParticles = new HashMap<Integer, Particle>();
		// modifica distantele particulelor in functie de ofertele serverelor
		// din DC

		DCOfferIterator = DCOffers.iterator();
		int i = 0;
		while (DCOfferIterator.hasNext()) {

			float[] counterDistance = new float[Particle.nrResources];
			selectedParticles = (HashMap<Integer, Particle>) psoAlgorithm
				.selectParticles(size/DCOffers.size());
			Offer offer = DCOfferIterator.next();
			counterDistance[0] = offer.getHdd();
			counterDistance[1] = offer.getCpu();
			counterDistance[2] = offer.getMemory();
			counterDistance[3] = offer.getCost();

			float[] compromise = computeCompromise(i, null);

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

		while (iterator.hasNext()) {

			serverOffer = iterator.next();
			if (serverOffer.getFitness() > 0.4 && serverOffer.getCostP() < 0.6)

				return true;

		}

		return false;
	}

	/**
	 * actualizeaza particulele din swarm dupa algoritmul clasic PSO
	 */
	public void updateSwarm() {

		int c1 = 2, c2 = 2;
		float W = 0.75f;// updateInitialWeight(0);

		psoAlgorithm.alterParticles(psoAlgorithm.getParticles(), c1, c2, W);

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

}
