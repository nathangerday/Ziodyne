package simulation.events.lamp;

import fr.sorbonne_u.devs_simulation.es.events.ES_Event;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

public class AbstractLampEvent extends ES_Event {

    private static final long serialVersionUID = 1L;

    public AbstractLampEvent(Time timeOfOccurrence, EventInformationI content) {
        super(timeOfOccurrence, content);
    }
}
