package sat;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;

import sat.env.Variable;
import sat.formula.Literal;
import sat.formula.PosLiteral;

class Graph {

    private HashMap<Literal, LinkedList<Literal>> listVertices = new HashMap<>();
    private TreeMap<String, Boolean> mapResult = new TreeMap<>();
    private Set<Literal> SCC = new HashSet<>();
    private int C = 0;
    private Stack<Literal> i = new Stack<>();
    private Stack<Literal> j = new Stack<>();
    private HashMap<Literal, Integer> n = new HashMap<>();

    String getResult() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Boolean> entry : mapResult.entrySet()) {
            if (entry.getValue() == Boolean.TRUE) {
                sb.append("1");
            } else {
                sb.append("0");
            }
            sb.append(" ");
        }
        return sb.toString();
    }

    // for implying
    void addEdge(Literal v, Literal w) {
        LinkedList<Literal> e = listVertices.get(v);
        if (e == null)
            e = new LinkedList<>();
        e.add(w);
        listVertices.put(v, e);
    }

    void doDFS(Literal k, HashMap<Literal, Boolean> visited, Stack<Literal> stack) throws Exception {
        visited.put(k, true);   // visited, put true
        n.put(k, C++);
        i.push(k);              // push k into i stack
        j.push(k);              // push k into j stack
        Literal adj;
        if (listVertices.get(k) != null) {
            Iterator<Literal> it = listVertices.get(k).iterator();
            while (it.hasNext()) {
                adj = it.next();
                Boolean isVisited = visited.get(adj);
                if (!n.containsKey(adj) && (isVisited == null || !isVisited)) {
                    // if not yet visited
                    doDFS(adj, visited, stack);
                } else if (!SCC.contains(adj)) {
                    while (n.get(j.peek()) > n.get(adj)) {
                        j.pop();
                    }
                }
            }
        }


        if (k.equals(j.peek())) {
            Set<Literal> component = new HashSet<>();
            Literal popped;
            do {
                popped = i.pop();
                // if within a SSC contains a negation of itself, it is unsatisfiable
                if (component.contains(popped.getNegation()))
                    throw new Exception("UNSATISFIABLE.");
                component.add(popped);
                SCC.add(popped);
            } while (popped != k);
            j.pop();
        }
        stack.push(k);
    }

    void doDFS() throws Exception {
        Stack<Literal> stack = new Stack<>();
        HashMap<Literal, Boolean> visited = new HashMap<>();
        for (Literal k : listVertices.keySet()) {
            Boolean isVisited = visited.get(k);
            if (isVisited == null || !isVisited) {
                doDFS(k, visited, stack);
            }
        }

        while (!stack.isEmpty()) {
            Literal literal = stack.pop();
            Variable v = literal.getVariable();
            if (mapResult.get(v.toString()) == null && mapResult.get(literal.getNegation().getVariable().toString()) == null)
                mapResult.put(v.toString(), !(literal instanceof PosLiteral));
        }
    }
}
