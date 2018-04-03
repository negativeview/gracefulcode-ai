/**
 * BehaviorProvider provides a list of behaviors that our AI system can use.
 *
 * Extends Iterable mostly to give it a nicer name. We don't (currently?) have
 * any demands past a basic Iterable.
 */
public interface BehaviorProvider<B extends Behavior> extends Iterable<B> {
}