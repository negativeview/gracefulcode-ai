package com.gracefulcode.ai;

/**
 * A Behavior is a single "thing" that your AI entity can do. Behaviors should
 * be small and single-purpose. AI intelligence comes from mixing and matching
 * behaviors. Making large behaviors that do a lot of things will drastically
 * limit their ability to "improvize."
 *
 * @version 0.1
 * @since 0.1
 */
public interface Behavior<WS extends WorldState> {
	/**
	 * Some behaviors have pre-conditions that allow them to run, this should
	 * check those pre-conditions.
	 * <p>
	 * The worldState parameter should be treated as immutable here.
	 *
	 * @param worldState The state that the world is currently in. All of your
	 *        preconditions should be represented in this world state.
	 * @return True of this behavior is allowed to run, otherwise false.
	 */
	public boolean isRunnable(WS worldState);

	/**
	 * Modifies the world state to what it would be like after this behavior
	 * has run.
	 * <p>
	 * Note that in some circumstance, it is encouraged for this function to
	 * "lie." If you cannot be completely sure what the world state will look
	 * like after this, just modify the world state as if the ideal case
	 * happens. For instance, for an attack behavior, you might claim that you
	 * are going to kill the enemy.
	 * <p>
	 * If you do this, you may have to re-plan after this behavior to react to
	 * what *really* happened.
	 *
	 * @param worldState The world state that you are to modify. This
	 *        worldState is already copied and can be modified at will.
	 */
	public void modifyState(WS worldState);

	/**
	 * Returns the "cost" of this behavior -- whatever that means for your
	 * application.
	 * <p>
	 * The AI system is looking for the "cheapest" chain of events that
	 * satisfies the goal. A baseline implementation of costs would rely
	 * entirely on wall clock time. If it will take this AI agent 5 frames to
	 * attack an enemy, an attack behavior might return 5 here.
	 * <p>
	 * You can get more "personal" behavior by thinking of cost more broadly.
	 * An AI agent that is a pacifist might have a very high cost for any
	 * violent action. They are still capable of it, but they have to exhaust
	 * their other options before they resort to such actions. Going this route
	 * makes things harder to think about since there is no unit tied to these
	 * numbers, but it also leads to more "human" feeling behaviors.
	 *
	 * @param worldState The current world state.
	 * @return What this behavior would cost to run in the given world state.
	 */
	public Float getCost(WS worldState);
}