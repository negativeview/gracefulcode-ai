/**
 * This is the first of the unit tests. You should read these in file sort
 * order and read from top to bottom. They are written to be read.
 *
 * This file mostly explains the classes that you will be subclassing and shows
 * the literla bare minimum examples of subclassing them. It then creates an
 * initial Planner.State and checks it for sanity.
 */
import com.gracefulcode.ai.*;
import com.gracefulcode.ai.internal.State;

import java.util.ArrayList;

import org.junit.Test;
import static org.junit.Assert.*;

public class A001_StateCreationTest {
	/**
	 * You should create a class that implements WorldState. That WorldState
	 * MUST override .clone() and follow the clone interface requirements.
	 *
	 * https://docs.oracle.com/javase/7/docs/api/java/lang/Cloneable.html
	 *
	 * You must also respect the contract for .equals() and .hashCode().
	 *
	 * https://docs.oracle.com/javase/7/docs/api/java/lang/Object.html#equals(java.lang.Object)
	 *
	 * https://docs.oracle.com/javase/7/docs/api/java/lang/Object.html#hashCode()
	 *
	 * In summary:
	 *
	 *     .equals() MUST return true for any PlannerWorldState that is
	 *     "logically equals" to another. What this means is specific to your
	 *     application, so I cannot make the AI system handle it for you, but
	 *     this should be pretty obvious.
	 *
	 *     .hashCode() MUST return the same int for objects that are .equals()
	 *     to eachother.
	 *
	 *     .hashCode() SHOULD return different integers for different objects,
	 *     but not doing this won't make the application break, it will just
	 *     impact performance. Don't stress too much about making every change
	 *     change the hashCode. Really. Don't.
	 *
	 * This is the bare minimum implementation for a world state that does
	 * literally nothing.
	 */
	public class PlannerWorldState implements WorldState {
		@Override
		public boolean equals(Object o) {
			/**
			 * These first two lines are boilerplate you should put in every
			 * one of your .equals() implementations. In theory they aren't
			 * needed, but they're a good idea to have just in case.
			 */
			if (o == null) return false;
			if (!(o instanceof PlannerWorldState)) return false;

			/**
			 * Normally you'd do more here. We'll do more later, but for now
			 * this is enough. If we're a PlannerWorldState we're going to say
			 * that we're equals.
			 */
			return true;
		}
		/**
		 * We want a world state that will be .equals() to our old one, but not
		 * == to it. Given our .equals() implementation, any PlannerWorldState
		 * will be .equals(), so to make it not ==, we just need a new one.
		 */
		@Override
		public Object clone() {
			return new PlannerWorldState();
		}
	}

	/**
	 * A behavior is an action that our AI can take that should impact our
	 * WorldState. Well, in this first ultra-simple test, our world states
	 * are completely blank, so we don't need a behavior to actually modify
	 * things. This, then, is the most simplistic implementation of
	 * PlannerBehavior we can possibly have.
	 */
	public class PlannerBehavior implements Behavior<PlannerWorldState> {
		/**
		 * The purpose of modifyState is to ... well, modify the state to what
		 * it would be like after this behavior is run. Our behavior doesn't
		 * DO anything, so we just don't modify the world state at all.
		 *
		 * We still have to make this function to make our class not abstract,
		 * though.
		 */
		@Override
		public void modifyState(PlannerWorldState pws) {
		}

		/**
		 * Sometimes behaviors aren't possible to run right now. Maybe this
		 * behavior is to fire a gun, but we aren't holding a gun. For our
		 * case, we can always run this do-nothing behavior, so just always
		 * return true. This is a bare minimum implementation, but it's also a
		 * situation you might see in a real implementation.
		 */
		@Override
		public boolean isRunnable(PlannerWorldState pws) {
			return true;
		}

		@Override
		public Float getCost(PlannerWorldState worldState) {
			return 1.0f;
		}
	}

	/**
	 * A goal represents the idea of a long term goal for this AI. A goal has
	 * some logic to tell whether a given WorldState "satisfies" it. This logic
	 * can be as complicated or simple as you want to make it. The ultimate goal
	 * of the AI logic is to figure out how to satisfy your goal, given the
	 * list of behaviors that it's allowed to run.
	 */
	public class PlannerGoal implements Goal<PlannerWorldState> {
		/**
		 * isSatisfied is just logic to tell whether we are satisfied. In this
		 * particular case, we have no variables to look at to tell whether we
		 * are satisfied, so we shall NEVER be satisfied. Always being
		 * satisfied is TOO simplistic even for this simple test.
		 */
		@Override
		public boolean isSatisfied(PlannerWorldState pws) {
			return false;
		}

		/**
		 * How does the AI system tell how to satisfy your goal? Your goal is
		 * responsible for looking at two world states and telling the system
		 * whether they are equal distance or, if not, which one is closer.
		 *
		 * Our world states are featureless, so obviously all are equal distance
		 * from eachother.
		 *
		 * You would return -1 if a is closer than b, and +1 if b is closer
		 * than a.
		 */
		@Override
		public int compare(PlannerWorldState a, PlannerWorldState b) {
			return 0;
		}
	}

	@Test
	public void testInitialStateCreation() {
		PlannerWorldState ws = new PlannerWorldState();
		PlannerGoal pg = new PlannerGoal();
		ArrayList<PlannerBehavior> pbp = new ArrayList<PlannerBehavior>();

		Planner<PlannerWorldState, PlannerGoal, PlannerBehavior, ArrayList<PlannerBehavior>> p = new Planner<>();

		State<PlannerWorldState, PlannerGoal, PlannerBehavior, ArrayList<PlannerBehavior>> ps = p.startPlanning(ws, pg, pbp);
		
		assertFalse("Initial state should not be considered 'done'.", ps.isDone());
		assertTrue("Initial planner should have the same world state as when created.", ps.getWorldState().equals(ws));
		assertTrue("Initial planner should have the same goal as when created.", ps.getGoal().equals(pg));
		assertEquals("Should have one state in the open set initially", 1, ps.getOpenSetSize());
		assertEquals("Should have no states in the closed set initially", 0, ps.getClosedSetSize());
		PlannerWorldState ws2 = ps.getHighestPriority();
		assertNotNull("Highest priority cannot be null directly after creation.", ws2);
		assertEquals("Highest priorty should be the same as we passed in, not just .equals", ws, ws2);
    }
}
