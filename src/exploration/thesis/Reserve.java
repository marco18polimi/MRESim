package exploration.thesis;

import agents.*;
import environment.Environment;
import environment.Frontier;
import exploration.ExplorationController;
import exploration.RandomWalk;
import exploration.SimulationFramework;
import path.Path;

import java.awt.*;
import java.util.LinkedList;

/**
 * Created by marco on 07/06/2017.
 */
public class Reserve {

    // <editor-fold defaultstate="collapsed" desc="TakeStep">
    /**
     * Handles timing and agents' roles during exploration
     * @param agent
     * @param env
     * @return nextStep
     */
    public static Point takeStep(RealAgent agent, Environment env){
        Point nextStep = agent.getLocation();

        // <editor-fold defaultstate="collapsed" desc="Get strategy support sets">
        IdleSet idleSet = IdleSet.getInstance();
        ActiveSet activeSet = ActiveSet.getInstance();
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="TIME = 0: set strategy support sets">
        if(agent.getTimeElapsed() == 0){
            idleSet.addPoolAgent(agent);
        }
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="TIME = 1 move starting agents">
        else if (agent.getTimeElapsed() == 1){
            //Choose the pair agent-frontier with the minimum distance and set that agent as starting one
            ExplorationController.setStartingAgent(agent,env);
            if(agent.getStarter()){
                //Re-plan activity
                Point goal = rePlan(agent,env);

                //Activate the agent from the pool
                idleSet.removePoolAgent(agent);
                activeSet.addActiveAgent(agent);

                nextStep = goal;
            }
        }
        // </editor-fold>

        return nextStep;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Re-plan">
    /**
     * Updates model exploration sets and calls model functions
     * @param agent
     * @param env
     * @return currentGoal
     */
    private static Point rePlan(RealAgent agent,Environment env){
        //F set
        LinkedList<Frontier> frontiers = ExplorationController.calculateFrontiers(agent,env);

        //P set
        LinkedList<Point> teamPositioning = ExplorationController.calculateTeamPositioning();

        //G set
        LinkedList<Point> teamGoals = ExplorationController.calculateTeamGoals();

        //Call appropriate goal function
        Point goal = null;
        goal = leaderGoalFunction(agent,frontiers,teamPositioning,teamGoals);

        return goal;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="BETA: goal function for leader agents">
    /**
     * Calculate goal location for leader agents
     * @param agent
     * @param frontiers
     * @param teamPositioning
     * @param teamGoals
     * @return leaderGoal
     */
    private static Point leaderGoalFunction(RealAgent agent,LinkedList<Frontier> frontiers,LinkedList<Point> teamPositioning,LinkedList<Point> teamGoals){
        agent.getStats().setTimeSinceLastPlan(0);

        //Check agent's frontiers
        if(agent.getFrontiers().isEmpty()){
            agent.setMissionComplete(true);
            agent.setPathToBaseStation();
            return agent.getNextPathPoint();
        }

        //Check clean frontiers
        if(frontiers.isEmpty()){
            agent.setMissionComplete(true);
            agent.setPathToBaseStation();
            return agent.getNextPathPoint();
        }

        //Calculate closer frontier
        Point pos = agent.getLocation();
        double min = 1000000000;
        Frontier closer = null;
        for(Frontier f: frontiers){
            if(pos.distance(f.getCentre()) < min){
                closer = f;
                min = pos.distance(f.getCentre());
            }
        }

        //Move agent
        Point goal = ExplorationController.moveAgent(agent,closer);
        return goal;

    }
    // </editor-fold>


}
