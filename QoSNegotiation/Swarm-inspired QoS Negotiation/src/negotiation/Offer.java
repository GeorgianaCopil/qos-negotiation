package negotiation;

import java.io.Serializable;

public class Offer implements Serializable{
	
	
	private static final long serialVersionUID = -7123008901457412911L;
	private float cpu, memory, hdd, cost;
	private float cpuP, memoryP, hddP, costP;
	private float fitness;

	
	public float getCpu() {
		return cpu;
	}

	public float getMemory() {
		return memory;
	}

	public float getHdd() {
		return hdd;
	}

	public void setCpu(float cpu) {
		this.cpu = cpu;
	}

	public void setMemory(float memory) {
		this.memory = memory;
	}

	public void setHdd(float hdd) {
		this.hdd = hdd;
	}

	@Override
	public String toString() {
		return "Offer [cpu=" + cpu + ", memory=" + memory + ", hdd=" + hdd
				+ ", cost="+cost+", fitness= "+fitness+"]";
	}

	public void setMemoryP(float memoryP) {
		this.memoryP = memoryP;
	}

	public float getMemoryP() {
		return memoryP;
	}

	public void setCpuP(float cpuP) {
		this.cpuP = cpuP;
	}

	public float getCpuP() {
		return cpuP;
	}

	public void setHddP(float hddP) {
		this.hddP = hddP;
	}

	public float getHddP() {
		return hddP;
	}

	public void setFitness(float fitness) {
		this.fitness = fitness;
	}

	public float getFitness() {
		return fitness;
	}

	public void setCostP(float costP) {
		this.costP = costP;
	}

	public float getCostP() {
		return costP;
	}

	public void setCost(float cost) {
		this.cost = cost;
	}

	public float getCost() {
		return cost;
	}
	
	
}
