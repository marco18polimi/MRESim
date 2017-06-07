package exploration;

import agents.IdleSet;
import agents.RealAgent;
import environment.Frontier;

import java.util.LinkedList;
import java.util.concurrent.Semaphore;

/**
 * Created by marco on 07/06/2017.
 */
public class ReserveController {

    private static ReserveController rc;
    private static LinkedList<Frontier> callFrontiers;
    private static Semaphore sem;

    public ReserveController(){}

    // <editor-fold defaultstate="collapsed" desc="Get instance">
    public static ReserveController getInstance(){
        if(rc == null){
            rc = new ReserveController();
            callFrontiers = new LinkedList<>();
            sem = new Semaphore(1);
        }
        return rc;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Getters and setters">

    public LinkedList<Frontier> getCallFrontiers(){ return callFrontiers; }

    public Semaphore getSem(){ return sem; }

    public void setCallFrontiers(LinkedList<Frontier> f){ callFrontiers = f; }

    public void setSem(Semaphore s){ sem = s; }
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
    // </editor-fold>

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
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Add and remove methods">
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
    // </editor-fold>

}
