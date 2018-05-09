import java.util.HashMap;
import java.util.ArrayList;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;


public class BaseballElimination {
    private final HashMap<String, Integer> teamMap;
    private final int teamsNum;
    private final String[] teamName;
    private final int[] w, l, r;
    private final int[][] g;
    private int s, t, V;
    private FlowNetwork flowNet;
    private FordFulkerson ff;

    // TODO create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {
        if (null == filename)
            throw new java.lang.IllegalArgumentException(String.format("filename %s is null", filename));

        In fileIn = new In(filename);
        teamsNum = Integer.parseInt(fileIn.readLine());
        w = new int[teamsNum];
        l = new int[teamsNum];
        r = new int[teamsNum];
        g = new int[teamsNum][teamsNum];
        teamName = new String[teamsNum];
        teamMap = new HashMap<>();

        V = teamsNum+1+(teamsNum-2)*(teamsNum-1)/2;
        s = V-2;
        t = V-1;

        int i = 0;
        for (String line : fileIn.readAllLines()) {
            String[] v = line.trim().split("\\s+");
            teamName[i] = v[0];
            teamMap.put(v[0], i);
            w[i] = Integer.parseInt(v[1]);
            l[i] = Integer.parseInt(v[2]);
            r[i] = Integer.parseInt(v[3]);
            for (int j = 0; j < teamsNum; j++) g[i][j] = Integer.parseInt(v[3+1+j]);
            i++;
        }
    }

    // run FF algorithm to eliminate
    private boolean checkEliminate(int idx) {
        if (buildGraph(idx)) return true;

        for (FlowEdge e : flowNet.adj(s)) {
            if (e.capacity() != e.flow()) {
                return true;
            }
        }
        flowNet = null;
        ff = null;
        return false;
    }

    // build max-flow/min-cut graph
    private boolean buildGraph(int idx) {
        flowNet = new FlowNetwork(V);
        int ws = teamsNum - 1, capacity;
        for (int i = 0; i < teamsNum-1; i++) {
            capacity = w[idx] + r[idx] - (i < idx ? w[i] : w[i+1]);
            if (capacity < 0) return true;
            flowNet.addEdge(new FlowEdge(i, t, capacity));
        }

        for (int i = 0; i < teamsNum-2; i++) {
            for (int j = i+1; j < teamsNum-1; j++) {
                int ti = i;
                int tj = j;
                if (i >= idx) ti++;
                if (j >= idx) tj++;
                flowNet.addEdge(new FlowEdge(s, ws, g[ti][tj]));
                flowNet.addEdge(new FlowEdge(ws, j, Double.POSITIVE_INFINITY));
                flowNet.addEdge(new FlowEdge(ws, i, Double.POSITIVE_INFINITY));
                ws++;
            }
        }
        ff = new FordFulkerson(flowNet, s, t);
        return false;
    }

    // TODO number of teams
    public int numberOfTeams() {
        return teamsNum;
    }

    // TODO all teams
    public Iterable<String> teams() {
        return teamMap.keySet();
    }

    // TODO number of wins for given team
    public int wins(String team) {
        validateTeam(team);
        return w[teamMap.get(team)];
    }

    // TODO number of losses for given team
    public int losses(String team) {
        validateTeam(team);
        return l[teamMap.get(team)];
    }

    // TODO number of remaining games for given team
    public int remaining(String team) {
        validateTeam(team);
        return r[teamMap.get(team)];
    }

    // TODO number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        validateTeam(team1);
        validateTeam(team2);
        return g[teamMap.get(team1)][teamMap.get(team2)];
    }

    // TODO is given team eliminated?
    public boolean isEliminated(String team) {
        validateTeam(team);
        return checkEliminate(teamMap.get(team));
    }

    // TODO subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        validateTeam(team);

        int idx = teamMap.get(team);
        ArrayList<String> arr = new ArrayList<>();
        for (int i = 0; i < teamsNum-1; i++) {
            if (w[idx] + r[idx] - (i < idx ? w[i] : w[i+1]) < 0)
                arr.add(i < idx ? teamName[i] : teamName[i+1]);
        }

        if (!arr.isEmpty()) return arr;

        buildGraph(idx);
        for (int i = 0; i < teamsNum-1; i++) {
            if (ff.inCut(i))
                arr.add(teamName[i < idx ? i : i+1]);
        }
        ff = null;
        flowNet = null;
        return !arr.isEmpty() ? arr : null;
    }

    private void validateTeam(String team) {
        if (null == team) throw new java.lang.IllegalArgumentException("team name is null");
        if (!teamMap.containsKey(team))
            throw new java.lang.IllegalArgumentException(String.format("team %s is not found", team));
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination("data/baseball/teams4.txt");
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}


