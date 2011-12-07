/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package paretofrontier;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;

/**
 *
 * @author Georgiana
 */
public class ParetoFrontier {

    /**
     * @param args the command line arguments
     */
    static int cpuMaxS = 150;
    static int cpuMinS = 0;
    static int cpuMaxT = 250;
    static int cpuMinT = 100;
    static int hddMaxT = 100;
    static int hddMinT = 1;
    static int hddMaxS = 200;
    static int hddMinS = 46;
    static int memMaxT = 200;
    static int memMinT = 100;
    static int memMaxS = 140;
    static int memMinS = 10;
    static int costMaxT = 14;
    static int costMinT = 1;
    static int costMaxS = 25;
    static int costMinS = 5;
    final static double CPU_WEIGHT_CLIENT = 0.4;
     final static double MEM_WEIGHT_CLIENT = 0.2;
      final static double COST_WEIGHT_CLIENT = 0.2;
    final static double CPU_WEIGHT_PROVIDER = 0.25;
     final static double MEM_WEIGHT_PROVIDER = 0.1;
      final static double COST_WEIGHT_PROVIDER = 0.7;  
    public static void main(String[] args) {
        //TODO : transf. in 0-1 pt fiecare resursa
        //CPU
        int cpuMin = cpuMinT;
        int cpuMax = cpuMaxS;
        int memMin = memMinT;
        int memMax = memMaxS;
        int costMin = costMinS;
        int costMax = costMaxT;
        ArrayList<Float> utilityClient = new ArrayList<Float>();
        ArrayList<Float> utilityProvider = new ArrayList<Float>();

        try {
            // Create file 
            FileWriter fstream = new FileWriter("out.csv");
            BufferedWriter out = new BufferedWriter(fstream);
            ArrayList<Float> utilitiesSoFar = new ArrayList<Float>();

            for (float cpu = 0; cpu <= 100; cpu += 1) {
                for (float mem = 0; mem <= 100; mem += 1) {
                    for (float cost = 0; cost <= 100; cost += 1) {
                        double uClient = computeEuclidianUtilityClient(cpu, mem, cost);
                        double uProvider = computeEuclidianUtilityProvider(cpu, mem, cost); 
                        boolean ok = true;
                    
                        for (float cpu1 = 0; cpu1 <= 100; cpu1 += 1) {
                            for (float mem1 = 0; mem1 <= 100; mem1 += 1) {
                                for (float cost1 = 0; cost1 <= 100; cost1 += 1) {
                                    double uClient1 = computeEuclidianUtilityClient(cpu1,mem1,cost1);
                                    double uProvider1 = computeEuclidianUtilityProvider(cpu1, mem1, cost1);
                                    if (cpu1 != cpu || mem1 != mem || cost1 != cost) {
                                        if (uClient<uClient1 && uProvider <  uProvider1) {
                                            ok = false;
                                            break;
                                        }
                                    }
                                }
                                if (!ok) {
                                    break;
                                }
                            }
                            if (!ok) {
                                break;
                            }
                        }
                        if (ok) {
                            out.write("Resources " + (float) (cpuMin + cpu * (cpuMax - cpuMin)/100) + "  " + (float) (memMin + mem * (memMax - memMin)/100) + "  " + (float) (costMin + cost * (costMax - costMin)/100) + " Utilities " + uClient + " " + " " + uProvider + " \n");
                            System.out.println("Resources " + (float)(cpuMin+cpu * (cpuMax-cpuMin)/100) + "  " + (float)(memMin+mem * (memMax-memMin)/100) + "  " + (float)(costMin+cost * (costMax-costMin)/100) + " Utilities " + uClient + " " + " " + uProvider + " ");
                            if (!utilitiesSoFar.contains(new Float(uClient))) {
                                System.out.println((float) (cpuMin + cpu * (cpuMax - cpuMin) + memMin + mem * (memMax - memMin) + costMin + cost * (costMax - costMin)) + "  " + uClient + " " + uProvider);
                                utilitiesSoFar.add(new Float(uClient));
                                utilityClient.add(new Float(uClient));
                                utilityProvider.add(new Float(uProvider));

//                                for (float cpu1 = 0; cpu1 <= 1; cpu1 += 0.05) {
//                                    for (float mem1 = 0; mem1 <= 1; mem1 += 0.05) {
//                                        //for (int hdd1 = hddMinT; hdd1 < hddMaxS; hdd1++) {
//                                        for (float cost1 = 0; cost1 <= 1; cost1 += 0.05) {
//                                            double uClient1 = Math.sqrt(0.25 * Math.pow(cpu1 - 1, 2) + 0.25 * Math.pow(mem1 - 1, 2) + 0.5 * Math.pow(cost1 - 0, 2));
//                                            double uProvider1 = Math.sqrt(0.25 * Math.pow(cpu1, 2) + 0.25 * Math.pow(mem1 - 0, 2) + 0.5 * Math.pow(cost1 - 1, 2));
//                                            //System.out.println((int)uClient1+uProvider1);
//                                            if (Math.abs(uClient1 + uProvider1 - uClient - uProvider) <= 0.0) {
//                                                System.out.println("Resources " + (float) (cpuMin + cpu1 * (cpuMax - cpuMin)) + "  " + (float) (memMin + mem1 * (memMax - memMin)) + "  " + (float) (costMin + cost1 * (costMax - costMin)) + " Utilities " + uClient1 + " " + " " + uProvider1 + " ");
//                                                utilityClient.add(new Float(uClient1));
//                                                utilityProvider.add(new Float(uProvider1));
//                                            }
//                                        }
//                                    }
//                                }


                            }


                        }
                    }

                }
            }
            int size = utilityClient.size();
            float aux1 = 0;
            float aux2 = 0;
            for (int i = 0; i < size; i++) {
                for (int j = i + 1; j < size; j++) {
                    if (utilityClient.get(i) > utilityClient.get(j)) {
                        aux1 = utilityClient.get(i);
                        aux2 = utilityProvider.get(i);
                        utilityClient.set(i, utilityClient.get(j));
                        utilityClient.set(j, aux1);
                        utilityProvider.set(i, utilityProvider.get(j));
                        utilityProvider.set(j, aux2);
                    }
                }
            }
            System.out.println("======================");
            out.write ("===================\n");
            for (int i = 0; i < size; i++) {
                System.out.println(utilityClient.get(i) + " " + utilityProvider.get(i));
                out.write (utilityClient.get(i) + " " + utilityProvider.get(i)+"\n");
            }
            System.out.println("======================");
            out.write ("===================\n");
            aux1 = 0;
            aux2 = 0;
            for (int i = 0; i < size; i++) {
                for (int j = i + 1; j < size; j++) {
                    if (utilityProvider.get(i) > utilityProvider.get(j)) {
                        aux1 = utilityClient.get(i);
                        aux2 = utilityProvider.get(i);
                        utilityClient.set(i, utilityClient.get(j));
                        utilityClient.set(j, aux1);
                        utilityProvider.set(i, utilityProvider.get(j));
                        utilityProvider.set(j, aux2);
                    }
                }
            }
            for (int i = 0; i < size; i++) {
                System.out.println(utilityClient.get(i) + " " + utilityProvider.get(i));
                out.write (utilityClient.get(i) + " " + utilityProvider.get(i)+"\n");
            }
            //Close the output stream
            out.close();
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }
    private static double computeEuclidianUtilityClient(double cpu, double mem, double cost){
       return Math.sqrt(CPU_WEIGHT_CLIENT * Math.pow(cpu, 2) + MEM_WEIGHT_CLIENT * Math.pow(mem , 2) + COST_WEIGHT_CLIENT * Math.pow(100-cost , 2));
    }
    private static double computeEuclidianUtilityProvider(double cpu, double mem, double cost){
    return Math.sqrt(CPU_WEIGHT_PROVIDER * Math.pow(100-cpu, 2) + MEM_WEIGHT_PROVIDER * Math.pow(100-mem , 2) + COST_WEIGHT_PROVIDER * Math.pow(cost , 2));
}
     private static double computeUtilityClient(double cpu, double mem, double cost){
         return CPU_WEIGHT_CLIENT*cpu+MEM_WEIGHT_CLIENT*mem-COST_WEIGHT_CLIENT*cost;
           }
    private static double computeUtilityProvider(double cpu, double mem, double cost){
        return -1*CPU_WEIGHT_PROVIDER*cpu-MEM_WEIGHT_PROVIDER*mem+COST_WEIGHT_PROVIDER*cost;
       // return Math.sqrt(CPU_WEIGHT_PROVIDER * Math.pow(1-cpu, 2) + MEM_WEIGHT_PROVIDER * Math.pow(1-mem , 2) + COST_WEIGHT_PROVIDER * Math.pow(cost , 2));
}
}
