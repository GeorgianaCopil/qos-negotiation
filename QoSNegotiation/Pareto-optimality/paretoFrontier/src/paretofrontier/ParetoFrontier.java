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
    static int cpuMaxS = 200;
    static int cpuMinS = 50;
    static int cpuMaxT = 300;
    static int cpuMinT = 100;
    static int hddMaxT = 100;
    static int hddMinT = 1;
    static int hddMaxS = 50;
    static int hddMinS = 1;
    static int memMaxT = 200;
    static int memMinT = 100;
    static int memMaxS = 150;
    static int memMinS = 25;
    static int costMaxT = 10;
    static int costMinT = 1;
    static int costMaxS = 20;
    static int costMinS = 2;

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
            FileWriter fstream = new FileWriter("out.txt");
            BufferedWriter out = new BufferedWriter(fstream);
            ArrayList<Float> utilitiesSoFar = new ArrayList<Float>();

            for (float cpu = 0; cpu <= 1; cpu += 0.1) {
                for (float mem = 0; mem <= 1; mem += 0.1) {
                    // for (int hdd = hddMinT; hdd < hddMaxS; hdd++) {
                    for (float cost = 0; cost <= 1; cost += 0.1) {
                        double uClient = 0.3 * Math.pow(cpu - 1, 2) + 0.3 * Math.pow(mem - 1, 2) + 0.4 * Math.pow(cost, 2);//+ 0.2*Math.pow(hdd - hddMaxT, 2)
                        double uProvider = 0.3 * Math.pow(cpu, 2) + 0.3 * Math.pow(mem, 2) + 0.4 * Math.pow(cost - 1, 2);// + 0.2*Math.pow(hdd - hddMinS, 2) 
                        boolean ok = true;
                        //System.out.println("Wanna be pareto"+mem +" "+ " "+cpu+" "+cost);
                        for (float cpu1 = 0; cpu1 <= 1; cpu1 += 0.1) {
                            for (float mem1 = 0; mem1 <= 1; mem1 += 0.1) {
                                //for (int hdd1 = hddMinT; hdd1 < hddMaxS; hdd1++) {
                                for (float cost1 = 0; cost1 <= 1; cost1 += 0.1) {
                                    double uClient1 = 0.3 * Math.pow(cpu1 - 1, 2) + 0.3 * Math.pow(mem1 - 1, 2) + 0.4 * Math.pow(cost1 - 0, 2);
                                    double uProvider1 = 0.3 * Math.pow(cpu1, 2) + 0.3 * Math.pow(mem1 - 0, 2) + 0.4 * Math.pow(cost1 - 1, 2);
                                    if (cpu1 != cpu || mem1 != mem || cost1 != cost) {
                                        if (uClient + uProvider > uClient1 + uProvider1) {
                                            // if (uClient>uClient1 || uProvider>uProvider1){
                                            ok = false;

//                                            System.out.println("==================");
//                                            
//                                            System.out.println(uClient+" "+uClient1+" "+uProvider+" "+uProvider1);
//                                            System.out.println(mem1+" "+cpu1+" "+cost1);
//                                            System.out.println(mem+" "+cpu+" "+cost);
//                                            
//                                            System.out.println("==================");

                                            break;
                                        }
                                    }
                                }
//                                    if (!ok) break;
//                            }
                                if (!ok) {
                                    break;
                                }
                            }
                            if (!ok) {
                                break;
                            }
                        }
                        if (ok) {
                            out.write("Resources " + (float) (cpuMin + cpu * (cpuMax - cpuMin)) + "  " + (float) (memMin + mem * (memMax - memMin)) + "  " + (float) (costMin + cost * (costMax - costMin)) + " Utilities " + uClient + " " + " " + uProvider + " \n");
                            //System.out.println("Resources " + (float)(cpuMin+cpu * (cpuMax-cpuMin)) + "  " + (float)(memMin+mem * (memMax-memMin)) + "  " + (float)(costMin+cost * (costMax-costMin)) + " Utilities " + uClient + " " + " " + uProvider + " ");
                            if (!utilitiesSoFar.contains(new Float(uClient))) {
                                System.out.println((float) (cpuMin + cpu * (cpuMax - cpuMin) + memMin + mem * (memMax - memMin) + costMin + cost * (costMax - costMin)) + "  " + uClient + " " + uProvider);
                                utilitiesSoFar.add(new Float(uClient));


                                for (float cpu1 = 0; cpu1 <= 1; cpu1 += 0.01) {
                                    for (float mem1 = 0; mem1 <= 1; mem1 += 0.01) {
                                        //for (int hdd1 = hddMinT; hdd1 < hddMaxS; hdd1++) {
                                        for (float cost1 = 0; cost1 <= 1; cost1 += 0.01) {
                                            double uClient1 = 0.3 * Math.pow(cpu1 - 1, 2) + 0.3 * Math.pow(mem1 - 1, 2) + 0.4 * Math.pow(cost1 - 0, 2);
                                            double uProvider1 = 0.3 * Math.pow(cpu1, 2) + 0.3 * Math.pow(mem1 - 0, 2) + 0.4 * Math.pow(cost1 - 1, 2);
                                            //System.out.println((int)uClient1+uProvider1);
                                            if (Math.abs(uClient1 + uProvider1 - uClient - uProvider) <= 0.01) {
                                                System.out.println("Resources " + (float) (cpuMin + cpu1 * (cpuMax - cpuMin)) + "  " + (float) (memMin + mem1 * (memMax - memMin)) + "  " + (float) (costMin + cost1 * (costMax - costMin)) + " Utilities " + uClient1 + " " + " " + uProvider1 + " ");
                                                utilityClient.add(new Float(uClient1));
                                                utilityProvider.add(new Float(uProvider1));
                                            }
                                        }
                                    }
                                }


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
            for (int i = 0; i < size; i++) {
                System.out.println(utilityClient.get(i) + " " + utilityProvider.get(i));

            }
            System.out.println("======================");
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

            }
            //Close the output stream
            out.close();
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }
}
