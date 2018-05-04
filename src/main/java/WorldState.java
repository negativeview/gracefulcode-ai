package com.gracefulcode.ai;

/**
 * WorldState is an encoding of the state of the world. It is highly
 * application-dependent and exists primary to declare intent rather than to
 * demand a specific contract.
 *
 * @version 0.1
 * @since 0.1
 */
public interface WorldState extends Cloneable {
	/**
	 * Clones this WorldState and returns the clone. The clone should behave
	 * such that original.isEqual returns true, but original == return false.
	 * Any modifications that you do inside of your {@link Behavior}s should
	 * affect only the clone, not the original.
	 *
	 * @return The clone.
	 */
	public Object clone();	
}