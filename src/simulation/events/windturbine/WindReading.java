package simulation.events.windturbine;

import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

public class WindReading extends Event {
    public static class Reading implements EventInformationI{
        private static final long serialVersionUID = 1L;
        public final double value ;

        public Reading(double value){
            super();
            this.value = value;
        }
    }

    private static final long serialVersionUID = 1L ;

    public WindReading(Time timeOfOccurrence, double windReading){
        super(timeOfOccurrence, new Reading(windReading)) ;
        assert windReading >= 0.0 ;
    }

    @Override
    public String eventAsString(){
        return "WindReading(" + this.eventContentAsString() + ")" ;
    }

    @Override
    public String eventContentAsString(){
        return  "time = " + this.getTimeOfOccurrence() + ", " +
                "wind = " + ((Reading)this.getEventInformation()).value
                + " km/h" ;
    }
}
