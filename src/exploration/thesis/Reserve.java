package exploration.thesis;

import agents.*;
import environment.Environment;
import environment.Frontier;
import exploration.ExplorationController;
import exploration.RandomWalk;
import exploration.ReserveController;
import exploration.SimulationFramework;
import path.Path;

import java.awt.*;
import java.util.LinkedList;

/**
 * Created by marco on 07/06/2017.
 */
@SuppressWarnings("Duplicates")
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

        //Call appropriate goal function
        Point goal = null;
        goal = leaderGoalFunction(agent,frontiers,teamPositioning,teamGoals);

        return goal;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="ALPHA: Activation function">
    private static Point activationFunction(RealAgent agent){
        agent.getStats().setTimeSinceLastPlan(0);
        Point activationGoal = null;

        try{
            ReserveController.getInstance().getSem().acquire();
            LinkedList<Frontier> freeCall = ReserveController.getInstance().getNotAssignedFrontiers();

            if(!freeCall.isEmpty()){
                Frontier f = ReserveController.getInstance().chooseBestReservePair(agent,freeCall);
                if(f!=null){
                    agent.setFirstCall(true);
                    ReserveController.getInstance().setAssignedFrontier(f);

                    IdleSet.getInstance().removePoolAgent(agent);
                    ActiveSet.getInstance().addActiveAgent(agent);

                    activationGoal = ExplorationController.moveAgent(agent,f);
                }
            }
        }catch(InterruptedException e){
            //Do something
        }finally{
            ReserveController.getInstance().getSem().release();
        }

        return activationGoal;
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

        //Call reserve agents
        frontiers.remove(closer);
        ReserveController.getInstance().addCallFrontiers(frontiers);

        //Move agent
        Point goal = ExplorationController.moveAgent(agent,closer);
        return goal;

    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="LAMBDA: proactivity function">
    private static Point proactivityFunction(RealAgent agent){
        return agent.getLocation();
    }
    // </editor-fold>
}
