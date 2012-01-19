package PSO;

import java.util.HashMap;
import java.util.Map;


public class PSO_subswarm {
	
	private float[] maxValues;
	private float[] minValues;
	private int noServers;
	private Map<Integer, PSO> algorithms;
	
	public PSO_subswarm(float[] max, float[] min, int noServers){
		
		maxValues = max;
		minValues = min;
		this.noServers = noServers;
		algorithms = new HashMap<Integer, PSO>();
	}
	
	public void initializeSubSwarms(int size){
		
		for(int i = 1; i <= noServers; i++){
			
			PSO pso = new PSO();
			pso.initializeSwarm(size, minValues, maxValues, maxValues);
			algorithms.put(i, pso);
		}
			
	}
	
	public void updateSwarm(float c1, float c2, float W){
		
		for (Map.Entry<Integer, PSO> subSwarm : algorithms.entrySet()){
			
			subSwarm.getValue().alterParticles(subSwarm.getValue().getParticles(), c1, c2, W);
		}
	}
	
	public Map<Integer, Particle> updateDistances(float[] compromise, Map<Integer, Particle> counterDistance, int size){
			
		Map<Integer, Particle> averageP = new HashMap<Integer, Particle>();
		for (Map.Entry<Integer, PSO> subSwarm : algorithms.entrySet()){
			
			Map<Integer, Particle> selectedParticles;
			selectedParticles = subSwarm.getValue().selectParticles(size);
			//subSwarm.getValue().alterVelocities(selectedParticles, compromise, counterDistance.get(subSwarm.getKey()).getDistance());
			Particle p = subSwarm.getValue().averageParticle(selectedParticles);
			averageP.put(subSwarm.getKey(), p);
		}
		return averageP;
	}
	

	

	public float[] getMaxValues() {
		return maxValues;
	}

	public void setMaxValues(float[] maxValues) {
		this.maxValues = maxValues;
	}

	public float[] getMinValues() {
		return minValues;
	}

	public void setMinValues(float[] minValues) {
		this.minValues = minValues;
	}

	public int getNoServers() {
		return noServers;
	}

	public void setNoServers(int noServers) {
		this.noServers = noServers;
	}
	
	
	

}
