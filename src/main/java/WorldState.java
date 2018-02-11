package com.gracefulcode.ai;

/**
 * Keeps track of information about the world that is relevant to behaviors and goals.
 *
 * @author Daniel Grace <dgrace@gracefulcode.com>
 * @version 0.1.0
 * @since 0.1.0
 */

public interface WorldState extends java.lang.Cloneable {
	/**
	 * Clones this WorldState, making it ready for minor alterations. While we
	 * are iterating through the possible paths, we are cloning WorldStates
	 * quite a bit.
	 *
	 * @return The new WorldState, which should .equals() the old one.
	 * @since 0.1.0
	 */
	public WorldState clone();
}