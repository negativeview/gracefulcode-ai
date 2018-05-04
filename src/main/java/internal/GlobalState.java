package com.gracefulcode.ai.internal;

import com.gracefulcode.ai.Behavior;
import com.gracefulcode.ai.Goal;
import com.gracefulcode.ai.WorldState;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.PriorityQueue;

/**
 * GlobalState is the state of the AI subsystem. These fields are largely
 * public to aid in debugging, but you should probably not fiddle with them in
 * a production application. I WILL change the names of things, and add or
 * remove fields in this class in minor version releases.
 *
 * @version 0.1
 * @since 0.1
 */
public class GlobalState<
	WS extends WorldState,
	B extends Behavior<WS>,
	BP extends Iterable<B>,
	G extends Goal<WS>
> implements Comparator<WS> {
	/**
	 * The world state that we were in when this planner state started. We
	 * don't strictly need to store this. It may be removed in the future.
	 * It is somewhat useful for sanity-checking though that our returned
	 * path ends where we expect it to.
	 */
	public WS initialState;

	/**
	 * Our list of behaviors that we are allowed to use.
	 */
	public BP behaviorProvider;

	/**
	 * The goal that we ultimately want to satisfy.
	 */
	public G goal;

	/**
	 * The closed set is a list of world states that we have fully
	 * explored. We know that there's no good path forward from here, so if
	 * we run into one of these states, we can dismiss it as having any
	 * valid paths forward.
	 */
	public HashSet<WS> closedSet;

	/**
	 * The open set is a list of states that we know how to get to from the
	 * initial state, and might contain the right path forward. We aren't
	 * sure yet. Initially the initial state is put into here to kick
	 * things off.
	 */
	public PriorityQueue<WS> openSet;

	/**
	 * The root node is the node where we started our planning.
	 */
	public Node<WS, B> rootNode;

	/**
	 * For any given world state, how did we get here? There is no entry for
	 * the root state, but should be one for any other state that we've
	 * discovered in our journey.
	 */
	public Hashtable<WS, Node<WS, B>> stateToNode;

	public GlobalState(WS initialState, BP behaviorProvider, G goal) {
		this.initialState = initialState;
		this.behaviorProvider = behaviorProvider;
		this.goal = goal;
		this.closedSet = new HashSet<WS>();
		this.openSet = new PriorityQueue<WS>(10, this);
		this.rootNode = new Node<WS, B>(initialState);
		this.stateToNode = new Hashtable<WS, Node<WS, B>>();
		this.stateToNode.put(this.initialState, this.rootNode);
	}

	/**
	 * The cost to get from our root node to the provided world state. Should
	 * only be called for world states that we definitely know about. Since any
	 * of the member variables can change in a minor version, this function
	 * provides a guaranteed-stable-in-minor-versions way to access the main
	 * data you want from this class.
	 *
	 * @throws Exception if the provided state is not a known state.
	 * @param state The world state you are asking about.
	 * @return The cumulative cost from the root node to this world state.
	 */
	public float getBestKnownCost(WS state) throws Exception {
		if (!this.stateToNode.containsKey(state)) {
			throw new Exception("getBestKnownCost for unknown world state!");
		}

		Node node = this.stateToNode.get(state);
		return node.getCost();
	}

	@Override
	public int compare(WS a, WS b) {
		int tmp = this.goal.compare(a, b);
		if (tmp != 0) return tmp;

		try {
			float gA = this.getBestKnownCost(a);
			float gB = this.getBestKnownCost(b);

			if (gA < gB) return -1;
			if (gB < gA) return 1;
		} finally {
			return 0;
		}
	}
}