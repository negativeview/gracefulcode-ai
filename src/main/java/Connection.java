package com.gracefulcode.ai;

/**
 * A simple class that encodes the transition from one world state to another and what behavior caused that transition.
 *
 * @author Daniel Grace <dgrace@gracefulcode.com>
 * @version 0.1.0
 * @since 0.1.0
 */

public class Connection<T> {
	/**
	 * The WorldState that we were in before this connection happened, ie.<!-- --> before this Behavior was run.
	 */
	private WorldState from;

	/**
	 * The WorldState that we are in after this connection happened, ie.<!-- --> before this Behavior was run.
	 */
	private WorldState to;

	/**
	 * The Behavior that caused this transition to happen.
	 */
	private Behavior<T> behavior;
	
	/**
	 * Create a new Connection.
	 *
	 * @param from The WorldState that we were in before this Behavior was run.
	 * @param to The WorldState that we are in after this Behavior has run.
	 * @param behavior The Behavior responsible for this transition.
	 * @since 0.1.0
	 */
	public Connection(WorldState from, WorldState to, Behavior<T> behavior) {
		this.from = from;
		this.to = to;
		this.behavior = behavior;
	}
	
	/**
	 * Gets the cost of this behavior, which is also the cost of the node in whatever unit the end user has used.
	 *
	 * @param entity The T that represents the AI entity doing the Behavior.
	 * @return A float that represents the cost of doing this Behavior. The unit is whatever you want to use as long as it's internally consistent.
	 * @since 0.1.0
	 */
	public float getCost(T entity) {
		return this.behavior.getCost(entity);
	}

	public WorldState getFromNode() {
		return this.from;
	}

	public WorldState getToNode() {
		return this.to;
	}

	public Behavior getBehavior() {
		return this.behavior;
	}
}
