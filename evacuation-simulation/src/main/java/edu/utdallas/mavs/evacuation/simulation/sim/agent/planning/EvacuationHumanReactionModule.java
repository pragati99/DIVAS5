package edu.utdallas.mavs.evacuation.simulation.sim.agent.planning;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jme3.math.Vector3f;

import edu.utdallas.mavs.divas.core.sim.agent.interaction.perception.data.CombinedReasonedData;
import edu.utdallas.mavs.divas.core.sim.agent.knowledge.internal.Goal;
import edu.utdallas.mavs.divas.core.sim.agent.planning.AbstractReactionModule;
import edu.utdallas.mavs.divas.core.sim.agent.planning.Plan;
import edu.utdallas.mavs.evacuation.simulation.sim.agent.interaction.HumanInteractionModule;
import edu.utdallas.mavs.evacuation.simulation.sim.agent.knowledge.EvacuationHumanKnowledgeModule;
import edu.utdallas.mavs.evacuation.simulation.sim.agent.knowledge.EvacuationHumanKnowledgeModule.MentalStateOfAgent;
import edu.utdallas.mavs.evacuation.simulation.sim.agent.task.HumanTaskModule;
import edu.utdallas.mavs.evacuation.simulation.sim.common.state.Posture;

public class EvacuationHumanReactionModule extends AbstractReactionModule<EvacuationHumanKnowledgeModule, HumanTaskModule>
{
    private static final long                           serialVersionUID = 1L;

    private final static Logger                         logger           = LoggerFactory.getLogger(EvacuationHumanReactionModule.class);

    private PathFinding<EvacuationHumanKnowledgeModule> pathFinding;
    private HumanInteractionModule                      interactionModule;

    private static final String                         BOMB             = "Bomb";
    private static final String                         VECTOR           = "Vector";

    private static final String                         POSITION         = "Position";

    /**
     * Where plans are stored
     */
    // protected PlanStorageModule planStorageModule;

    public EvacuationHumanReactionModule(EvacuationHumanKnowledgeModule knowledgeModule, HumanTaskModule taskModule, HumanInteractionModule humanInteractionModule, EvacuationHumanPathFinding pathFinding)
    {
        super(knowledgeModule, taskModule);
        this.interactionModule = humanInteractionModule;
        this.pathFinding = pathFinding;
    }

    @Override
    public Plan react()
    {
        reactToEvents();
        return planMovement();
    }

    protected Plan planMovement()
    {
        EvacuationPlan generatedPlan = new EvacuationPlan();

        if(knowledgeModule.getReactGoal() != null)
        {
            if(knowledgeModule.getCurrentMentalState().equals(MentalStateOfAgent.FLEEING))
            {
                knowledgeModule.desiredSpeed = knowledgeModule.getSelf().getMaxSpeed() / 2.5f;
                knowledgeModule.getSelf().setPosture(Posture.Run);
            }
            else
            {
                knowledgeModule.desiredSpeed = knowledgeModule.getSelf().getMaxSpeed() / 5;
                knowledgeModule.getSelf().setPosture(Posture.Walk);
            }
            pathFinding.pathplan((Vector3f) knowledgeModule.getReactGoal().getValue(), generatedPlan);
        }

        generatedPlan.setPlanUtility(knowledgeModule.getTime());
        // planStorageModule.addPlan(generatedPlan);
        knowledgeModule.getSelf().setAgentPath(makeVectorPath());
        return generatedPlan;
    }

    private List<Vector3f> makeVectorPath()
    {
        List<Vector3f> ret = new ArrayList<>();
        ret.add((Vector3f) knowledgeModule.getReactGoal().getValue());

        return ret;

    }

    protected void reactToEvents()
    {
        List<CombinedReasonedData> events = knowledgeModule.getEventsThisTick();

        for(int i = 0; i < events.size(); i++)
        {
            CombinedReasonedData thisEvent = events.get(i);
            Vector3f pos = knowledgeModule.getSelf().getPosition();

            // System.out.println(thisEvent.getPredicatedName() + " " + thisEvent.getCertaintyPercent());

            if(thisEvent.getPredicatedName().equals(BOMB) && (thisEvent.getCertaintyPercent() > 15))
            {
                int bombTime = 8;
                Vector3f runDir = new Vector3f();
                if(thisEvent.hasDirection())
                {
                    runDir = thisEvent.getDirection().mult(-1);
                    runDir.normalizeLocal().multLocal(bombTime * 2 + 5);
                    knowledgeModule.setThreatDirection(thisEvent.getDirection());
                }
                else if(thisEvent.hasOrigin())
                {
                    runDir = pos.subtract(thisEvent.getOrigin());
                    runDir.normalizeLocal().multLocal(bombTime * 2 + 5);
                    knowledgeModule.setThreatDirection(thisEvent.getOrigin().subtract(pos));
                }
                Vector3f finGoalDir = pos.add(runDir);
                finGoalDir.setY(0);
                long time = knowledgeModule.getTime() + 20;
                // knowledgeModule.getGoal().addGoals(new GoalModel(VECTOR, POSITION, finGoalDir, 100, time));
                knowledgeModule.setReactGoal(new Goal(VECTOR, POSITION, finGoalDir, 100, time));
                pathFinding.notifyReact();
                knowledgeModule.getGoals().remove(knowledgeModule.getCurrentGoal());
                knowledgeModule.setCurrentMentalState(MentalStateOfAgent.FLEEING);
                knowledgeModule.setPanicTime(bombTime - 1);
            }
        }
    }

}
