package server;

public class RelativeDistance {

	private float hddDistance;
	private float cpuDistance;
	private float memoryDistance;

	public RelativeDistance(float hddDistance, float cpuDistance,
			float memoryDistance) {
		super();
		this.setHddDistance(hddDistance);
		this.setCpuDistance(cpuDistance);
		this.setMemoryDistance(memoryDistance);
	}

	public void setHddDistance(float hddDistance) {
		this.hddDistance = hddDistance;
	}

	public float getHddDistance() {
		return hddDistance;
	}

	public void setCpuDistance(float cpuDistance) {
		this.cpuDistance = cpuDistance;
	}

	public float getCpuDistance() {
		return cpuDistance;
	}

	public void setMemoryDistance(float memoryDistance) {
		this.memoryDistance = memoryDistance;
	}

	public float getMemoryDistance() {
		return memoryDistance;
	}

}
