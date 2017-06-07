package exploration.thesis;

import agents.*;
import environment.Environment;
import environment.Frontier;
import exploration.ExplorationController;
import exploration.LeaderFollower;
import exploration.RandomWalk;
import exploration.SimulationFramework;
import path.Path;

import java.awt.*;
import java.util.LinkedList;

/**
 * Created by marco on 17/05/2017.
 */
public class PureExploration {

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
        LeaderSet leaderSet = LeaderSet.getInstance();
        FollowerSet followerSet = FollowerSet.getInstance();
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="TIME = 0: set strategy support sets">
        if(agent.getTimeElapsed() == 0){
            idleSet.addPoolAgent(agent);
            leaderSet.addLeader(agent);
        }
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="TIME = 1 move starting agents">
        else if (agent.getTimeElapsed() == 1){
            ExplorationController.setStartingAgent(agent,env);
            if(agent.getStarter()){
                Point goal = rePlan(agent,env);
                idleSet.removePoolAgent(agent);
                activeSet.addActiveAgent(agent);
                nextStep = goal;
            }
        }
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="TIME > 1: move remaining agents">
        else if(agent.getTimeElapsed() > 1) {
            if(activeSet.isActive(agent)) {
                Point goal = rePlan(agent,env);
                nextStep = goal;
            }else{
                Point activationGoal = activationFunction(agent);
                if(activationGoal != null){
                    idleSet.removePoolAgent(agent);
                    activeSet.addActiveAgent(agent);
                    nextStep = activationGoal;
                }else{
                    Point proactivityGoal = proactivityFunction(agent);
                    nextStep = proactivityGoal;
                }
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

        //Call goal function
        Point goal = null;
        if(LeaderSet.getInstance().isLeader(agent)){
            goal = leaderGoalFunction(agent,frontiers,teamPositioning,teamGoals);
        }else if(FollowerSet.getInstance().isFollower(agent)){
            Point splittingGoal = splittingFunction(agent);
            if(splittingGoal != null){
                goal = splittingGoal;
            }else {
                goal = followerGoalFunction(agent, frontiers, teamPositioning, teamGoals);
            }
        }

        return goal;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="ALPHA: Activation function">
    private static Point activationFunction(RealAgent agent){
        return agent.getLocation();
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
        Point goal = moveAgent(agent,closer);
        return goal;

    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="GAMMA: goal function for following agents">
    /**
     * Calculate goal location for following agents
     * @param agent
     * @param frontiers
     * @param teamPositioning
     * @param teamGoals
     * @return followerGoal
     */
    private static Point followerGoalFunction(RealAgent agent,LinkedList<Frontier> frontiers,LinkedList<Point> teamPositioning,LinkedList<Point> teamGoals){
        return agent.getLocation();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="LAMBDA: proactivity function">
    private static Point proactivityFunction(RealAgent agent){
        return agent.getLocation();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="PHI: splitting function">
    private static Point splittingFunction(RealAgent agent){
        return agent.getLocation();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="MoveAgent">
    private static Point moveAgent(RealAgent agent,Frontier f){
        Point nextStep;
        Path goal = agent.calculatePath(agent.getLocation(),f.getCentre());
        agent.setPath(goal);

        // <editor-fold defaultstate="collapsed" desc="Handle path errors">
        if (agent.getPath() == null
                || agent.getPath().getPoints() == null
                || agent.getPath().getPoints().isEmpty()) {
            SimulationFramework.log("Path problems","errConsole");
            LinkedList<Point> outline = f.getPolygonOutline();
            boolean found = false;

            for(Point p : outline){
                SimulationFramework.log("Out. point: "+p,"errConsole");
                Path curr = agent.calculatePath(agent.getLocation(),p);
                agent.setPath(curr);
                if (!(agent.getPath() == null)
                        && !(agent.getPath().getPoints() == null)
                        && !agent.getPath().getPoints().isEmpty()){
                    found = true;
                }
            }

            if(!found) {
                nextStep = RandomWalk.takeStep(agent);
                agent.setEnvError(false);
                return nextStep;
            }

            SimulationFramework.log("Path problems SOLVED","errConsole");
        }
        // </editor-fold>

        agent.getPath().getPoints().remove(0);
        return agent.getNextPathPoint();
    }
    // </editor-fold>

}
