package GA;

import java.io.Serializable;

public class Chromosome implements Serializable, Comparable<Object>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static int nrResources = 4;
	
	private float hdd;
	private float cpu;
	private float memory;
	private float fitness;
	private float cost;
	
	private float memoryP;
	private float cpuP;
	private float costP;
	private float hddP;
	

	public Chromosome(float hdd, float cpu, float memory, float cost) {
		super();
		this.hdd = hdd;
		this.cpu = cpu;
		this.memory = memory;
		this.cost = cost;
	}

	public Chromosome() {
	}

	public float getHdd() {
		return hdd;
	}

	public void setHdd(float hdd) {
		this.hdd = hdd;
	}

	public float getCpu() {
		return cpu;
	}

	public void setCpu(float cpu) {
		this.cpu = cpu;
	}

	public float getMemory() {
		return memory;
	}

	public void setMemory(float memory) {
		this.memory = memory;
	}

	public float getFitness() {
		return fitness;
	}

	public void setFitness(float fitness) {
		this.fitness = fitness;
	}

	@Override
	public String toString() {
		return "Chromosome [hdd=" + hdd + ", cpu=" + cpu + ", memory=" + memory
				+ ", cost=" + cost + "]";
	}
	 public double distanceToChromosome(Chromosome c){
            return Math.sqrt(Math.pow(c.getCpu()-cpu,2)+Math.pow(c.getHdd()-hdd, 2)+Math.pow(c.getMemory()-memory,2)+
            		Math.pow(c.getCost() - cost, 2));
        }
       
 
    public int compare(Object o1, Object o2) {
        Chromosome c1 = (Chromosome) o1;
        Chromosome c2 = (Chromosome) o2;
        if (c1.getFitness()>c2.getFitness()) return 1;
        else
            if (c1.getFitness()<c2.getFitness()) return -1;
         return 0;
    }

    public int compareTo(Object o) {
 
        Chromosome c2 = (Chromosome) o;
        if (this.getFitness()>c2.getFitness()) return -1;
        else
            if (this.getFitness()<c2.getFitness()) return 1;
         return 0;
    }

	public void setCost(float cost) {
		this.cost = cost;
	}

	public float getCost() {
		return cost;
	}
	
	public float getMemoryP() {
		return memoryP;
	}

	public void setMemoryP(float memoryP) {
		this.memoryP = memoryP;
	}

	public float getCpuP() {
		return cpuP;
	}

	public void setCpuP(float cpuP) {
		this.cpuP = cpuP;
	}

	public float getCostP() {
		return costP;
	}

	public void setCostP(float costP) {
		this.costP = costP;
	}

	public float getHddP() {
		return hddP;
	}

	public void setHddP(float hddP) {
		this.hddP = hddP;
	}

	

}
