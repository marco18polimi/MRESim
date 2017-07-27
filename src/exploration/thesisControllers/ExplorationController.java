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

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Calculate agent's team positioning">

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

    // <editor-fold defaultstate="collapsed" desc="Calculate agents' goals">

    public static LinkedList<Point> calculateTeamGoals(){
        LinkedList<RealAgent> pool = IdleSet.getInstance().getPool();
        LinkedList<RealAgent> active = ActiveSet.getInstance().getActive();

        LinkedList<Point> goals = new LinkedList<>();
        for(RealAgent a: pool){
            goals.add(a.getCurrentGoal());
        }
        for(RealAgent a: active){
            goals.add(a.getCurrentGoal());
        }

        return goals;
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Set starting agent">

    private static class AgentFrontierPair {

        private RealAgent agent;
        private Frontier frontier;
        private double distance;

        public AgentFrontierPair(RealAgent a,Frontier f,double d){
            agent = a;
            frontier = f;
            distance = d;
        }

        public RealAgent getAgent(){ return agent; }

        public Frontier getFrontier(){ return frontier; }

        public double getDistance(){ return distance; }

        public void setAgent(RealAgent a){ agent = a; }

        public void setFrontier(Frontier f){ frontier = f; }

        public void setDistance(double d){ distance = d; }

    }

    public static void setStartingAgent(RealAgent agent,Environment env){
        ExplorationController.calculateFrontiers(agent,env);
        PriorityQueue<Frontier> frontiers = agent.getFrontiers();
        LinkedList<RealAgent> team = IdleSet.getInstance().getPool();

        LinkedList<AgentFrontierPair> pairs = new LinkedList<>();
        for(Frontier f: frontiers){
            Point currCenter = f.getCentre();
            for(RealAgent a: team){
                Point currLoc = a.getLocation();
                double currDist = currLoc.distance(currCenter);
                pairs.add(new AgentFrontierPair(a,f,currDist));
            }
        }

        AgentFrontierPair currPair = pairs.get(0);
        double currMin = currPair.getDistance();
        for(int i=1;i<pairs.size();i++){
            if(pairs.get(i).getDistance() < currMin){
                currMin = pairs.get(i).getDistance();
                currPair = pairs.get(i);
            }
        }

        currPair.getAgent().setStarter(true);
    }

    // </editor-fold>

}
