/**
 * This is the fourth of the unit tests. You should read these in file sort
 * order and read from top to bottom. They are written to be read.
 *
 * The comments in this file assume that you've read the previous ones and
 * doesn't re-explain things that were already explained. You've been warned.
 *
 * Now we're moving on to "real" behaviors and goals. These are harder to test,
 * so I save them until last.
 */
import com.gracefulcode.ai.*;
import com.gracefulcode.ai.internal.State;

import java.util.ArrayList;

import org.junit.Test;
import static org.junit.Assert.*;

public class A004_ModerateToCompleteTest {
	public class PlannerWorldState implements WorldState {
		public boolean hasAxe;
		public int amountOfWood;

		/**
		 * This is just a logical extension of what we've done before. This
		 * shouldn't look too surprising to you.
		 */
		@Override
		public Object clone() {
			PlannerWorldState tmp = new PlannerWorldState();
			tmp.hasAxe = this.hasAxe;
			tmp.amountOfWood = this.amountOfWood;
			return tmp;
		}

		@Override
		public String toString() {
			return "PlannerWorldState[hasAxe:" + this.hasAxe + "; amountOfWood:" + this.amountOfWood + "]";
		}

		/**
		 * Again, a logical extension.
		 */
		@Override
		public boolean equals(Object o) {
			if (o == null) return false;
			if (!(o instanceof PlannerWorldState)) return false;

			PlannerWorldState pws = (PlannerWorldState)o;
			if (this.hasAxe != pws.hasAxe) return false;
			if (this.amountOfWood != pws.amountOfWood) return false;
			return true;
		}

		/**
		 * Note that we're adding 100 to the hashCode if we have an axe. This
		 * avoids the problem that having an axe and 2 wood would otherwise
		 * look the same as not having an axe and having 1 wood. In general, I
		 * wouldn't worry about this too much as hashCode collisions just makes
		 * things a bit slower rather than making them fail, but it was easy
		 * to avoid here, so I did.
		 */
		@Override
		public int hashCode() {
			return (this.hasAxe ? 100 : 0) + this.amountOfWood;
		}
	}

	/**
	 * We are still avoiding testing anything cost-related, so we still just
	 * have a static cost.
	 */
	public abstract class PlannerBehavior implements Behavior<PlannerWorldState> {
		@Override
		public Float getCost(PlannerWorldState worldState) {
			return 1.0f;
		}
	}

	public class PlannerBehaviorChopWood extends PlannerBehavior {
		@Override
		public void modifyState(PlannerWorldState pws) {
			pws.amountOfWood++;
		}

		@Override
		public boolean isRunnable(PlannerWorldState worldState) {
			return worldState.hasAxe;
		}
	}

	public class PlannerBehaviorMakeAxe extends PlannerBehavior {
		@Override
		public void modifyState(PlannerWorldState pws) {
			pws.amountOfWood -= 2;
			pws.hasAxe = true;
		}

		@Override
		public boolean isRunnable(PlannerWorldState worldState) {
			if (worldState.amountOfWood < 2) return false;
			return true;
		}
	}

	public class PlannerGoal implements Goal<PlannerWorldState> {
		@Override
		public boolean isSatisfied(PlannerWorldState pws) {
			return (pws.amountOfWood >= 20);
		}

		@Override
		public int compare(PlannerWorldState a, PlannerWorldState b) {
			if (a.amountOfWood > b.amountOfWood) {
				return -1;
			}
			if (a.amountOfWood < b.amountOfWood) {
				return 1;
			}
			return 0;
		}
	}

	@Test
	public void testToCompleteHasAxe() throws Exception {
		PlannerWorldState ws = new PlannerWorldState();
		ws.hasAxe = true;
		ws.amountOfWood = 0;
		PlannerGoal pg = new PlannerGoal();

		ArrayList<PlannerBehavior> pbp = new ArrayList<PlannerBehavior>();
		pbp.add(new PlannerBehaviorChopWood());
		pbp.add(new PlannerBehaviorMakeAxe());

		Planner<PlannerWorldState, PlannerGoal, PlannerBehavior, ArrayList<PlannerBehavior>> p = new Planner<>();
		State<PlannerWorldState, PlannerGoal, PlannerBehavior, ArrayList<PlannerBehavior>> ps = p.startPlanning(ws, pg, pbp);

		int steps = 0;
		while (!ps.isDone()) {
			steps++;
			boolean didStep = p.stepState(ps);
			//assertTrue("Steps should all succeed.", didStep);
		}
		//assertEquals("It should take 20 steps to get to completion.", 20, steps);

		ArrayList<PlannerBehavior> plan = p.getPlan(ps);
		assertEquals("We should return 20 behaviors from getPlan", 20, plan.size());
	}

	@Test
	public void testToCompleteMustCreateAxeButHasNoWood() throws Exception {
		PlannerWorldState ws = new PlannerWorldState();
		ws.hasAxe = false;
		ws.amountOfWood = 0;
		PlannerGoal pg = new PlannerGoal();

		ArrayList<PlannerBehavior> pbp = new ArrayList<PlannerBehavior>();
		pbp.add(new PlannerBehaviorChopWood());
		pbp.add(new PlannerBehaviorMakeAxe());

		Planner<PlannerWorldState, PlannerGoal, PlannerBehavior, ArrayList<PlannerBehavior>> p = new Planner<>();
		State<PlannerWorldState, PlannerGoal, PlannerBehavior, ArrayList<PlannerBehavior>> ps = p.startPlanning(ws, pg, pbp);

		int steps = 0;
		boolean didStep = p.stepState(ps);
		assertFalse("We cannot progress any further. Step should fail.", didStep);
	}

	@Test
	public void testToCompleteMustCreateAxeAndHasWood() throws Exception {
		PlannerWorldState ws = new PlannerWorldState();
		ws.hasAxe = false;
		ws.amountOfWood = 10;
		PlannerGoal pg = new PlannerGoal();

		ArrayList<PlannerBehavior> pbp = new ArrayList<PlannerBehavior>();
		pbp.add(new PlannerBehaviorChopWood());
		pbp.add(new PlannerBehaviorMakeAxe());

		Planner<PlannerWorldState, PlannerGoal, PlannerBehavior, ArrayList<PlannerBehavior>> p = new Planner<>();
		State<PlannerWorldState, PlannerGoal, PlannerBehavior, ArrayList<PlannerBehavior>> ps = p.startPlanning(ws, pg, pbp);

		int steps = 0;
		while (!ps.isDone()) {
			steps++;
			boolean didStep = p.stepState(ps);
			//assertTrue("Steps should all succeed. Failed on step " + steps + ":" + ps, didStep);
		}
		//assertEquals("It should take 13 steps to get to completion.", 13, steps);

		ArrayList<PlannerBehavior> plan = p.getPlan(ps);
		assertEquals("We should return 13 behaviors from getPlan", 13, plan.size());
	}
}
