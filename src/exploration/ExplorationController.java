package exploration;

import agents.ActiveSet;
import agents.IdleSet;
import agents.RealAgent;
import config.Constants;
import environment.ContourTracer;
import environment.Environment;
import environment.Frontier;

import java.awt.*;
import java.util.LinkedList;
import java.util.PriorityQueue;

/**
 * Created by marco on 05/04/2017.
 */
public class ExplorationController {

    //<editor-fold defaultstate="collapsed" desc="Calculate and set agent's frontiers">

    public static LinkedList<Frontier> calculateFrontiers(RealAgent agent,Environment env) {
        // If recalculating frontiers, must set old frontiers dirty for image rendering
        for(Frontier f : agent.getFrontiers())
            agent.addDirtyCells(f.getPolygonOutline());

        LinkedList <LinkedList> contours = ContourTracer.findAllContours(agent.getOccupancyGrid());
        PriorityQueue<Frontier> frontiers = new PriorityQueue();
        Frontier currFrontier;

        for(LinkedList<Point> currContour : contours) {
            currFrontier = new Frontier(agent.getX(), agent.getY(), currContour);

            if (!agent.isBadFrontier(currFrontier)) {
                if(currFrontier.getArea() >= Constants.MIN_FRONTIER_SIZE){
                    {
                        frontiers.add(currFrontier);
                    }
                }
            }
        }
        agent.setFrontiers(frontiers);

        return filterCleanFrontiers(agent,env);
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Filter clean frontiers">

    public static LinkedList<Frontier> filterCleanFrontiers(RealAgent agent, Environment env) {
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

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Calculate and set agent's team positioning">

    public static LinkedList<Point> calculateTeamPositioning(){
        IdleSet iSet = IdleSet.getInstance();
        ActiveSet aSet = ActiveSet.getInstance();

        LinkedList<Point> positioning = new LinkedList<>();
        for(RealAgent a: iSet.getPool()){
            positioning.add(a.getLocation());
        }
        for(RealAgent a: aSet.getActive()){
            positioning.add(a.getLocation());
        }

        return positioning;
    }

    // </editor-fold>

}
