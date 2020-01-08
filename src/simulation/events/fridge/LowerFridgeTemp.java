package simulation.events.fridge;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulation.models.fridge.FridgeModel;

public class LowerFridgeTemp extends AbstractFridgeEvent {

	public LowerFridgeTemp(Time timeOfOccurrence) {
		super(timeOfOccurrence,null);
		// TODO Auto-generated constructor stub
	}
	
	@Override
    public String eventAsString()
    {
        return "Fridge::LowerFridgeTemp" ;
    }

    @Override
    public boolean	hasPriorityOver(EventI e)
    {
    	 if (e instanceof SwitchOn  || e instanceof RaiseFridgeTemp) {
             return false ;
         } else {
             return true ;
         }
    }

    @Override
    public void	executeOn(AtomicModel model)
    {
        assert	model instanceof FridgeModel;

        ((FridgeModel)model).lowerFridgeTemperature();
    }

}
