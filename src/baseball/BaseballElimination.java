import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FordFulkerson;

// The last six methods should throw a java.lang.IllegalArgumentException if one (or both) of the input arguments are invalid teams.
public final class BaseballElimination {
    private int _teamsSize = 0;
    private String[] _teams = null;
    private Map<String, Integer> _toTeam = null;
    private List<List<String>> _certificates = null;
    private int[] _wins = null;
    private int[] _losses = null;
    private int[] _remaining = null;
    private int[][] _games = null;

    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {
        In in = new In(filename);
        _teamsSize = in.readInt();
        _teams = new String[_teamsSize];
        _toTeam = new HashMap<String, Integer>(_teamsSize);
        _certificates = new ArrayList< List<String> >(_teamsSize);
        _wins = new int[_teamsSize];
        _losses = new int[_teamsSize];
        _remaining = new int[_teamsSize];
        _games = new int[_teamsSize][_teamsSize];

        int maxWins = 0;
        String maxWinner = null;
        for (int i = 0; i < _teamsSize; i++) {
            _teams[i] = in.readString();
            _toTeam.put(_teams[i], i);
            _certificates.add(i, null);
            _wins[i] = in.readInt();
            _losses[i] = in.readInt();
            _remaining[i] = in.readInt();

            for (int j = 0; j < _teamsSize; j++) {
                _games[i][j] = in.readInt();
            }

            if (_wins[i] > maxWins) {
                maxWins = _wins[i];
                maxWinner = _teams[i];
            }
        }

        int V = _teamsSize + _teamsSize * _teamsSize + 1;
        for (int i = 0; i < _teamsSize; i++) {
            // First check check naive approach.
            if (_wins[i] + _remaining[i] < maxWins) {
                List<String> winner = new ArrayList<String>();
                winner.add(maxWinner);
                _certificates.add(i, winner);
                continue;
            }

            int s = i, t = _teamsSize;
            FlowNetwork G = new FlowNetwork(V);

            // Wire up all the other games.
            int gamesV = t;
            for (int team1 = 0; team1 < _teamsSize-1; ++team1) {
                if (team1 == s) continue;
                for (int team2 = team1+1; team2 < _teamsSize; ++team2) {
                    if (team2 == s) continue;
                    double cap = _games[team1][team2];
                    if (cap == 0) continue;
                    int w = ++gamesV;
                    // Start team to other teams game.
                    G.addEdge(new FlowEdge(s, w, cap));
                    // Other team game to the possible outcomes.
                    G.addEdge(new FlowEdge(w, team1, Double.POSITIVE_INFINITY));
                    G.addEdge(new FlowEdge(w, team2, Double.POSITIVE_INFINITY));
                }
            }
            // Wire up teams to termination.
            for (int team = 0; team < _teamsSize; ++team) {
                if (team == s) continue;
                double cap = _wins[s] + _remaining[s] - _wins[team];
                G.addEdge(new FlowEdge(team, t, cap));
            }

            FordFulkerson maxflow = new FordFulkerson(G, s, t);
            double maxflowValue = maxflow.value();

            boolean eliminated = false;
            for (FlowEdge e : G.adj(s)) {
                if ((s == e.from()) && e.flow() < e.capacity()) {
                    eliminated = true;
                }
            }

            if (eliminated) {
                List<String> winners = new ArrayList<String>();
                for (int team = 0; team < _teamsSize; ++team) {
                    if (team != s && maxflow.inCut(team)) {
                        winners.add(_teams[team]);
                    }
                }
                _certificates.add(i, winners);
            }
        }
    }
    // number of teams
    public int numberOfTeams() {
        return _teamsSize;
    }
    // all teams
    public Iterable<String> teams() {
        return _toTeam.keySet();
    }
    // number of wins for given team
    public int wins(String team) {
        _validateTeam(team);
        return _wins[_toTeam.get(team)];
    }
    // number of losses for given team
    public int losses(String team) {
        _validateTeam(team);
        return _losses[_toTeam.get(team)];
    }
    // number of remaining games for given team
    public int remaining(String team) {
        _validateTeam(team);
        return _remaining[_toTeam.get(team)];
    }
    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        _validateTeam(team1);
        _validateTeam(team2);
        return _games[_toTeam.get(team1)][_toTeam.get(team2)];
    }
    // is given team eliminated?
    public boolean isEliminated(String team) {
        _validateTeam(team);
        return certificateOfElimination(team) != null;
    }
    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        _validateTeam(team);
        return _certificates.get(_toTeam.get(team));
    }


    private void _validateTeam(String team) {
        if (!_toTeam.containsKey(team)) {
            throw new IllegalArgumentException(team + " is not a valid team.");
        }
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
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