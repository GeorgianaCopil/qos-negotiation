package negotiation;

public class Fitness {
	
	private float[] goal;
	private float[] resourceWeight; 
	private float[] minValues;
	private float[] maxValues;

	public void rateOffer(Offer offer){


		float[] resources = offer.getResources();
		float[] resourcesPercent = new float[resources.length];
		float fitness = 0;
		
		for(int i = 0; i < resources.length; i++){
		
			resourcesPercent[i] = (resources[i] -  minValues[i])/(maxValues[i] - minValues[i]);
			
			if(goal[i] == minValues[i])
				resourcesPercent[i] = 1 - resourcesPercent[i];
		}
		
		for(int i = 0; i < resourcesPercent.length; i++)
			fitness+= resourcesPercent[i]*resourceWeight[i];
		
		offer.setFitness(Math.abs(fitness));
	}

	
	public float[] getGoal() {
		return goal;
	}

	public void setGoal(float[] goal) {
		this.goal = goal;
	}

	public float[] getResourceWeight() {
		return resourceWeight;
	}

	public void setResourceWeight(float[] resourceWeight) {
		this.resourceWeight = resourceWeight;
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
