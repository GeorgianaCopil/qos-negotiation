package server;

import java.util.ArrayList;
import java.util.Iterator;

import GA.Chromosome;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import java.io.IOException;
import negotiation.GlobalVars;

public class ServiceCenterAgent extends Agent {

    private ArrayList<Server> servers;
    private float[] minValues;
    private float[] maxValues;
    private int time;
    private int noServers;
    private int offerNb;

   public ServiceCenterAgent(){
        super();
    }
    public ServiceCenterAgent(int noServers, float[] min, float[] max, int time, int size) {
        servers = new ArrayList<Server>();
        setNoServers(noServers);
        this.time = time;
        this.maxValues = max;
        this.minValues = min;
        int aux = noServers;
        while (aux >= 0) {
            Server server = new Server(time, max, min, size);
            servers.add(server);
            aux--;
        }
    }

    public boolean checkAcceptOffer(Chromosome offer) {
        //TODO: add some accept constraint
        if (offer.getFitness() < 4 || offerNb > 100) {
            return true;
        }
        return false;
    }

    public void sendMessage(ArrayList<Chromosome> offer) {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM_REF);
        try {
            msg.setContentObject( offer);
            System.out.println(offerNb+"Offer from Server:"+offer.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        msg.addReceiver(new AID(GlobalVars.CLIENT_NAME + "@" + this.getContainerController().getPlatformName()));
        this.send(msg);
    }

    public void sendAcceptOfferMessage() {
        ACLMessage msg = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
        msg.setContent(offerNb+"I accept your offer");
        msg.addReceiver(new AID(GlobalVars.CLIENT_NAME + "@" + this.getContainerController().getPlatformName()));
        this.send(msg);
    }

    public void sendRefuseOfferMessage() {
        ACLMessage msg = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
        msg.setContent(offerNb+"I despise your offer. Negotiation over! ");
        msg.addReceiver(new AID(GlobalVars.CLIENT_NAME + "@" + this.getContainerController().getPlatformName()));
        this.send(msg);
    }

    public ArrayList<Chromosome> computeCounterOffer(Chromosome offerClient) {
        // TODO Auto-generated method stub
        ArrayList<Chromosome> offers = new ArrayList<Chromosome>();

        Iterator<Server> iterator = getServers().iterator();
        while (iterator.hasNext()) {
            Chromosome offerServer = iterator.next().computeCounterOffer(offerClient);
            offers.add(offerServer);
        }
        offerNb++;
        return offers;
    }

    public void waitForOffer() {
        // TODO Auto-generated method stub
        Iterator<Server> iterator = getServers().iterator();
        while (iterator.hasNext()) {
            iterator.next().waitForOffer();
        }

    }

    public void setNoServers(int noServers) {
        this.noServers = noServers;
    }

    public int getNoServers() {
        return noServers;
    }

    @Override
    protected void setup() {
        System.out.println("ServiceCenter Agent " + getLocalName() + " started.");

        Object[] args = getArguments();

        if (args != null && args.length == 5) {

            setServers(new ArrayList<Server>());
            this.setNoServers((Integer) args[0]);
            time = (Integer) args[1];
            this.minValues = (float[]) args[2];
            this.maxValues = (float[]) args[3];
            int populationSize = (Integer) args[4];
            int aux = noServers;
            Server server;
            while (aux >= 0) {
                server = new Server(time, maxValues, minValues, populationSize);
                getServers().add(server);
                aux--;
            }

            addBehaviour(new ReceiveMessageServiceCenterBehavior(this));
        } else {
            System.err.println("ServiceCenter Agent has not been properly initialized! ");
        }

    }

    /**
     * @return the servers
     */
    public ArrayList<Server> getServers() {
        return servers;
    }

    /**
     * @param servers the servers to set
     */
    public void setServers(ArrayList<Server> servers) {
        this.servers = servers;
    }

   
}
