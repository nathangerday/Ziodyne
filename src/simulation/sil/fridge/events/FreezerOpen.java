package simulation.sil.fridge.events;

import components.Fridge.DoorState;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulation.sil.fridge.models.FridgeModel;


/**
 * The class <code>FreezerOpen</code> represents the opening 
 * of the freezer's door.
 */
public class FreezerOpen extends AbstractFridgeEvent{

    private static final long serialVersionUID = 1L;

    public FreezerOpen(Time timeOfOccurrence) {
		super(timeOfOccurrence, null);
	}
	
	@Override
    public String eventAsString()
    {
        return "Fridge::OpenFreezer" ;
    }

    @Override
    public void	executeOn(AtomicModel model)
    {
        assert	model instanceof FridgeModel;

        ((FridgeModel)model).setFreezerDoor(DoorState.OPEN);
    }

}
