package com.gracefulcode.ai;

import java.util.Comparator;

/**
 * A goal is what we are ultimately trying to do. This interface lets us
 * programmatically evaluate whether we have done it, and if not, if we're
 * progressing in the correct direction.
 *
 * @version 0.1
 * @since 0.1
 */
public interface Goal<WS extends WorldState> extends Comparator<WS> {
	/**
	 * Checks whether this goal is satisfied. If so, we can bail on planning
	 * any further. Note that for very generial goals such as "increase
	 * happiness", you never *have* to return true here if you manually handle
	 * your AI loop.
	 *
	 * @param a The world state that may or may not satisfy this goal.
	 * @return True if this goal is satisfied, otherwise False.
	 */
	public boolean isSatisfied(WS a);
}