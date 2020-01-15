package simulation.events.fridge;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulation.models.fridge.FridgeModel;

public class FreezerOn extends AbstractFridgeEvent{

    private static final long serialVersionUID = 1L;

    public FreezerOn(Time timeOfOccurrence) {
		super(timeOfOccurrence, null);
	}
	
	@Override
    public String eventAsString()
    {
        return "Fridge::SwitchFreezerOn" ;
    }

    @Override
    public boolean	hasPriorityOver(EventI e)
    {
    	 return true;
    }

    @Override
    public void	executeOn(AtomicModel model)
    {
        assert	model instanceof FridgeModel;

        ((FridgeModel)model).setStateFreezer(FridgeModel.State.ON);
    }

}