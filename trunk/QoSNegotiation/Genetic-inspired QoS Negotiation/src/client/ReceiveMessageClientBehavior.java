/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package client;

import GA.Chromosome;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author Administrator
 */
public class ReceiveMessageClientBehavior extends CyclicBehaviour {
    ClientAgent client;
    public ReceiveMessageClientBehavior(Agent agent){
       client = (ClientAgent) agent;
       
    }
    @Override
    public void action() {
        ACLMessage message = client.receive();
        if (message == null) {
            return;
        }

            switch (message.getPerformative()) {
                case ACLMessage.INFORM_REF:
                {
            try {
                ArrayList<Chromosome> offers = (ArrayList<Chromosome>) message.getContentObject();
                Chromosome bestOffer = client.getClosest(offers);
                bestOffer.setFitness(new FitnessFunction().euclidianDistance(client.getNegotiation().getGoal(), bestOffer));
                if (client.checkAcceptOffer(bestOffer)){
                    System.err.println("Client: Offer accepted: "+bestOffer.toString());
                    client.sendAcceptOfferMessage();
                }else {
                    Chromosome offer= client.computeCounterOffer(client.getClosest(offers));
                    if (offer!=null){
                        client.getNegotiation().updateGoal(bestOffer);
//                        System.out.println("==========================================");
//                        List<Chromosome> population = client.getNegotiation().getPopulation();
//                        for (Chromosome c: population){
//                            System.out.println(c.toString());
//                        }
//                        System.out.println("===========================================");
                    client.sendMessage(offer);
                    }else{
                        client.sendRefuseOfferMessage();
                    }
                }

            } catch (UnreadableException ex) {
                Logger.getLogger(ReceiveMessageClientBehavior.class.getName()).log(Level.SEVERE, null, ex);
            }
                }
                  break;
            case ACLMessage.ACCEPT_PROPOSAL: {
                System.out.println("Client: WoooHooo!");
            }
            break;
            case ACLMessage.REJECT_PROPOSAL: {
                System.out.println("Client: Sorry.");
            }
            break;
            }

    }

}
