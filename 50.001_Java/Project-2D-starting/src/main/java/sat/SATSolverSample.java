package sat;

import java.util.Iterator;

import immutable.EmptyImList;
import immutable.ImList;
import sat.env.Bool;
import sat.env.Environment;
import sat.formula.Clause;
import sat.formula.Formula;
import sat.formula.Literal;

/**
 * A simple DPLL SAT solver. See http://en.wikipedia.org/wiki/DPLL_algorithm
 */
public class SATSolverSample {
    /**
     * Solve the problem using a simple version of DPLL with backtracking and
     * unit propagation. The returned environment binds literals of class
     * bool.Variable rather than the special literals used in clausification of
     * class clausal.Literal, so that clients can more readily use it.
     *
     * @return an environment for which the problem evaluates to Bool.TRUE, or
     *         null if no such environment exists.
     */
    public static Environment solve(Formula formula) {
        Environment env = new Environment();
        return solve(formula.getClauses(), env);

    }

    /**
     * Takes a partial assignment of variables to values, and recursively
     * searches for a complete satisfying assignment.
     *
     * @param clauses
     *            formula in conjunctive normal form
     * @param env
     *            assignment of some or all variables in clauses to true or
     *            false values.
     * @return an environment for which all the clauses evaluate to Bool.TRUE,
     *         or null if no such environment exists.
     */
    private static Environment solve(ImList<Clause> clauses, Environment env) {
        if (clauses.size() == 0){
            return env;
        }
        int literalsInClause = 2000;
        Clause shortestClause = null;
        Iterator<Clause> iter = clauses.iterator();
        while (iter.hasNext()){
            Clause c = iter.next();
            int size = c.size();
            if (size == 0)
                return null;

            if (size < literalsInClause){
                shortestClause = c;
                literalsInClause = size;
            }
        }
        if (literalsInClause == 1){
            env = env.putTrue(shortestClause.chooseLiteral()); //choose literal picks the first literal
            return (solve(substitute(clauses,shortestClause.chooseLiteral()), env));
        }
        else{
            Literal l = shortestClause.chooseLiteral(); // setting to true
            env = env.putTrue(l);
            Environment testAns = solve(substitute(clauses, l),env);
            if (testAns == null){
                env = env.putFalse(l);
                return solve(substitute(clauses,l.getNegation()),env);
            }
            else{
                return testAns;
            }
        }

    }

    /**
     * given a clause list and literal, produce a new list resulting from
     * setting that literal to true
     *
     * @param clauses
     *            , a list of clauses
     * @param l
     *            , a literal to set to true
     * @return a new list of clauses resulting from setting l to true
     */
    private static ImList<Clause> substitute(ImList<Clause> clauses,
                                             Literal l) {
        ImList<Clause> output = new EmptyImList<Clause>();
        Iterator<Clause> iterator = clauses.iterator();
        while (iterator.hasNext()){
            Clause clause = iterator.next();
            if (clause.contains(l)||clause.contains(l.getNegation()))
                clause = clause.reduce(l);
            if (clause != null) output = output.add(clause);
        }
        return output;
    }

}