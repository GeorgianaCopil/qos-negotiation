package genetic_algorithm;

public class Crossover {

	private double crossoverRate;
	private float[] minValues;
	private float[] maxValues;

	/**
	 * this method computes and returns the offspring resulting from the uniform
	 * crossover between two individuals
	 * 
	 * 
	 * @param parent1
	 *            chromosome selected for cross-over
	 * @param parent2
	 *            chromosome selected for cross-over
	 * @return the offspring resulting from the uniform crossover between
	 *         parent1 and parent2
	 */
	public Chromosome uniformCrossover(Chromosome parent1, Chromosome parent2) {

		float[] genes = new float[Chromosome.genesNo];
		int random;

		if (Math.random() < crossoverRate) {

			for (int i = 0; i < Chromosome.genesNo; i++) {
				random = (int) Math.round(Math.random());

				genes[i] = random * parent1.getGenes()[i] + (1 - random)
						* parent2.getGenes()[i];

			}

			return new Chromosome(genes);
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
	 *            chromosome selected for crossover
	 * @param parent2
	 *            chromosome selected for crossover
	 * @return the offspring resulting from the arithmetic crossover between
	 *         parent1 and parent2
	 */
	public Chromosome arithmeticCrossover(Chromosome parent1, Chromosome parent2) {

		float[] genes = new float[Chromosome.genesNo];

		if (Math.random() < crossoverRate) {

			for (int i = 0; i < Chromosome.genesNo; i++)
				genes[i] = (parent1.getGenes()[i] + parent2.getGenes()[i]) / 2;
			return new Chromosome(genes);

		}
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
	 *            chromosome selected for crossover
	 * @param parent2
	 *            chromosome selected for crossover
	 * @return the offspring resulting from the heuristic crossover between
	 *         parent1 and parent2
	 */
	public Chromosome heuristicCrossover(Chromosome worstParent, Chromosome bestParent) {

		float[] genes = new float[Chromosome.genesNo];

		for (int i = 0; i < Chromosome.genesNo; i++) {
			genes[i] = (float) (Math.random()
					* (bestParent.getGenes()[i] - worstParent
							.getGenes()[i]) + worstParent.getGenes()[i]);
			if (genes[i] < minValues[i])
				genes[i] = minValues[i];
			if (genes[i] > maxValues[i])
				genes[i] = maxValues[i];
		}

		return new Chromosome(genes);

	}

	public double getCrossoverRate() {
		return crossoverRate;
	}

	public void setCrossoverRate(double crossoverRate) {
		this.crossoverRate = crossoverRate;
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
