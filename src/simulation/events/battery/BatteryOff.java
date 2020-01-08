package simulation.events.battery;

import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.time.Time;

public class BatteryOff extends Event {
	   private static final long serialVersionUID = 1L;

	    public BatteryOff(Time timeOfOccurrence){
	        super(timeOfOccurrence, null);
	    }
	    public String eventAsString(){
	        return "BatteryOff(" + this.getTimeOfOccurrence().getSimulatedTime() + ")";
	    }
}
