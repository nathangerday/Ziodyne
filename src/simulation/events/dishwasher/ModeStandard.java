package simulation.events.dishwasher;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulation.models.dishwasher.DishwasherModel;

public class ModeStandard extends AbstractDishwasherEvent {
    
    private static final long serialVersionUID = 1L;

    public ModeStandard(Time timeOfOccurrence) {
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
    	if (e instanceof DishwasherOn || e instanceof ModeEco) {
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
