package client;

import java.util.HashMap;
import java.util.Map;

import PSO.PSO_subswarm;
import PSO.Particle;

import negotiation.Offer;

public class ClientSubswarm {
	
	private int swarmSize;
	private float[] maxValues;
	private float[] minValues;
	private PSO_subswarm psoAlgoritm;

	 public ClientSubswarm(float[] minValues, float[] maxValues, int no_servers, int size) {

		this.maxValues = maxValues;
		this.minValues = minValues;
		this.swarmSize = size;
		
		psoAlgoritm = new PSO_subswarm(maxValues, minValues, no_servers);
		psoAlgoritm.initializeSubSwarms(swarmSize);
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
	 public Map<Integer, Offer> computeOffer(Map<Integer, Offer> DCOffers){
		 
		 Map<Integer, Offer> offers = new HashMap<Integer, Offer>();
		 
		 //TODO adauga timpul
		 int size = computeNoAffectedParticles(10);
		 float[] compromise =  computeCompromise(10);
		  
		 offers = particlesToOffers(psoAlgoritm.updateDistances(compromise, offersToParticles(DCOffers), size));
		 return offers; 	 
	 }
	 
	 
	 public void updateSwarm(){
		 
		 //TODO seteaza param
		 psoAlgoritm.updateSwarm(1, 1, 0);
	 }
	 
	/**
	 * conversteste o lista de particule intr-o lista de oferte
	 * @param particles lista de particule
	 * @return
	 */
	 public Map<Integer, Offer> particlesToOffers(Map<Integer, Particle> particles){
		 
		 Map<Integer, Offer> offers = new HashMap<Integer, Offer>();
		  for (Map.Entry<Integer, Particle> particle : particles.entrySet()){
			  
			  Offer offer = new Offer();
			  
			  offer.setHdd( particle.getValue().getDistance()[0]);
			  offer.setCpu( particle.getValue().getDistance()[1]);
			  offer.setMemory( particle.getValue().getDistance()[2]);
			  offer.setCost( particle.getValue().getDistance()[3]);
			  
			  offers.put(particle.getKey(), offer);
			  
		  }
		 return offers;
	 }
	 
	 /**
	  * conversteste o lista de oferte intr-o lista de particule
	  * @param offers lista de oferte
	  * @return
	  */
	 public Map<Integer, Particle> offersToParticles(Map<Integer, Offer> offers){
		 
		 Map<Integer, Particle> particles = new HashMap<Integer, Particle>();
		 for (Map.Entry<Integer, Offer> offer : offers.entrySet()){
			 
			 Particle particle = new Particle();
			 float[] resources = new float[Particle.nrResources];
			 
			 resources[0] = offer.getValue().getHdd();
			 resources[1] = offer.getValue().getCpu();
			 resources[2] = offer.getValue().getMemory();
			 resources[3] = offer.getValue().getCost();
			 
			 particle.setDistance(resources);
			 particle.setPbest(resources);
			 particle.setVelocity(resources);
			 
			 particles.put(offer.getKey(), particle);
		 }
		 
		 return particles;
	 }

	/**
	 * calculeaza impactul avut de oferta contra agentului, pentru fiecare
	 * resursa
	 */
	public float[] computeCompromise(long time) {
		// TODO
		float compromise[]= new float[Particle.nrResources];
		compromise[0] = 0.1f;
		compromise[1] = 0.3f;
		compromise[2] = 0.5f;
		compromise[3] = 0.4f;
		return compromise;
	}

	/**
	 * calculeaza numarul de particule care sunt afectate de oferta agentului
	 * advers
	 */
	public int computeNoAffectedParticles(long time) {
		return 10;
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
	public boolean acceptOffer(Map<Integer, Offer> DCOffers, int time) {
		// TODO
		return false;
	}

	/**
	 * actualizeaza valorile parametrilor din swarm
	 * 
	 * @param time
	 *            timpul ramas pentru negociere
	 */

	public float updateInitialWeight(long time) {
		return 1;
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
