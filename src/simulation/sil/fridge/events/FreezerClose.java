package simulation.sil.fridge.events;

import components.Fridge.DoorState;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulation.sil.fridge.models.FridgeModel;

/**
 * The class <code>FreezerClose</code> represents the closing 
 * of the freezer's .
 */
public class FreezerClose extends AbstractFridgeEvent {

    private static final long serialVersionUID = 1L;

    public FreezerClose(Time timeOfOccurrence) {
		super(timeOfOccurrence,null);
	}
	
	@Override
    public String eventAsString()
    {
        return "Fridge::CloseFreezer" ;
    }


    @Override
    public void	executeOn(AtomicModel model)
    {
        assert	model instanceof FridgeModel;

        ((FridgeModel)model).setFreezerDoor(DoorState.CLOSE);
    }

}
