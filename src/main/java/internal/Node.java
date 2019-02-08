package com.gracefulcode.ai.internal;

import com.gracefulcode.ai.Behavior;
import com.gracefulcode.ai.WorldState;

import java.util.ArrayList;

/**
 * A Node is a node in a tree of behaviors that we are dynamcially building. It
 * contains four bits of information:
 * <p>
 * a) The world state that this node represents. This never changes and
 * effectively is this node's "ID."
 * <p>
 * b) The world state that we "came from," called the parent. If we find a
 * cheaper way to get here, this may be updated.
 * <p>
 * c) The behavior needed to get from our parent to us. If we find a cheaper
 * way to get here, this may also be updated and is often updated at the same
 * time our parent is.
 * <p>
 * d) A list of our children. That is, nodes for which we are the parent. This
 * is largely used for internal bookkeeping and shouldn't be needed outside of
 * the AI sysetm proper.
 *
 * @version 0.1
 * @since 0.1
 */
public class Node<WS extends WorldState, B extends Behavior<WS>>  {
	private WS worldState;
	private B behavior;
	private Node<WS, B> parent;
	private ArrayList<Node> children;

	public Node(WS worldState, B behavior, Node<WS, B> parent) {
		this.worldState = worldState;
		this.behavior = behavior;
		this.parent = parent;
		this.children = new ArrayList<Node>();
		if (parent != null) {
			parent.addChild(this);
		}
	}

	public Node(WS worldState) {
		this(worldState, null, null);
	}

	/**
	 * Gets the behavior associated with this node.
	 *
	 * @return The behavior.
	 */
	public B getBehavior() {
		return this.behavior;
	}

	/**
	 * Adds a child to this node. This is mostly used for internal bookkeeping.
	 * You should make sure to always keep child references up to date,
	 * however, as the system may remove nodes with no children.
	 *
	 * @param child The child to add.
	 */
	public void addChild(Node child) {
		this.children.add(child);
	}

	public void debugParent() throws IllegalCostException {
		System.out.println(this.behavior + ":" + this.getCost());
		if (this.parent != null) {
			this.parent.debugParent(2);
		}
	}

	public void debugParent(int indent) throws IllegalCostException {
		for (int i = 0; i < indent; i++) {
			System.out.print(" ");
		}
		System.out.println(this.behavior + ":" + this.getCost());
		if (this.parent != null) {
			this.parent.debugParent(indent + 2);
		}
	}

	/**
	 * Spits out some debugging information to stdout.
	 */
	public void debug() {
		System.out.println(this.behavior);
		for (Node n: this.children) {
			n.debug(2);
		}
	}

	/**
	 * Spits out some debugging information to stdout with a level of
	 * indentation. Do not call this directly, call the version without a
	 * parameter.
	 *
	 * @param indent The number of spaces to indent.
	 */
	public void debug(int indent) {
		for (int i = 0; i < indent; i++) {
			System.out.print(" ");
		}

		System.out.println(this.behavior + ":" + this.behavior.getCost(this.worldState));
		for (Node n: this.children) {
			n.debug(2 + indent);
		}
	}

	/**
	 * Gets the parent node of this node. Used primarily to reconstruct the
	 * path once a plan is complete.
	 *
	 * @return The parent of this node.
	 */
	public Node<WS, B> getParent() {
		return this.parent;
	}

	/**
	 * Gets the cost of this node. Does so by adding the cost of its behavior
	 * to the cost of its parent node. This means that we effectively walk the
	 * tree back to the root node whenver you call this function.
	 *
	 * @throws IllegalCostException if your cost ever returns &lt;= 0.0f
	 *
	 * @return The cost of this behavior and every behavior that comes before it.
	 */
	public Float getCost() throws IllegalCostException {
		if (this.behavior == null) {
			return 0.0f;
		}

		Float tmpCost = this.behavior.getCost(this.worldState);
		if (tmpCost <= 0) {
			throw new IllegalCostException(this.behavior, tmpCost);
		}

		if (this.parent != null) {
			return tmpCost + this.parent.getCost();
		}
		return tmpCost;
	}

	/**
	 * When we find a cheaper way to get to this node, we need to update that
	 * information by setting a new parent and behavior combination. This
	 * function updates the child record of the previous parent.
	 *
	 * @param newParent Our new parent.
	 * @param newBehavior Our new behavior.
	 */
	public void changeParent(Node<WS, B> newParent, B newBehavior) {
		this.parent.removeChild(this);
		this.parent = newParent;
		this.behavior = newBehavior;
		// Our children can stay just fine.
	}

	/**
	 * We have found a better way to get to one of our children nodes, so we
	 * are no longer considered their parent. Remove their record.
	 *
	 * @param myChild The node that used to be considered our child.
	 */
	public void removeChild(Node myChild) {
		this.children.remove(myChild);
	}

	public WS getWorldState() {
		return this.worldState;
	}
}

