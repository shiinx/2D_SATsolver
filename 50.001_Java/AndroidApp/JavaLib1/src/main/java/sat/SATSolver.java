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
        if (clauses.size() == 0) {
            return env;
        } //env has no clauses

        Clause smallest_Clause = clauses.first();
        for (Clause i : clauses) {

            if (i.isEmpty()) { //Check for an empty clause
                return null;
            }
            if (smallest_Clause.size() > i.size()) { //track the smallest clause
                smallest_Clause = i;
            }
        }
        Literal literal = smallest_Clause.chooseLiteral(); // Randomly picking a literal
        if (smallest_Clause.isUnit()) { //if smallest only contains 1 literal
            if (literal instanceof PosLiteral) {
                Environment new_env_true = env.putTrue(literal.getVariable());
                return solve(substitute(clauses, literal), new_env_true);
            } else {
                Environment new_env_false = env.putFalse(literal.getVariable());
                return solve(substitute(clauses, literal), new_env_false);
            }
        } else {
            Environment new_env_true = env.putTrue(literal.getVariable());
            ImList<Clause> new_temp_clauses = substitute(clauses, literal);

            Environment possible_solution = solve(new_temp_clauses, new_env_true);
            if (possible_solution == null) {
                Environment new_env_false = env.putFalse(literal.getVariable());
                return solve(substitute(clauses, literal.getNegation()), new_env_false);

            } else {
                return possible_solution;
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