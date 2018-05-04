/**
 * This is the second of the unit tests. You should read these in file sort
 * order and read from top to bottom. They are written to be read.
 *
 * The comments in this file assume that you've read the previous ones and
 * doesn't re-explain things that were already explained. You've been warned.
 *
 * This file creates one of the simplest sets of behaviors and goals and walks
 * through the first step. The class implementations are informative as being
 * some of the simplest "real world" implementations I could think of. The
 * tests themselves are somewhat low level and check a lot of things that are
 * important to be true for performance reasons. It would be okay to only skim
 * the actual tests here and read the comments for the classes themselves.
 */
import com.gracefulcode.ai.*;
import com.gracefulcode.ai.internal.State;

import java.util.ArrayList;

import org.junit.Test;
import static org.junit.Assert.*;

public class A002_SingleStepTest {
	public class PlannerWorldState implements WorldState {
		/**
		 * Now we are storing actual state in our world state. That changes a
		 * few things significantly and means we can put actual logic into our
		 * tests!
		 *
		 * For now we have the most simple state possible: a single integer.
		 */
		public int value = 0;

		/**
		 * Now when we clone, we have to copy the value over. Primitive types
		 * are literally this easy. Object values won't be as easy, but we'll
		 * get to those later.
		 */
		@Override
		public Object clone() {
			PlannerWorldState tmp = new PlannerWorldState();
			tmp.value = this.value;
			return tmp;
		}

		/**
		 * Now we can see the general pattern for how to implement .equals()
		 * with a more complex world state. For efficiency's sake, if you have
		 * a world state value that's expensive to check for equality (say, a
		 * String value), check it last. Also try to check the values that
		 * change the most often first. That way you won't have to check the
		 * later values at all since all we want to know is whether it's
		 * different, not HOW different.
		 */
		@Override
		public boolean equals(Object o) {
			if (o == null) return false;
			if (!(o instanceof PlannerWorldState)) return false;

			PlannerWorldState pws = (PlannerWorldState)o;
			if (this.value != pws.value) return false;
			return true;
		}

		/**
		 * hashCode is now more complicated as well. Well, not very much more.
		 * We want our hashCode to change any time our logical value changes.
		 * Well our logical value is just a single variable, and it's already
		 * an int, so let's just return that.
		 */
		@Override
		public int hashCode() {
			return this.value;
		}
	}

	/**
	 * This class isn't strictly necessary in this case. It is useful for later
	 * so that we can say "ArrayList<PlannerBehavior>" rather than
	 * "ArrayList<Behavior<PlannerWorldState>>", which is much more cumbersome.
	 *
	 * I find that I often have a need to subclass Behavior anyway.
	 */
	public abstract class PlannerBehavior implements Behavior<PlannerWorldState> {
		@Override
		public Float getCost(PlannerWorldState worldState) {
			return 1.0f;
		}

		@Override
		public boolean isRunnable(PlannerWorldState pws) {
			return true;
		}
	}

	/**
	 * BehaviorA will increase the state value. This is a "good" thing for the
	 * goal that we will define later.
	 */
	public class PlannerBehaviorA extends PlannerBehavior {
		/**
		 * This is the first time we've seen modifying state, so let's talk
		 * about some high level design principles I've noticed in my own work.
		 *
		 *     a) If a behavior can fail, for instance if the behavior is
		 *        something like "search for an enemy" and you cannot tell
		 *        whether you are going to find an enemy or not, you should
		 *        assume that you will here. Don't try to do anyhting
		 *        complicated, just pretend like you succeeded.
		 *
		 *     b) If you may want to run this behavior many times back to back,
		 *        you may want to exaggerate the effect of running this
		 *        behavior. For instance, if you have an "eat" behavior, you
		 *        may want to set hunger to 0 here rather than just slightly
		 *        reduce it like you may do in a real world application. This
		 *        allows the planner to not have to discover the ability to run
		 *        it multiple times. Use your human logic to figure out if this
		 *        is a good idea.
		 *
		 * These only really work if you generate a plan, execute the first
		 * behavior, and then plan again. That's what I've been doing and what
		 * I suggest. You can try to get fancy and only sometimes plan again,
		 * but I haven't been bothering at least so far.
		 *
		 * Of note, I am arguably violating principle b (depending on what
		 * "value" represents here). I am doing this for the test, not because
		 * it's necessarily how I would normally write my behaviors. Again, it
		 * depends on what "value" represents here.
		 */
		@Override
		public void modifyState(PlannerWorldState pws) {
			pws.value++;
		}
	}

