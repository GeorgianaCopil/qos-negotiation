package client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import GA.Chromosome;
import java.util.PriorityQueue;

public class GeneticAlgorithmNegotiation {

	private List<Chromosome> population;
	private List<Chromosome> newPopulation;
	private List<Chromosome> offerPool;
	private long populationMaximumSize;
	private Chromosome bestIndividual;
	private int generation;
	private Chromosome goal;
	public static float[] minValues;
	public static float[] maxValues;
	private FitnessFunction fitness;
	private Crossover crossover;
	private Mutation mutation;
	private Selection selection;
            private float compromise = 0.05f;

	public GeneticAlgorithmNegotiation(long populationMaxmimunSize,
			float[] minValues, float[] maxValues) {

		population = new ArrayList<Chromosome>();
		newPopulation = new ArrayList<Chromosome>();
		offerPool = new ArrayList<Chromosome>();
		generation = 0;
		fitness = new FitnessFunction();
		crossover = new Crossover();
		mutation = new Mutation(0.007);
		selection = new Selection();
		this.populationMaximumSize = populationMaxmimunSize;
		GeneticAlgorithmNegotiation.minValues = minValues;
		GeneticAlgorithmNegotiation.maxValues = maxValues;

		goal = new Chromosome(maxValues[0], maxValues[1], maxValues[2], maxValues[3]);
		bestIndividual = randomChromosome();
		bestIndividual.setFitness(fitness.euclidianDistance(goal,
				bestIndividual));
		initializePopulation();
	}
   public void updateGoal(Chromosome offer){
      float newGoal =goal.getCpu()-compromise*offer.getCpu();
     if (newGoal>minValues[0])
       goal.setCpu(newGoal);
      newGoal = goal.getHdd()-compromise*offer.getHdd();
     if (newGoal>minValues[1])
     goal.setHdd(newGoal);
      newGoal = goal.getMemory()-compromise*offer.getMemory();
      if (newGoal>minValues[2])
     goal.setMemory(newGoal);
 //    System.err.println("Client Goal"+goal.toString());
  }
   public boolean checkChromosomeConsistency(Chromosome c){
       if (c.getCpu()>maxValues[0] || c.getCpu()<minValues[0])return false;
       if (c.getHdd()>maxValues[1] || c.getHdd()<minValues[1])return false;
       if (c.getMemory()>maxValues[2] || c.getMemory()<minValues[2]) return false;
       return true;
   }
    public Chromosome closestFromSubpopulation(List<Chromosome> list){
            double min = 100000;
            double dist = 0;
            Chromosome closest = null;
         
            for (Chromosome c:list){
                
                dist =  Math.sqrt(Math.pow(c.getCpu()-goal.getCpu(),2)+Math.pow(c.getHdd()-goal.getHdd(), 2)+Math.pow(c.getMemory()-goal.getMemory(),2));
                
                if (dist<min){
                    min = dist;
                    closest = c;
                }
            }
            closest.setFitness(new FitnessFunction().euclidianDistance(goal, closest));
            return closest;
        }
     public Chromosome getClosestIndividual(List<Chromosome> offers){
            double min =1000000;
            Chromosome closest = null;
            Chromosome c = null;
            for (Chromosome x:getPopulation()){
                 c = closestFromSubpopulation(offers);
                if (c.getFitness()<min){
                    min=c.getFitness();
                    closest=c;
                }
            }
            return closest;
        }
	public void initializePopulation() {
		setPopulation(new ArrayList<Chromosome>());
		getPopulation().add(bestIndividual);
		Chromosome chromosome;
		while (getPopulation().size() < populationMaximumSize) {
			chromosome = randomChromosome();
			getPopulation().add(chromosome);
		}
	}

