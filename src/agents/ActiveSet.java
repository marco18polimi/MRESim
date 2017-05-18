package agents;

import java.util.LinkedList;

/**
 * Created by marco on 18/05/2017.
 */
public class ActiveSet {
    private static ActiveSet as;
    private static LinkedList<RealAgent> active;

    // <editor-fold defaultstate="collapsed" desc="Constructor and getInstance method">
    public ActiveSet(){}

    public synchronized  static ActiveSet getInstance() {
        if(as == null){
            as = new ActiveSet();
            active = new LinkedList<>();
        }
        return as;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Getters and setters">
    public LinkedList<RealAgent> getActive(){ return active; }

    public void setActive(LinkedList<RealAgent> a){ active = a; }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Adders and removers">
    public LinkedList<RealAgent> addActiveAgent(RealAgent a){
        active.add(a);
        return active;
    }

    public LinkedList<RealAgent> removeActiveAgent(RealAgent a){
        active.remove(a);
        return active;
    }
    // </editor-fold>
}
