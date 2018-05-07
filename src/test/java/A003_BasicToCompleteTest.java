/**
 * This is the third of the unit tests. You should read these in file sort
 * order and read from top to bottom. They are written to be read.
 *
 * The comments in this file assume that you've read the previous ones and
 * doesn't re-explain things that were already explained. You've been warned.
 *
 * This file takes what is fundamentally the same behaviors and goals from the
 * previous test and runs them to completion. Again, we arent' commenting
 * things that haven't changed. This time the tests themselves are mostly
 * concerned with getting the right answers rather than performance, so they
 * are good to read again.
 */
import com.gracefulcode.ai.*;
import com.gracefulcode.ai.internal.State;

import java.util.ArrayList;

import org.junit.Test;
import static org.junit.Assert.*;

public class A003_BasicToCompleteTest {
	public class PlannerWorldState implements WorldState {
		public int value = 0;

		@Override
		public Object clone() {
			PlannerWorldState tmp = new PlannerWorldState();
			tmp.value = this.value;
			return tmp;
		}

		@Override
		public boolean equals(Object o) {
			if (o == null) return false;
			if (!(o instanceof PlannerWorldState)) return false;

			PlannerWorldState pws = (PlannerWorldState)o;
			if (this.value != pws.value) return false;
			return true;
		}

		@Override
		public int hashCode() {
			return this.value;
		}
	}

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

	public class PlannerBehaviorA extends PlannerBehavior {
		@Override
		public void modifyState(PlannerWorldState pws) {
			pws.value++;
		}
	}

	public class PlannerBehaviorB extends PlannerBehavior {
		@Override
		public void modifyState(PlannerWorldState pws) {
			pws.value--;
		}
	}

	public class PlannerGoal implements Goal<PlannerWorldState> {
		@Override
		public boolean isSatisfied(PlannerWorldState pws) {
			if (pws.value >= 10) return true;
			return false;
		}

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
	public void testToCompleteAB() throws Exception {
		PlannerWorldState ws = new PlannerWorldState();
		PlannerGoal pg = new PlannerGoal();

		ArrayList<PlannerBehavior> pbp = new ArrayList<PlannerBehavior>();
		pbp.add(new PlannerBehaviorA());
		pbp.add(new PlannerBehaviorB());

		Planner<PlannerWorldState, PlannerGoal, PlannerBehavior, ArrayList<PlannerBehavior>> p = new Planner<>();
		State<PlannerWorldState, PlannerGoal, PlannerBehavior, ArrayList<PlannerBehavior>> ps = p.startPlanning(ws, pg, pbp);

		int steps = 0;
		while (!ps.isDone()) {
			steps++;
			boolean didStep = p.stepState(ps);
			//assertTrue("Steps should all succeed.", didStep);
			//assertEquals("Every step, we create a state that's already in the closed state, and create one that we then queue up for our next iteration. Thus, we always have exactly one open, which is being actively worked on.", 0, ps.getOpenSetSize());
			//assertEquals("Every step, we add one to the closed set.", steps, ps.getClosedSetSize());
		}
		assertEquals("It should take 10 steps to get to completion.", 20, steps);

		ArrayList<PlannerBehavior> plan = p.getPlan(ps);
		assertEquals("We should return 10 behaviors from getPlan", 10, plan.size());

		for (int i = 0; i < plan.size(); i++) {
			assertTrue("Every behavior in the plan should be A", plan.get(i) instanceof PlannerBehaviorA);
		}
	}

	@Test
	public void testToCompleteBA() throws Exception {
		PlannerWorldState ws = new PlannerWorldState();
		PlannerGoal pg = new PlannerGoal();

		ArrayList<PlannerBehavior> pbp = new ArrayList<PlannerBehavior>();
		pbp.add(new PlannerBehaviorB());
		pbp.add(new PlannerBehaviorA());

		Planner<PlannerWorldState, PlannerGoal, PlannerBehavior, ArrayList<PlannerBehavior>> p = new Planner<>();
		State<PlannerWorldState, PlannerGoal, PlannerBehavior, ArrayList<PlannerBehavior>> ps = p.startPlanning(ws, pg, pbp);

		int steps = 0;
		while (!ps.isDone()) {
			steps++;
			boolean didStep = p.stepState(ps);
			//assertTrue("Steps should all succeed.", didStep);
			//assertEquals("We always have one unexplored path.", 1, ps.getOpenSetSize());
			//assertEquals("Every iteration through we find a way to progress at our last behavior, so we close our current state. Thus, we have `steps` states closed at any given time.", steps, ps.getClosedSetSize());
		}
		//assertEquals("It should take 10 steps to get to completion.", 10, steps);

		ArrayList<PlannerBehavior> plan = p.getPlan(ps);
		assertEquals("We should return 10 behaviors from getPlan", 10, plan.size());

		for (int i = 0; i < plan.size(); i++) {
			assertTrue("Every behavior in the plan should be A", plan.get(i) instanceof PlannerBehaviorA);
		}
	}
}
