package com.gracefulcode.ai.internal;

import com.gracefulcode.ai.WorldState;

import java.lang.Exception;

/**
 * Concrete classes of WorldState need to provide a clone operation. This
 * exception if thrown if that clone operation ever returns the same object.
 * This would cause some really hard-to-debug issues, so I have decided to be
 * very proactive in catching it.
 *
 * @version 0.1
 * @since 0.1
 */
public class IllegalCloneException extends Exception {
	public WorldState worldState;

	public IllegalCloneException(WorldState worldState) {
		super("Your clone operation on your WorldState seems to be not cloning.");
		this.worldState = worldState;
	}
}