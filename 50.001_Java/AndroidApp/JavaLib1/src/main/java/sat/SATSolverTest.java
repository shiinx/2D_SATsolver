package sat;

/*
 import static org.junit.Assert.*;

 import org.junit.Test;
 */

import sat.formula.*;
import java.io.*;
import java.util.Iterator;


public class SATSolverTest {
    Literal a = PosLiteral.make("a");
    Literal b = PosLiteral.make("b");
    Literal c = PosLiteral.make("c");
    Literal na = a.getNegation();
    Literal nb = b.getNegation();
    Literal nc = c.getNegation();
    private static Graph g = new Graph();


    public static Formula parseCNF(String filename) {
        try {
            BufferedReader br1 = new BufferedReader(new FileReader(filename));
            String line1;
            Clause newClause = new Clause();
            Formula newFormula = new Formula();

            while ((line1 = br1.readLine()) != null) {
                if (line1.equals("") || line1.charAt(0) == 'c' || line1.charAt(0) == 'p') {
                    continue;
                }
                String[] literalStrings = line1.split("\\s+");
                for (String literalString : literalStrings) {
                    if (literalString.equals("")) {
                        continue;
                    }
                    if (!literalString.equals("0")) {
                        Literal literal;
                        if (literalString.charAt(0) == '-') {
                            literal = NegLiteral.make(literalString.substring(1));
                        } else {
                            literal = PosLiteral.make(literalString);
                        }
                        newClause = newClause.add(literal);
                    } else {
                        newFormula = newFormula.addClause(newClause);
                        newClause = new Clause();
                    }
                }
            }
            return newFormula;
        } catch (FileNotFoundException ex) {
            System.out.println("File not found.");
            return null;
        } catch (java.io.IOException ex) {
            System.out.println("Error reading file.");
            return null;
        }
    }

    public static Graph getGraph(){
        return g;
    }

    public static void doEdges(Formula f){
        Iterator<Clause> it;
        it = f.getClauses().iterator();
        while(it.hasNext()){
            Iterator<Literal> it2;
            it2 = it.next().iterator();
            while(it2.hasNext()){
                Literal l = it2.next();
                Literal l2;
                if (it2.hasNext()) {
                    l2 = it2.next();
                }else{
                    l2 = l;
                }
                g.addEdge(l.getNegation() , l2);
                g.addEdge(l2.getNegation() , l);
            }
        }
    }


    public static void solveSAT(String file) {
        System.out.println("File: " + file);
        Formula f2= parseCNF(file);
        if(f2 != null) {
            doEdges(f2);
            System.out.println("SAT solve starts!!!");
            Graph graph = getGraph();
            long started = System.nanoTime();
            try {
                graph.doDFS();
                long time = System.nanoTime();
                long timeTaken = time - started;
                System.out.println("Time:" + timeTaken/1_000_000.0 + "ms");
                System.out.println("Satisfiable");
                System.out.println(graph.getResult());
            } catch(Exception e){
                // if unsatisfiable, will enter exception and print unsatisfiable
                System.out.println(e);
            }
        }
        else{
            System.out.println("File error");
        }


    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("File not provided");
            System.exit(1);
        }

        String filename = args[0];

        solveSAT(filename);

    }


    private static Formula makeFm(Clause... e) {
        Formula f = new Formula();
        for (Clause c : e) {
            f = f.addClause(c);
        }
        return f;
    }

    private static Clause makeCl(Literal... e) {
        Clause c = new Clause();
        for (Literal l : e) {
            c = c.add(l);
        }
        return c;
    }



}