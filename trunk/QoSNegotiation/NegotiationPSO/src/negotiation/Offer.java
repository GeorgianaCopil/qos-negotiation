package negotiation;

import java.io.Serializable;


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
		for(int i = 1; i< resources.length; i++)
			offer.append(resources[i]+ " ");
		
		
		return  offer.toString();
	}
	
	
}
