import java.util.Comparator;

/**
 * A goal is what we are ultimately trying to do. This interface lets us
 * programmatically evaluate whether we have done it, and if not, if we're
 * progressing in the correct direction.
 */
public interface Goal<WS extends WorldState> extends Comparator<WS> {
	public boolean isSatisfied(WS a);
}