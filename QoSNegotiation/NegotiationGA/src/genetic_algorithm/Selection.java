package genetic_algorithm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

public class Selection {

	public Chromosome rouletteWheelSelection(List<Chromosome> population) {

		float sum = 0;
		Iterator<Chromosome> it = population.iterator();

		while (it.hasNext())
			sum += (it.next()).getFitness();

		float random = (float) (Math.random() * sum);
		float s = 0;
		Iterator<Chromosome> iterator = population.iterator();
		Chromosome chromo = null;
		while (iterator.hasNext() && (chromo = iterator.next()) != null
				&& s < random) {
			chromo = (Chromosome) iterator.next();
			s += chromo.getFitness();
		}
		return chromo;

	}

	//TODO verficat logica
	public Chromosome rankSelection(List<Chromosome> population) {

		PriorityQueue<Chromosome> orderedPopulation = new PriorityQueue<Chromosome>();

		for (Chromosome c : population) {
			orderedPopulation.add(c);
		}

		ArrayList<ArrayList<Chromosome>> rankedList = new ArrayList<ArrayList<Chromosome>>();
		int nr = 0;

		int current = 0;
		for (Chromosome c : orderedPopulation) {
			if (current == 0) {
				
				ArrayList<Chromosome> myList = new ArrayList<Chromosome>();
				myList.add(c);
				rankedList.add(myList);
				current++;
			} else {
				
				if (rankedList.get(nr).get(0).getFitness() != c.getFitness()) {
					ArrayList<Chromosome> myList = new ArrayList<Chromosome>();
					myList.add(c);
					rankedList.add(myList);
					nr++;
				} else {
					rankedList.get(nr).add(c);
				}
			}

		}
		int sel = (int) (Math.random() * nr);
		ArrayList<Chromosome> selected = rankedList.get(sel);
		
		return selected.get((int) Math.random() * selected.size());
	}

	public Chromosome averageChromosome(List<Chromosome> population) {
		
		float[] genes = new float[Chromosome.genesNo];
		for(int i = 0; i< Chromosome.genesNo; i++)
			genes[i] = 0;
		
		for (Chromosome chromo : population) {
			for(int i = 0; i< Chromosome.genesNo; i++)
				genes[i] += chromo.getGenes()[i];
		}
		
		for(int i  =0 ; i< Chromosome.genesNo; i++)
			genes[i] = genes[i]/ population.size();
		
		return new Chromosome(genes);
	}

	
	public Chromosome getBestChromosome(List<Chromosome> population) {
		
		Chromosome bestChromo = new Chromosome();
		bestChromo.setFitness(-1);

		for (Chromosome chromo : population) {
			if (chromo.getFitness() > bestChromo.getFitness())
				bestChromo = chromo;
		}
		return bestChromo;
	}

}
