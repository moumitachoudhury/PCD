import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

public class PFDConstrictionFactor_Agent extends Agent{

    double alpha;
    PFDConstrictionFactor_Agent(MailManager[] mailManager, int AgentNo, Vector<Integer> neighbors, int parent, Vector<Integer> child, int population, double domain_lb, double domain_ub, int maxIteration, boolean ismax, ArrayList<Edge> edgelist, int[][] indexToEdge, HashMap<Edge, Constraint> constraints, double w, double c1, double c2, double alpha) {
        super(mailManager, AgentNo, neighbors, parent, child, population, domain_lb, domain_ub, maxIteration, ismax, edgelist, indexToEdge, constraints, w, c1, c2);
        this.alpha = alpha;
    }


    public void updateValues()
    {
        double ub = 1.0;
        double lb = 0.0;
        double r1 = (Math.random() * (ub - lb)) + lb;
        double r2 = (Math.random() * (ub - lb)) + lb;
        for(int i = 0; i < population; i++)
        {
            double c1 = 2.05, c2=2.05;

            this.velocity[i] = this.alpha * (this.velocity[i] + r1 * c1 * (this.pbest_position[i] - this.position[i]) +  r2 * c2 * (this.gbest_position - this.position[i]));
            this.position[i] = this.position[i] + this.velocity[i];


            if(this.position[i] < this.domain_lb)
            {
                this.position[i] = this.domain_lb;
            }
            else if(this.position[i] > this.domain_ub){

                this.position[i] = this.domain_ub;
            }

        }
    }
}
