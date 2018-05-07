import com.gracefulcode.ai.*;
import com.gracefulcode.ai.internal.State;

import java.util.ArrayList;

import org.junit.Test;
import static org.junit.Assert.*;

public class A006_DifferingPathLengths {
	public class PlannerWorldState implements WorldState {
		public int score = 0;

		@Override
		public Object clone() {
			PlannerWorldState tmp = new PlannerWorldState();
			tmp.score = this.score;
			return tmp;
		}

		@Override
		public String toString() {
			return "PlannerWorldState[score: " + this.score + "]";
		}

		@Override
		public boolean equals(Object o) {
			if (o == null) return false;
			if (!(o instanceof PlannerWorldState)) return false;

			PlannerWorldState pws = (PlannerWorldState)o;
			if (this.score != pws.score) return false;
			return true;
		}

		@Override
		public int hashCode() {
			return this.score;
		}
	}

	public abstract class PlannerBehavior implements Behavior<PlannerWorldState> {
	}

	public class PlannerBehaviorSmallStep extends PlannerBehavior {
		@Override
		public void modifyState(PlannerWorldState pws) {
			pws.score += 1;
		}

		@Override
		public Float getCost(PlannerWorldState worldState) {
			return 1.0f;
		}

		@Override
		public boolean isRunnable(PlannerWorldState worldState) {
			return true;
		}
	}

	public class PlannerBehaviorBigStep extends PlannerBehavior {
		@Override
		public void modifyState(PlannerWorldState pws) {
			pws.score += 2;
		}

		@Override
		public Float getCost(PlannerWorldState worldState) {
			return 5.0f;
		}

		@Override
		public boolean isRunnable(PlannerWorldState worldState) {
			return true;
		}
	}

	public class PlannerGoal implements Goal<PlannerWorldState> {
		@Override
		public boolean isSatisfied(PlannerWorldState pws) {
			if (pws.score >= 2) return true;
			return false;
		}

		@Override
		public int compare(PlannerWorldState a, PlannerWorldState b) {
			if (a.score > b.score) return -1;
			if (b.score > a.score) return 1;
			return 0;
		}
	}

	@Test
	public void testScoreHasAnEffect() throws Exception {
		PlannerWorldState ws = new PlannerWorldState();
		ws.score = 0;

		PlannerGoal pg = new PlannerGoal();

		ArrayList<PlannerBehavior> pbp = new ArrayList<PlannerBehavior>();
		pbp.add(new PlannerBehaviorSmallStep());
		pbp.add(new PlannerBehaviorBigStep());

		Planner<PlannerWorldState, PlannerGoal, PlannerBehavior, ArrayList<PlannerBehavior>> p = new Planner<>();
		State<PlannerWorldState, PlannerGoal, PlannerBehavior, ArrayList<PlannerBehavior>> ps = p.startPlanning(ws, pg, pbp);

		assertEquals(1, ps.getOpenSetSize());
		assertEquals(0, ps.getClosedSetSize());

		p.stepState(ps);

		assertFalse(ps.isDone());
		assertEquals(0, ps.getOpenSetSize());
		assertEquals(2, ps.getClosedSetSize());

		p.stepState(ps);

		assertEquals(0, ps.getOpenSetSize());
		assertEquals(3, ps.getClosedSetSize());
		assertTrue("PlannerState should be done now.", ps.isDone());
		assertEquals(2, ps.getBestWorldState().score);

		System.out.println("Best world state: " + ps.getBestWorldState());

		ArrayList<PlannerBehavior> plan = p.getPlan(ps);
		System.out.println("Behaviors:");
		for (PlannerBehavior pb: plan) {
			System.out.println("\t" + pb);
		}

		System.out.println("State to Node:");
		for (PlannerWorldState ws2: ps.getGlobalState().stateToNode.keySet()) {
			System.out.println("\t" + ws2.toString());
			ps.getGlobalState().stateToNode.get(ws2).debugParent(10);
		}
		assertEquals("We should return 2 behaviors from getPlan: " + plan.toString(), 2, plan.size());
	}
}