	/**
	 * This behavior decrements the value. For this particular goal we never
	 * actually WANT this, it's just here for testing purposes.
	 */
	public class PlannerBehaviorB extends PlannerBehavior {
		@Override
		public void modifyState(PlannerWorldState pws) {
			pws.value--;
		}
	}

	/**
	 * Our goal can now have some logic, since our world state is more complex!
	 */
	public class PlannerGoal implements Goal<PlannerWorldState> {
		/**
		 * To satisfy this goal, we need to get our value to at least 10. This
		 * is pretty simple logic, but still the first time we've seen logic
		 * in isSatisfied.
		 */
		@Override
		public boolean isSatisfied(PlannerWorldState pws) {
			if (pws.value >= 10) return true;
			return false;
		}

		/**
		 * We can now compare world states. Higher values are better. If our
		 * goal were to get to exactly 10 we would have to implement this a bit
		 * differently. You'll see that in a future test. But since we're
		 * checking for >= 10, I'm saying that higher numbers are always
		 * "better."
		 */
		@Override
		public int compare(PlannerWorldState a, PlannerWorldState b) {
			if (a.value > b.value) {
				return -1;
			}
			if (a.value < b.value) {
				return 1;
			}
			return 0;
		}
	}

    @Test
    public void testSingleStepAB() throws Exception {
    	System.out.println("testSingleStepAB");
		PlannerWorldState ws = new PlannerWorldState();
		PlannerGoal pg = new PlannerGoal();

		ArrayList<PlannerBehavior> pbp = new ArrayList<PlannerBehavior>();
		pbp.add(new PlannerBehaviorA());
		pbp.add(new PlannerBehaviorB());

		Planner<PlannerWorldState, PlannerGoal, PlannerBehavior, ArrayList<PlannerBehavior>> p = new Planner<>();
		State<PlannerWorldState, PlannerGoal, PlannerBehavior, ArrayList<PlannerBehavior>> ps = p.startPlanning(ws, pg, pbp);
		boolean didStep = p.stepState(ps);
		assertTrue(".step() should succeed!", didStep);

		/**
		 * After a single step, our original world state is closed, and the new highest is in the chamber, so we only
		 * have one in open and one in closed.
		 */
		assertFalse("Should not be done after a single step.", ps.isDone());
		assertEquals("Should have one closed state.", 1, ps.getClosedSetSize());
		assertEquals("We should have one open state.", 1, ps.getOpenSetSize());

		assertEquals("Our current state should now be the one at value of 1", 1, ps.getWorldState().value);

		PlannerWorldState ws2 = ps.getHighestPriority();
		assertNotNull("Highest priority should not be null!", ws2);
		assertEquals("Our highest state should have a value of -1 now.", -1, ws2.value);
		assertNotEquals("Highest priority should not be our initial state.", ws2, ws);

		assertFalse("Planner after a step should have a different world state than when created.", ps.getWorldState().equals(ws));
		assertNotEquals("We should be looking at a new state now.", ps.getWorldState(), ws);
    }

    @Test
    public void testSingleStepBA() throws Exception {
    	System.out.println("testSingleStepBA");
		PlannerWorldState ws = new PlannerWorldState();
		PlannerGoal pg = new PlannerGoal();

		ArrayList<PlannerBehavior> pbp = new ArrayList<PlannerBehavior>();
		pbp.add(new PlannerBehaviorB());
		pbp.add(new PlannerBehaviorA());

		Planner<PlannerWorldState, PlannerGoal, PlannerBehavior, ArrayList<PlannerBehavior>> p = new Planner<>();
		State<PlannerWorldState, PlannerGoal, PlannerBehavior, ArrayList<PlannerBehavior>> ps = p.startPlanning(ws, pg, pbp);
		boolean didStep = p.stepState(ps);
		assertTrue(".step() should succeed!", didStep);
		assertFalse("Should not be done after a single step.", ps.isDone());

		/**
		 * After a single step, our original world state is closed, and the new highest is in the chamber, so we only
		 * have one in open and one in closed.
		 */
		assertEquals("Should have one state in the closed set after a run", 1, ps.getClosedSetSize());
		assertEquals("We should have one open state.", 1, ps.getOpenSetSize());

		PlannerWorldState ws2 = ps.getHighestPriority();
		assertNotNull("Highest priority should not be null!", ws2);
		assertFalse("Highest priority should not be == our initial state", ws2 == ws);

		assertFalse("Planner after a step should have a different world state than when created.", ps.getWorldState().equals(ws));
		assertNotEquals("We should be looking at a new state now.", ps.getWorldState(), ws);
    }
}
