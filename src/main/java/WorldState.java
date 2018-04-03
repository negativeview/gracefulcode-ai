/**
 * WorldState is an encoding of the state of the world. It is highly
 * application-dependent and exists primary to declare intent rather than to
 * demand a specific contract.
 *
 * @author Daniel Grace<dgrace@gracefulcode.com>
 * @version 0.1
 * @since 0.1
 */
public interface WorldState {
	public Object clone();	
}