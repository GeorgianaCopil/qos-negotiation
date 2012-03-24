package negotiation;

import java.io.Serializable;

import genetic_algorithm.Chromosome;

public class Offer implements Serializable {
	
	private static final long serialVersionUID = -6070662293971298760L;
	private float fitness;
	private float[] resources;
	

	public float getFitness() {
		return fitness;
	}


	public void setFitness(float fitness) {
		this.fitness = fitness;
	}

	
	
	public Chromosome toChromosome(){
		
		return new Chromosome(this.resources);
		
	}


	public float[] getResources() {
		return resources;
	}


	public void setResources(float[] resources) {
		this.resources = resources;
	}
	
	@Override
	public String toString(){
		
		StringBuffer offer = new StringBuffer();
		
		offer.append("Offer: ");
		for(int i = 0; i< resources.length; i++)
			offer.append(resources[i]+ " ");
		
		
		return  offer.toString();
	}
	
	
}
