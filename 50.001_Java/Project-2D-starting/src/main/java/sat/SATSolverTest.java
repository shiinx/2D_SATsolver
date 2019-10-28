package sat;

/*
 import static org.junit.Assert.*;

 import org.junit.Test;
 */

import sat.env.*;
import sat.formula.*;

import java.io.*;


public class SATSolverTest {
    Literal a = PosLiteral.make("a");
    Literal b = PosLiteral.make("b");
    Literal c = PosLiteral.make("c");
    Literal na = a.getNegation();
    Literal nb = b.getNegation();
    Literal nc = c.getNegation();


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

    public static void solveSAT(String file) {
        System.out.println("File: " + file);
        Formula f2= parseCNF(file);
        String[] path = file.split("/");
        File fileDirectory = new File(file);

        String filename = fileDirectory.getParent() + "\\";

        System.out.println("SAT solve starts!!!");
        long started = System.nanoTime();
        Environment e = SATSolver.solve(f2);
        long time = System.nanoTime();
        long timeTaken = time - started;
        System.out.println("Time: " + timeTaken/1000000.0 + "ms");
        if (e != null) {
            System.out.println("satisfiable");
            File txtFile = new File(filename + "BoolAssignment"  + ".txt");


            if (!txtFile.exists()){
                try (Writer writeFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(txtFile), "utf-8"))){

                    String s = e.toString().replace("Environment:[", "");
                    s = s.replace("]", "");
                    String[] lines = s.split(", ");
                    for (String line: lines) {
                        writeFile.write(line + "\r\n");
                    }
                    writeFile.close();
                    System.out.println("Wrote solution to " + filename + "BoolAssignment_"  + ".txt");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

        }
        else {
            System.out.println("unsatisfiable");
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
    public void testSATSolver1(){
        // (a v b)
        Environment e = SATSolver.solve(makeFm(makeCl(a,b)) );
/*
        assertTrue( "one of the literals should be set to true",
                Bool.TRUE == e.get(a.getVariable())
                || Bool.TRUE == e.get(b.getVariable())  );

*/
    }


    public void testSATSolver2(){
        // (~a)
        Environment e = SATSolver.solve(makeFm(makeCl(na)));
/*
        assertEquals( Bool.FALSE, e.get(na.getVariable()));
*/
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