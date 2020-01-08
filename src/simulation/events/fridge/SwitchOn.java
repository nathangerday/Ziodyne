package simulation.events.fridge;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulation.models.fridge.FridgeModel;

public class SwitchOn  extends AbstractFridgeEvent{

	public SwitchOn(Time timeOfOccurrence) {
		super(timeOfOccurrence, null);
	}
	
	@Override
    public String eventAsString()
    {
        return "Fridge::SwitchOn" ;
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

        ((FridgeModel)model).setState(FridgeModel.State.ON) ;
    }
    
}
