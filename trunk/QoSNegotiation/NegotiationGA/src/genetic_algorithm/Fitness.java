package genetic_algorithm;

public class Fitness {
	
	private float[] minValues;
	private float[] maxValues;
	private float[] goal;
	
	public float percents(float[] gene, float[] weight){
		
		float fitness = 0;
		
		
		float[] genePercent = new float[Chromosome.genesNo];
		for(int i = 0; i < gene.length; i++){
			
			genePercent[i] = (gene[i] -  minValues[i])/(maxValues[i] - minValues[i]);
			
			if(goal[i] == minValues[i]){
				genePercent[i] = 1 - genePercent[i];
			}
		}
		
		for(int i = 0; i < genePercent.length; i++)
			fitness+= genePercent[i]*weight[i];
		
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

	public float[] getGoal() {
		return goal;
	}

	public void setGoal(float[] goal) {
		this.goal = goal;
	}
	
	
	
	
}
