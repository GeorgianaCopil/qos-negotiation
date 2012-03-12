package genetic_algorithm;

import genetic_algorithm.Chromosome;

public class Mutation {

	private double mutationRate;
	private float[] minValues;
	private float[] maxValues;

	public Mutation() {
		
	}

	public Chromosome boundaryMutation(Chromosome chromosome) {
		
		int rand = (int) Math.round(Math.random() * Chromosome.genesNo);
		float[] genes = chromosome.getGenes();
		if (Math.round(Math.random()) <= mutationRate) {
			
			for(int i = 0; i < Chromosome.genesNo; i++)
				if(rand == i)
					if((Math.random()*10)%2==0)
						genes[i] = minValues[i];
					else
						genes[i] = maxValues[i];
			return new Chromosome(genes);
		}

		return chromosome;
	}

	
	public Chromosome uniformMutation(Chromosome chromosome) {
		
		int rand = (int) Math.round(Math.random() * Chromosome.genesNo);
		float[] genes = chromosome.getGenes();
		if (Math.round(Math.random()) <= mutationRate) {
			
			for(int i = 0; i<Chromosome.genesNo; i++)
				if(rand == i)
					genes[i] = (float) Math.random()*(maxValues[i] - minValues[i]);

			return new Chromosome(genes);
		}

		return chromosome;
	}

	public double getMutationRate() {
		return mutationRate;
	}

	public void setMutationRate(double mutationRate) {
		this.mutationRate = mutationRate;
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
