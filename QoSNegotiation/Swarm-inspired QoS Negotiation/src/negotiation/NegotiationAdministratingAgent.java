package negotiation;

import client.ClientAgent;
import jade.core.Agent;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import jade.wrapper.PlatformController;
import jade.wrapper.StaleProxyException;

public class NegotiationAdministratingAgent extends Agent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AgentController clientAgentController;
	//private AgentController serviceCenterAgentController;
	private static Agent agent;
	float[] minS;
	float[] maxS;
	float[] minC;
	float[] maxC;

	public NegotiationAdministratingAgent() {
		agent = this;
		minS = new float[3];
		maxS = new float[3];
		minS[0] = 1f;
		minS[1] = 2.0f;
		minS[2] = 3.0f;

		maxS[0] = 40f;
		maxS[1] = 8f;
		maxS[2] = 9f;

		minC = new float[3];
		maxC = new float[3];
		minC[0] = 20f;
		minC[1] = 3.0f;
		minC[2] = 5.0f;

		maxC[0] = 50f;
		maxC[1] = 10f;
		maxC[2] = 10f;
	}

	@Override
	protected void setup() {
		System.out.println("CMA Agent " + getLocalName() + " started.");
		PlatformController container = (PlatformController) getContainerController();
		try {
			clientAgentController = container.createNewAgent(
					GlobalVars.CLIENT_NAME, ClientAgent.class.getName(),
					new Object[] { 20, minC, maxC, 10 });
		} catch (ControllerException ex) {
			ex.printStackTrace();
		}
		try {
			clientAgentController.start();

		} catch (StaleProxyException ex) {
			ex.printStackTrace();
		}

	}

	public static Agent getNegotiationStarterInstance() {
		return agent;
	}
}