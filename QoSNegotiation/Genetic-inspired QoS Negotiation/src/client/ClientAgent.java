package client;

import java.util.ArrayList;

import GA.Chromosome;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import java.io.IOException;
import java.util.List;
import negotiation.GlobalVars;

public class ClientAgent extends Agent {

    private GeneticAlgorithmNegotiation negotiation;
    private float[] maxValues;
    @SuppressWarnings("unused")
    private float[] minValues;
    @SuppressWarnings("unused")
    private int size;
    private int offerNb;
    int time;

    public ClientAgent() {
        super();
        
    }

    public ClientAgent(int time, float maxValues[], float minValues[], int size) {

        this.maxValues = maxValues;
        this.minValues = minValues;
        this.size = size;
        this.time = time;
        
       

        negotiation = new GeneticAlgorithmNegotiation(size, minValues, maxValues);
    }

    public Chromosome getClosest(List<Chromosome> offers) {
        return getNegotiation().getClosestIndividual(offers);
    }

    @Override
    protected void setup() {
        System.out.println("Client Agent " + getLocalName() + " started.");
        Object[] args = getArguments();

        if (args != null && args.length == 4) {
            time = (Integer) args[0];
            this.minValues = (float[]) args[1];
            this.maxValues = (float[]) args[2];
            this.size = (Integer) args[3];
            setNegotiation(new GeneticAlgorithmNegotiation(size, minValues, maxValues));
            getNegotiation().initializePopulation();
            addBehaviour(new ReceiveMessageClientBehavior(this));
            startNegotiation();
        } else {
            System.err.println("Client Agent has not been properly initialized! ");
        }

    }

    public void sendMessage(Chromosome offer) {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM_REF);
        try {
            msg.setContentObject( offer);
            System.out.println(offerNb+"Offer from Client:"+offer.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        msg.addReceiver(new AID(GlobalVars.SERVICE_CENTER_NAME + "@" + this.getContainerController().getPlatformName()));
        this.send(msg);
    }

    public void sendAcceptOfferMessage() {
        ACLMessage msg = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
        msg.setContent(offerNb+"I accept your offer");
        msg.addReceiver(new AID(GlobalVars.SERVICE_CENTER_NAME + "@" + this.getContainerController().getPlatformName()));
        this.send(msg);
    }

    public void sendRefuseOfferMessage() {
        ACLMessage msg = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
        msg.setContent(offerNb+"I despise your offer. Negotiation over! ");
        msg.addReceiver(new AID(GlobalVars.SERVICE_CENTER_NAME + "@" + this.getContainerController().getPlatformName()));
        this.send(msg);
    }
    private void startNegotiation(){
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM_REF);
        try {
            Chromosome offer = computeCounterOffer(null);
            msg.setContentObject(offer );
            System.out.println("Offer from Client:"+offer.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        msg.addReceiver(new AID(GlobalVars.SERVICE_CENTER_NAME + "@" + this.getContainerController().getPlatformName()));
        this.send(msg);
    }
    public boolean checkAcceptOffer(Chromosome offer) {
        //TODO: add some accept constraint
        if (offer.getFitness() < 4 || offerNb > 100) {
            return true;
        }
        return false;
    }

    public Chromosome computeCounterOffer(Chromosome offer) {
        if (offerNb == 0) {
            offerNb++;
            return new Chromosome(maxValues[0], maxValues[1], maxValues[2], maxValues[3]);
        }
        offerNb++;
        return getNegotiation().computeOffer(offer, time, 0.1);
    }

    public void waitForOffer() {

        getNegotiation().runAlgorithm(time);
    }

    /**
     * @return the offerNb
     */
    public int getOfferNb() {
        return offerNb;
    }

    /**
     * @param offerNb the offerNb to set
     */
    public void setOfferNb(int offerNb) {
        this.offerNb = offerNb;
    }

    /**
     * @return the negotiation
     */
    public GeneticAlgorithmNegotiation getNegotiation() {
        return negotiation;
    }

    /**
     * @param negotiation the negotiation to set
     */
    public void setNegotiation(GeneticAlgorithmNegotiation negotiation) {
        this.negotiation = negotiation;
    }
}
