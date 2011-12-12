
package negotiation;

import GA.Chromosome;
import client.ClientAgent;
import jade.core.Agent;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import jade.wrapper.PlatformController;

import jade.wrapper.StaleProxyException;
import server.ServiceCenterAgent;

/**
 * 
 * @author Administrator
 */
public class NegotiationAdministratingAgent extends Agent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AgentController clientAgentController;
	private AgentController serviceCenterAgentController;
	private static Agent agent;
	float[] minS;
	float[] maxS;
	float[] minC;
	float[] maxC;

	public NegotiationAdministratingAgent() {
		agent = this;
		minS = new float[Chromosome.nrResources];
		maxS = new float[Chromosome.nrResources];
		minS[0] = 1f;
		minS[1] = 2.0f;
		minS[2] = 3.0f;
		minS[3] = 100f;

		maxS[0] = 40f;
		maxS[1] = 8f;
		maxS[2] = 9f;
		minS[3] = 500f;

		minC = new float[Chromosome.nrResources];
		maxC = new float[Chromosome.nrResources];
		minC[0] = 20f;
		minC[1] = 3.0f;
		minC[2] = 5.0f;
		minC[3] = 100f;

		maxC[0] = 50f;
		maxC[1] = 10f;
		maxC[2] = 10f;
		minC[3] = 500f;
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
		try {
			try {
				serviceCenterAgentController = container.createNewAgent(
						GlobalVars.SERVICE_CENTER_NAME,
						ServiceCenterAgent.class.getName(), new Object[] { 5,
								20, minS, maxS, 10 });
			} catch (ControllerException ex) {
				ex.printStackTrace();
			}
			serviceCenterAgentController.start();
		} catch (StaleProxyException e) {
			e.printStackTrace(); // To change body of catch statement use File
									// |Settings | File Templates.
		}

	}

	public static Agent getNegotiationStarterInstance() {
		return agent;
	}
}
