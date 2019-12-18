package simulation.events.lamp;

import fr.sorbonne_u.devs_simulation.es.events.ES_Event;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

public class AbstractLampEvent extends ES_Event {
    /**
     * create an event from the given time of occurrence and event description.
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     * pre	timeOfOccurrence != null
     * post	this.getTimeOfOccurrence().equals(timeOfOccurrence)
     * post	this.getEventInformation.equals(content)
     * </pre>
     *
     * @param timeOfOccurrence time of occurrence of the created event.
     * @param content          description of the created event.
     */
    public AbstractLampEvent(Time timeOfOccurrence, EventInformationI content) {
        super(timeOfOccurrence, content);
    }
}
