package genetic_algorithm;


public class Chromosome implements Comparable<Chromosome> {

	private float[] genes;
	private float fitness;
	public static final int genesNo = 4;
	
	public Chromosome(){
		
	}

	public Chromosome(float[] genes){

		super();
		this.genes = genes;
	}
	
	public float[] getGenes() {
		return genes;
	}

	public void setGenes(float[] genes) {
		this.genes = genes;
	}

	public float getFitness() {
		return fitness;
	}

	public void setFitness(float fitness) {
		this.fitness = fitness;
	}

	@Override
	public int compareTo(Chromosome chromo) {

		if (this.getFitness() > chromo.getFitness())
			return -1;
		else if (this.getFitness() < chromo.getFitness())
			return 1;
		return 0;
	}

}
