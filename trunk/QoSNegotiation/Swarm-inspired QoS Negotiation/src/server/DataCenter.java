package server;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import negotiation.Offer;

public class DataCenter {

	private Map<Integer, Server> servers;
	private int numberOfServers;
	private int timeLeft;
	private long totalTime;
	private Server acceptedServer;
	private float[] minValues;
	private float[] maxValues;

	public DataCenter(long totalTime, int numberOfServers,
			HashMap<Integer, Integer> swarmSizeAll, float[] maxValues,
			float[] minValues, HashMap<Integer, Float> threshold_lower, HashMap<Integer, Float> threshold_upper,
			HashMap<Integer, Float[]> resourceWeightAll,
			HashMap<Integer, Float[]> maxCompromiseAll) {

		this.setNumberOfServers(numberOfServers);
		this.servers = new HashMap<Integer, Server>();
		this.setMaxValues(maxValues);
		this.setMinValues(minValues);
		this.setTotalTime(totalTime);
		int counter = 0;

		while (numberOfServers > 0) {

			int swarmSize = swarmSizeAll.get(counter);

			Server server = new Server(minValues, maxValues,
					resourceWeightAll.get(counter), swarmSize);
			server.setTotalTime(totalTime);
			server.setThreshold(threshold_lower.get(counter), threshold_upper.get(counter));
			server.setMaxCompromise(maxCompromiseAll.get(counter));
			this.servers.put(numberOfServers, server);
			numberOfServers--;
			counter++;
		}

	}

	public List<Offer> sendCounterOffers(Offer clientOffer) {

		List<Offer> counterOffers = new ArrayList<Offer>();

		for (Map.Entry<Integer, Server> server : servers.entrySet()) {

			counterOffers.add(server.getValue().computeOffer(clientOffer,
					timeLeft));
		}
		return counterOffers;

	}

	public void waitForOffer() {
		for (Map.Entry<Integer, Server> server : servers.entrySet()) {

			server.getValue().updateSwarm();
		}

	}

	/**
	 * returneaza true daca se accepta oferta, false altfel
	 * 
	 * @param clientOffer
	 *            oferta clientului
	 * @return true daca se accepta oferta, false altfel
	 */
	public boolean acceptOffer(Offer clientOffer) {

		for (Map.Entry<Integer, Server> server : servers.entrySet()) {

			if (server.getValue().acceptOffer(clientOffer, timeLeft)) {
				setAcceptedServer(server.getValue());
				return true;
			}
		}
		return false;
	}

	public void setNumberOfServers(int numberOfServers) {
		this.numberOfServers = numberOfServers;
	}

	public int getNumberOfServers() {
		return numberOfServers;
	}

	public void setMinValues(float[] minValues) {
		this.minValues = minValues;
	}

	public float[] getMinValues() {
		return minValues;
	}

	public void setMaxValues(float[] maxValues) {
		this.maxValues = maxValues;
	}

	public float[] getMaxValues() {
		return maxValues;
	}

	public void setAcceptedServer(Server acceptedServer) {
		this.acceptedServer = acceptedServer;
	}

	public Server getAcceptedServer() {
		return acceptedServer;
	}

	public long getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(long totalTime) {
		this.totalTime = totalTime;
	}

	public void printNegotiationResults(String filename) {

		PrintWriter result_file = null;
		PrintWriter fitness_file  = null;
		HashMap<Integer, Offer> clientOffers;
		HashMap<Integer, Offer> serverOffers;

		try {
			result_file = new PrintWriter(filename+".txt");
			fitness_file = new PrintWriter(filename+"_fitness.txt");
		} catch (FileNotFoundException e) {
			System.err.println("Error creating file!");
			e.printStackTrace();
		}

		fitness_file.println("client/server");
		for (Map.Entry<Integer, Server> server : servers.entrySet()) {

			result_file.println("Server number " + server.getKey());
			fitness_file.println("server #:" + server.getKey());
			
			
			clientOffers = server.getValue().getOppositeAgentOffers();
			serverOffers = server.getValue().getCounterOffers();

			int iteration = 1;
			
			while (iteration < clientOffers.size()
					&& iteration < serverOffers.size()) {

				result_file.println("Iteration: " + iteration);
				
				fitness_file.append(new Integer(iteration).toString()+" ");
				
				result_file.println("Client: "
						+ clientOffers.get(iteration).toString());
				fitness_file.append(new Float(clientOffers.get(iteration).getFitness())+" ");
				
				result_file.println("Server: "
						+ serverOffers.get(iteration).toString());
				fitness_file.println(new Float(serverOffers.get(iteration).getFitness()));

				iteration++;
			}
		}

		result_file.close();
		fitness_file.close();
	}

}
