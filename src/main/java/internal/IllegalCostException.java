package com.gracefulcode.ai.internal;

import com.gracefulcode.ai.Behavior;

import java.lang.Exception;

/**
 * Weird things happen if your behavior ever returns a cost of &lt;= 0. The
 * planner decides that doing this action takes no time/effort/etc, so why not
 * just do *nothing else*? Since there's no valid reason to do this, catch it
 * and throw an Exception!
 *
 * @version 0.1
 * @since 0.1
 */
public class IllegalCostException extends Exception {
	public Behavior behavior;
	public Float cost;

	public IllegalCostException(Behavior behavior, Float cost) {
		super("A behavior has an illegal cost.");
		this.behavior = behavior;
		this.cost = cost;
	}
}