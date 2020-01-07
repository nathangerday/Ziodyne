package simulation.events.dishwasher;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulation.events.lamp.SetLow;
import simulation.events.lamp.SetMedium;
import simulation.events.lamp.SwitchOn;
import simulation.models.dishwasher.DishwasherModel;

public class SetModeStandard extends AbstractDishwasherEvent {
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
     * @param timeOfOccurrence time of occurrence of the created event
     */
    public SetModeStandard(Time timeOfOccurrence) {
        super(timeOfOccurrence, null);
    }

    @Override
    public String eventAsString()
    {
        return "Dishwasher::SetModeStandard" ;
    }

    @Override
    public boolean	hasPriorityOver(EventI e)
    {
    	if (e instanceof SwitchOn || e instanceof SetModeEco) {
            return false ;
        } else {
            return true ;
        }
    }

    @Override
    public void	executeOn(AtomicModel model)
    {
        assert	model instanceof DishwasherModel;

        ((DishwasherModel)model).setState(DishwasherModel.State.STD) ;
    }
}
