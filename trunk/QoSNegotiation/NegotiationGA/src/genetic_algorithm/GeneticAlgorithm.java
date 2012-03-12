package genetic_algorithm;

import java.util.ArrayList;

public class GeneticAlgorithm {

	private int populationSize;
	private ArrayList<Chromosome> population;
	
	private int generation;

	private Selection selection;
	private Mutation mutation;
	private double mutationRate;
	private Crossover crossover;
	private double crossoverRate;

	private float[] minValues;
	private float[] maxValues;

	private Chromosome goal;
	
	public GeneticAlgorithm(float[] minValues, float maxValues[],
			int populationSize) {

		this.selection = new Selection();
		this.crossover = new Crossover();
		this.mutation = new Mutation();

		this.minValues = minValues;
		this.maxValues = maxValues;

		this.populationSize = populationSize;
		this.population = new ArrayList<Chromosome>();
		
		generation = 0;

	}

	public void inititializePopulation(float percent) {
		
		
		while(population.size() < populationSize){
			
			population.add(generateChromosome(percent));
		}

	}

	public Chromosome generateChromosome(float percent) {

		float[] genes = new float[Chromosome.genesNo];

		for (int i = 0; i < Chromosome.genesNo; i++)
			genes[i] = (float) (maxValues[i] - Math.random()
					* (maxValues[i] - minValues[i]))
					* percent;
		
		//TODO rate chromosome

		return new Chromosome(genes);
	}
	
	public void evolve(){
		
		Chromosome parent1, parent2;
		Chromosome worstParent, bestParent;
		Chromosome offspring;
		
		generation++;
		
		parent1 = selection.rouletteWheelSelection(population);
		parent2 = selection.rouletteWheelSelection(population);
		

		if (parent1.getFitness() < parent2.getFitness()) {
			bestParent = parent2;
			worstParent = parent1;
		} else {
			bestParent = parent1;
			worstParent = parent2;
		}
		
		offspring = crossover.heuristicCrossover(worstParent, bestParent);
		offspring = mutation.uniformMutation(offspring);
		
		//TODO rate offspring
		
		population.remove(worstParent);
		population.add(offspring);
		
		
	}

	public Chromosome alterPopulation(Chromosome chromosome, int numberOfSelectedChromosomes){
		
		Chromosome parent;
		Chromosome offspring;
		ArrayList<Chromosome> chromoPool = new ArrayList<Chromosome>();
		
		do{
			
			parent = selection.rouletteWheelSelection(population);
			population.remove(parent);
			
			offspring = crossover.arithmeticCrossover(chromosome, parent);
			//TODO rate chromo
			chromoPool.add(offspring);
			
			numberOfSelectedChromosomes--;
			
		}while(numberOfSelectedChromosomes > 0);
		
		Chromosome selectedChromosome = selection.rouletteWheelSelection(chromoPool);
		
		return selectedChromosome;
	}
	
	public void chromosomeConsistency(Chromosome chromo){
		
		float[] genes = new float[Chromosome.genesNo];
		
		for(int i = 0; i < Chromosome.genesNo; i++){
			
			genes[i] = chromo.getGenes()[i];
			if(chromo.getGenes()[i] < minValues[i] || chromo.getGenes()[i]>maxValues[i])
				genes[i] = (float) (Math.random()*(minValues[i] + (maxValues[i]-minValues[i])));
		}
		
		chromo.setGenes(genes);
		
		//TODO rate chromosome
		
	}

	public int getGeneration() {
		return generation;
	}

	public void setGeneration(int generation) {
		this.generation = generation;
	}

	public double getMutationRate() {
		return mutationRate;
	}

	public void setMutationRate(double mutationRate) {
		this.mutationRate = mutationRate;
	}

	public double getCrossoverRate() {
		return crossoverRate;
	}

	public void setCrossoverRate(double crossoverRate) {
		this.crossoverRate = crossoverRate;
	}

	public Chromosome getGoal() {
		return goal;
	}

	public void setGoal(Chromosome goal) {
		this.goal = goal;
	}
	
	
}
