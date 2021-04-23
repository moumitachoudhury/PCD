import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void cost_calc(HashMap<Edge, Constraint> constraints, ArrayList<Edge> edgelist) {
        double sum = 0.0;


        for (Edge e : edgelist) {
            double x = 0.6418937344000017;

            double y = 0.6418937344000017;

            Constraint cons = constraints.get(e);
            int a = cons.getA();
            int b = cons.getB();
            int c = cons.getC();
            if (e.getNode1() == 0) {
                x = 0.6418937344000017;
            } else if (e.getNode2() == 0) {
                y = 0.6418937344000017;
            }
            sum += a * Math.pow(x, 2) + b * (x) * (y) + c * Math.pow(y, 2);
        }
//        System.out.println("SUMMMMMMM = "+ sum/2.0);
    }

    public static void main(String args[]) throws IOException, InterruptedException {

        String problemName = args[0];
        int problem = Integer.parseInt(problemName);
//        System.out.println("problem name " + problem);
        /** Parameters */
        /**
         * MAX_SAME_PROB = No. of runs for a problem
         * MAX_DIFF_PROB =  No. of problems
         * MAX_NODE = No. of Agents
         * w = Inertia Weight
         * c1 = Cognitive Constant
         * c2 = Social Constant
         */


        int MAX_SAME_PROB = 1;
        int MAX_DIFF_PROB = 2;
        int MAX_NODE = 4;
        double w = 0.72;
        double c1 = 1.49;
        double c2 = 1.49;
        Problem p1 = new Problem(0, 0, MAX_SAME_PROB, MAX_DIFF_PROB, MAX_NODE, w, c1, c2);
        p1.newProblem();

    }
}