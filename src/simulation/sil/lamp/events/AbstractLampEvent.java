package simulation.sil.lamp.events;

import fr.sorbonne_u.devs_simulation.es.events.ES_Event;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

/**
 * The class <code>AbstractLampEvent</code> represents an abstract 
 * event for the lamp model.
 *
 */
public class AbstractLampEvent extends ES_Event {

    private static final long serialVersionUID = 1L;

    public AbstractLampEvent(Time timeOfOccurrence, EventInformationI content) {
        super(timeOfOccurrence, content);
    }
}
