/**
 * Behaviors are the things responsible for transforming one WorldState into
 * another. I don't want to make doing that copy too onerous, though.
 */
public interface Behavior<WS extends WorldState> {
	public boolean isRunnable();
	public void modifyState(WS worldState);
}