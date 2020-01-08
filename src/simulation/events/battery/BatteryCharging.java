package simulation.events.battery;

import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.time.Time;

public class BatteryCharging extends Event{

    private static final long serialVersionUID = 1L;

    public BatteryCharging(Time timeOfOccurrence){
        super(timeOfOccurrence, null);
    }
    public String eventAsString(){
        return "SwitchOn(" + this.getTimeOfOccurrence().getSimulatedTime() + ")";
    }
}