package PSO;

import java.util.HashMap;
import java.util.Map;

public class PSO {

	
	private Map<Integer, Particle> particles;
	private Particle gBest;
	float[] min;
	float[] max;

	/**
	 * initializeaza swarmul cu particule aleatoare in limitele definite de
	 * param. min si max
	 * 
	 * @param size
	 *            dimensiunea populatiei
	 * @param min
	 *            valorile minime pentru resursele negocierii
	 * @param max
	 *            valorile maxime pentru resursele negocierii
	 */
	public void initializeSwarm(int size, float min[], float max[],
			float gBestValues[]) {

		particles = new HashMap<Integer, Particle>();

		int id = 1;

		while (particles.size() < size) {
			particles.put(new Integer(id), generateParticle(min, max));
			id++;
		}

		this.min = min;
		this.max = max;
		initializeGBest(gBestValues);
	}

	public void initializeGBest(float[] values) {

		gBest = new Particle();
		gBest.setDistance(values);
		gBest.setVelocity(values);
	}

	public Map<Integer, Particle> getParticles() {
		return particles;
	}

	/**
	 * afiseaza populatia (swarm-ul)
	 */
	public void printSwarm() {

		for (Map.Entry<Integer, Particle> entry : particles.entrySet()) {
			System.out.println("distance");
			System.out.println(entry.getKey() + " "
					+ entry.getValue().getDistance()[0] + " "
					+ entry.getValue().getDistance()[1] + " "
					+ entry.getValue().getDistance()[2] + " ");
			System.out.println("velocity");
			System.out.println(entry.getKey() + " "
					+ entry.getValue().getVelocity()[0] + " "
					+ entry.getValue().getVelocity()[1] + " "
					+ entry.getValue().getVelocity()[2] + " ");
			System.out.println("");
		}
	}

	/**
	 * calculeaza media particulelor selectate din swarm pentru determinarea
	 * contra-ofertei
	 * 
	 * @param selectedParticles
	 *            - particulele selectate din swarm pentru determinarea
	 *            contra-ofertei
	 * @return media particulelor din selectedParticles
	 */
	public Particle averageParticle(Map<Integer, Particle> selectedParticles){
		
		Particle average = new Particle();
		float[] resource = new float[Particle.nrResources];
		for(int i = 0; i < Particle.nrResources; i++)
			resource[i] = 0;
		for (Map.Entry<Integer, Particle> presentParticle : selectedParticles.entrySet()) {
			
			for(int i = 0; i < Particle.nrResources; i++)
				resource[i] = resource[i] + presentParticle.getValue().getDistance()[i];
		 }
		for(int i = 0; i < Particle.nrResources; i++)
			resource[i]= resource[i]/selectedParticles.size(); 
		
		average.setDistance(resource);
		
		return average;
		
	}

	/**
	 * genereaza o particula aleatore cu valori intre min si max
	 * 
	 * @param min
	 *            valorile minime pentru resursele negocierii
	 * @param max
	 *            valorile maxime pentru resursele negocierii
	 * @return particula aleatoare cu valori intre min si max
	 */
	public Particle generateParticle(float min[], float max[]) {

		Particle particle = new Particle();
		float[] velocity, distance;

		velocity = new float[Particle.nrResources];
		distance = new float[Particle.nrResources];

		for(int i = 0; i < Particle.nrResources; i++)
			distance[i] = (float) (min[i]+(max[i]-min[i])/2 + Math.random() * (max[i] +min[i])/2);

		
		for (int i = 0; i < Particle.nrResources; i++) {
			if (distance[i] < min[i]){
				distance[i] = min[i];
			}
			if (distance[i] > max[i]){
				distance[i] = max[i];
			}
		}
		

		for (int i = 0; i < Particle.nrResources; i++){
			float minV = -Math.abs(max[i] - min[i]);
			float maxV = Math.abs(max[i]-min[i]);
			
			velocity[i] = (float) (minV + Math.random() * (maxV - minV));	
		}

		particle.setDistance(distance);
		particle.setVelocity(velocity);
		particle.setPbest(distance);
		

		return particle;
	}

	/**
	 * actualizeaza valorile swarm-ului dat ca parametru conform algoritmului
	 * PSO
	 * 
	 * @param particles
	 *            swarmu-ul (populatia) care se va actualiza (va evolua)
	 * @param c1
	 *            determina impactul lui pBest (cea mai buna valoare a
	 *            particulei)
	 * @param c2
	 *            determina impactul lui gBest (particula cea mai buna din
	 *            populatie)
	 * @param W
	 *            determina inertia: impactul avut de viteza anterioara
	 * @return populatia "evoluata"
	 */
	
