package client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import PSO.PSO;
import PSO.Particle;

import negotiation.Offer;

public class Client {

	private PSO psoAlgorithm;
	private int swarmSize;
	private float[] maxValues;
	private float[] minValues;
	private List<Offer> clientHistory;
	Fitness fitness;

	public Client(float[] minValues, float[] maxValues) {

		this.maxValues = maxValues;
		this.minValues = minValues;
		this.swarmSize = 60;
		psoAlgorithm = new PSO();

		// se initializeaza swarmul
		psoAlgorithm.initializeSwarm(swarmSize, minValues, maxValues, maxValues);
		clientHistory = new ArrayList<Offer>();
		
		fitness = new Fitness();
		fitness.setMax(maxValues);
		fitness.setMin(minValues);
	}

	/**
	 * calculeaza countra oferta clientului pentru ofertele DC date de
	 * parametrul DCOffers
	 * 
	 * @param DCOffers
	 *            lista cu ofertele primite de la fiecare server din DC
	 * @param size
	 *            numarul de particule afectate de ofertele DC - parametru ce
	 *            depinde de timp
	 * @param compromise
	 *            impactul avut de ofertele DC - parametru ce depinde de timp
	 * @return contra oferta clientului
	 */

	/**
	 * calculeaza impactul avut de oferta contra agentului, pentru fiecare
	 * resursa
	 */
	public float[] computeCompromise(long time) {
		// TODO
		float compromise[]= new float[Particle.nrResources];
		compromise[0] = 0.02f;
		compromise[1] = 0.096f;
		compromise[2] = 0.005f;
		compromise[3] =  0.03f;
		return compromise;
	}

	/**
	 * calculeaza numarul de particule care sunt afectate de oferta agentului
	 * advers
	 */
	public int computeNoAffectedParticles(long time) {
		return 10;
	}

	public Offer computeOffer(ArrayList<Offer> DCOffers) {

		Offer clientOffer = new Offer();
		Iterator<Offer> DCOfferIterator = DCOffers.iterator();

		// TODO implementare computeNoAffectedParticles
		int size = computeNoAffectedParticles(10);

		HashMap<Integer, Particle> selectedParticles = (HashMap<Integer, Particle>) psoAlgorithm
				.selectParticles(size);
		List<Particle> counterOffers = new ArrayList<Particle>();

		// TODO implementare computeCompormise
		float[] compromise = computeCompromise(10);

		// modifica distantele particulelor in functie de ofertele serverelor
		// din DC
		while (DCOfferIterator.hasNext()) {

			float[] counterDistance = new float[Particle.nrResources];
			Offer offer = DCOfferIterator.next();
			counterDistance[0] = offer.getHdd();
			counterDistance[1] = offer.getCpu();
			counterDistance[2] = offer.getMemory();
			counterDistance[3] = offer.getCost();


			counterOffers.addAll(new ArrayList<Particle>((psoAlgorithm
					.alterDistance(selectedParticles, compromise,
							counterDistance)).values()));
		}

		// se selecteaza aleator o particula din particulele modificate
		// aceasta particula reprezinta contra-oferta clientului

//		int random = (int) (Math.random() * counterOffers.size());
//		int random = 1 + (int)(Math.random() * ((counterOffers.size() - 1) + 1));
//		int counter = 0;
//		Iterator<Particle> counterOfferIterator = counterOffers.iterator();
//		Particle selectedParticle = null;
//
//		while (counter < random && counterOfferIterator.hasNext()) {
//
//			counter++;
//			selectedParticle = counterOfferIterator.next();
//		}

		//float[] distance = selectedParticle.getDistance();
		Particle selectedParticle = psoAlgorithm.averageParticle(selectedParticles);

		clientOffer.setHdd(selectedParticle.getDistance()[0]);
		clientOffer.setCpu(selectedParticle.getDistance()[1]);
		clientOffer.setMemory(selectedParticle.getDistance()[2]);
		clientOffer.setCost(selectedParticle.getDistance()[3]);

		// se adauga oferta la istoricul clientului
		clientHistory.add(clientOffer);

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
		
		while(iterator.hasNext()){
			
			serverOffer = iterator.next();
			if(serverOffer.getFitness() > 0.8)
			//
				return true;
			
		}
		return false;
	}

	/**
	 * actualizeaza valorile parametrilor din swarm
	 * 
	 * @param time
	 *            timpul ramas pentru negociere
	 */

	public float updateInitialWeight(long time) {
		// TODO
		return 1f;
	}

	public void updateSwarm() {

		// TODO rafinat paramterii
		int c1 = 2, c2 = 2;
		float W = 0.5f;//updateInitialWeight(0);

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

}
