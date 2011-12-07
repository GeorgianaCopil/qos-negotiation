/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package seesawnegotiation;

import sun.awt.windows.WEmbeddedFrame;

/**
 *
 * @author Georgiana
 */
public class SeeSawNegotiation {

    public class Offer {

        private double cpu;
        private double mem;
        private double hdd;
        private double cost;
        private double gravity;
        private double cpuWeight;
        private double memWeight;
        private double costWeight;

        public Offer() {
        }

        public Offer(double cpu, double mem, double cost) {
            this.cpu = cpu;
            this.mem = mem;
            this.hdd = hdd;
            this.cost = cost;
        }

        /**
         * @return the cpu
         */
        public double getCpu() {
            return cpu;
        }

        /**
         * @param cpu the cpu to set
         */
        public void setCpu(double cpu) {
            this.cpu = cpu;
        }

        /**
         * @return the mem
         */
        public double getMem() {
            return mem;
        }

        /**
         * @param mem the mem to set
         */
        public void setMem(double mem) {
            this.mem = mem;
        }

        /**
         * @return the cost
         */
        public double getCost() {
            return cost;
        }

        /**
         * @param cost the cost to set
         */
        public void setCost(double cost) {
            this.cost = cost;
        }

        /**
         * @return the gravity
         */
        public double getGravity() {
            return gravity;
        }

        /**
         * @param gravity the gravity to set
         */
        public void setGravity(double gravity) {
            this.gravity = gravity;
        }

        public double computeEuclidianDistance(Offer otherOffer) {
            return Math.sqrt(getCpuWeight() * Math.pow(cpu - otherOffer.getCpu(), 2) + getMemWeight() * Math.pow(mem - otherOffer.getMem(), 2) + getCostWeight() * Math.pow(cost - otherOffer.getCost(), 2));
        }

        /**
         * @return the cpuWeight
         */
        public double getCpuWeight() {
            return cpuWeight;
        }

        /**
         * @param cpuWeight the cpuWeight to set
         */
        public void setCpuWeight(double cpuWeight) {
            this.cpuWeight = cpuWeight;
        }

        /**
         * @return the memWeight
         */
        public double getMemWeight() {
            return memWeight;
        }

        /**
         * @param memWeight the memWeight to set
         */
        public void setMemWeight(double memWeight) {
            this.memWeight = memWeight;
        }

        /**
         * @return the costWeight
         */
        public double getCostWeight() {
            return costWeight;
        }

        /**
         * @param costWeight the costWeight to set
         */
        public void setCostWeight(double costWeight) {
            this.costWeight = costWeight;
        }
    }
    float currentRequestClient = 0;
    float currentOfferProvider = 0;
    static double cpuMaxS = 150;
    static double cpuMinS = 1;
    static double cpuMaxT = 250;
    static double cpuMinT = 100;
    static double hddMaxT = 100;
    static double hddMinT = 1;
    static double hddMaxS = 200;
    static double hddMinS = 46;
    static double memMaxT = 200;
    static double memMinT = 100;
    static double memMaxS = 140;
    static double memMinS = 10;
    static double costMaxT = 14;
    static double costMinT = 1;
    static double costMaxS = 25;
    static double costMinS = 5;
    final static double CPU_WEIGHT_CLIENT = 0.4;
    final static double MEM_WEIGHT_CLIENT = 0.2;
    final static double COST_WEIGHT_CLIENT = 0.2;
    final static double CPU_WEIGHT_PROVIDER = 0.25;
    final static double MEM_WEIGHT_PROVIDER = 0.1;
    final static double COST_WEIGHT_PROVIDER = 0.7;
    final static double COMPROMISE_FACTOR_CLIENT = 0.1;
    final static double COMPROMISE_FACTOR_PROVIDER = 0.1;

    public Offer computeProviderOffer(Offer prevOffer,Offer offer, Offer goal) {
        Offer res = new Offer();
        double compromiseCPU = COMPROMISE_FACTOR_PROVIDER* offer.getCpu() ;
        double compromiseMEM = COMPROMISE_FACTOR_PROVIDER * offer.getMem() ;
        double compromiseCost = COMPROMISE_FACTOR_PROVIDER * offer.getCost() ;
        res.setCost(prevOffer.getCost() - compromiseCost);
        res.setCpu(compromiseCPU+prevOffer.getCpu() );
        res.setMem( compromiseMEM+prevOffer.getMem() );
        res.setCostWeight(COST_WEIGHT_PROVIDER);
        res.setCpuWeight(CPU_WEIGHT_PROVIDER);
        res.setMemWeight(MEM_WEIGHT_PROVIDER);
        return res;
    }

