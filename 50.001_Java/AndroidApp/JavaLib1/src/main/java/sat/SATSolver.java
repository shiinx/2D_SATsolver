package sat;

import java.util.Iterator;

import immutable.EmptyImList;
import immutable.ImList;
import sat.env.Environment;
import sat.formula.Clause;
import sat.formula.Formula;
import sat.formula.Literal;
import sat.formula.PosLiteral;

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
        if (clauses.size()==0){   //env has no clauses
            return env;
        }

        Clause smallestC = clauses.first();
        for(Clause i: clauses) {
            if (i.isEmpty()) {                                  //Check for an empty clause using for each loop
                return null;
            }
            if (smallestC.size() > i.size()) {
                smallestC = i;                                  //Assign clause as smallest clause
            }
        }
        Literal randomL = smallestC.chooseLiteral();            //Choose 1 literal at random from smallest clause
            if (smallestC.isUnit()) {                           //Check if smallest clause contains only 1 literal
                if (randomL instanceof PosLiteral) {            //Given clause only 1 Literal, if this literal is pos, assign it to be true
                    Environment newEnvT = env.putTrue(randomL.getVariable());
                    return solve(substitute(clauses,randomL), newEnvT);
            }
                else {                                          //if this literal is neg, assign it to be false and send to solve
                    Environment newEnvF = env.putFalse(randomL.getVariable());
                    return solve(substitute(clauses,randomL), newEnvF);
            }
        }
            else {                                              //if smallest clause has more than 1 literal
                Environment newEnvT = env.putTrue(randomL.getVariable()); //Assign literal true
                ImList<Clause> newClauses = substitute(clauses, randomL); //Create a temp new list of clauses of substitute class

                Environment potSoln = solve(newClauses,newEnvT); //Create new environment and solve by settingb lit to True
                if (potSoln != null){
                    return potSoln;

                }
                else{
                    Environment newEnvF = env.putFalse(randomL.getVariable());
                    return solve(substitute(clauses,randomL.getNegation()), newEnvF);
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
            if (clause.contains(l)||clause.contains(l.getNegation()))           //as long as the input literal is within the clause, regardless of true/false
                clause = clause.reduce(l);                                      //perform reduction ie, if its true, remove entire clause, else remove only literal l
            if (clause != null) outpClauseList = outpClauseList.add(clause);    //add the clause to the new list of clauses
        }
        return outpClauseList;
    }

}