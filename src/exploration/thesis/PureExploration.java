package exploration.thesis;

import agents.ActiveSet;
import agents.IdleSet;
import agents.RealAgent;
import environment.Environment;
import environment.Frontier;
import exploration.ExplorationController;
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
    public static Point takeStep(RealAgent agent, Environment env){

        IdleSet idleSet = IdleSet.getInstance();
        ActiveSet activeAgents = ActiveSet.getInstance();

        // <editor-fold defaultstate="collapsed" desc="TIME 0: set strategy structure">
        if(agent.getTimeElapsed() == 0){
            idleSet.addPoolAgent(agent);
        }
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="TIME > 0: move agents">
        else if(agent.getTimeElapsed() > 0) {
            //Set agents frontiers
            LinkedList<Frontier> frontiers = ExplorationController.calculateFrontiers(agent,env);

            //Get agent positioning
            LinkedList<Point> teamPositioning = ExplorationController.calculateTeamPositioning();

            //Choose navigation goal
            Point goal = leaderGoalFunction(agent,frontiers,teamPositioning);
            return goal;
        }
        // </editor-fold>

        return agent.getLocation();

    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="BETA: goal function for leader agents">
    private static Point leaderGoalFunction(RealAgent agent,LinkedList<Frontier> frontiers,LinkedList<Point> teamPositioning){
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
