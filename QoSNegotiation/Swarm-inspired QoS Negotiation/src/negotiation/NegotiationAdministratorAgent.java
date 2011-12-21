package negotiation;

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
	float[] minS;
	float[] maxS;
	float[] minC;
	float[] maxC;

	public NegotiationAdministratorAgent() {
		
		agent = this;
	
	}

	@Override
	protected void setup() {
		System.out.println("CMA Agent " + getLocalName() + " started.");
		PlatformController container = (PlatformController) getContainerController();
		try {
			clientAgentController = container.createNewAgent(
					GlobalVars.CLIENT_NAME, ClientAgent.class.getName(),
					new Object[]{null, null, null});
				//	new Object[] { 30, minC, maxC, 100 });
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
						//new Object[] { 6, 20,minS, maxS, 100 });
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
}
