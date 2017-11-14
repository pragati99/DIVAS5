package edu.utdallas.mavs.evacuation.simulation.sim.agent.task;

import java.io.Serializable;
import java.util.Set;

import edu.utdallas.mavs.divas.core.sim.agent.task.AbstractTaskModule;
import edu.utdallas.mavs.evacuation.simulation.sim.agent.knowledge.EvacuationHumanKnowledgeModule;

public class HumanTaskModule extends AbstractTaskModule<EvacuationHumanKnowledgeModule> implements Serializable
{
    private static final long serialVersionUID = 1L;

    public HumanTaskModule(EvacuationHumanKnowledgeModule knowledgeModule)
    {
        super(knowledgeModule);
    }

    @Override
    protected void loadTasks()
    {
        Set<String> availableTasks = knowledgeModule.getSelf().getTaskNames();
        tasks.put(MoveTask.NAME, new MoveTask(availableTasks.contains(MoveTask.NAME)));
        tasks.put(FaceTask.NAME, new FaceTask(availableTasks.contains(FaceTask.NAME)));
        tasks.put(OpenDoorTask.NAME, new OpenDoorTask(availableTasks.contains(OpenDoorTask.NAME)));
        tasks.put(CloseDoorTask.NAME, new CloseDoorTask(availableTasks.contains(CloseDoorTask.NAME)));
        tasks.put(IdleTask.NAME, new IdleTask(availableTasks.contains(IdleTask.NAME)));
    }
}
