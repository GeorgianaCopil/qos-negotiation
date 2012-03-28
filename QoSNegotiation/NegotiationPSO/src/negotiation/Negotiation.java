package negotiation;

public interface Negotiation {

	public boolean acceptOffer(Offer offer);
	
	public Offer computeCounterOffer(Offer offer);
	
	public void waitForOffer();
	
}
