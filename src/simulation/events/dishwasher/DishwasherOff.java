package simulation.events.dishwasher;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulation.models.dishwasher.DishwasherModel;

public class DishwasherOff extends AbstractDishwasherEvent {

    private static final long serialVersionUID = 1L;

    public DishwasherOff(Time timeOfOccurrence) {
        super(timeOfOccurrence, null);
    }

    @Override
    public String eventAsString()
    {
        return "Dishwasher::SwitchOff" ;
    }

    @Override
    public boolean	hasPriorityOver(EventI e)
    {
        return false;
    }

    @Override
    public void	executeOn(AtomicModel model)
    {
        assert	model instanceof DishwasherModel;

        ((DishwasherModel)model).setState(DishwasherModel.State.OFF) ;
    }
}
