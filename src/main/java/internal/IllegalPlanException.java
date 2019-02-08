package com.gracefulcode.ai.internal;

import com.gracefulcode.ai.WorldState;

import java.lang.Exception;

/**
 * Sometimes your plan cannot be completed. If you ask for the plan and it
 * cannot be completed, this exception will be thrown. We include the
 * GlobalState for investigation in a debugging context. There's unlikely to be
 * much you can do in an automated way.
 *
 * @version 0.1
 * @since 0.1
 */
public class IllegalPlanException extends Exception {
	public GlobalState globalState;

	public IllegalPlanException(GlobalState globalState) {
		super("The plan could not complete.");
		this.globalState = globalState;
	}
}