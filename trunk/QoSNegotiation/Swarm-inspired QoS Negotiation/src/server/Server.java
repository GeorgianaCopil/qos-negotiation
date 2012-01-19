package server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import PSO.PSOS;
import PSO.Particle;

import negotiation.Offer;

public class Server {

	private PSOS psoAlgorithm;
	private List<Offer> serverHistory;
	private int swarmSize;
	Fitness fitness;
	private List<RelativeDistance> relativeDistances;
	int i = 0;
	private long totalTime;
	private long availableTime;
	private float scale = 0;
	private int offerNumber;

	public Server(float[] minValues, float[] maxValues, int size) {

		
		totalTime = 10000;
		availableTime = 10000;
		
		offerNumber = 0;
		
		psoAlgorithm = new PSOS();
		serverHistory = new ArrayList<Offer>();
		relativeDistances = new ArrayList<RelativeDistance>();
		
		float gBestValues[] = new float[Particle.nrResources];
		
		for(int i = 0 ; i < 3; i++)
			gBestValues[i] = minValues[i];
		gBestValues[3] = maxValues[3];
		
		psoAlgorithm.initializeSwarm(size, minValues, maxValues, gBestValues);
		this.swarmSize = size;
		this.fitness = new Fitness();
		
		fitness.setMax(maxValues);
		fitness.setMin(minValues);

	}

	/**
	 * calculeaza impactul avut de oferta clientului asupra particulelor  selectate din swarm
	 * @param timeLeft timpul ramas pntru negociere
	 * @return factorul de compromis
	 */
	private float[] computeCompromise(int timeLeft) {
		
		float[] maxCompromise = new float[Particle.nrResources];
		
		maxCompromise = new float[Particle.nrResources];
		
		maxCompromise[0] = 0.612f;
		maxCompromise[1] = 0.7496f;
		maxCompromise[2] = 0.2115f;
		maxCompromise[3] = -0.413f;

		float[] randomPoint = new float[Particle.nrResources];
		float[] compromise = new float[Particle.nrResources];
		
		scale = (float) (scale + offerNumber*4);
		
		
		for (int i = 0; i < Particle.nrResources; i++) {

			randomPoint[i] =  scale;
			compromise[i] = maxCompromise[i] * randomPoint[i]
					/ (randomPoint[i] + 50);
		}

		return compromise;
	}

	/**
	 * calculeaza numarul de particule afectate de oferta clientului
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
	 * @param clientOffer oferta din partea clinetului
	 * @param timeLeft timpul ramas pentru negociere
	 * @return contra-oferta serverului
	 */
	public Offer computeOffer(Offer clientOffer, int timeLeft) {

		float[] counterDistance = new float[Particle.nrResources];
		
		offerNumber++;
		
		counterDistance[0] = clientOffer.getHdd();
		counterDistance[1] = clientOffer.getCpu();
		counterDistance[2] = clientOffer.getMemory();
		counterDistance[3] = clientOffer.getCost();

		Map<Integer, Particle> alteredParticles = psoAlgorithm.alterDistance(
				psoAlgorithm
						.selectParticles(numberOfAlteredParticles()),
				computeCompromise(timeLeft), counterDistance);

		Particle selectedParticle = psoAlgorithm
				.selectParticle(alteredParticles);

		Offer serverOffer = new Offer();
		serverOffer.setHdd(selectedParticle.getDistance()[0]);
				serverOffer.setCpu(selectedParticle.getDistance()[1]);
				serverOffer.setMemory(selectedParticle.getDistance()[2]);
				serverOffer.setCost(selectedParticle.getDistance()[3]);

		serverHistory.add(serverOffer);
		
		return serverOffer;
	}
	
	/**
	 * actualizeaza valorile parametrilor din swarm
	 * @param time timpul ramas pentru negociere
	 */
	public void updateSwarm(int time){
		
		int c1 = 2, c2 = 2;
		float W = 0.6f;
		
		psoAlgorithm.alterParticles(psoAlgorithm.getParticles(), c1, c2, W);
		
	}

	/**
	 * returneaza true daca se accepta oferta, false altfel
	 * @param clientOffer oferta clientului
	 * @param timeLeft timpul ramas pentru negociere
	 * @return true daca se accepta oferta, false altfel
	 */	
	public boolean acceptOffer(Offer clientOffer, int timeLeft) {

		
		fitness.rateOffer(clientOffer);
		
		if(clientOffer.getFitness() < 0.5){
			return true;
		}
		return false;
	}
	
	/**
	 * se "scoate" serverul din negociere
	 * @throws Throwable
	 */
	public void exitNegotiation() throws Throwable {

		this.finalize();
	}

	public List<RelativeDistance> getRelativeDistances() {

		return this.relativeDistances;
	}
}