	public Map<Integer, Particle> alterParticles(
			Map<Integer, Particle> particles, float c1, float c2, float W) {

		Map<Integer, Particle> newParticles = new HashMap<Integer, Particle>();

		float[] velocity;
		float[] particleDistance, particleVelocity, particleBest;

		for (Map.Entry<Integer, Particle> presentParticle : particles
				.entrySet()) {

			velocity = new float[Particle.nrResources];

			Particle newParticle = new Particle();
			particleDistance = presentParticle.getValue().getDistance();
			particleVelocity = presentParticle.getValue().getVelocity();
			particleBest = presentParticle.getValue().getPbest();
			
			for (int i = 0; i < Particle.nrResources; i++){
			velocity[i] = (float) (W
				* particleVelocity[i]
				+ Math.random()
				* c1
				* (presentParticle.getValue().getPbest()[i] - particleDistance[i]) + 
				Math
				.random()
				* c2
				* (gBest.getDistance()[i] - particleDistance[i]));
			}

			newParticle.setVelocity(velocity);

			// se tesetaza consistenta valorilor obtinute
			for (int i = 0; i < Particle.nrResources; i++){
				
				particleDistance[i] = particleDistance[i]+velocity[i];
				
				if (particleDistance[i] < min[i]){
					particleDistance[i] = min[i];
				}
				else if (particleDistance[i] > max[i]){
					particleDistance[i] = max[i];
				}
			}
			
			newParticle.setDistance(particleDistance);
			 newParticle.setPbest(particleBest);
			newParticle.updatePBest(gBest,particleBest);
			newParticles.put(presentParticle.getKey(), newParticle);

		}

		updateSwarm(newParticles);
		return newParticles;

	}

	/**
	 * adauga/modifica valorile particulelor din populatie in functie de
	 * newSwarm
	 * 
	 * @param newSwarm
	 *            valorile particulelor noi/actualizate
	 */
	public void updateSwarm(Map<Integer, Particle> newSwarm) {

		for (Map.Entry<Integer, Particle> presentParticle : newSwarm.entrySet()) {
			particles.put(presentParticle.getKey(), presentParticle.getValue());
		}

	}

	/**
	 * selecteaza aleator size particule din swarm. Particulele vor fi afectate
	 * de oferta DC
	 * 
	 * @param size
	 *            numarul de particule care se selecteaza
	 * @return particulele selectate
	 */
	public Map<Integer, Particle> selectParticles(int size) {

		Map<Integer, Particle> selectedParticles = new HashMap<Integer, Particle>();
		int swarmSize = particles.size();
		int currentSize = 0;
		int id;

		while (currentSize < size) {

			id = (int) (Math.random() * (swarmSize - 1)) + 1;
			if (id > 0 && id < particles.size()) {
				selectedParticles.put(id, particles.get(id));
				currentSize++;
			}
		}

		return selectedParticles;
	}

	/**
	 * actualizeaza distanta particulelor care sunt influentate de ofertele unui
	 * server al DC
	 * 
	 * @param particles
	 *            particulele care sunt infulentate de counterDistance
	 *            (determinata de oferta unui sever al DC)
	 * @param compromise
	 *            factor de compromis (se modifica in functie de momentul
	 *            negocierii)
	 * @param counterVelocity
	 *            distanta calculalta pentru o oferta a unui server al DC
	 */
	public Map<Integer, Particle> alterDistance(
			Map<Integer, Particle> particles, float[] compromise,
			float[] counterDistance) {

		for (Map.Entry<Integer, Particle> currentParticle : particles
				.entrySet()) {

			float[] distance;
			distance = currentParticle.getValue().getDistance();

			for (int i = 0; i < Particle.nrResources; i++){
				float distance_;
				distance_ = distance[i] -  compromise[i] * counterDistance[i];
				while(distance_ < min[i] ){
					distance_ = (float) (distance[i] +  Math.random());
				}
				
				while(distance_ >max[i])
					distance_ = (float) (distance[i] -  Math.random());
				distance[i] = distance_;
			}
			


			currentParticle.getValue().setDistance(distance);
		}

		updateSwarm(particles);
		return particles;
	}
}
