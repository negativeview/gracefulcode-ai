package com.gracefulcode.ai;

/**
 * PlannerDebugger allows you visibiliy into the inner workings of the AI
 * system for debugging purposes. You should not use this to affect your actual
 * behavior. If you have a use case where you need this information for actual
 * behavior, please let me know so that I can support your use case officially.
 *
 * @version 0.1
 * @since 0.1
 */
public interface PlannerDebugger<WS extends WorldState, B extends Behavior<WS>> {
	/**
	 * Called when stepState is called, before anything else.
	 */
	public void didStartStep();

	/**
	 * Called when we start evaluating whether this behavior is viable. This is
	 * called before any sanity checking, so every single behavior will go
	 * through this.
	 *
	 * @param behavior The behavior that is being evaluated.
	 */
	public void startEvaluateBehavior(B behavior);

	/**
	 * Called after we are done evaluating this behavior. This is called for
	 * every single behavior, regardless of isRunnable, or any other sanity
	 * checks.
	 *
	 * @param behavior The behavior that was being evaluated.
	 */
	public void endEvaluateBehavior(B behavior);

	/**
	 * This step is done being evaluated.
	 *
	 * @param hasMoreWork True if the planner does not consider itself done.
	 *        False otherwise.
	 */
	public void didEndStep(boolean hasMoreWork);

	/**
	 * Called whenever a world state is being added to our list. Note that this
	 * is NOT called if we found a new path to an existing world state.
	 *
	 * @param worldState The world state that we have newly discovered.
	 */
	public void didAddState(WS worldState);
}