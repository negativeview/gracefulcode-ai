import com.gracefulcode.ai.*;
import com.gracefulcode.ai.internal.State;

import java.util.ArrayList;

import org.junit.Test;
import static org.junit.Assert.*;

public class A005_Happiness {
	public class PlannerWorldState implements WorldState {
		public int happiness = 0;
		public boolean hasSocializedRecently = false;

		@Override
		public Object clone() {
			PlannerWorldState tmp = new PlannerWorldState();
			tmp.happiness = this.happiness;
			tmp.hasSocializedRecently = this.hasSocializedRecently;
			return tmp;
		}

		@Override
		public String toString() {
			return "PlannerWorldState[happiness:" + this.happiness + ", hasSocializedRecently: " + this.hasSocializedRecently + "]";
		}

		@Override
		public boolean equals(Object o) {
			if (o == null) return false;
			if (!(o instanceof PlannerWorldState)) return false;

			PlannerWorldState pws = (PlannerWorldState)o;
			if (this.happiness != pws.happiness) return false;
			if (this.hasSocializedRecently != pws.hasSocializedRecently) return false;
			return true;
		}

		@Override
		public int hashCode() {
			return this.happiness + (this.hasSocializedRecently ? 100 : 0);
		}
	}

	public abstract class PlannerBehavior implements Behavior<PlannerWorldState> {
	}

	public class PlannerBehaviorSocialize extends PlannerBehavior {
		@Override
		public void modifyState(PlannerWorldState pws) {
			if (pws.hasSocializedRecently) {
				pws.hasSocializedRecently = true;
				pws.happiness += 10;
			}
		}

		@Override
		public Float getCost(PlannerWorldState worldState) {
			return 10.0f;
		}

		@Override
		public boolean isRunnable(PlannerWorldState worldState) {
			return true;
		}
	}

	public class PlannerBehaviorRelaxAlone extends PlannerBehavior {
		@Override
		public void modifyState(PlannerWorldState pws) {
			pws.hasSocializedRecently = false;
			pws.happiness -= 5;
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

	public class PlannerGoal implements Goal<PlannerWorldState> {
		@Override
		public boolean isSatisfied(PlannerWorldState pws) {
			return false;
		}

		@Override
		public int compare(PlannerWorldState a, PlannerWorldState b) {
			if (a.happiness > b.happiness) return -1;
			if (b.happiness > a.happiness) return 1;
			return 0;
		}
	}

	@Test
	public void testAlternatingBehaviors() throws Exception {
		PlannerWorldState ws = new PlannerWorldState();
		ws.happiness = 0;
		ws.hasSocializedRecently = false;
		PlannerGoal pg = new PlannerGoal();

		ArrayList<PlannerBehavior> pbp = new ArrayList<PlannerBehavior>();
		pbp.add(new PlannerBehaviorSocialize());
		pbp.add(new PlannerBehaviorRelaxAlone());

		Planner<PlannerWorldState, PlannerGoal, PlannerBehavior, ArrayList<PlannerBehavior>> p = new Planner<>();
		State<PlannerWorldState, PlannerGoal, PlannerBehavior, ArrayList<PlannerBehavior>> ps = p.startPlanning(ws, pg, pbp);

		for (int i = 0; i < 10; i++) {
			boolean didStep = p.stepState(ps);
			assertTrue("Steps should all succeed.", didStep);
		}

		ArrayList<PlannerBehavior> plan = p.getPlan(ps);
		assertEquals("We should return 10 behaviors from getPlan", 10, plan.size());
		System.out.println(plan);
		assertEquals("First we should socialize.", plan.get(9).getClass(), PlannerBehaviorSocialize.class);
	}
}
