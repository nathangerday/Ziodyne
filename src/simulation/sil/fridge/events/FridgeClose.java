package simulation.sil.fridge.events;

import components.Fridge.DoorState;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulation.sil.fridge.models.FridgeModel;


/**
 * The class <code>FridgeClose</code> represents the closing 
 * of the fridge's door.
 */
public class FridgeClose extends AbstractFridgeEvent{

    private static final long serialVersionUID = 1L;

    public FridgeClose(Time timeOfOccurrence) {
		super(timeOfOccurrence,null);
	}
	

	@Override
    public String eventAsString()
    {
        return "Fridge::CloseFridge" ;
    }


	@Override
    public void	executeOn(AtomicModel model)
    {
        assert	model instanceof FridgeModel;

        ((FridgeModel)model).setFridgeDoor(DoorState.CLOSE);
    }
	
	

}
