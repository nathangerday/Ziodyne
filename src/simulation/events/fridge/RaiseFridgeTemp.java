package simulation.events.fridge;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulation.models.fridge.FridgeModel;

public class RaiseFridgeTemp extends AbstractFridgeEvent{

	public RaiseFridgeTemp(Time timeOfOccurrence) {
		super(timeOfOccurrence,null);
	}
	

	@Override
    public String eventAsString()
    {
        return "Fridge::RaiseFridgeTemp" ;
    }

    @Override
    public boolean	hasPriorityOver(EventI e)
    {
    	 if (e instanceof SwitchOn  || e instanceof LowerFridgeTemp) {
             return false ;
         } else {
             return true ;
         }
    }

    @Override
    public void	executeOn(AtomicModel model)
    {
        assert	model instanceof FridgeModel;

        ((FridgeModel)model).raiseFridgeTemperature();
    }
	
	

}
