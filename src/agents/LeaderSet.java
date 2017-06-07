package agents;

import java.util.LinkedList;

/**
 * Created by marco on 20/05/2017.
 */
public class LeaderSet {
    private static LeaderSet ls;
    private static LinkedList<RealAgent> leaders;

    // <editor-fold defaultstate="collapsed" desc="Constructor and getInstance method">

    public LeaderSet(){}

    public synchronized static LeaderSet getInstance(){
        if(ls == null){
            ls = new LeaderSet();
            leaders = new LinkedList<>();
        }
        return ls;
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Getters and setters">

    public boolean isLeader(RealAgent a){
        return leaders.contains(a);
    }

    public LinkedList<RealAgent> getLeaders(){ return leaders; }

    public void setLeaders(LinkedList<RealAgent> l){ leaders = l; }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Adders and removers">

    public LinkedList<RealAgent> addLeader(RealAgent a){
        leaders.add(a);
        return leaders;
    }

    public LinkedList<RealAgent> removeLeader(RealAgent a){
        leaders.remove(a);
        return leaders;
    }

    // </editor-fold>
}
