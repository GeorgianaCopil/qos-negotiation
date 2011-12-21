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

	public Server(float[] minValues, float[] maxValues, int size) {

		psoAlgorithm = new PSOS();
		serverHistory = new ArrayList<Offer>();
		relativeDistances = new ArrayList<RelativeDistance>();
		psoAlgorithm.initializeSwarm(size, minValues, maxValues, minValues);
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
		// TODO
		float[] compromise = new float[Particle.nrResources];
		compromise[0] = 0.194f;
		compromise[1] = 0.32f;
		compromise[2] = 0.08f;
		compromise[3] = 0.001f;
		return compromise;
	}

	/**
	 * calculeaza numarul de particule afectate de oferta clientului
	 * @param timeLeft timpul ramas pentru negociere
	 * @return numarul de particule afectate de oferta clientului
	 */
	private int numberOfAlteredParticles(int timeLeft) {
		// TODO
		return swarmSize * 30 / 100;
	}
	


	/**
	 * calculeaza contra-oferta serverului
	 * @param clientOffer oferta din partea clinetului
	 * @param timeLeft timpul ramas pentru negociere
	 * @return contra-oferta serverului
	 */
	public Offer computeOffer(Offer clientOffer, int timeLeft) {

		float[] counterDistance = new float[Particle.nrResources];
		
		counterDistance[0] = clientOffer.getHdd();
		counterDistance[1] = clientOffer.getCpu();
		counterDistance[2] = clientOffer.getMemory();
		counterDistance[3] = clientOffer.getCost();

		Map<Integer, Particle> alteredParticles = psoAlgorithm.alterDistance(
				psoAlgorithm
						.selectParticles(numberOfAlteredParticles(timeLeft)),
				computeCompromise(timeLeft), counterDistance);

//		Particle selectedParticle = psoAlgorithm
//				.selectParticle(alteredParticles);
		Particle selectedParticle = psoAlgorithm.averageParticle(alteredParticles);

		Offer serverOffer = new Offer();
		serverOffer.setHdd(selectedParticle.getDistance()[0]);
				serverOffer.setCpu(selectedParticle.getDistance()[1]);
				serverOffer.setMemory(selectedParticle.getDistance()[2]);
				serverOffer.setCost(selectedParticle.getDistance()[3]);

		serverHistory.add(serverOffer);
		
//		relativeDistances.add(new RelativeDistance(clientOffer.getHdd()
//				/ serverOffer.getHdd(), clientOffer.getCpu()
//				/ serverOffer.getCpu(), clientOffer.getMemory()
//				/ serverOffer.getMemory()));

		return serverOffer;
	}
	
	/**
	 * actualizeaza valorile parametrilor din swarm
	 * @param time timpul ramas pentru negociere
	 */
	public void updateSwarm(int time){
		
		//TODO rafinat paramterii 
		int c1 = 2, c2 = 2;
		float W = 0.3f;
		
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
		
		if(clientOffer.getFitness() < 0.15){
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
