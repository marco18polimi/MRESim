package exploration.thesisControllers;

import agents.RealAgent;
import agents.sets.FollowerSet;
import agents.sets.IdleSet;
import agents.sets.LeaderSet;
import environment.Frontier;
import exploration.SimulationFramework;

import java.awt.*;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

/**
 * Created by marco on 16/07/2017.
 */
@SuppressWarnings("Duplicates")
public class BuddyController {
    // <editor-fold defaultstate="collapsed" desc="Variables">
    private static BuddyController bc;
    private static LinkedList<Frontier> callFrontiers;
    private static LinkedList<ExplorationController.AgentFrontierPair> followerFrontiers;
    private static Semaphore sem;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Constructor">
    public BuddyController(){}
    // /editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Get instance">
    public static synchronized BuddyController getInstance(){
        if(bc == null){
            bc = new BuddyController();
            callFrontiers = new LinkedList<>();
            followerFrontiers = new LinkedList<>();
            sem = new Semaphore(1);
        }
        return bc;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Getters and setters">
    public static LinkedList<ExplorationController.AgentFrontierPair> getFollowerFrontiers() {
        return followerFrontiers;
    }

    public static void setFollowerFrontiers(LinkedList<ExplorationController.AgentFrontierPair> followerFrontiers) {
        BuddyController.followerFrontiers = followerFrontiers;
    }

    public LinkedList<Frontier> getCallFrontiers(){ return callFrontiers; }

    public Semaphore getSem(){ return sem; }

    public void setCallFrontiers(LinkedList<Frontier> f){ callFrontiers = f; }

    public void setSem(Semaphore s){ sem = s; }

    public void setAssignedFrontier(Frontier f){
        for(Frontier cf : this.callFrontiers){
            if(cf.isClose(f)){
                cf.setAssigned(true);
            }
        }
    }

    public LinkedList<Frontier> getNotAssignedFrontiers(){
        LinkedList<Frontier> notAssigned = new LinkedList<>();
        for(Frontier f : this.getCallFrontiers()){
            if(!f.getAssigned()){
                notAssigned.add(f);
            }
        }
        return notAssigned;
    }

    public LinkedList<Frontier> getAssignedFrontiers(){
        LinkedList<Frontier> assigned = new LinkedList<>();
        for(Frontier f : this.getCallFrontiers()){
            if(f.getAssigned()){
                assigned.add(f);
            }
        }
        return assigned;
    }

    public Frontier getFollowerFrontier(RealAgent follower){
        for(ExplorationController.AgentFrontierPair pair : followerFrontiers){
            if(pair.getAgent().getID() == follower.getID()){
                return pair.getFrontier();
            }
        }
        return null;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Add and remove methods">
    public void addCallFrontiers(LinkedList<Frontier> callFronts){
        for(Frontier f : callFronts) {
            boolean alreadyIn = false;
            for(Frontier cf : callFrontiers){
                if(f.isClose(cf)){
                    alreadyIn = true;
                }
            }

            if(!alreadyIn) {
                callFrontiers.add(f);
            }
        }
    }

    public void addFolowerFrontier(Frontier splitFront,RealAgent follower){
        double distance = splitFront.getCentre().distance(follower.getLocation());
        ExplorationController.AgentFrontierPair pair = new ExplorationController.AgentFrontierPair(follower,splitFront,distance);
        followerFrontiers.add(pair);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Utils">
    public static Frontier chooseBestReservePair(RealAgent agent, LinkedList<Frontier> call){
        LinkedList<Frontier> candidates = filterCandidateFrontiers(agent,call);
        if(candidates.isEmpty()){
            return null;
        }

        Frontier choice = null;
        double min = agent.getLocation().distance(candidates.get(0).getCentre());
        for(Frontier can : candidates){
            double curr = agent.getLocation().distance(can.getCentre());
            if(curr < min){
                min = curr;
                choice = can;
            }
        }
        if(choice == null){
            choice = candidates.get(0);
        }

        return choice;
    }

    private static LinkedList<Frontier> filterCandidateFrontiers(RealAgent agent, LinkedList<Frontier> call){
        LinkedList<Frontier> candidates = new LinkedList<>();
        for(Frontier cf : call) {
            boolean closer = true;
            double min = agent.getLocation().distance(cf.getCentre());
            for (RealAgent a : IdleSet.getInstance().getPool()) {
                double curr = a.getLocation().distance(cf.getCentre());
                if(curr < min && a.getID() != 1){
                    closer = false;
                }
            }
            if(closer){
                candidates.add(cf);
            }
        }
        return candidates;
    }

    public static Frontier checkSplit(LinkedList<Frontier> frontiers,Frontier selected,RealAgent leader){
        Point pos = leader.getLocation();
        double min = 1000000000;
        Frontier closer = null;
        for(Frontier f: frontiers){
            if(pos.distance(f.getCentre()) < min && !f.isClose(selected)){
                closer = f;
                min = pos.distance(f.getCentre());
            }
        }
        return closer;
    }

    public static void handleSplit(RealAgent follower){
        //Update leader and follower sets
        LeaderSet.getInstance().addLeader(follower);
        FollowerSet.getInstance().removeFollower(follower);

        //Update buddy couple
        RealAgent leader = follower.getBuddy();
        leader.setBuddy(leader);
        follower.setBuddy(follower);
    }
    // </editor-fold>
}
