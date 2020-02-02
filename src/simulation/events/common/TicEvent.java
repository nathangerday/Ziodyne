package simulation.events.common;

import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.time.Time;

/**
 * The class <code>TicEvent</code> represents a event that helps
 * simulate the passage of time.
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 */
public class TicEvent extends Event{

	private static final long serialVersionUID = 1L;
	
	/**
	 * Constructs a TicEvent using a specified time 
	 * @param timeOfOccurrence time of the event
	 */
	public TicEvent(Time timeOfOccurrence){
		super(timeOfOccurrence, null);
	}
	
	/**
	 * returns a string reprsentation of the event
	 */
	public String eventAsString(){
		return "TicEvent(" + this.getTimeOfOccurrence().getSimulatedTime() + ")";
	}
}
//------------------------------------------------------------------------------
