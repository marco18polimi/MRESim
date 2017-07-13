package agents;

/**
 * Created by marco on 13/07/2017.
 */
public class AgentCouple {
    // <editor-fold defaultstate="collapsed" desc="Variables">
    private RealAgent leader;
    private RealAgent follower;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Constructors">
    public AgentCouple(RealAgent lead, RealAgent follow){
        leader = lead;
        follower = follow;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Getters and setters">
    public RealAgent getLeader(){ return leader; }

    public RealAgent getFollower(){ return follower; }

    public void setLeader(RealAgent lead){ leader = lead; }

    public void setFollower(RealAgent follow){ follower = follow; }
    // </editor-fold>
}
