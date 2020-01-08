package simulation.events.battery;

import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.time.Time;

public class TicEvent extends Event {
	private static final long serialVersionUID = 1L;

	public TicEvent(Time timeOfOccurrence){
		super(timeOfOccurrence, null);
	}
	public String eventAsString(){
		return "TicEvent(" + this.getTimeOfOccurrence().getSimulatedTime() + ")";
	}
}
