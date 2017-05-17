package exploration.thesis;

import agents.RealAgent;
import environment.Environment;
import exploration.ExplorationController;

import java.awt.*;

/**
 * Created by marco on 17/05/2017.
 */
public class PureExploration {
    public static Point takeStep(RealAgent agent, Environment env){

        //Set agents frontiers
        ExplorationController.calculateFrontiers(agent);

        //Set team positioning
        ExplorationController.calculateTeamPositioning(agent);


        return agent.getLocation();
    }
}
