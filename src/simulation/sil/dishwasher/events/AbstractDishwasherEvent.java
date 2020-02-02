package simulation.sil.dishwasher.events;

import fr.sorbonne_u.devs_simulation.es.events.ES_Event;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

/**
 * The class <code>AbstractDishwasherEvent</code> represents an abstract 
 * event for the dishwasher model.
 *
 */
public class AbstractDishwasherEvent extends ES_Event{

    private static final long serialVersionUID = 1L;

    public AbstractDishwasherEvent(Time timeOfOccurence, EventInformationI content){
        super(timeOfOccurence, content);
    }
}