package client;

import java.util.Iterator;
import java.util.List;

import GA.Chromosome;
import java.util.ArrayList;
import java.util.PriorityQueue;


public class Selection {

	public Chromosome rouletteWheelSelection(List<Chromosome> population) {

		 float sum = 0;
	     Iterator<Chromosome> it = population.iterator();
	       
	        while (it.hasNext())
	            sum += ((Chromosome) it.next()).getFitness();
	  
		
		float random = (float) (Math.random() * sum);
		float s = 0;
		Iterator<Chromosome> iterator = population.iterator();
		Chromosome chromo = null;
		while ( iterator.hasNext() && (chromo=iterator.next())!=null && s < random) {
                        chromo = (Chromosome) iterator.next();
			s += chromo.getFitness();
		}
		return chromo;


	}

	 public Chromosome rankSelection(List<Chromosome> population) {
        PriorityQueue<Chromosome> orderedPopulation = new PriorityQueue<Chromosome>();
        for (Chromosome c:population){
            orderedPopulation.add(c);
        }
        ArrayList< ArrayList<Chromosome> > list = new ArrayList<ArrayList<Chromosome>>();
        int nr = 0;

        int current=0;
        for (Chromosome c:orderedPopulation){
        if (current==0){
            ArrayList<Chromosome> myList = new ArrayList<Chromosome>();
            myList.add(c);
            list.add(myList);
             current++;
        }else{
            if (list.get(nr).get(0).getFitness()!=c.getFitness())
            {
              ArrayList<Chromosome> myList = new ArrayList<Chromosome>();
              myList.add(c);
              list.add(myList);
              nr++;  
            }else{
               list.get(nr).add(c);                          
            }
        }
           
        }
        int sel = (int) (Math.random()*nr);
        ArrayList<Chromosome> selected = list.get(sel);
        return selected.get((int)Math.random()*selected.size());
    }
public Chromosome averageChromosome(List<Chromosome> population){
        float cpu = 0;
        float mem = 0;
        float hdd = 0;
        float cost = 0;
        
        for (Chromosome c: population){
            cpu += c.getCpu();
            mem += c.getMemory();
            hdd += c.getHdd();
            cost +=c.getCost();
            
        }
        Chromosome c= new Chromosome();
        c.setCpu(cpu/population.size());
        c.setMemory(mem/population.size());
        c.setHdd(hdd/population.size());
        c.setCost(cost/population.size());
        return c;
    }
public Chromosome getBestChromosome(List<Chromosome> population){
        Chromosome c = new Chromosome();
        c.setFitness(100000);
        
        for (Chromosome c1:population){
            if (c1.getFitness()<c.getFitness()) c=c1;
        }
        return c;
    }

}