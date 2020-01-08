package simulation.events.fridge;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulation.events.lamp.SwitchOn;
import simulation.models.fridge.FridgeModel;

public class LowerFreezerTemp extends AbstractFridgeEvent{

	public LowerFreezerTemp(Time timeOfOccurrence) {
		super(timeOfOccurrence, null);
	}
	
	@Override
    public String eventAsString()
    {
        return "Fridge::LowerFreezerTemp" ;
    }

    @Override
    public boolean	hasPriorityOver(EventI e)
    {
    	 if (e instanceof SwitchOn  || e instanceof RaiseFreezerTemp) {
             return false ;
         } else {
             return true ;
         }
    }

    @Override
    public void	executeOn(AtomicModel model)
    {
        assert	model instanceof FridgeModel;

        ((FridgeModel)model).lowerFreezerTemperature();
    }

}
