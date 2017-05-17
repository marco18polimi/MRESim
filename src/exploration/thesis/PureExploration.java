package exploration.thesis;

import agents.RealAgent;
import environment.Environment;
import environment.Frontier;
import exploration.ExplorationController;
import exploration.RandomWalk;
import exploration.SimulationFramework;
import path.Path;

import java.awt.*;
import java.util.LinkedList;
import java.util.PriorityQueue;

/**
 * Created by marco on 17/05/2017.
 */
public class PureExploration {

    public static Point takeStep(RealAgent agent, Environment env){

        if(agent.getTimeElapsed() > 0) {
            //Set agents frontiers
            ExplorationController.calculateFrontiers(agent);

            //Get agent positioning
            Point pos = agent.getLocation();

            //Choose navigation goal
            Point goal = goalFunction(agent,pos,env);
            return goal;
        }

        return agent.getLocation();

    }

    private static Point goalFunction(RealAgent agent,Point pos,Environment env){
        agent.getStats().setTimeSinceLastPlan(0);

        //Get agent's frontiers
        PriorityQueue<Frontier> frontiers = agent.getFrontiers();
        if(agent.getFrontiers().isEmpty()){
            Point base = agent.getTeammate(1).getLocation();
            agent.setMissionComplete(true);
            return base;
        }

        //Filter clean frotniers
        LinkedList<Frontier> clean = filterCleanFrontiers(agent,env);
        if(clean.isEmpty()){
            agent.setMissionComplete(true);
            agent.setPathToBaseStation();
            return agent.getNextPathPoint();
        }

        //Calculate closer frontier
        double min = 1000000000;
        Frontier closer = null;
        for(Frontier f: clean){
            if(pos.distance(f.getCentre()) < min){
                closer = f;
                min = pos.distance(f.getCentre());
            }
        }

        //Move agent
        Point goal = moveAgent(agent,closer);
        return goal;

    }

    private static LinkedList<Frontier> filterCleanFrontiers(RealAgent agent,Environment env) {
        PriorityQueue<Frontier> frontiers = agent.getFrontiers();
        LinkedList<Frontier> clean = new LinkedList<>();
        for(Frontier f: frontiers){
            if(!closeToObstacle(f.getCentre(),env)) {
                clean.add(f);
            }
        }

        return clean;
    }

    private static boolean closeToObstacle(Point p,Environment env){
        int x = (int)p.getX();
        int y = (int)p.getY();
        int margin = 2;

        if(env.obstacleAt(x,y+margin) || env.obstacleAt(x,y-margin) || env.obstacleAt(x+margin,y) || env.obstacleAt(x-margin,y)){
            return true;
        }
        return false;
    }

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

}
