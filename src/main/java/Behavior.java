package com.gracefulcode.ai;

/**
 * A behavior that your AI entity is capable of performing.
 *
 * T is the object type that represents your AI entity. It is passed around the
 * system so that your custom code can use it to determine what behaviors are
 * possible, to determine what their costs are, etc.
 *
 * @author Daniel Grace <dgrace@gracefulcode.com>
 * @version 0.1.0
 * @since 0.1.0
 */

public interface Behavior<T> {
	/**
	 * Gets the total cost of this behavior if run. The cost is used,
	 * essentially, to figure out which path to the desired end state is the
	 * "most desirable." If this maps to time taken, your AI will take the
	 * fastest possible path to the end goal. Alternatively, you can make
	 * drastic behaviors such as murder have a very large weight. This would
	 * make your AI not jump to murder at the first possible opportunity, but
	 * still have that be an option when backed into a corner.
	 *
	 * @param entity The entity that is potentially performing this action.
	 * @return int The cost in some arbitrary scale.
	 * @since 0.1.0
	 */
	public int getCost(T entity);

	/**
	 * Checks if this behavior is valid to run in the given state by the given
	 * entity.
	 * 
	 * @param state The WorldState that we are in when potentially running this Behavior.
	 * @param entity The T that represents the entity that would perform the behavior.
	 * @return true if this behavior is capable of running in this WorldState. Otherwise, false.
	 * @since 0.1.0
	 */
	public boolean canRun(WorldState state, T entity);

	/**
	 * Calculate what the WorldState would look like if this behavior was ran.
	 * The way this WorldState is used during pathfinding necessitates every
	 * return from ifRan to be a new instance of WorldState. You are expected
	 * to call WorldState::clone on the WorldState that you are passed, modify
	 * it a bit, and return the new instance.
	 *
	 * @param state The WorldState before this behavior is run.
	 * @param entity The entity that is running this behavior.
	 * @return The WorldState after this behavior has run.
	 * @since 0.1.0
	 */
	public WorldState ifRan(WorldState state, T entity);

	/**
	 * Gets the name of this behavior. Mostly for debugging purposes. Not used
	 * by the internal system at all.
	 *
	 * @return The String that represents this behavior in a nice user-readable manner.
	 * @since 0.1.0
	 */
	public String getName();
}
