package simulation.events.dishwasher;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulation.models.dishwasher.DishwasherModel;

public class DishwasherOn extends AbstractDishwasherEvent{

    private static final long serialVersionUID = 1L;

    public DishwasherOn(Time timeOfOccurrence) {
        super(timeOfOccurrence, null);
    }

    @Override
    public String eventAsString()
    {
        return "Dishwasher::SwitchOn" ;
    }

    @Override
    public boolean	hasPriorityOver(EventI e)
    {
        return true ;
    }

    @Override
    public void	executeOn(AtomicModel model)
    {
        assert	model instanceof DishwasherModel;

        ((DishwasherModel)model).setState(DishwasherModel.State.ON) ;
    }
}
