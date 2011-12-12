/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import GA.Chromosome;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 *
 * @author Administrator
 */
public class ReceiveMessageServiceCenterBehavior extends CyclicBehaviour {

    ServiceCenterAgent serviceCenter;

    public ReceiveMessageServiceCenterBehavior(Agent agent) {
        serviceCenter = (ServiceCenterAgent) agent;
    }

    @Override
    public void action() {
        ACLMessage message = serviceCenter.receive();
        if (message == null) {
            return;
        }

        switch (message.getPerformative()) {
            case ACLMessage.INFORM_REF: {
                try {
                    Chromosome offer = (Chromosome) message.getContentObject();
                    
                    FitnessFunction f = new FitnessFunction();
                    double d = 0;
                    double min = 100000;
                    Server s1 = null;
                    for (Server s : serviceCenter.getServers()) {
                        if (s.getNegotiation().checkChromosomeConsistency(offer)){
                        d = f.euclidianDistance(offer, s.getNegotiation().getGoal());
                        if (d < min) {
                            s1 = s;
                            min = d;
                        }
                        }
                    }
                    offer.setFitness((float) min);
                    if (serviceCenter.checkAcceptOffer(offer)) {
                        System.err.println("ServiceCenterServer Offer accepted: " + offer.toString());
                        serviceCenter.sendAcceptOfferMessage();
                    } else {
                        ArrayList<Chromosome> offers = serviceCenter.computeCounterOffer(offer);
                        if (offers.isEmpty()) {
                            serviceCenter.sendRefuseOfferMessage();
                        } else {
                            for (Server s : serviceCenter.getServers()) {
                              s.getNegotiation().updateGoal(offer);
//                                 System.out.println("==========================================");
//                        List<Chromosome> population = s.getNegotiation().getPopulation();
//                        for (Chromosome c: population){
//                            System.out.println(c.toString());
//                        }
//                        System.out.println("===========================================");
                            }
                            
                            serviceCenter.sendMessage(offers);
                        }
                    }
                } catch (UnreadableException ex) {
                    ex.printStackTrace();
                }
            }
            break;
            case ACLMessage.ACCEPT_PROPOSAL: {
                System.out.println("ServiceCenter Server: WoooHooo!");
            }
            break;
            case ACLMessage.REJECT_PROPOSAL: {
                System.out.println("ServiceCenter Server: Sorry.");
            }
            break;
        }

    }
}
