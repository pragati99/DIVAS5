package edu.utdallas.simulation.sim.agent;

//import java.net.URISyntaxException;
//
//import org.springframework.util.Assert;
//
//import com.jme3.math.Vector3f;
//
//import edu.utdallas.mavs.divas.sim.agent.Agent;
//import edu.utdallas.mavs.divas.sim.agent.interaction.perception.AuditorySensor;
//import edu.utdallas.mavs.divas.sim.agent.interaction.perception.OlfactorySensor;
//import edu.utdallas.mavs.divas.sim.agent.interaction.perception.VisualSensor;
//import edu.utdallas.mavs.divas.sim.state.AgentState;
//import edu.utdallas.mavs.divas.spec.agent.AgentSpecHelper;
//import edu.utdallas.mavs.simulation.sim.agent.EvacuationHumanAgent;
//import edu.utdallas.mavs.simulation.sim.agent.knowledge.EvacuationHumanKnowledgeModule;
///*import divas.spec.agent.AgentSpecDocument;
//import divas.spec.agent.AgentSpecDocument.AgentSpec;
//import divas.spec.agent.DivasTask;*/
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration
//public class AgentTest
//{
//
//    @Test
//    public void testAgent()
//    {
//
//    }
//
//   @Test
//    public void testVision() throws URISyntaxException
//    {
//        AgentState state = loadAgentState();
//        EvacuationHumanKnowledgeModule knowledgeModule = new EvacuationHumanKnowledgeModule(state);
//        VisualSensor olf = new VisualSensor(knowledgeModule);
//        Assert.assertNotNull(olf);
//    }
//
//    @Test
//    public void testHearing() throws URISyntaxException
//    {
//        AgentState state = loadAgentState();
//        EvacuationHumanKnowledgeModule knowledgeModule = new EvacuationHumanKnowledgeModule(state);
//        AuditorySensor aud = new AuditorySensor(knowledgeModule);
//        Assert.assertNotNull(aud);
//    }
//
//    @Test
//    public void testOlfactory() throws URISyntaxException
//    {
//        AgentState state = loadAgentState();
//        EvacuationHumanKnowledgeModule knowledgeModule = new EvacuationHumanKnowledgeModule(state);
//        OlfactorySensor olf = new OlfactorySensor(knowledgeModule);
//        Assert.assertNotNull(olf);
//    }
//
//    public AgentState loadAgentState() throws URISyntaxException
//    {
//    	AgentSpecHelper ash = new AgentSpecHelper();
//        AgentState initialState = new AgentState();
//
//        try
//        {
//
//            ash.loadDataFrom(initialState, "default.agentspec");
//
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//        }
//        Vector3f location = new Vector3f(0, 0, 0);
//        initialState.setPosition(location);
//        location = new Vector3f(1, 0, 1);
//        initialState.setHeading(location);
//
//        // make the agent from state
//
//        Agent<?, ?, ?, ?> agent = new EvacuationHumanAgent(initialState);
//        Assert.assertEquals(location, agent.getState().getHeading());
//
//        return initialState;
//    }
//
//}
