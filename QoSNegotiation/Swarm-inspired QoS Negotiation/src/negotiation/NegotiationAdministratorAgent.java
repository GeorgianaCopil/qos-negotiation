package negotiation;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import server.DataCenterAgent;
import client.ClientAgent;
import jade.core.Agent;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import jade.wrapper.PlatformController;

import jade.wrapper.StaleProxyException;

/**
 * 
 * @author Administrator
 */
public class NegotiationAdministratorAgent extends Agent {

	private static final long serialVersionUID = 1L;
	private AgentController clientAgentController;
	private AgentController dataCenterAgentController;
	private static Agent agent;
	private static PrintWriter file;
	float[] minS;
	float[] maxS;
	float[] minC;
	float[] maxC;

	public NegotiationAdministratorAgent() {
		
		agent = this;
		try {
			file = new PrintWriter("rezultate.txt");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}

	@Override
	protected void setup() {
		System.out.println("CMA Agent " + getLocalName() + " started.");
		PlatformController container = (PlatformController) getContainerController();
		try {
			clientAgentController = container.createNewAgent(
					GlobalVars.CLIENT_NAME, ClientAgent.class.getName(),
					new Object[]{null, null, null});
		} catch (ControllerException ex) {
			ex.printStackTrace();
		}
		try {
			clientAgentController.start();

		} catch (StaleProxyException ex) {
			ex.printStackTrace();
		}
		try {
			try {
				dataCenterAgentController = container.createNewAgent(
						GlobalVars.SERVICE_CENTER_NAME,
						DataCenterAgent.class.getName(), 
						new Object[]{null, null, null});
			} catch (ControllerException ex) {
				ex.printStackTrace();
			}
			dataCenterAgentController.start();
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}

	}

	public static Agent getNegotiationStarterInstance() {
		return agent;
	}

	public static PrintWriter getFile() throws FileNotFoundException {
		return file;
	}
}
