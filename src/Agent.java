import java.util.*;

public class Agent{
    protected int suc_cnt = 0;
    protected int fail_cnt = 0;
    protected int sc = 15;
    protected int fc = 5;
    protected double dsa_prob = 0.7, row = 1;

    protected boolean isMax;
    protected MailManager[] mailManager;
    protected int agentNo;
    private String agentName;
    protected int population;
    protected double domain_lb, domain_ub;

    protected double[] velocity;

    protected double[] position;

    protected HashMap<Integer, double[]> inbox;
    private HashMap<Integer, double[]> cons_inbox;


    public double getGbest_val() {
        return gbest_val;
    }

    protected double[] pbest_val;
    protected double gbest_val;
    protected double[] pbest_position;
    protected double gbest_position;
    protected double[] pbestParticleNo;
    protected double gbestParticleNo;

    protected Vector<Integer> neighbors;
    private int parent;
    protected Vector<Integer> child;

    public int getCurrentIter() {
        return currentIter;
    }

    public void setCurrentIter(int currentIter) {
        this.currentIter = currentIter;
    }

    protected int currentIter = 0;

    public int getMaxIteration() {
        return maxIteration;
    }

    private int maxIteration;

    protected ArrayList<Edge> edgelist;
    protected int[][] indexToEdge;
    protected HashMap<Edge, Constraint> constraints;

    protected double[] cons_cost;
    protected double[] globalRootCost;

    double w;

    public void setW(double w) {
        this.w = w;
    }

    public void setC1(double c1) {
        this.c1 = c1;
    }

    public void setC2(double c2) {
        this.c2 = c2;
    }

    double c1;
    double c2;
    long starttime;

    public MailManager[] getMailManager() {
        return mailManager;
    }

    Agent(MailManager[] mailManager, int AgentNo, Vector<Integer> neighbors, int parent, Vector<Integer> child, int population, double domain_lb, double domain_ub, int maxIteration, boolean ismax, ArrayList<Edge> edgelist, int[][] indexToEdge, HashMap<Edge, Constraint> constraints, double w, double c1, double c2)
    {
        this.mailManager = mailManager;
        this.agentNo = AgentNo;
        this.agentName = "Agent-"+this.agentNo;
        this.population = population;
        this.domain_lb = domain_lb;
        this.domain_ub = domain_ub;
        this.velocity = new double[this.population];
        this.position = new double[this.population];

        this.inbox = new HashMap<>();
        this.cons_inbox = new HashMap<>();

        this.pbest_position = new double[this.population];
        this.cons_cost = new double[this.population];
        this.pbestParticleNo = new double[this.population+1];
        Arrays.fill(this.pbestParticleNo, -1);
        this.gbestParticleNo = -1;

        this.neighbors = neighbors;
        this.parent = parent;
        this.child = child;

        this.maxIteration = maxIteration;

        this.edgelist = edgelist;
        this.indexToEdge = indexToEdge;
        this.constraints = constraints;

        this.globalRootCost = new double[this.population];

        this.isMax = ismax;

        if(ismax)
        {
            this.pbest_val = new double[this.population];
            Arrays.fill(this.pbest_val, Double.MIN_VALUE);
            this.gbest_val = Double.MIN_VALUE;

        }
        else {
            this.pbest_val = new double[this.population];
            Arrays.fill(this.pbest_val, Double.MAX_VALUE);
            this.gbest_val = Double.MAX_VALUE;
        }
        this.w = w;
        this.c1 = c1;
        this.c2 = c2;
        this.starttime = System.currentTimeMillis();
    }


    public double cal_row()
    {
        if (this.fail_cnt > this.fc)
        {
            this.row = 0.5 * this.row;
//            # if(this.fail_cnt > this.fc_max):
//            #     this.row = 1
        }

        else if(this.suc_cnt > this.sc)
        {
            this.row = Math.min(2 * this.row, 100);
        }
        else
        {
            this.row = this.row;

        }
        return this.row;
    }
    public void initValues()
    {
        for(int i = 0; i< this.population; i++)
        {
            Random r = new Random();
            double randomValue = ((Math.random() * (this.domain_ub - this.domain_lb)) + this.domain_lb);
            assert(randomValue <= this.domain_ub && randomValue >= this.domain_lb);
//            double randomValue = this.domain_lb + (this.domain_ub - this.domain_lb) * r.nextDouble();
            double randomValueVx =  ((Math.random() * (1.0 - 0.0)) + 0.0);

            this.position[i] = randomValue;
            this.velocity[i] = 0;
        }
    }

