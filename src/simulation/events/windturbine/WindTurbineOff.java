package simulation.events.windturbine;

import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.time.Time;

public class WindTurbineOff extends Event{

    private static final long serialVersionUID = 1L;

    public WindTurbineOff(Time timeOfOccurrence){
        super(timeOfOccurrence, null);
    }
    public String eventAsString(){
        return "SwitchOff(" + this.getTimeOfOccurrence().getSimulatedTime() + ")";
    }
}