    public Offer computeClientRequest(Offer prevOffer, Offer offer, Offer goal) {
        Offer res = new Offer();
          double compromiseCPU = COMPROMISE_FACTOR_CLIENT * offer.getCpu() ;
        double compromiseMEM = COMPROMISE_FACTOR_CLIENT * offer.getMem();
        double compromiseCost = COMPROMISE_FACTOR_CLIENT * offer.getCost() ;
         res.setCost(prevOffer.getCost() + compromiseCost);
        res.setCpu(prevOffer.getCpu() - compromiseCPU);
        res.setMem(prevOffer.getMem() - compromiseMEM);
        res.setCostWeight(COST_WEIGHT_CLIENT);
        res.setCpuWeight(CPU_WEIGHT_CLIENT);
        res.setMemWeight(MEM_WEIGHT_CLIENT);
        return res;
    }

    public void negotiate() {
        double clientGravity = Math.sqrt(COST_WEIGHT_CLIENT * Math.pow(costMaxT - costMinT, 2) + CPU_WEIGHT_CLIENT * Math.pow(cpuMaxT - cpuMinT, 2) + MEM_WEIGHT_CLIENT * Math.pow(memMaxT - memMinT, 2));
        double providerGravity = Math.sqrt(COST_WEIGHT_PROVIDER * Math.pow(costMaxS - costMinS, 2) + CPU_WEIGHT_PROVIDER * Math.pow(cpuMaxS - cpuMinS, 2) + MEM_WEIGHT_PROVIDER * Math.pow(memMaxS - memMinS, 2));
        Offer clientOffer;
        Offer providerOffer = null;
        Offer clientGoal = new Offer(cpuMaxT, memMaxT, costMinT);
        clientGoal.setGravity(clientGravity);
         clientGoal.setCostWeight(COST_WEIGHT_CLIENT);
        clientGoal.setCpuWeight(CPU_WEIGHT_CLIENT);
        clientGoal.setMemWeight(MEM_WEIGHT_CLIENT);
        Offer providerGoal = new Offer(cpuMinS, memMinS, costMaxS);
        providerGoal.setGravity(providerGravity);
        providerGoal.setCostWeight(COST_WEIGHT_PROVIDER);
        providerGoal.setCpuWeight(CPU_WEIGHT_PROVIDER);
        providerGoal.setMemWeight(MEM_WEIGHT_PROVIDER);
        clientOffer = clientGoal;
        boolean clientTurn = false;
        Offer prevClientOffer = clientGoal;
        Offer prevProviderOffer = providerGoal;
        double differenceClient = 0;
        double differenceServer = 0;
        while (true) {
            if (clientTurn) {
                 differenceClient = (double) (Math.abs(clientGravity - providerOffer.computeEuclidianDistance(clientGoal)));
               
                 System.out.println("Client Equilibrium difference " +(double) (Math.abs(clientGravity - providerOffer.computeEuclidianDistance(clientGoal))) );
               if (differenceClient<5){
                   System.out.println("Client: offer accepted");
                   break;
               } 
                clientOffer = computeClientRequest(prevClientOffer, providerOffer, clientGoal);
                prevClientOffer = clientOffer;
                System.out.println("Client " + clientOffer.getCpu() + " " + clientOffer.getMem() + " " + clientOffer.getCost());
                clientTurn = false;
               
            } else {
                 differenceServer = Math.abs(providerGravity - clientOffer.computeEuclidianDistance(providerGoal));
                
                System.out.println("Provider Equilibrium difference " + (double) (Math.abs(providerGravity - clientOffer.computeEuclidianDistance(providerGoal))));
                 if (differenceServer<5){
                   System.out.println("Provider: offer accepted");
                   break;
               } 
                
                providerOffer = computeProviderOffer(prevProviderOffer,clientOffer, providerGoal);
                prevProviderOffer = providerOffer;
                System.out.println("Provider " + providerOffer.getCpu() + " " + providerOffer.getMem() + " " + providerOffer.getCost());

                clientTurn = true;
            }
//          if (Math.abs(differenceClient-differenceServer)<30){
//              System.out.println("agreement reached");
//              break;
//          }
        }
    }

    public static void main(String[] args) {
        SeeSawNegotiation seeSawNegotiation = new SeeSawNegotiation();
        seeSawNegotiation.negotiate();
    }
}