    public void initValues_example_trace()
    {
        double[] a1 = {-1.0, -2.0, 0.0, 1.1};
        double[] a2 = {1.2, 2.0, 1.0, -1.0};
        double[] a3 = {-2.0, -1.0, 2.0, 1.5};
        double[] a4 = {2.0, 1.0, -2.0, 0.5};
        for(int i = 0; i< this.population; i++)
        {
            if(this.agentNo==0){
                this.position[i] = a1[i];
            }
            else if(this.agentNo==1)
            {
                this.position[i] = a2[i];
            }
            else if(this.agentNo==2)
            {
                this.position[i] = a3[i];
            }
            else if(this.agentNo==3)
            {
                this.position[i] = a4[i];
            }

            this.velocity[i] = 0;
        }
    }

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
                this.velocity[i] = w * this.velocity[i] + c1 * r1 * (this.pbest_position[i] - this.position[i]) + c2 * r2 * (this.gbest_position - this.position[i]);
                this.position[i] = this.position[i] + this.velocity[i];

            }


            if(this.position[i] < this.domain_lb)
            {
                this.position[i] = this.domain_lb;
            }
            if(this.position[i] > this.domain_ub){

                this.position[i] = this.domain_ub;
            }

        }
    }

    public void updateValues_example_trace()
    {

        double r1 = 0.7;
        double r2 = 0.4;
        double w = 0.72;
        double c1 = 1.49;
        double c2 = 1.49;
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
            if(this.position[i] > this.domain_ub){

                this.position[i] = this.domain_ub;
            }


        }
        System.out.print("velocity " + this.agentNo +" = ");

        for(int i=0;i<population;i++)
        {
            System.out.print(this.velocity[i] + ",");
        }
        System.out.println("");
        System.out.print("position " + this.agentNo +" = ");

        for(int i=0;i<population;i++)
        {
            System.out.print(this.position[i] + ",");
        }
        System.out.println("");

    }
    public void sendValueMessage() throws InterruptedException {
        for(int neigh: neighbors)
        {
            Message valueMsg = new Message(this.agentNo, neigh, 202, this.position);
            this.mailManager[neigh].putMessage(valueMsg);
        }
    }

    public void receiveValueMessage() throws InterruptedException {
        int cnt = 0;
        while (cnt < neighbors.size())
        {
            Message rcvdValueMsg = this.mailManager[this.agentNo].getMessage();
            this.inbox.put(rcvdValueMsg.getSenderId(), rcvdValueMsg.getMsgContent());
            cnt += 1;
        }

    }

    public void calculateCost()
    {
        double[] consCostList = new double[this.population];
        Arrays.fill(consCostList, 0.0);
        for (int neigh: neighbors)
        {
            Constraint cons = this.constraints.get(edgelist.get(indexToEdge[this.agentNo][neigh]));

            for(int i=0; i<population; i++)
            {
                double cons_calc = cons.getA() * Math.pow(this.position[i], 2) + cons.getB() * this.position[i] * this.inbox.get(neigh)[i] + cons.getC() * Math.pow(this.inbox.get(neigh)[i], 2);
                consCostList[i] += cons_calc;

            }
        }

        this.cons_cost = consCostList;
    }


    public void sendCostMessage() throws InterruptedException {
        double[] sumChildCost = new double[this.population];
        Arrays.fill(sumChildCost, 0.0);
        for(int baccha: child)
        {
            for(int i = 0; i < population; i++)
            {
                sumChildCost[i] += this.cons_inbox.get(baccha)[i];
            }
        }
        double[] sumTotalCost = new double[this.population];
        for(int i = 0; i < population; i++)
        {
            sumTotalCost[i] = sumChildCost[i] + this.cons_cost[i];
        }
        int neigh = this.parent;
        Message costMsg = new Message(this.agentNo, neigh, 204, sumTotalCost);
        this.mailManager[neigh].putCostMessage(costMsg);

    }

    public void receiveCostMessage() throws InterruptedException {
        int cnt = 0;
        while (cnt < child.size())
        {
            Message rcvdCostValueMsg = this.mailManager[this.agentNo].getCostMessage();
            this.cons_inbox.put(rcvdCostValueMsg.getSenderId(), rcvdCostValueMsg.getMsgContent());
            cnt += 1;
        }

    }

    public void sumRootChildCost() throws InterruptedException {
        double[] sumChildCost = new double[this.population];
        Arrays.fill(sumChildCost, 0.0);
        for(int baccha: child)
        {
            for(int i = 0; i < population; i++)
            {
                sumChildCost[i] += this.cons_inbox.get(baccha)[i];
            }

        }


        for(int i = 0; i < population; i++)
        {
            this.globalRootCost[i] = sumChildCost[i] + this.cons_cost[i];
            this.globalRootCost[i] /= 2;
        }

    }

    public void setPbestGbest()
    {
        Arrays.fill(this.pbestParticleNo, -1);
//        this.gbestParticleNo = -1;
        if(isMax)
        {

            for(int i = 0; i < population; i++)
            {
                if(this.globalRootCost[i] > this.pbest_val[i])
                {
                    this.pbest_val[i] = this.globalRootCost[i];
                    this.pbest_position[i] = this.position[i];
                    this.pbestParticleNo[i] = i;
                }
                if(this.globalRootCost[i] > this.gbest_val)
                {
                    this.gbest_val = this.globalRootCost[i];
                    this.gbest_position = this.position[i];
                    this.gbestParticleNo = i;
                }
            }
        }
        else{
            for(int i = 0; i < population; i++)
            {
                if(this.globalRootCost[i] < this.pbest_val[i])
                {
                    this.pbest_val[i] = this.globalRootCost[i];
                    this.pbest_position[i] = this.position[i];
                    this.pbestParticleNo[i] = i;

                }
                if(this.globalRootCost[i] < this.gbest_val)
                {
                    this.gbest_val = this.globalRootCost[i];
                    this.gbest_position = this.position[i];
                    this.gbestParticleNo = i;
                }
            }
        }
        this.pbestParticleNo[pbestParticleNo.length-1] = this.gbestParticleNo;
    }

    public void sendPbestGbestpositionMessage() throws InterruptedException {
        for(int baccha: child)
        {
            Message bestMsg = new Message(this.agentNo, baccha, 206, this.pbestParticleNo);
            this.mailManager[baccha].putBestMessage(bestMsg);
        }
    }

    public void receivePbestGbestPositionMessage() throws InterruptedException {

        Message rcvdBestMsg = this.mailManager[this.agentNo].getBestMessage();
        this.pbestParticleNo = rcvdBestMsg.getMsgContent();

        for(int i = 0; i < this.population; i++)
        {
            if(this.pbestParticleNo[i]!=-1)
            {
                this.pbest_position[i] = this.position[(int)(this.pbestParticleNo[i])];
            }

        }
        if(this.gbestParticleNo==-1 || this.position[(int)this.gbestParticleNo]!=this.position[(int)(this.pbestParticleNo[this.pbestParticleNo.length-1])])
        {
            this.suc_cnt += 1;
            this.fail_cnt = 0;
        }
        else {
            this.fail_cnt += 1;
            this.suc_cnt = 0;
        }
        this.gbestParticleNo = this.pbestParticleNo[this.pbestParticleNo.length-1];

        this.gbest_position = this.position[(int)(this.pbestParticleNo[this.pbestParticleNo.length-1])];

    }

    public void printPositions(int iteration){
        System.out.println("Position of " + this.agentName + " on iteration " + iteration + " = " + Arrays.toString(this.position));


    }


}