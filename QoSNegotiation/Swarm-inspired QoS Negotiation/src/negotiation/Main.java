package negotiation;

/**
 * 
 * @author Administrator
 */
public class Main {
	public static void main(String[] args) {
		String[] jadeArgs = new String[] {
				"-mtp jamr.jademtp.http.MessageTransportProtocol",
				"-gui",
				GlobalVars.NEGOTIATION_STARTER_NAME + ":"
						+ NegotiationAdministratorAgent.class.getName() };
		jade.Boot.main(jadeArgs);

	}
}