import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

public class PFDDSA_Agent extends Agent{
    Vector<Integer> neighbors;
    PFDDSA_Agent(MailManager[] mailManager, int AgentNo, Vector<Integer> neighbors, int parent, Vector<Integer> child, int population, double domain_lb, double domain_ub, int maxIteration, boolean ismax, ArrayList<Edge> edgelist, int[][] indexToEdge, HashMap<Edge, Constraint> constraints, double w, double c1, double c2) {
        super(mailManager, AgentNo, neighbors, parent, child, population, domain_lb, domain_ub, maxIteration, ismax, edgelist, indexToEdge, constraints, w, c1, c2);
        this.neighbors = neighbors;
    }
    @Override
    public void updateValues()
    {
        int threshold = 10;
        if(this.getCurrentIter()%threshold==0)
        {
            runDSA();
        }
        else {
            PFDUpdate();
        }
    }
    public void PFDUpdate()
    {
        double ub = 1.0;
        double lb = 0.0;
        double r1 = (Math.random() * (ub - lb)) + lb;
        double r2 = (Math.random() * (ub - lb)) + lb;
        for(int i = 0; i < population; i++)
        {
            if(i==this.gbestParticleNo)
            {
                this.velocity[i] = -this.position[i] + this.gbest_position + (w * this.velocity[i]) + cal_row() * (1- (2 * r2));
                this.position[i] = this.position[i] + this.velocity[i];

            }
            else{
                this.velocity[i] = w * this.velocity[i] + c1 * r1 * (this.pbest_position[i] - this.position[i]) + c2 * r2 * (this.gbest_position - this.position[i]);
                this.position[i] = this.position[i] + this.velocity[i];

            }

            if(this.position[i] < this.domain_lb)
            {
                this.position[i] = this.domain_lb;
            }
            else if(this.position[i] > this.domain_ub){

                this.position[i] = this.domain_ub;
            }

        }
    }
    public void runDSA()
    {
        double A = 0, B = 0, C = 0;
        double mid;

        for(int i = 0; i < this.population; i++)
        {

            for(int neigh: this.neighbors)
            {
                Constraint cons = this.constraints.get(edgelist.get(indexToEdge[this.agentNo][neigh]));
                A += cons.getA();
                B += cons.getB() * this.inbox.get(neigh)[i];
            }
            if(A!=0)
            {
                mid = -B/(2*A);
            }
            else {
                mid = (this.domain_lb + this.domain_ub) / 2.0;
            }
            double cons_calc_lb = 0;
            double cons_calc_ub = 0;
            double cons_calc_mid = 0;

            double temp_cost, temp=0;
            if(this.isMax)
            {
                temp_cost = Double.MIN_VALUE;

            }
            else
            {
                temp_cost = Double.MAX_VALUE;
            }
            for(int neigh: neighbors)
            {
                Constraint cons = this.constraints.get(edgelist.get(indexToEdge[this.agentNo][neigh]));
                cons_calc_lb += cons.getA() * Math.pow(this.domain_lb, 2) + cons.getB() * this.domain_lb * this.inbox.get(neigh)[i] + cons.getC() * Math.pow(this.inbox.get(neigh)[i], 2);
                cons_calc_ub += cons.getA() * Math.pow(this.domain_ub, 2) + cons.getB() * this.domain_ub * this.inbox.get(neigh)[i] + cons.getC() * Math.pow(this.inbox.get(neigh)[i], 2);

                if(mid>=this.domain_lb && mid<=this.domain_ub)
                {
                    cons_calc_mid += cons.getA() * Math.pow(mid, 2) + cons.getB() * mid * this.inbox.get(neigh)[i] + cons.getC() * Math.pow(this.inbox.get(neigh)[i], 2);

                }
                else {
                    if(this.isMax)
                    {
                        cons_calc_mid = Double.MIN_VALUE;

                    }
                    else
                    {
                        cons_calc_mid = Double.MAX_VALUE;
                    }
                }

            }
            if(isMax)
            {
                if(cons_calc_mid >= cons_calc_lb && cons_calc_mid >= cons_calc_ub)
                {
                    temp_cost = cons_calc_mid;
                    temp = mid;
                }
                else if(cons_calc_lb >= cons_calc_mid && cons_calc_lb >= cons_calc_ub)
                {
                    temp_cost = cons_calc_lb;
                    temp = this.domain_lb;

                }
                else if(cons_calc_ub >= cons_calc_mid && cons_calc_ub >= cons_calc_lb)
                {
                    temp_cost = cons_calc_ub;
                    temp = this.domain_ub;
                }

                if(temp_cost > this.cons_cost[i] && Math.random() < this.dsa_prob)
                {
                    this.position[i] = temp;
                }
            }
            else
            {
                if(cons_calc_mid <= cons_calc_lb && cons_calc_mid <= cons_calc_ub)
                {
                    temp_cost = cons_calc_mid;
                    temp = mid;
                }
                else if(cons_calc_lb <= cons_calc_mid && cons_calc_lb <= cons_calc_ub)
                {
                    temp_cost = cons_calc_lb;
                    temp = this.domain_lb;

                }
                else if(cons_calc_ub <= cons_calc_mid && cons_calc_ub <= cons_calc_lb)
                {
                    temp_cost = cons_calc_ub;
                    temp = this.domain_ub;
                }

                if(temp_cost < this.cons_cost[i] && Math.random() < this.dsa_prob)
                {
                    this.position[i] = temp;
                }
            }

        }
    }

}