	public Chromosome randomChromosome() {

		float hdd, cpu, memory, cost;
		Chromosome chromo;
		hdd = (float) (maxValues[0] - Math.random()
				* (maxValues[0] - minValues[0])*20/100);
		cpu = (float) (maxValues[1] - Math.random()
				* (maxValues[1] - minValues[1])*20/100);
		memory = (float) (maxValues[2] - Math.random()
				* (maxValues[2] - minValues[2])*20/100);
		cost = (float) (maxValues[3] - Math.random()
				* (maxValues[3] - minValues[3])*20/100);
		
		chromo = new Chromosome(hdd, cpu, memory, cost);

		chromo.setFitness(fitness.euclidianDistance(getGoal(), chromo));

		return chromo;
	}

	public Chromosome computeOffer(Chromosome offer, int time,
			double percent) {

		Chromosome offspring, parent1, parent2;

		
		long start = System.currentTimeMillis();
		//int number = (int) (population.size() * percent / offers.size());

		generation++;
			parent1 = offer;
			parent1.setFitness(fitness.euclidianDistance(getGoal(), parent1));
                int number = getPopulation().size();
		do {

			parent2 = selection.rankSelection(getPopulation());

			if (parent1.getFitness() > parent2.getFitness()) {
				offspring = crossover.heuristicCrossover(parent2, parent1);
				offerPool.add(parent2);
			} else {
				offerPool.add(parent1);
				offspring = crossover.heuristicCrossover(parent1, parent2);
			}
			number-=2;
			offspring.setFitness(fitness.euclidianDistance(getGoal(), offspring));
                        
                         Chromosome offspring2 = crossover.arithmeticCrossover(parent2, offer);
                        
                         offspring2.setFitness(fitness.euclidianDistance(getGoal(), offspring2));
			offerPool.add(offspring);
                      
                       offerPool.add(offspring2);
                       
		} while (System.currentTimeMillis() < start + time && number > 0);

		Chromosome selected = selection.getBestChromosome(offerPool);

		remove(offerPool.size());
		getPopulation().addAll(offerPool);

		return selected;
	}

	public void remove(int number) {
            PriorityQueue<Chromosome> pop = new PriorityQueue(population);
            float nr = (float) (0.1 * population.size());
            int index = 0; 
            for (Chromosome c: pop){
                if(index<nr)
               population.remove(c);
                index++;
            }
		

	}

	public void runAlgorithm(int time) {

		long start = System.currentTimeMillis();
		Chromosome parent1, parent2;
		Chromosome offspring1, offspring2;

		generation++;

		while (System.currentTimeMillis() < start + time) {

			parent1 = selection.rouletteWheelSelection(getPopulation());
			parent2 = selection.rouletteWheelSelection(getPopulation());

			if (parent1.getFitness() < bestIndividual.getFitness())
				bestIndividual = parent1;

			if (parent2.getFitness() < bestIndividual.getFitness())
				bestIndividual = parent2;

			if (parent1.getFitness() > parent2.getFitness())
				offspring2 = parent1;
			else
				offspring2 = parent2;

			offspring1 = crossover.heuristicCrossover(parent1, parent2);

			offspring1 = mutation.boundaryMutation(offspring1);
			offspring2 = mutation.boundaryMutation(offspring1);

			if (offspring1.getFitness() < bestIndividual.getFitness())
				bestIndividual = offspring1;

			if (offspring2.getFitness() < bestIndividual.getFitness())
				bestIndividual = offspring2;

			offspring1.setFitness(fitness.euclidianDistance(getGoal(), offspring1));

			offspring2.setFitness(fitness.euclidianDistance(getGoal(), offspring2));

			newPopulation.add(offspring1);
			newPopulation.add(offspring2);

		}

		getPopulation().clear();
		generation++;
		getPopulation().addAll(newPopulation);
		newPopulation.clear();
	}

    /**
     * @return the population
     */
    public List<Chromosome> getPopulation() {
        return population;
    }

    /**
     * @param population the population to set
     */
    public void setPopulation(List<Chromosome> population) {
        this.population = population;
    }

    /**
     * @return the goal
     */
    public Chromosome getGoal() {
        return goal;
    }

    /**
     * @param goal the goal to set
     */
    public void setGoal(Chromosome goal) {
        this.goal = goal;
    }

	

}