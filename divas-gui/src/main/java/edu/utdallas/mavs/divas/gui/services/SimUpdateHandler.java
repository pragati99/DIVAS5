/*package edu.utdallas.mavs.divas.gui.services;

import javafx.application.Platform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.pennychecker.eventbus.EventBus;

import edu.utdallas.mavs.divas.core.client.SimAdapter;
import edu.utdallas.mavs.divas.core.client.dto.CellStateDto;
import edu.utdallas.mavs.divas.core.client.dto.SimDto;
import edu.utdallas.mavs.divas.core.msg.RemoveStateMsg;
import edu.utdallas.mavs.divas.core.sim.common.state.AgentState;
import edu.utdallas.mavs.divas.core.sim.common.state.EnvObjectState;
import edu.utdallas.mavs.divas.gui.event.SimStatusUpdateEvent;
import edu.utdallas.mavs.divas.gui.event.SimSummaryUpdateEvent;
import edu.utdallas.mavs.divas.gui.repository.SimRepository;
import edu.utdallas.mavs.divas.mts.DivasTopic;
import edu.utdallas.mavs.divas.mts.MTSPayload;
import edu.utdallas.mavs.divas.mts.Subscriber;

public class SimUpdateHandler
{
    private static final Logger logger = LoggerFactory.getLogger(SimUpdateHandler.class);

    private SimAdapter          simAdapter;

    private EventBus            eventBus;

    private SimRepository       simRepository;

    @Inject
    public SimUpdateHandler(SimAdapter simAdapter, EventBus eventBus, SimRepository simRepository)
    {
        this.simAdapter = simAdapter;
        this.eventBus = eventBus;
        this.simRepository = simRepository;
    }

    public void start()
    {
        simAdapter.addSubscription(DivasTopic.envTopic, handleCellUpdate());
        simAdapter.addSubscription(DivasTopic.simSummaryTopic, handleSimSummaryUpdate());
        simAdapter.addSubscription(DivasTopic.simStatusTopic, handleSimStatusUpdate());
        simAdapter.addSubscription(DivasTopic.destroyEntityTopic, destroyEntityMsgHandler());
        simAdapter.addSubscription(DivasTopic.hostConfigTopic, simEndingMsgHandler());
    }

    private Subscriber handleCellUpdate()
    {
        return new Subscriber()
        {
            @Override
            public void messageReceived(String topic, final MTSPayload payload)
            {
                Runnable task = new Runnable()
                {
                    @Override
                    public void run()
                    {

                        if(payload.getData() instanceof CellStateDto)
                        {
                            final CellStateDto cell = (CellStateDto) payload.getData();
                            logger.debug("{} agents {} envobjects received.", cell.getAgentStates().size(), cell.getEnvObjects().size());
                            simRepository.update(cell);
                        }
                    }
                };

                Platform.runLater(task);
            }
        };
    }

    private Subscriber handleSimSummaryUpdate()
    {
        return new Subscriber()
        {
            @Override
            public void messageReceived(String topic, final MTSPayload payload)
            {
                Runnable task = new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if(payload.getData() instanceof SimDto)
                        {
                            final SimDto summary = (SimDto) payload.getData();
                            eventBus.fireEvent(new SimSummaryUpdateEvent(summary));
                        }
                    }
                };

                Platform.runLater(task);
            }
        };
    }

    private Subscriber handleSimStatusUpdate()
    {
        return new Subscriber()
        {
            @Override
            public void messageReceived(String topic, final MTSPayload payload)
            {
                Runnable task = new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if(payload.getData() instanceof Boolean)
                        {
                            final boolean running = (Boolean) payload.getData();
                            eventBus.fireEvent(new SimStatusUpdateEvent(running));
                        }
                    }
                };

                Platform.runLater(task);
            }
        };
    }

    private Subscriber destroyEntityMsgHandler()
    {
        return new Subscriber()
        {
            @Override
            public void messageReceived(String topic, MTSPayload payload)
            {
                if(payload.getData() instanceof RemoveStateMsg)
                {
                    RemoveStateMsg msg = (RemoveStateMsg) payload.getData();

                    if(msg.getState() instanceof AgentState)
                        simRepository.removeAgent((AgentState) msg.getState());
                    else if(msg.getState() instanceof EnvObjectState)
                        simRepository.removeEnvObject((EnvObjectState) msg.getState());
                }
            }
        };
    }

    private Subscriber simEndingMsgHandler()
    {
        return new Subscriber()
        {
            @Override
            public void messageReceived(String topic, MTSPayload payload)
            {
                switch(payload.getKey())
                {
                case 2:
                    SimRepository.clear();
                    break;
                }
            }
        };
    }
}
*/