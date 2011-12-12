package client;

import GA.Chromosome;


public class Crossover {

	private double crossoverRate;
	

	/**
	 * this method computes and returns the offspring resulting from the uniform
	 * crossover between two individuals
	 * 
	 * 
	 * @param parent1
	 *            the first parent selected for crossover
	 * @param parent2
	 *            the second parent selected for crossover
	 * @return the offspring resulting from the uniform crossover between
	 *         parent1 and parent2
	 */
	public Chromosome uniformCrossover(Chromosome parent1, Chromosome parent2) {

		float hdd, cpu, memory, cost;
		int random;

		if (Math.random() < crossoverRate) {
			random = (int) Math.round(Math.random());
			hdd = random * parent1.getHdd() + (1 - random) * parent2.getHdd();

			random = (int) Math.round(Math.random());
			cpu = random * parent1.getCpu() + (1 - random) * parent2.getCpu();

			random = (int) Math.round(Math.random());
			memory = random * parent1.getMemory() + (1 - random)
					* parent2.getMemory();
			
			random = (int) Math.round(Math.random());
			cost = random * parent1.getCost() + (1 - random)
			* parent2.getCost();

			return new Chromosome(hdd, cpu, memory, cost);
		}
		return parent1;
	}

	/**
	 * this method computes and returns the offspring resulted from the
	 * arithmetic crossover between two individuals
	 * 
	 * resource(offspring) = (resource(parent1)+resource(parent2))/2;
	 * 
	 * @param parent1
	 *            the first parent selected for crossover
	 * @param parent2
	 *            the second parent selected for crossover
	 * @return the offspring resulting from the arithmetic crossover between
	 *         parent1 and parent2
	 */
	public Chromosome arithmeticCrossover(Chromosome parent1, Chromosome parent2) {

		if (Math.random() < crossoverRate)
			return new Chromosome((parent1.getHdd() + parent2.getHdd()) / 2,
					(parent1.getCpu() + parent2.getCpu()) / 2,
					(parent1.getMemory() + parent2.getMemory()) / 2,
					(parent1.getCost() + parent2.getCost()) / 2);
		return parent1;
	}

	/**
	 * this method computes and returns the offspring resulted from the
	 * arithmetic crossover between two individuals
	 * 
	 * resource(offspring) =
	 * resource(worstParent)+random*(resource(bestParent)-resource
	 * (worstParent));
	 * 
	 * @param parent1
	 *            the first parent selected for crossover
	 * @param parent2
	 *            the second parent selected for crossover
	 * @return the offspring resulting from the heuristic crossover between
	 *         parent1 and parent2
	 */
	public Chromosome heuristicCrossover(Chromosome parent1, Chromosome parent2) {

		float hdd, cpu, memory, cost;

		
			hdd = (float) (Math.random()
					* (parent1.getHdd() - parent2.getHdd()) + parent2.getHdd());
			if (hdd < GeneticAlgorithmNegotiation.minValues[0])
				hdd = GeneticAlgorithmNegotiation.minValues[0];
			if (hdd > GeneticAlgorithmNegotiation.maxValues[0])
				hdd = GeneticAlgorithmNegotiation.maxValues[0];
			cpu = (float) (Math.random()
					* (parent1.getCpu() - parent2.getCpu()) + parent2.getCpu());
			if (cpu < GeneticAlgorithmNegotiation.minValues[1])
				cpu = GeneticAlgorithmNegotiation.minValues[1];
			if (cpu > GeneticAlgorithmNegotiation.maxValues[1])
				cpu = GeneticAlgorithmNegotiation.maxValues[1];
			memory = (float) (Math.random()
					* (parent1.getMemory() - parent2.getMemory()) + parent2
					.getMemory());
			if (memory < GeneticAlgorithmNegotiation.minValues[2])
				memory = GeneticAlgorithmNegotiation.minValues[2];
			if (memory > GeneticAlgorithmNegotiation.maxValues[2])
				memory = GeneticAlgorithmNegotiation.maxValues[2];
			
			cost = (float) (Math.random()
					* (parent1.getCost() - parent2.getCost()) + parent2
					.getCost());
			if (cost < GeneticAlgorithmNegotiation.minValues[3])
				cost = GeneticAlgorithmNegotiation.minValues[3];
			if (cost > GeneticAlgorithmNegotiation.maxValues[3])
				cost = GeneticAlgorithmNegotiation.maxValues[3];

			return new Chromosome(hdd, cpu, memory, cost);
		
	}
}

