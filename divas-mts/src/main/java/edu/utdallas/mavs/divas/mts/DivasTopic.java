package edu.utdallas.mavs.divas.mts;

/**
 * This class defines the message topics that are used in the Message transport system.
 */
public class DivasTopic
{
    /**
     * A message topic for handling the agent manual transfer from a cell controller to another one.
     */
    public static String runtimeAgentCommandTopic  = "RuntimeAgentCommandTopic";
    /**
     * A message topic for assigning an ID to an object.
     */
    public static String assignIDTopic             = "AssignIDTopic";
    /**
     * A message topic for splitting and merging cell controllers.
     */
    public static String cellStructureTopic        = "CellStructureTopic";
    /**
     * A message topic for creating objects in the simulation.
     */
    public static String createEntityTopic         = "CreateEntityTopic";
    /**
     * A message topic for destroying an entity in the simulation.
     */
    public static String destroyEntityTopic        = "DestroyEntityTopic";
    /**
     * A message topic for sending the cell state update at the end of each cycle.
     */
    public static String envTopic                  = "EnvironmentTopic";
    /**
     * A message topic for applying an external stimulus on the simulation.
     */
    public static String externalStimulusTopic     = "ExternalStimulusTopic";
    /**
     * A message topic for <code>Heartbeat</code> status starting or ending.
     */
    public static String heartbeatTopic            = "HeartbeatTopic";
    /**
     * A message topic for sending host configuration.
     */
    public static String hostConfigTopic           = "HostConfigTopic";
    /**
     * A message topic for sending simulation configurations.
     */
    public static String simulationPropertiesTopic = "SimulationPropertiesTopic";
    /**
     * A message topic for controlling simulation beats or cycles.
     */
    public static String timeControlTopic          = "TimeControlTopic";
    /**
     * A message topic that is used to indicate the end of a simulation phase.
     */
    public static String phaseCompletionTopic      = "PhaseCompletionTopic";
    /**
     * A message topic for changing the simulation status to Running, Pause, Reset.
     */
    public static String simulationControlTopic    = "SimulationControlTopic";
    /**
     * A message topic for sending simulation summary message.
     */
    public static String simSummaryTopic           = "SimSummaryTopic";
    /**
     * A message topic for handling environment reorganization requests.
     */
    public static String reorganizationTopic       = "ReorganizationTopic";
    /**
     * A message topic for sending the current simulation status like running, stopped.
     */
    public static String simStatusTopic            = "SimStatusTopic";
    /**
     * A message topic for sending a simulation snapshot.
     */
    public static String simSnapshotTopic          = "SimSnapshotTopic";
    /**
     * A message topic for sending agent-to-agent messages.
     */
    public static String agentMessageTopic         = "AgentMessageTopic";
    /**
     * A message topic for sending cell controller-to-cell controller messages.
     */
    public static String c2cMessageTopic           = "C2CMessageTopic";
    /**
     * A message topic for sending assistance requests to coordinators.
     */
    public static String assistanceRequestTopic           = "AssistanceRequestTopic";
}

