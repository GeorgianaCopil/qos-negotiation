package server;

import java.util.HashMap;
import java.util.Map;

import negotiation.Offer;

public class DataCenterSubswarm {
	
	private Map<Integer, Server> servers;
	private int numberOfServers;
	private int timeLeft;
	private Server acceptedServer;
	private float[] minValues;
	private float[] maxValues;

	public DataCenterSubswarm(int numberOfServers, float[] maxValues, float[] minValues) {

		this.setNumberOfServers(numberOfServers);
		this.servers = new HashMap<Integer, Server>();
		this.setMaxValues(maxValues);
		this.setMinValues(minValues);
		while (numberOfServers > 0) {
			// TODO  nr popularie variabila
			
			this.servers.put(numberOfServers, new Server(minValues, maxValues,
					30));
			numberOfServers--;
		}
	}

	public Map<Integer, Offer> sendCounterOffers(Map<Integer, Offer> clientOffer) {

		Map<Integer, Offer> counterOffers = new HashMap<Integer, Offer>();

		for (Map.Entry<Integer, Server> server : servers.entrySet()) {
			counterOffers.put(server.getKey(), server.getValue().computeOffer(clientOffer.get(server.getKey()),
					timeLeft));
		}
		return counterOffers;

	}
	
	public void waitForOffer(int time){
		for (Map.Entry<Integer, Server> server : servers.entrySet()) {
			
			//TODO timpul
			time = 10;
			server.getValue().updateSwarm(10);
		}
		
	}

	/**
	 * returneaza true daca se accepta oferta, false altfel
	 * 
	 * @param clientOffer
	 *            oferta clientului
	 * @return true daca se accepta oferta, false altfel
	 */
	public boolean acceptOffer(Map<Integer, Offer> clientOffer) {
		
		for (Map.Entry<Integer, Server> server : servers.entrySet()) {

			if (server.getValue().acceptOffer(clientOffer.get(server.getKey()), timeLeft)) {
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

}
