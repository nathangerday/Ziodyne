package simulation.events.dishwasher;

import fr.sorbonne_u.devs_simulation.es.events.ES_Event;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

public class AbstractDishwasherEvent extends ES_Event{
    public AbstractDishwasherEvent(Time timeOfOccurence, EventInformationI content){
        super(timeOfOccurence, content);
    }
}