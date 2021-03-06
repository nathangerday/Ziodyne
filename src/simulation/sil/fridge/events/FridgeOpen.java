package simulation.sil.fridge.events;

import components.Fridge.DoorState;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulation.sil.fridge.models.FridgeModel;

/**
 * The class <code>FridgeOpen</code> represents the opening 
 * of the fridge's door.
 */
public class FridgeOpen extends AbstractFridgeEvent {

    private static final long serialVersionUID = 1L;

    public FridgeOpen(Time timeOfOccurrence) {
		super(timeOfOccurrence,null);
	}
	
	@Override
    public String eventAsString()
    {
        return "Fridge::OpenFridge" ;
    }

	
    @Override
    public void	executeOn(AtomicModel model)
    {
        assert	model instanceof FridgeModel;

        ((FridgeModel)model).setFridgeDoor(DoorState.OPEN);
    }

}
