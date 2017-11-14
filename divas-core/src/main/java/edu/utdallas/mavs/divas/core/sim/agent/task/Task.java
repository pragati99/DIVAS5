package edu.utdallas.mavs.divas.core.sim.agent.task;

import edu.utdallas.mavs.divas.core.sim.common.stimulus.Stimuli;

/**
 * This interface describes the operations of agent task.
 */
public interface Task {
	/**
	 * Executes the agent's task.
	 * 
	 * @param agentId
	 *            the ID of the agent
	 * @return a set of stimulus in response to the execution of the task
	 */
	public Stimuli execute(int agentId);

	/**
	 * Checks if this task is enabled
	 * 
	 * @return the enabled status of the task
	 */
	public boolean isEnabled();

	/**
	 * Updates the enabled status of this task
	 * 
	 * @param enabled
	 *            the new enabled status
	 */
	public void setEnabled(boolean enabled);

	/**
	 * Gets the name of this task
	 * 
	 * @return the task's name
	 */
	public String getName();

	/**
	 * Gets the type of this task
	 * 
	 * @return the task's type
	 */
	public String getType();

	/**
	 * Creates a new task to be executed in the schedule execution cycle.
	 * 
	 * @param executionCycle
	 *            the scheduled execution cycle of the task
	 * @return the newly created task
	 */
	public Task createTask(long executionCycle);
}
