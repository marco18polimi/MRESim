package exploration.thesisStrategies;

import agents.RealAgent;
import environment.Environment;
import environment.Frontier;

import java.awt.*;
import java.util.LinkedList;

/**
 * Created by marco on 28/07/2017.
 */
public class DivideAndConquer {

    // <editor-fold defaultstate="collapsed" desc="TakeStep">
    /**
     * Handles timing and agents' roles during exploration
     *
     * @param agent
     * @param env
     * @return nextStep
     */
    public static Point takeStep(RealAgent agent, Environment env) {
        return agent.getLocation();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Re-plan">
    /**
     * Updates model exploration sets and calls model functions
     *
     * @param agent
     * @param env
     * @return currentGoal
     */
    private static Point rePlan(RealAgent agent, Environment env) {
        return agent.getLocation();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="BETA: goal function for leader agents">
    /**
     * Calculate goal location for leader agents
     *
     * @param agent
     * @param frontiers
     * @param teamPositioning
     * @param teamGoals
     * @return leaderGoal
     */
    private static Point leaderGoalFunction(RealAgent agent, LinkedList<Frontier> frontiers, LinkedList<Point> teamPositioning, LinkedList<Point> teamGoals) {
        return agent.getLocation();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="GAMMA: goal function for follower agents">
    /**
     * Calculate goal location for leader agents
     * @param agent
     * @param frontiers
     * @param teamPositioning
     * @param teamGoals
     * @return leaderGoal
     */
    private static Point followerGoalFunction(RealAgent agent,LinkedList<Frontier> frontiers,LinkedList<Point> teamPositioning,LinkedList<Point> teamGoals){
        return agent.getLocation();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="PHI: splitting function">
    private static Point splittingFunction(RealAgent agent){
        return agent.getLocation();
    }
    // </editor-fold>
}
