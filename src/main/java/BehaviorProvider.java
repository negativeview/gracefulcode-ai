package com.gracefulcode.ai;

import java.util.ArrayList;

/**
 * A class responsible for providing a list of behaviors for a given entity.
 *
 * T is meant to be the class that represents your AI entity.
 *
 * @author Daniel Grace <dgrace@gracefulcode.com>
 * @version 0.1.0
 * @since 0.1.0
 */

public interface BehaviorProvider<T> {
	/**
	 * Gets the list of behaviors that this entity can theoretically perform. This list is meant to be dynamic and dependent on the class of the character, or whatever else makes sense for your game.
	 *
	 * @return An ArrayList of Behaviors for your T.
	 * @since 0.1.0
	 */
	public ArrayList<Behavior<T>> getBehaviors(T entity);
}
