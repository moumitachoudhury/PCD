import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class CrossOverAgent extends Agent {
    public void setBp(double[] bp) {
        this.bp = bp;
    }

    double[] bp;

    CrossOverAgent(MailManager[] mailManager, int AgentNo, Vector<Integer> neighbors, int parent, Vector<Integer> child, int population, double domain_lb, double domain_ub, int maxIteration, boolean ismax, ArrayList<Edge> edgelist, int[][] indexToEdge, HashMap<Edge, Constraint> constraints, double w, double c1, double c2, double[] bp) {
        super(mailManager, AgentNo, neighbors, parent, child, population, domain_lb, domain_ub, maxIteration, ismax, edgelist, indexToEdge, constraints, w, c1, c2);
        this.bp = bp;
    }

    public void calcBPProb() {
        double costSum = 0;
        for (int i = 0; i < population; i++) {
            costSum += Math.abs(this.cons_cost[i]);
        }
        for (int i = 0; i < population; i++) {
            this.bp[i] = Math.abs(this.cons_cost[i]) / costSum;
        }
    }

    public int randomgen() {
        if (this.getCurrentIter() != 0)
            calcBPProb();
        double totalWeight = 0.0d;
        for (double prob : bp) {
            totalWeight += prob;
        }
// Now choose a random item
        int randomIndex = 0;
        double random = Math.random() * totalWeight;
        for (int i = 0; i < bp.length; ++i) {
            random -= bp[i];
            if (random <= 0.0d) {
                randomIndex = i;
                break;
            }
        }
        return randomIndex;
    }

    public void calcBPProb_maxfirst() {

        double bestCost = Double.MAX_VALUE;
        double worstCost = Double.MIN_VALUE;

        for (int i = 0; i < population; i++) {
            if (this.cons_cost[i] < bestCost) {
                bestCost = this.cons_cost[i];
            }
            if (this.cons_cost[i] > worstCost) {
                worstCost = this.cons_cost[i];
            }
        }
        double rankSum = 0;
        double[] rankList = new double[population];
        for (int i = 0; i < population; i++) {
            double rank = (this.cons_cost[i] - bestCost + 1) / (worstCost - bestCost + 1);
            rankList[i] = rank;
            rankSum += rank;
        }
        for (int i = 0; i < population; i++) {
            this.bp[i] = rankList[i] / rankSum;
        }

    }

    public void calcBPProb_minfirst() {

        double costSum = 0;
        double bestCost = Double.MAX_VALUE;
        double worstCost = Double.MIN_VALUE;
        for (int i = 0; i < population; i++) {
            if (this.cons_cost[i] < bestCost) {
                bestCost = this.cons_cost[i];
            }
            if (this.cons_cost[i] > worstCost) {
                worstCost = this.cons_cost[i];
            }
            costSum += this.cons_cost[i];
        }
        double rankSum = 0;
        double[] rankList = new double[population];
        for (int i = 0; i < population; i++) {
            double rank = (worstCost - this.cons_cost[i] + 1) / (worstCost - bestCost + 1);
            rankList[i] = rank;
            rankSum += rank;
//            System.out.println("tresttttt = " + i + " , " + costSum + " " + this.cons_cost[i]);
        }
        for (int i = 0; i < population; i++) {
            this.bp[i] = rankList[i] / rankSum;
        }

    }

    public double setParam() {
        double K = 0.8;
        double phi = 4.1;
        double chiX = 2 * K / (phi - 2 + Math.sqrt(Math.pow(phi, 2) - 4 * phi));
        double alpha = (1 + chiX * (phi - 2)) / (phi - 1);

        return alpha;

    }


    public void updateValues() {
        double ub = 1.0;
        double lb = 0.0;
        double r1 = (Math.random() * (ub - lb)) + lb;
        double r2 = (Math.random() * (ub - lb)) + lb;


        int selectedPar1 = randomgen();
        int selectedPar2 = randomgen();

        for (int i = 0; i < population; i++) {
            if (i == selectedPar1) {
                this.position[i] = r1 * this.position[i] + (1.0 - r1) * this.position[selectedPar2];

                double nom = (this.velocity[i] + this.velocity[selectedPar2]) * this.velocity[selectedPar2];
                double deNom = Math.abs(this.velocity[i] + this.velocity[selectedPar2]);

                if (deNom == 0 || this.velocity[i] == 0) {
                    if (i == this.gbestParticleNo) {
                        this.velocity[i] = -this.position[i] + this.gbest_position + (w * this.velocity[i]) + cal_row() * (1 - (2 * r2));

                    } else {
                        this.velocity[i] = w * this.velocity[i] + c1 * r1 * (this.pbest_position[i] - this.position[i]) + c2 * r2 * (this.gbest_position - this.position[i]);

                    }
                } else {
                    this.velocity[i] = nom / deNom;

                }
            } else if (i == selectedPar2) {
                this.position[i] = r1 * this.position[i] + (1.0 - r1) * this.position[selectedPar1];
                double nom = (this.velocity[i] + this.velocity[selectedPar1]) * this.velocity[selectedPar1];
                double deNom = Math.abs(this.velocity[i] + this.velocity[selectedPar1]);

                if (deNom == 0) {
                    if (i == this.gbestParticleNo) {
                        this.velocity[i] = -this.position[i] + this.gbest_position + (w * this.velocity[i]) + cal_row() * (1 - (2 * r2));


                    } else {
                        this.velocity[i] = w * this.velocity[i] + c1 * r1 * (this.pbest_position[i] - this.position[i]) + c2 * r2 * (this.gbest_position - this.position[i]);

                    }
                } else {
                    this.velocity[i] = nom / deNom;

                }
            } else {
                if (i == this.gbestParticleNo) {
                    this.velocity[i] = -this.position[i] + this.gbest_position + (w * this.velocity[i]) + cal_row() * (1 - (2 * r2));
                    this.position[i] = this.position[i] + this.velocity[i];


                } else {
                    this.velocity[i] = w * this.velocity[i] + c1 * r1 * (this.pbest_position[i] - this.position[i]) + c2 * r2 * (this.gbest_position - this.position[i]);
                    this.position[i] = this.position[i] + this.velocity[i];

                }
            }

            if (this.position[i] < this.domain_lb) {
                this.position[i] = this.domain_lb;
            } else if (this.position[i] > this.domain_ub) {

                this.position[i] = this.domain_ub;
            }

        }


    }
}

