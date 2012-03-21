package genetic_algorithm;

public class Fitness {
	
	private float[] minValues;
	private float[] maxValues;
	
	public float percents(float[] genes, float[] weights){
		
		float fitness = 0;
		
		for(int i = 0; i < genes.length; i++)
			fitness+= (genes[i] - minValues[i])/(maxValues[i]-minValues[i])*weights[i];
		
		return fitness;	
	}
	
	public float euclidinaDistance(float[] genes, float[] goal){
		
		float distance = 0;
		
		for(int i = 0; i < genes.length; i++)
			distance+= Math.pow(goal[i] - genes[i], 2.0);
		
		return (float)Math.sqrt(distance);
	}
	
	public float cebisevDistance(float[] genes, float[] goal){
		
		float max = -1;
		for(int i = 0; i < genes.length; i++)
			if(max < Math.abs(goal[i]- genes[i]))
				max = Math.abs(goal[i]- genes[i]);
		
		return max;
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
	
	
}
