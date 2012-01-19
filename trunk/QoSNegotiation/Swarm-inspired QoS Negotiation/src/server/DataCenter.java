package server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import negotiation.Offer;

public class DataCenter {

	private Map<Integer, Server> servers;
	private List<Offer> offerHistory;
	private int numberOfServers;
	private int timeLeft;
	private Server acceptedServer;
	private float[] minValues;
	private float[] maxValues;

	public DataCenter(int numberOfServers, float[] maxValues, float[] minValues) {

		numberOfServers = 1;
		this.setNumberOfServers(numberOfServers);
		this.servers = new HashMap<Integer, Server>();
		offerHistory = new ArrayList<Offer>();
		this.setMaxValues(maxValues);
		this.setMinValues(minValues);
		while (numberOfServers > 0) {
			// TODO  nr popularie variabila
			
			this.servers.put(numberOfServers, new Server(minValues, maxValues,
					30));
			numberOfServers--;
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
	
	public void waitForOffer(int time){
		for (Map.Entry<Integer, Server> server : servers.entrySet()) {
			
			//TODO timpul
			time = 10;
			server.getValue().updateSwarm(10);
		}
		
	}

	/**
	 * adauga oferta clientului la istoricul ofertelor
	 * 
	 * @param offer
	 *            oferta clientului
	 */
	public void saveOffer(Offer offer) {

		offerHistory.add(offer);
	}

	/**
	 * determina daca un server poate fi "scos" din negociere
	 *TODO 
	 */
	public void pruneServer() {

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

}
