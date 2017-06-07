package agents;

import java.util.LinkedList;

/**
 * Created by marco on 20/05/2017.
 */
public class FollowerSet {
    private static FollowerSet fs;
    private static LinkedList<RealAgent> followers;

    // <editor-fold defaultstate="collapsed" desc="Constructor and getInstance method">

    public FollowerSet(){}

    public synchronized static FollowerSet getInstance(){
        if(fs == null){
            fs = new FollowerSet();
            followers = new LinkedList<>();
        }
        return fs;
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Getters and setters">

    public boolean isFollower(RealAgent a){
        return followers.contains(a);
    }

    public LinkedList<RealAgent> getFollowers(){ return followers; }

    public void setFollowers(LinkedList<RealAgent> l){ followers = l; }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Adders and removers">

    public LinkedList<RealAgent> addFollower(RealAgent a){
        followers.add(a);
        return followers;
    }

    public LinkedList<RealAgent> removeFollower(RealAgent a){
        followers.remove(a);
        return followers;
    }

    // </editor-fold>
}
