import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

public class PFDCognitionOnly_Agent extends Agent{

    PFDCognitionOnly_Agent(MailManager[] mailManager, int AgentNo, Vector<Integer> neighbors, int parent, Vector<Integer> child, int population, double domain_lb, double domain_ub, int maxIteration, boolean ismax, ArrayList<Edge> edgelist, int[][] indexToEdge, HashMap<Edge, Constraint> constraints, double w, double c1, double c2) {
        super(mailManager, AgentNo, neighbors, parent, child, population, domain_lb, domain_ub, maxIteration, ismax, edgelist, indexToEdge, constraints, w, c1, c2);
    }

    @Override
    public void updateValues()
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
                this.velocity[i] = w * this.velocity[i] + c1 * r1 * (this.pbest_position[i] - this.position[i]) ;
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
}
