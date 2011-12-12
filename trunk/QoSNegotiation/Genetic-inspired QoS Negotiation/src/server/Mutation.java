package server;

import GA.Chromosome;

public class Mutation {

	private double mutationRate;

	public Mutation(double mutationRate) {
		this.mutationRate = mutationRate;
	}

	public Chromosome boundaryMutation(Chromosome chromosome) {
		
		int rand = (int) Math.round(Math.random() * 3);
		if (Math.round(Math.random()) <= mutationRate) {
			if (rand == 0)
				if (Math.random() % 2 == 0)
					chromosome.setHdd(GeneticAlgorithmNegotiation.minValues[0]);
				else
					chromosome.setHdd(GeneticAlgorithmNegotiation.maxValues[0]);
			if (rand == 1)
				if (Math.random() % 2 == 0)
					chromosome.setCpu(GeneticAlgorithmNegotiation.minValues[1]);
				else
					chromosome.setCpu(GeneticAlgorithmNegotiation.maxValues[1]);
			if (rand == 2)
				if (Math.random() % 2 == 0)
					chromosome.setMemory(GeneticAlgorithmNegotiation.minValues[2]);
				else
					chromosome.setMemory(GeneticAlgorithmNegotiation.maxValues[2]);
		}

		return chromosome;
	}

	public Chromosome uniformMutation(Chromosome chromosome) {
		int rand = (int) Math.round(Math.random() * 3);
		if (Math.round(Math.random()) <= mutationRate) {
			if (rand == 0)
				chromosome
						.setHdd((float) Math.random()
								* (GeneticAlgorithmNegotiation.maxValues[0] - GeneticAlgorithmNegotiation.maxValues[0]));
			if (rand == 1)
				chromosome
						.setCpu((float) Math.random()
								* (GeneticAlgorithmNegotiation.maxValues[1] - GeneticAlgorithmNegotiation.maxValues[1]));

			if (rand == 2)

				chromosome
						.setMemory((float) Math.random()
								* (GeneticAlgorithmNegotiation.maxValues[2] - GeneticAlgorithmNegotiation.maxValues[2]));

		}

		return chromosome;
	}

}
