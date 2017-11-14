package edu.utdallas.simulation.sim.agent;

//import java.sql.PreparedStatement;
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.Random;
//
//import com.jme3.math.Vector3f;
//
//import edu.utdallas.mavs.divas.gui.frames.agentspec.MetaAgentSpecFrame.DivasAttributeType;
//import edu.utdallas.mavs.divas.recording.db.DivasDB;
//import edu.utdallas.mavs.divas.sim.agent.interaction.HumanPerceptionModule;
//import edu.utdallas.mavs.divas.sim.agent.knowledge.SensedData;
//import edu.utdallas.mavs.divas.sim.event.BombEvent;
//import edu.utdallas.mavs.divas.sim.event.EnvEvent;
//import edu.utdallas.mavs.divas.sim.event.FireworkEvent;
//import edu.utdallas.mavs.divas.sim.state.AgentState;
//import edu.utdallas.mavs.divas.sim.state.EnvObjectState;
//import edu.utdallas.mavs.divas.spec.agent.AgentSpecHelper;
//import edu.utdallas.mavs.simulation.sim.agent.knowledge.EvacuationHumanKnowledgeModule;
//
//public class PerceiveTest {
//	static PreparedStatement psSensedDataTableInsert;
//	static ArrayList<SensedData> perceivedThisTick;
//	static DivasDB db;
//	static String atPrefix = "Agent".concat(Integer.toString(0));
//
//	static ArrayList<EnvEvent> ee;
//	static ArrayList<EnvObjectState> eel;
//
//	public static void main(String[] args) {
//		System.out.println(DivasAttributeType.valueOf("Words").ordinal());
//
//		Float f = 1f;
//		float z = f;
//
//		if (z == f) {
//			System.out.println("work");
//			System.out.println(z);
//			System.out.println(f);
//		}
//		int testsize = 50;
//
//		Random r = new Random(System.currentTimeMillis());
//		DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS");
//		AgentSpecHelper ash = new AgentSpecHelper();
//
//		AgentState initialState = new AgentState();
//
//		try {
//			ash.loadDataFrom(initialState, "default.agentspec");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		Vector3f location = new Vector3f(0, 0, 0);
//		initialState.setPosition(location);
//		location = new Vector3f(1, 0, 1);
//		initialState.setHeading(location);
//
//		// make the agent from state
//
//		EvacuationHumanKnowledgeModule knowledgeModule = new EvacuationHumanKnowledgeModule(
//				initialState);
//		TestAgentPerceptionModule pm = new TestAgentPerceptionModule(
//				knowledgeModule);
//
//		ee = new ArrayList<EnvEvent>();
//		eel = new ArrayList<EnvObjectState>();
//
//		for (int i = 0; i < testsize; i++) {
//			Vector3f loc = new Vector3f(-20 + r.nextFloat() * 40, -20
//					+ r.nextFloat() * 40, -20 + r.nextFloat() * 40);
//			createBox(loc);
//		}
//
//		System.out.println("before:\t\t\t" + df.format(new Date()));
//
//		pm.perceive2(eel);
//		System.out.println("after per:\t\t" + df.format(new Date()));
//
//		Vector3f jjf = new Vector3f(5, 5, 5);
//		int xxz = 55;
//		System.out.println(jjf);
//		testMethod(jjf, xxz);
//		System.out.println(jjf);
//		System.out.println(xxz);
//	}
//
//	private static void testMethod(Vector3f jjf, int xxz) {
//		jjf.setX(11);
//		jjf.setZ(353);
//		xxz = 53300;
//	}
//
//	public static void createExplosion(Vector3f location) {
//		BombEvent explosion = new BombEvent();
//
//		explosion.setOrigin(location);
//		explosion.setIntensity(0.00000003f);
//		explosion.setCurrentlyAudible(true);
//		explosion.setCurrentlySmellable(true);
//		explosion.setCurrentlyVisible(true);
//		explosion.setSmoke(false);
//
//		ee.add(explosion);
//	}
//
//	public static void createFirework(Vector3f location) {
//		FireworkEvent firework = new FireworkEvent();
//
//		firework.setOrigin(location);
//		firework.setIntensity(0.00000003f);
//		firework.setCurrentlyAudible(true);
//		firework.setCurrentlySmellable(true);
//		firework.setCurrentlyVisible(true);
//		firework.setSmoke(false);
//
//		ee.add(firework);
//	}
//
//	public static void createBox(Vector3f location) {
//		EnvObjectState envObj = new EnvObjectState();
//
//		envObj.setPosition(location);
//		envObj.setScale(new Vector3f(5, 5, 5));
//
//		eel.add(envObj);
//	}
//}
//
//class TestAgentPerceptionModule extends HumanPerceptionModule {
//
//	public TestAgentPerceptionModule(
//			EvacuationHumanKnowledgeModule knowledgeModule) {
//		super(knowledgeModule);
//		// TODO Auto-generated constructor stub
//	}
//
//	public void perceive(List<EnvEvent> ee) {
//
//		// clear data perceived in previous ticks
//		knowledgeModule.clearPerceivedThisTick();
//		knowledgeModule.clearReasonedThisTick();
//		knowledgeModule.clearCombinedThisTick();
//		knowledgeModule.clearEventsThisTick();
//
//		knowledgeModule.setEventNumber(0);
//
//		auditorySensor
//				.setEnabled(knowledgeModule.getSelf().isAuditoryEnabled());
//		olfactorySensor.setEnabled(knowledgeModule.getSelf()
//				.isOlfactoryEnabled());
//
//		perceiveEvents(ee);
//
//		combinePerceptions();
//
//	}
//
//	public void perceive2(List<EnvObjectState> ee) {
//
//		// clear data perceived in previous ticks
//		knowledgeModule.clearPerceivedThisTick();
//		knowledgeModule.clearReasonedThisTick();
//		knowledgeModule.clearCombinedThisTick();
//		knowledgeModule.clearEventsThisTick();
//
//		knowledgeModule.setEventNumber(0);
//
//		auditorySensor
//				.setEnabled(knowledgeModule.getSelf().isAuditoryEnabled());
//		olfactorySensor.setEnabled(knowledgeModule.getSelf()
//				.isOlfactoryEnabled());
//
//		perceiveEnvObjs(ee);
//
//		// perceptionModule.combinePerceptions();
//	}
//}
