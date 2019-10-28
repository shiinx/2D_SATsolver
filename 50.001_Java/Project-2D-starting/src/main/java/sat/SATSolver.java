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
public class SATSolver {
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
            return env;                                 //if no clauses left, return environment
        }
        int literalsInClause = 2000;                    //set a value for smallest number of literals in a clause
        Clause shortestClause = null;                   //to find the shortest clause
        Iterator<Clause> iter = clauses.iterator();
        while (iter.hasNext()){                         //iterating through the list of clauses
            Clause c = iter.next();
            int size = c.size();
            if (size == 0)                              // if no literals in the clause, return null
                return null;

            if (size < literalsInClause){
                shortestClause = c;                     //while number of literals in clause is less than the defined value, set that number as the defined value for literalsInClause
                literalsInClause = size;                //while number of literals in clause is less than the defined value, set that clause as the shortest clause
            }
        }
        if (literalsInClause == 1){                                                     //if only 1 literal in the clause, set its value to true
            env = env.putTrue(shortestClause.chooseLiteral());
            return (solve(substitute(clauses,shortestClause.chooseLiteral()), env));
        }
        else{
            Literal l = shortestClause.chooseLiteral();                                 //choose the first literal in the clause
            env = env.putTrue(l);                                                       //and assign it a TRUE value
            Environment testAns = solve(substitute(clauses, l),env);                    //create new clause list and solve
            if (testAns == null){                                                       //if is unsatisfied
                env = env.putFalse(l);                                                  //set FALSE value instead
                return solve(substitute(clauses,l.getNegation()),env);                  //create new clause list and solve
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
        ImList<Clause> outpClauseList = new EmptyImList<Clause>();              //create empty new list of clauses
        Iterator<Clause> iterator = clauses.iterator();                         //create iterator of input list of clauses
        while (iterator.hasNext()){
            Clause clause = iterator.next();
            if (clause.contains(l)||clause.contains(l.getNegation()))           //as long as the input literal is within the clause, regardless of value
                clause = clause.reduce(l);                                      //remove it from the clause
            if (clause != null) outpClauseList = outpClauseList.add(clause);    //add the clause to the created list of clauses
        }
        return outpClauseList;
    }

}