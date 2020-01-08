package simulation.events.fridge;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulation.models.fridge.FridgeModel;

public class SwitchFreezerOff extends AbstractFridgeEvent {

	public SwitchFreezerOff(Time timeOfOccurrence) {
		super(timeOfOccurrence,null);
		// TODO Auto-generated constructor stub
	}
	
	@Override
    public String eventAsString()
    {
        return "Fridge::SwitchFreezerOff" ;
    }

    @Override
    public boolean	hasPriorityOver(EventI e)
    {
    	return false;
    }

    @Override
    public void	executeOn(AtomicModel model)
    {
        assert	model instanceof FridgeModel;

        ((FridgeModel)model).setStateFreezer(FridgeModel.State.OFF);
    }

}
