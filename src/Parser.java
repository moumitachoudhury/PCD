
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;


public class Parser {
    public static void main(String[] args) throws IOException {
        Parser parser = new Parser();
        Parse_Output output = parser.getGraph();

    }
    public Parse_Output getGraph_sensor() throws IOException {
        int MAX_NODE = 64;
        String path = "benchmarks/";
        File file = new File(path+"sensor_config_new_64.txt");
        ArrayList<Integer> all_nodes = new ArrayList<>();
        ArrayList<ArrayList<Edge>> all_edges = new ArrayList<>();
        ArrayList<HashMap<Edge, Constraint>> all_cons = new ArrayList<>();

        BufferedReader br = new BufferedReader(new FileReader(file));
        String st;
        int[][] indexToEdge = new int[MAX_NODE][MAX_NODE];

        while ((st = br.readLine()) != null) {
            if(st.startsWith("nodes")) {
                String temp = st.replace("nodes=", "");
                int node_number = Integer.parseInt(temp);
                for(int i = 0; i < node_number; i++) {
                    all_nodes.add(i);
                }
            }

            if(st.startsWith("edges")) {
                indexToEdge = new int[MAX_NODE][MAX_NODE];
                String temp = st.replace("edges=", "");
                String[] arrOfEdge = temp.split(" ", -2);
                ArrayList<Edge> nums = new ArrayList<>();

                int idx = 0;
                for (String s : arrOfEdge) {
                    String[] anEdge = s.split(",", -2);
                    if(anEdge.length != 2) {
                        continue;
                    }
                    int a = Integer.parseInt(anEdge[0]);
                    int b = Integer.parseInt(anEdge[1]);
                    Edge an_edge = new Edge(a, b);
                    nums.add(an_edge);

                    Edge ulta_edge = new Edge(b, a);
                    nums.add(ulta_edge);
                    indexToEdge[a][b] = idx;
                    idx+=1;
                    indexToEdge[b][a] = idx;

                    idx += 1;
                }
                all_edges.add(nums);
            }

            if(st.startsWith("cons")) {
                String temp = st.replace("cons=", "");
                String[] arrOfCons = temp.split(">", -2);

                HashMap<Edge, Constraint> cons = new HashMap<>();
                for (String s : arrOfCons) {
                    s = s.replace("(", "");
                    s = s.replace(")", "");

                    String[] aCons = s.split(":", -2);
                    if(aCons.length < 2) continue;
                    String[] key = aCons[0].split(",");
                    String[] value = aCons[1].split(",");
                    int a = Integer.parseInt(key[0].trim());
                    int b = Integer.parseInt(key[1].trim());
                    int c = Integer.parseInt(value[0].trim());
                    int d = Integer.parseInt(value[1].trim());
                    int r1 = Integer.parseInt(value[2].trim());
                    int r2 = Integer.parseInt(value[3].trim());
                    int r3 = Integer.parseInt(value[4].trim());
                    int r4 = Integer.parseInt(value[5].trim());
//                    System.out.println("ee = "+ r1 + " " + r2 + " " + r3 + " " + r4);
//                    Edge an_edge = new Edge(a, b);
                    Edge an_edge = all_edges.get(all_edges.size()-1).get(indexToEdge[a][b]);
                    Constraint a_con = new Constraint(c, d, r1, r2, r3, r4);
                    cons.put(an_edge, a_con);
                }
                all_cons.add(cons);
            }
        }
        return new Parse_Output(all_nodes, all_edges, all_cons);
    }


    public Parse_Output getGraph() throws IOException {
        int MAX_NODE = 4;
        String path = "benchmarks/";
        File file = new File(path+"example.txt");
        ArrayList<Integer> all_nodes = new ArrayList<>();
        ArrayList<ArrayList<Edge>> all_edges = new ArrayList<>();
        ArrayList<HashMap<Edge, Constraint>> all_cons = new ArrayList<>();

        BufferedReader br = new BufferedReader(new FileReader(file));
        String st;
        int[][] indexToEdge = new int[MAX_NODE][MAX_NODE];

        while ((st = br.readLine()) != null) {
            if(st.startsWith("nodes")) {
                String temp = st.replace("nodes=", "");
                int node_number = Integer.parseInt(temp);
                for(int i = 0; i < node_number; i++) {
                    all_nodes.add(i);
                }
            }

            if(st.startsWith("edges")) {
                indexToEdge = new int[MAX_NODE][MAX_NODE];
                String temp = st.replace("edges=", "");
                String[] arrOfEdge = temp.split(" ", -2);
                ArrayList<Edge> nums = new ArrayList<>();

                int idx = 0;
                for (String s : arrOfEdge) {
                    String[] anEdge = s.split(",", -2);
                    if(anEdge.length != 2) {
                        continue;
                    }
                    int a = Integer.parseInt(anEdge[0]);
                    int b = Integer.parseInt(anEdge[1]);
                    Edge an_edge = new Edge(a, b);
                    nums.add(an_edge);

                    Edge ulta_edge = new Edge(b, a);
                    nums.add(ulta_edge);
                    indexToEdge[a][b] = idx;
                    idx+=1;
                    indexToEdge[b][a] = idx;

                    idx += 1;
                }
                all_edges.add(nums);
            }

            if(st.startsWith("cons")) {
                String temp = st.replace("cons=", "");
                String[] arrOfCons = temp.split(">", -2);

                HashMap<Edge, Constraint> cons = new HashMap<>();
                for (String s : arrOfCons) {
                    s = s.replace("(", "");
                    s = s.replace(")", "");

                    String[] aCons = s.split(":", -2);
                    if(aCons.length < 2) continue;
                    String[] key = aCons[0].split(",");
                    String[] value = aCons[1].split(",");
                    int a = Integer.parseInt(key[0].trim());
                    int b = Integer.parseInt(key[1].trim());
                    int c = Integer.parseInt(value[0].trim());
                    int d = Integer.parseInt(value[1].trim());
                    int e = Integer.parseInt(value[2].trim());
//                    Edge an_edge = new Edge(a, b);
                    Edge an_edge = all_edges.get(all_edges.size()-1).get(indexToEdge[a][b]);
                    Constraint a_con = new Constraint(c, d, e);
                    cons.put(an_edge, a_con);
                }
                all_cons.add(cons);
            }
        }
        return new Parse_Output(all_nodes, all_edges, all_cons);
    }
}
