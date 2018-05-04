package com.gracefulcode.ai;

import com.gracefulcode.ai.internal.GlobalState;
import com.gracefulcode.ai.internal.Node;
import com.gracefulcode.ai.internal.State;

import java.util.ArrayList;

/**
 * Planner is the real meat and potatoes of this operation. Here's where all
 * the work gets done. It's also the place you shouldn't have to look. If you
 * do, it's my fault and I am truly sorry.
 *
 * @version 0.1
 * @since 0.1
 */
public class Planner<
	WS extends WorldState,
	G extends Goal<WS>,
	B extends Behavior<WS>,
	BP extends Iterable<B>
> {
	public Planner() {

	}

	/**
	 * startPlanning consructs the initial state representing where the AI
	 * system begins. You must call stepState to step this state forward in
	 * planning.
	 *
	 * @param initialState The world state that this AI system begins at.
	 * @param goal The goal that we are ultimately trying to achieve.
	 * @param behaviorProvider The behaviors that we are allowed to use in our
	 *        plan.
	 * @return The initial State.
	 */
	public State<WS, G, B, BP> startPlanning(
		WS initialState,
		G goal,
		BP behaviorProvider
	) {
		// TODO: Pool this?
		return new State<WS, G, B, BP>(
			initialState,
			initialState,
			goal,
			behaviorProvider
		);
	}

	/**
	 * Gets an ArrayList of the behaviors that the AI system has come up with.
	 * <p>
	 * Note that these are in the reverse order that you may expect.
	 *
	 * @throws Exception if the end state was not found within the global state.
	 * @param endState The State on which you want to end.
	 * @return An ArrayList of behaviors to follow to get from the start to the
	 *         provided end State, in reverse order.
	 */
	public ArrayList<B> getPlan(State<WS, G, B, BP> endState) throws Exception {
		ArrayList<B> tmp = new ArrayList<B>();
		WS currentSearch = endState.getWorldState();

		GlobalState<WS, B, BP, G> globalState = endState.getGlobalState();

		Node<WS, B> n = globalState.stateToNode.get(endState.getWorldState());
		if (n == null) {
			throw new Exception("End state wasn't saved.");
		}
		while (n.getParent() != null) {
			tmp.add(n.getBehavior());
			n = n.getParent();
		}
		return tmp;
	}

	/**
	 */
	private void stepStateWithBehavior(
		State<WS, G, B, BP> state,
		B behavior,
		PlannerDebugger<WS, B> debugger
	) throws Exception {
		WS priorWorldState = state.getWorldState();

		// If we cannot run this behavior, we don't have to do anything.
		if (!behavior.isRunnable(priorWorldState)) return;

		GlobalState<WS, B, BP, G> globalState = state.getGlobalState();

		Node<WS, B> previousNodeInstance = globalState.stateToNode.get(priorWorldState);

		@SuppressWarnings("unchecked")
		WS worldStateAfterBehavior = (WS)priorWorldState.clone();

		if (worldStateAfterBehavior == priorWorldState) {
			throw new Exception("Your clone operation seems to be returning the same state.");
		}
		behavior.modifyState(worldStateAfterBehavior);

		// We have already investigated this world state and closed it.
		if (globalState.closedSet.contains(worldStateAfterBehavior)) {
			Node<WS, B> previousBestNodeInstance = globalState.stateToNode.get(worldStateAfterBehavior);
			float previousBestNodeCost = previousBestNodeInstance.getCost();

			float previousNodeCost = previousNodeInstance.getCost() + behavior.getCost(priorWorldState);
			if (previousNodeCost < previousBestNodeCost) {
				previousBestNodeInstance.changeParent(previousNodeInstance, behavior);
			}

			return;
		}

		// We haven't evaluated this before, make a new node.
		Node<WS, B> newNode = new Node<WS, B>(worldStateAfterBehavior, behavior, previousNodeInstance);
		globalState.stateToNode.put(worldStateAfterBehavior, newNode);

		if (debugger != null) {
			debugger.didAddState(worldStateAfterBehavior);
		}
		globalState.openSet.add(worldStateAfterBehavior);
	}

	/**
	 * Steps the provided State forward by one planning tick. In common cases,
	 * you would call this once per frame in your game. You can call it more or
	 * less often depending on your use case.
	 * <p>
	 * stepState will alter the passed in state to represent the new state of
	 * the AI system.
	 *
	 * @throws Exception if the world state clone is the same object.
	 * @param state The current state of this AI system.
	 * @return True if the AI system cannot proceed any more, otherwise False.
	 */
	public boolean stepState(State<WS, G, B, BP> state) throws Exception {
		return this.stepState(state, null);
	}

	/**
	 * Steps the provided state forward by one planning tick, while also
	 * providing a debugger. In common cases, you would call this once per
	 * frame in your game. You can call it more or less often depending on your
	 * use case.
	 * <p>
	 * stepState will alter the passed in state to represent the new state of
	 * the AI system.
	 *
	 * @throws Exception if the world state clone is the same object.
	 * @param state The current state of this AI system.
	 * @param debugger The debugger you want to use in order to debug the AI
	 *        system.
	 * @return True if the AI system cannot proceed any more, otherwise False.
	 */
	public boolean stepState(State<WS, G, B, BP> state, PlannerDebugger<WS, B> debugger) throws Exception {
		GlobalState<WS, B, BP, G> globalState = state.getGlobalState();

		if (debugger != null) {
			debugger.didStartStep();
		}

		// TODO: Check that we aren't being called with an already-closed
		// state.
		for (B b: globalState.behaviorProvider) {
			if (debugger != null) {
				debugger.startEvaluateBehavior(b);
			}
			this.stepStateWithBehavior(state, b, debugger);
			if (debugger != null) {
				debugger.endEvaluateBehavior(b);
			}
		}

		globalState.openSet.remove(state.getWorldState());
		globalState.closedSet.add(state.getWorldState());

		if (globalState.openSet.size() == 0) {
			if (debugger != null) {
				debugger.didEndStep(false);
			}
			return false;
		}

		state.setCurrentState(globalState.openSet.poll());

		if (debugger != null) {
			debugger.didEndStep(true);
		}
		return true;
	}
}