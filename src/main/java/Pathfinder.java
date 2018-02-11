package com.gracefulcode.ai;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Pathfinder is the main workhorse of the AI that decides what path to take to
 * transform a given WorldState into one that satisifies a Goal. T is the class
 * that represents your AI entity, ie. a Character class or something similar.
 *
 * @author Daniel Grace <dgrace@gracefulcode.com>
 * @version 0.1.0
 * @since 0.1.0
 */

public class Pathfinder<T> implements java.util.Comparator<WorldState> {
	private ArrayList<WorldState> closedSet;
	private ArrayList<WorldState> openSet;
	private Hashtable<WorldState, WorldStateBehavior> cameFrom;
	private Hashtable<WorldState, Float> gScore;
	private Hashtable<WorldState, Float> fScore;
	
	private class WorldStateBehavior {
		private WorldState worldState;
		private Behavior behavior;
		
		public WorldStateBehavior(WorldState state, Behavior behavior) {
			this.worldState = state;
			this.behavior = behavior;
		}
	}
	
	/**
	 * Constructs a new pathfinder with a given behaviorProvider.
	 */
	public Pathfinder() {
	}

	/**
	 * Finds an optimal path from a starting world state to one that satisfies a
	 * given goal.
	 *
	 * @param startNode The WorldState that we are starting in. This will
	 * usually be a "current" WorldState.
	 * @param goal The Goal that we wish to meet.
	 * @param entity The T which is going to be performing these actions once
	 * the path is planned. This is used to only look at behaviors that this T
	 * can perform and to use the proper weights in the case of dynamic weights.
	 * @param behaviorProvider The BehaviorProvider that is going to be used to
	 * select which behaviors are available.
	 * @param outPath This is the output. You provide an instantiated ArrayList.
	 * If we found a path, that path will be in this ArrayList once this method
	 * returns. If we cannot find a path, it will be empty.
	 *
	 * @since 0.1.0
	 */
	public boolean searchConnectionPath(
			WorldState startNode,
			Goal goal,
			T entity,
			BehaviorProvider<T> behaviorProvider,
			ArrayList<Connection> outPath
	) {	
		this.closedSet = new ArrayList<WorldState>();
		this.openSet = new ArrayList<WorldState>();
		this.openSet.add(startNode);
		
		this.cameFrom = new Hashtable<WorldState, WorldStateBehavior>();
		this.gScore = new Hashtable<WorldState, Float>();
		this.gScore.put(startNode, (float)0);
		
		this.fScore = new Hashtable<WorldState, Float>();
		this.fScore.put(startNode, (float)goal.distance(startNode));
		
		int loopCount = 0;
		
		while (this.openSet.size() > 0) {
			this.openSet.sort(this);
			
			loopCount++;
			if (loopCount > 10) break;
			
			WorldState current = this.getLowestFScore();
			if (goal.isSatisfied(current)) {
				this.buildPath(current, outPath);
				return true;
			}

			this.openSet.remove(current);
			this.closedSet.add(current);
			
			for (Behavior<T> b: behaviorProvider.getBehaviors(entity)) {
				if (!b.canRun(current, entity)) continue;
				
				WorldState toNode = b.ifRan(current, entity);
				
				if (this.closedSet.indexOf(toNode) != -1) {
					continue;
				}
				
				if (this.openSet.indexOf(toNode) == -1) {
					this.openSet.add(toNode);
				}
				
				Float tentative_gscore = this.gScore.get(current);
				if (tentative_gscore == null)
					tentative_gscore = Float.POSITIVE_INFINITY;
				tentative_gscore += b.getCost(entity);

				Float tmp = this.gScore.get(toNode);
				if (tmp == null)
					tmp = Float.POSITIVE_INFINITY;
				if (tentative_gscore >= tmp) {
					continue;
				}
				
				float estimate = goal.distance(current);
				this.cameFrom.put(toNode, new WorldStateBehavior(current, b));
				this.gScore.put(toNode, tentative_gscore);
				this.fScore.put(toNode, tentative_gscore + estimate);
			}
		}
		
		return false;
	}
	
	private void buildPath(WorldState current, ArrayList<Connection> outPath) {
		while (this.cameFrom.containsKey(current)) {
			WorldStateBehavior wsb = this.cameFrom.get(current);
			outPath.add(new Connection(wsb.worldState, current, wsb.behavior));
			current = wsb.worldState;
		}
	}

	private WorldState getLowestFScore() {
		Float currentLowestFScore = this.fScore.get(this.openSet.get(0));
		if (currentLowestFScore == null)
			currentLowestFScore = Float.POSITIVE_INFINITY;
		int currentIndex = 0;
		for (int i = 1; i < this.openSet.size(); i++) {
			Float fScore = this.fScore.get(this.openSet.get(i));
			if (fScore == null)
				fScore = Float.POSITIVE_INFINITY;
			if (fScore < currentLowestFScore) {
				currentLowestFScore = fScore;
				currentIndex = i;
			}
		}
		
		System.out.println("Returning " + currentIndex);
		
		return this.openSet.get(currentIndex);
	}

	/**
	 * Used to sort WorldStates by their fScore. There is no reason for an
	 * external user to ever call this.
	 *
	 * @param o1 The first WorldState.
	 * @param o2 The second WorldState.
	 * @return -1,0, or 1
	 */
	@Override
	public int compare(WorldState o1, WorldState o2) {
		Float a = this.fScore.get(o1);
		if (a == null)
			a = Float.POSITIVE_INFINITY;

		Float b = this.fScore.get(o2);
		if (b == null)
			b = Float.POSITIVE_INFINITY;
		
		if (a > b) return -1;
		if (b > a) return 1;
		return 0;
	}
}
