package edu.utdallas.mavs.evacuation.visualization.vis3D.spectator;

import edu.utdallas.mavs.divas.core.sim.common.event.EnvEvent;
import edu.utdallas.mavs.divas.core.sim.common.state.AgentState;
import edu.utdallas.mavs.divas.core.sim.common.state.EnvObjectState;
import edu.utdallas.mavs.divas.visualization.vis3D.spectator.PlayGround;
import edu.utdallas.mavs.divas.visualization.vis3D.vo.AgentVO;
import edu.utdallas.mavs.divas.visualization.vis3D.vo.EnvObjectVO;
import edu.utdallas.mavs.divas.visualization.vis3D.vo.EventVO;
import edu.utdallas.mavs.evacuation.simulation.sim.common.state.EHumanAgentState;
import edu.utdallas.mavs.evacuation.visualization.vis3D.vo.EvacuationEnvObjectVO;
import edu.utdallas.mavs.evacuation.visualization.vis3D.vo.EvacuationEventVO;
import edu.utdallas.mavs.evacuation.visualization.vis3D.vo.EvacuationHumanAgentVO;

public class EvacuationPlayground extends PlayGround
{

    @Override
    protected AgentVO<?> createAgentVO(AgentState state, long cycle)
    {
        return new EvacuationHumanAgentVO((EHumanAgentState) state, cycle);
    }

    @Override
    protected EnvObjectVO createEnvObjectVO(EnvObjectState state, long cycle)
    {
        return new EvacuationEnvObjectVO(state, cycle);
    }

    @Override
    protected EventVO createEventVO(EnvEvent event, long cycle)
    {
        return new EvacuationEventVO(event, cycle);
    }

}
