import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class RunnableDemo implements Runnable {
    public Thread getT() {
        return t;
    }

    private Thread t;
    private String threadName;
    private int threadNo;
    private Agent agent;
    private int maxIteration;
    MailManager[] mailManagers;
    private int currentIter;
    private ConcurrentHashMap<String, Long> timeResults;

    public ConcurrentHashMap<String, Long> getTimeResults() {
        return timeResults;
    }

    public ConcurrentHashMap<Integer, ArrayList<Long>> getIterTimeResults() {
        return iterTimeResults;
    }

    public ArrayList<Long> getSimTimeArray() {
        return simTimeArray;
    }

    private ConcurrentHashMap<Integer, ArrayList<Long>> iterTimeResults;
    private ArrayList<Long> simTimeArray;

    public ArrayList<Long> getRunTimeArray() {
        return runTimeArray;
    }

    private ArrayList<Long> runTimeArray;

    public ArrayList<Double> getIterSol() {
        return iterSol;
    }

    private ArrayList<Double> iterSol;
    static volatile boolean exit = false;

    double[] bp;
    Problem problem;
    RunnableDemo( Problem problem, String agentType, MailManager[] mailManagers, int threadNo, String name, int maxIteration, Vector<Integer> neighbors, int parent, Vector<Integer> child, int population, double domain_lb, double domain_ub, boolean isMax, ArrayList<Edge> edgelist, int[][] indexToEdge, HashMap<Edge, Constraint> constraints, double w, double c1, double c2) {
        this.problem = problem;
        this.threadNo = threadNo;
        this.threadName = name;
        this.maxIteration = maxIteration;
        this.currentIter = 0;
        this.mailManagers = mailManagers;
        this.timeResults = new ConcurrentHashMap<>();
        this.iterTimeResults = new ConcurrentHashMap<>();
        this.simTimeArray = new ArrayList<>();
        this.iterSol = new ArrayList<>();
        this.runTimeArray = new ArrayList<>();

        this.bp = new double[population];
        if (agentType.equals("PFD"))
            agent = new Agent(mailManagers, threadNo, neighbors, parent, child, population, domain_lb, domain_ub, maxIteration, isMax, edgelist, indexToEdge, constraints, w, c1, c2);
        else if (agentType.equals("PFDDSA"))
            agent = new PFDDSA_Agent(mailManagers, threadNo, neighbors, parent, child, population, domain_lb, domain_ub, maxIteration, isMax, edgelist, indexToEdge, constraints, w, c1, c2);
        else if (agentType.equals("ConsPFD"))
            agent = new PFDConstrictionFactor_Agent(mailManagers, threadNo, neighbors, parent, child, population, domain_lb, domain_ub, maxIteration, isMax, edgelist, indexToEdge, constraints, w, c1, c2, setParam());
        else if (agentType.equals("CrossOverAgent"))
            agent = new CrossOverAgent(mailManagers, threadNo, neighbors, parent, child, population, domain_lb, domain_ub, maxIteration, isMax, edgelist, indexToEdge, constraints, w, c1, c2, initBP(population));
        else if (agentType.equals("CognitionOnly"))
            agent = new PFDCognitionOnly_Agent(mailManagers, threadNo, neighbors, parent, child, population, domain_lb, domain_ub, maxIteration, isMax, edgelist, indexToEdge, constraints, w, c1, c2);
        else if (agentType.equals("SocialOnly"))
            agent = new PFDSocialOnly_Agent(mailManagers, threadNo, neighbors, parent, child, population, domain_lb, domain_ub, maxIteration, isMax, edgelist, indexToEdge, constraints, w, c1, c2);

    }

    public double[] var_to_coordinate(int agent_id, double lb, double ub)
    {
//        System.out.println("lb ub 222222 " + agent_id + " "+ lb + " " + ub);
        int col = 8;
        int x = agent_id / col;
        int y = agent_id % col;

        double domain_lbx  = lb + x * 5 + ub * x;
        double domain_ubx = ub + x * 5 + ub * x;

        double domain_lby = lb + y * 5 + ub * y;
        double domain_uby = ub + y * 5 + ub * y;
//        System.out.println("var to coord "+ agent_id + " " + x + " " + y + " " + domain_lbx + " " + domain_ubx  + " " + domain_lby + " "+  domain_uby);

        double [] domains = {domain_lbx, domain_ubx, domain_lby, domain_uby};
        return domains;
    }

    public double setParam()
    {
        double K = 0.8;
        double phi = 4.1;
        double chiX = 2*K / (phi-2+Math.sqrt(Math.pow(phi, 2)-4*phi));
        double alpha = (1 + chiX * (phi-2)) / (phi-1);
//        System.out.println("alpha = "+ alpha);
        return alpha;

    }

    public double[] initBP(int population)
    {
        for(int i = 0; i< population; i++ )
        {
            bp[i] = (double)1/population;
        }
//        System.out.println("XXXXXXXX " + Arrays.toString(bp));

        return bp;
    }

    public void run() {
//        System.out.println("Running " +  threadName );

        try {
//            init values
//            send value to neighbors -> send message()
//            calculate cost after reIs ceiving neighbor's value (on listener)
//            send cost to neighbors -> send cost()
//            pbest, gbest -> send pbest, gbest
//            update value
//            this.agent.printPositions(this.agent.getCurrentIter());
            long start = ManagementFactory.getThreadMXBean().getCurrentThreadUserTime();

            while (this.agent.getCurrentIter() < this.agent.getMaxIteration())
            {
                this.agent.getMailManager()[0].startNewIter();
                double wmax = 1.4;
                double wmin = 0.4;

                double decreasingW = wmax - (wmax - wmin) * ((double)this.agent.getCurrentIter() / this.agent.getMaxIteration());

                this.agent.setW(decreasingW);

                if(this.agent.getCurrentIter()==0)
                {
                    this.agent.initValues();
                }

                else {
                    this.agent.updateValues();
                }

                this.agent.sendValueMessage();
                this.agent.receiveValueMessage();
                this.agent.calculateCost();


                this.agent.receiveCostMessage();
                if(threadNo!=0)
                {
                    this.agent.sendCostMessage();
                    this.agent.receivePbestGbestPositionMessage();
                    this.agent.sendPbestGbestpositionMessage();
                }
                else
                {
                    this.agent.sumRootChildCost();
                    this.agent.setPbestGbest();
                    this.agent.sendPbestGbestpositionMessage();
                }

                if(threadNo==0)
                {
                    System.out.println("Iteration " + this.agent.getCurrentIter()+ " gbest val" + this.agent.getGbest_val());
                    long endTime = System.currentTimeMillis();
                    long diff = endTime - this.agent.starttime;
                    long iterEnd = ManagementFactory.getThreadMXBean().getCurrentThreadUserTime();
                    long iterDiffInMillis = (iterEnd-start) / 1000000;
                    this.simTimeArray.add(iterDiffInMillis);
                    this.runTimeArray.add(diff);
                    this.iterTimeResults.put(threadNo, this.simTimeArray);
                    this.iterSol.add(this.agent.getGbest_val());


                }
                this.agent.getMailManager()[0].makeTrue(threadNo);
                this.agent.getMailManager()[0].checkAllTrue(threadNo);
                this.agent.setCurrentIter(this.agent.getCurrentIter() + 1);

                if(this.agent.getCurrentIter()==9)
                {
                    System.out.println("agent " + this.agent.agentNo + " pos = " + this.agent.gbest_position);
                }

                Thread.sleep(50);

            }


        } catch (InterruptedException e) {
            System.out.println("Thread " +  threadName + " interrupted.");
        }
        if(threadNo==0)
        {
            int arrayNo = problem.problemNo * problem.MAX_SAME_Problem + problem.sameProbIt;

            int same_it = problem.sameProbIt + 1;
            try {
                if(same_it < problem.MAX_SAME_Problem)
                {
                    problem.setSameProbIt(same_it);
                    problem.newProblem();
                }
                else {
                    int diff_it = problem.problemNo + 1;
                    if(diff_it < problem.MAX_DIFF_PROB)
                    {
                        problem.setSameProbIt(0);
                        problem.setProblemNo(diff_it);
                        problem.newProblem();
                    }
                    else
                    {
                        System.out.println("DONEEEEE");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

//        System.out.println("Thread " +  threadName + " exiting.");


    }

    public void start () {

//        System.out.println("Starting " +  threadName );
        if (t == null) {

            t = new Thread (this, threadName);

            t.start ();


        }
    }
}