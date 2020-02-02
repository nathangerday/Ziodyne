package simulation.sil.windturbine.events;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulation.sil.windturbine.models.WindTurbineModel;

/**
 * The class <code>WindReading</code> represents a new reading 
 * of the wind speed
 */
public class WindReading extends Event {

    private static final long serialVersionUID = 1L ;

    public static class Reading implements EventInformationI{
        private static final long serialVersionUID = 1L;
        public final double value ;

        public Reading(double value){
            super();
            this.value = value;
        }
    }

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

    @Override
    public void executeOn(AtomicModel model) {
        WindTurbineModel m = (WindTurbineModel)model ;
        m.setSpeed(((Reading)this.getEventInformation()).value);
    }
}
