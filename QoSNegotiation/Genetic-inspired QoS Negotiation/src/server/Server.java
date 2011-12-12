package server;

import GA.Chromosome;


public class Server {
	
	private GeneticAlgorithmNegotiation negotiation;
	@SuppressWarnings("unused")
	private float[] maxValues;
	@SuppressWarnings("unused")
	private float[] minValues;
	@SuppressWarnings("unused")
	private int size;
	int time;
	
	public Server(int time, float maxValues[], float minValues[], int size){
		
		this.maxValues = maxValues;
		this.minValues = minValues;
		this.size = size;
		this.time = time;
		
		negotiation = new GeneticAlgorithmNegotiation(size, minValues, maxValues);
                negotiation.setGoal(new Chromosome (minValues[0],minValues[1],minValues[2], maxValues[3]));
	}
	
	
	public boolean acceptOffer(Chromosome offer){
		
		
		if(offer.getFitness() < 30)
			return true;
		
		return false;
	}
	
	public Chromosome computeCounterOffer(Chromosome offer){
		
		return getNegotiation().computeOffer(offer, time, 0.1);
		
	}
	
	public void waitForOffer(){
		
		getNegotiation().runAlgorithm(time);
	}

    /**
     * @return the negotiation
     */
    public GeneticAlgorithmNegotiation getNegotiation() {
        return negotiation;
    }

    /**
     * @param negotiation the negotiation to set
     */
    public void setNegotiation(GeneticAlgorithmNegotiation negotiation) {
        this.negotiation = negotiation;
    }
	

}
