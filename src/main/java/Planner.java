import java.util.HashSet;
import java.util.Hashtable;
import java.util.PriorityQueue;

/**
 * Planner is the real meat and potatoes of this operation. Here's where all
 * the work gets done. It's also the place you shouldn't have to look. If you
 * do, it's my fault and I am truly sorry.
 *
 * @author Daniel Grace <dgrace@gracefulcode.com>
 * @version 0.1
 * @since 0.1
 */
public class Planner<
	WS extends WorldState,
	G extends Goal,
	B extends Behavior<WS>,
	BP extends BehaviorProvider<B>
> {
	public class WorldStateBehavior {
		public WS worldState;
		public B behavior;
	}

	public class State {
		private WS initialState;
		private WS currentState;
		private G goal;
		private HashSet<WS> closedSet;
		private PriorityQueue<WS> openSet;
		private Hashtable<WS, WorldStateBehavior> cameFrom;
		private BP behaviorProvider;

		public State(WS initialState, WS currentState, G goal, BP behaviorProvider) {
			this.initialState = initialState;
			this.currentState = currentState;
			this.behaviorProvider = behaviorProvider;
			this.goal = goal;
			this.closedSet = new HashSet<WS>();
			this.openSet = new PriorityQueue<WS>(10, goal);
			this.cameFrom = new Hashtable<WS, WorldStateBehavior>();

			this.openSet.add(initialState);
		}

		public WS getWorldState() {
			return this.currentState;
		}

		public G getGoal() {
			return this.goal;
		}

		public int getOpenSetSize() {
			return this.openSet.size();
		}

		public int getClosedSetSize() {
			return this.closedSet.size();
		}

		public WS getHighestPriority() {
			return this.openSet.peek();
		}
	}

	public Planner() {

	}

	public State startPlanning(WS initialState, G goal, BP behaviorProvider) {
		return new State(initialState, initialState, goal, behaviorProvider);
	}

	public boolean stepState(State state) {
		// TODO: Check that we aren't being called with an already-closed
		// state.
		java.util.Iterator<B> iterator = state.behaviorProvider.iterator();
		while (iterator.hasNext()) {
			B b = iterator.next();

			// Basic checking just in case behaviors have crazy logic.
			if (!b.isRunnable()) {
				continue;
			}

			WS newWorldState = (WS)state.getWorldState().clone();
			b.modifyState(newWorldState);

			if (state.closedSet.contains(newWorldState)) {
				// Would be a place to release a state object.
				continue;
			}

			if (state.goal.compare(newWorldState, state.getWorldState()) == 1) {
				// We found a better state. Let's start over now.
				state.openSet.add(newWorldState);
				state.currentState = newWorldState;
				if (!iterator.hasNext()) {
					// We found our match at the very end. Let's go ahead and
					// close this out.
					state.closedSet.add(state.getWorldState());
					state.openSet.remove(state.getWorldState());
				}
				return true;
			} else {
				// Record that this state exists, but don't bail early.
				state.openSet.add(newWorldState);
			}
		}
		state.openSet.remove(state.getWorldState());
		state.closedSet.add(state.getWorldState());
		state.currentState = state.openSet.poll();
		return false;
	}
}