package com.gracefulcode.ai;

/**
 * Responsible for encoding what an AI entity wants to accomplish, and to tell when that has happened.
 *
 * @author Daniel Grace <dgrace@gracefulcode.com>
 * @version 0.1.0
 * @since 0.1.0
 */

public interface Goal {
	/**
	 * Gets the user-readable name of this Goal.
	 * 
	 * @return The String that can be used in messages to the user about this Goal.
	 * @since 0.1.0
	 */
	public String getName();

	/**
	 * Checks if the given WorldState satisfies this goal.
	 *
	 * @param state The WorldState that may or may not satisfy this goal.
	 * @return A boolean that is true if this WorldState is a valid solution. Otherwise, false.
	 * @since 0.1.0
	 */
	public boolean isSatisfied(WorldState state);

	/**
	 * Gets the "distance" that this WorldState is from the Goal. NOTE: Due to how A* works, you must take care to never "overestimate" here. You should always be safe returning only 0 (if the WorldState is perfect) or 1 (if anything is wrong, no matter how wrong). The pathfinding may explore more paths and take longer, but it will land on the right answer. If you ever overestimate here, you are no longer guaranteed to find the optimal path.
	 *
	 * @param state The WorldState that we wish to check.
	 * @return How far away this WorldState is from the goal. Units are whatever you want to use as long as they are internally consistent.
	 * @since 0.1.0
	 */
	public int distance(WorldState state);
}