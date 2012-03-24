package genetic_algorithm;

import java.util.ArrayList;
import java.util.Iterator;

public class GeneticAlgorithm {

	private int populationSize;
	private ArrayList<Chromosome> population;
	
	private int generation;

	private Selection selection;
	private Mutation mutation;
	private double mutationRate;
	private Crossover crossover;
	private double crossoverRate;
	private Fitness fitness;

	private float[] minValues;
	private float[] maxValues;
	private float[] weights;
	
	private Chromosome goal;
	
	public GeneticAlgorithm(float[] minValues, float maxValues[], float[] weights,
			int populationSize) {
		
		this.minValues = minValues;
		this.maxValues = maxValues;
		this.weights = weights;

		this.selection = new Selection();
		
		this.crossover = new Crossover();
		crossover.setMinValues(minValues);
		crossover.setMaxValues(maxValues);
		crossover.setCrossoverRate(1);
		
		this.mutation = new Mutation();
		mutation.setMinValues(minValues);
		mutation.setMaxValues(maxValues);
		mutation.setMutationRate(0.07);
		
		fitness = new Fitness();
		fitness.setMaxValues(maxValues);
		fitness.setMinValues(minValues);
		
		this.populationSize = populationSize;
		this.population = new ArrayList<Chromosome>();
		
		generation = 0;
		inititializePopulation(1);
	}

	public void inititializePopulation(float percent) {
		
		
		while(population.size() < populationSize){
			
			population.add(generateChromosome(percent));
		}

	}

	public Chromosome generateChromosome(float percent) {

		float[] genes = new float[Chromosome.genesNo];

		for (int i = 0; i < Chromosome.genesNo; i++)
			genes[i] = (float) (minValues[i] + Math.random()
					* (maxValues[i] - minValues[i])*percent);
		
		Chromosome chromo = new Chromosome(genes);
			
		chromo.setFitness(fitness.percents(chromo.getGenes(), weights));

		return chromo;
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
		
		offspring.setFitness(fitness.percents(offspring.getGenes(), weights));
		
		population.remove(worstParent);
		population.add(offspring);
		
		
	}

	public Chromosome alterPopulation(Chromosome chromosome, int numberOfSelectedChromosomes){
		
		Chromosome parent;
		Chromosome offspring;
		ArrayList<Chromosome> chromoPool = new ArrayList<Chromosome>();
		
		do{
			
			parent = selection.rouletteWheelSelection(population);
			
			
			offspring = crossover.heuristicCrossover(chromosome, parent);
			offspring.setFitness(fitness.percents(offspring.getGenes(), weights));
			chromoPool.add(offspring);
				
			population.remove(parent);
			
			numberOfSelectedChromosomes--;
			
		}while(numberOfSelectedChromosomes > 0);
		
	//	Chromosome selectedChromosome = selection.rouletteWheelSelection(chromoPool);
		Chromosome selectedChromosome = averageChromosome(chromoPool);
		population.addAll(chromoPool);
		
		
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
		
		chromo.setFitness(fitness.percents(chromo.getGenes(), weights));
		
	}
	
	public Chromosome averageChromosome(ArrayList<Chromosome> chromosomes){
		
		Iterator<Chromosome> chromosomesIterator =chromosomes.iterator(); 
		
		float[] averageGenes = new float[Chromosome.genesNo];
		float[] genes;
		
		int size = chromosomes.size();
		
		for(int i = 0; i< averageGenes.length; i++)
			averageGenes[i] = 0;
		
		while(chromosomesIterator.hasNext()){
			
			genes = chromosomesIterator.next().getGenes();
			for(int i = 0; i< genes.length; i++)
				averageGenes[i] = averageGenes[i] + genes[i]/size;
		}
		
		return new Chromosome(averageGenes);
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
