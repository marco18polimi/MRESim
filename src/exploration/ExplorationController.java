package exploration;

import agents.RealAgent;
import config.Constants;
import environment.ContourTracer;
import environment.Frontier;

import java.awt.*;
import java.util.LinkedList;
import java.util.PriorityQueue;

/**
 * Created by marco on 05/04/2017.
 */
public class ExplorationController {

    //<editor-fold defaultstate="collapsed" desc="Calculate and set agent's frontiers">

    public static void calculateFrontiers(RealAgent agent) {
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
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Calculate and set agent's team positioning">

    public static void calculateTeamPositioning(RealAgent agent){

    }

    // </editor-fold>

}
