package com.gracefulcode.ai.internal;

import com.gracefulcode.ai.Behavior;
import com.gracefulcode.ai.Goal;
import com.gracefulcode.ai.WorldState;

/**
 * State is the real meat of this library. It contains all of the working
 * memory of the planner. It is returned to the API user as a
 * mostly-black-box. There are accessors for most of the fields, but this
 * is mostly for debugging and unit test purposes. They shouldn't be used
 * in production programs.
 *
 * @version 0.1
 * @since 0.1
 */
public class State<WS extends WorldState, G extends Goal<WS>, B extends Behavior<WS>, BP extends Iterable<B>> {
	/**
	 * That state that this branch of the planner is currently at. This is
	 * what all behaviors that are running are being ran against.
	 */
	private WS currentState;

	/**
	 * The cost that it has taken to get to this point. Determined by the
	 * behaviors, this is a "real cost" between here and the start. It has
	 * nothing to do with the distance between here and goal.
	 */
	private float currentCost;

	/**
	 * Our globalState is shared between all instances of State. It's for
	 * holding bits of our state that are never changing or are more logical
	 * to be considered global.
	 */
	private GlobalState<WS, B, BP, G> globalState;

	public State(WS initialState, WS currentState, G goal, BP behaviorProvider) {
		this.globalState = new GlobalState<WS, B, BP, G>(initialState, behaviorProvider, goal);
		this.currentState = currentState;
		this.globalState.openSet.add(initialState);
	}

	private State(State<WS, G, B, BP> oldState, WS currentWorldState) {
		this.globalState = oldState.globalState;
		this.currentState = currentWorldState;
	}

	public void setCurrentState(WS state) {
		this.currentState = state;
	}

	public GlobalState<WS, B, BP, G> getGlobalState() {
		return this.globalState;
	}

	public String toString() {
		return "State:openSet(" + this.globalState.openSet.size() + ")";
	}

	/**
	 * The actual cost between here and the initial state. Only use this
	 * for debugging, do not rely on this information being accessible in
	 * production code.
	 *
	 * @return The behavior cost between here and the start.
	 */
	public float getCurrentCost() {
		// TODO: Make sure that this value is correct even as we find
		// cheaper paths.
		return this.currentCost;
	}

	/**
	 * Is this goal done at this state?
	 *
	 * @return True if the goal is satisfied, false otherwise.
	 */
	public boolean isDone() {
		return this.globalState.openSet.size() == 0 && this.currentState == null;
	}

	public WS getBestWorldState() {
		if (this.globalState.bestSolution != null)
			return this.globalState.bestSolution.getWorldState();
		return this.currentState;
	}

	/**
	 * Our current world state. Only use this for debugging, do not make
	 * your logic rely on being able to get this value.
	 *
	 * @return Our current world state.
	 */
	public WS getWorldState() {
		return this.currentState;
	}

	/**
	 * Our current goal. Only use this for debugging, do not make your
	 * logic rely on being able to get this value.
	 *
	 * @return Our goal.
	 */
	public G getGoal() {
		return this.globalState.goal;
	}

	/**
	 * The size of our open set. Only use this for debugging, do not make
	 * your logic rely on being able to get this value. In general, large
	 * open sets are a sign of an inefficiency. What "large" means depends
	 * on your specific use case. Having some idea of how large your open
	 * set "should" be is a good thing for debugging. If the actual open
	 * set is much larger than you expect, you may not have implemented
	 * WorldState.equals/WorldState.hashCode correctly.
	 *
	 * @return The size of the open set.
	 */
	public int getOpenSetSize() {
		return this.globalState.openSet.size();
	}

	/**
	 * The size of your closed set. Only use this for debugging, do not
	 * make your logic rely on being able to get this value. The closed set
	 * is less useful then the open set for debugging performance issues.
	 *
	 * @return The size of the closed set.
	 */
	public int getClosedSetSize() {
		return this.globalState.closedSet.size();
	}

	/**
	 * The world state that is the most promising in the open set. This is
	 * generally the state that will be run next. Only use this for
	 * debugging, do not make your logic rely on being able to get this
	 * value. This is very useful to step through and make sure that the
	 * system is generally picking increasingly good world states to
	 * explore. Note that if it hits a dead end, it will then go back to a
	 * worse state to explore, but *in general* world states should improve
	 * over time.
	 *
	 * @return The best world state currently.
	 */
	public WS getHighestPriority() {
		return this.globalState.openSet.peek();
	}
}

