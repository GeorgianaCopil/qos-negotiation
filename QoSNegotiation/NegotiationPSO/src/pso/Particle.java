package pso;

public class Particle {

	public static int nrResources = 4;
	private float fitness;
	private float[] pBest;
	private float[] velocity;
	private float[] distance;

	public Particle() {

	}

//	public void updatePBest(Particle gBest, float[] pBest) {
//
//		if (euclidianDistance(gBest, pBest) > euclidianDistance(gBest,
//				this.distance))
//			setPbest(this.distance);
//	}

	public float getFitness() {
		return fitness;
	}

	public void setFitness(float fitness) {
		this.fitness = fitness;
	}

	public void setPbest(float[] pbest) {
		this.pBest = pbest;
	}

	public float[] getPbest() {
		return pBest;
	}

	public void setVelocity(float[] velocity) {
		this.velocity = velocity;
	}

	public float[] getVelocity() {
		return velocity;
	}

	public float euclidianDistance(Particle gBest, float[] curr) {

		float distance;
		distance = (float) Math.pow((curr[0] - gBest.getDistance()[0]), 2)
				+ (float) Math.pow((curr[1] - gBest.getDistance()[1]), 2)
				+ (float) Math.pow((curr[2] - gBest.getDistance()[2]), 2);

		return (float) Math.sqrt(distance);

	}

	public void setDistance(float[] distance) {
		this.distance = distance;

	}

	public float[] getDistance() {
		return distance;
	}

}
