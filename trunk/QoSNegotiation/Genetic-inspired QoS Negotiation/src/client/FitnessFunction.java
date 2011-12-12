package client;

import GA.Chromosome;


public class FitnessFunction {
	

	public float percents(Chromosome goal, Chromosome chromosome){
		
		float hdd, cpu, memory;
		hdd = chromosome.getHdd()*100/goal.getHdd();
		cpu = chromosome.getCpu()*100/goal.getCpu();
		memory = chromosome.getMemory()*100/goal.getMemory();
		
		return (hdd+memory+cpu)/3;
	}	

	public float euclidianDistance(Chromosome goal, Chromosome chromosome){
		
		float hdd, cpu,  memory;
		hdd = chromosome.getHdd()-goal.getHdd();
		memory = chromosome.getMemory() - goal.getMemory();
		cpu = chromosome.getCpu() - goal.getCpu();
		
		return (float) Math.sqrt(Math.pow(hdd, 2)+ Math.pow(memory, 2)+ Math.pow(cpu, 2));
		
	}
	
	public float cebisevDistance(Chromosome goal, Chromosome chromosome){
		
		float hddDist, cpuDist, memoryDist;
		
		hddDist = Math.abs(chromosome.getHdd()-goal.getHdd());
		memoryDist = Math.abs(chromosome.getMemory() - goal.getMemory());
		cpuDist = Math.abs(chromosome.getCpu() - goal.getCpu());
		
		return Math.max(Math.max(hddDist, memoryDist), cpuDist);
	}

}
