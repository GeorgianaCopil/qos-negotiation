package client;

import java.util.ArrayList;
import java.util.List;

import PSO.Particle;

import negotiation.Offer;

public class Fitness {

	private List<Offer> offers;
	private float[] max;
	private float[] min;
	private float[] weight;

	public Fitness() {

		this.offers = new ArrayList<Offer>();
		this.weight = new float[Particle.nrResources];

		weight[0] = 1;
		weight[1] = 1;
		weight[2] = 1;
		weight[3] = 2;
	}

	public void rateOffer(Offer offer) {

		offer.setHddP((offer.getHdd() - min[0]) / (max[0] - min[0]));
		offer.setCpuP((offer.getCpu() - min[1]) / (max[1] - min[1]));
		offer.setMemoryP((offer.getMemory() - min[2]) / (max[2] - min[2]));
		offer.setCostP(1-(offer.getCost() - min[3]) / (max[3] - min[3]));

		offer.setFitness(offerPercent(offer));

		offers.add(offer);

	}

	private float offerPercent(Offer offer) {

		return (offer.getHddP() * weight[0] + offer.getCpuP() * weight[1]
				+ offer.getMemoryP() * weight[2] )
				/ (weight[1] + weight[2] + weight[0]);

	}

	@SuppressWarnings("unused")
	private float eucliadianDIstance(Offer offer) {

		return (float) Math.sqrt(Math.pow(offer.getHdd() - max[0], 2.0)
				* weight[0] + Math.pow(offer.getCpu() - max[1], 2.0)
				* weight[1] + Math.pow(offer.getMemory() - max[2], 2.0)
				* weight[2] + Math.pow(offer.getCost() - max[3], 2.0));
	}

	@SuppressWarnings("unused")
	private float cebishevDistance(Offer offer) {
		return Math.max(
				Math.max(Math.abs(offer.getHdd() - max[0]) * weight[0],
						Math.abs(offer.getCpu() - max[1]) * weight[1]),
				Math.abs(offer.getMemory() - max[2]) * weight[2]);
	}

	public float[] getMax() {
		return max;
	}

	public void setMax(float[] max) {
		this.max = max;
	}

	public void setWeight(float[] weight) {
		this.weight = weight;
	}

	public float[] getWeight() {
		return weight;
	}

	public float[] getMin() {
		return min;
	}

	public void setMin(float[] min) {
		this.min = min;
	}

}
