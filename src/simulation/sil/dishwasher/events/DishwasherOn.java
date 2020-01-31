package simulation.sil.dishwasher.events;

import components.Dishwasher.DWState;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulation.sil.dishwasher.models.DishwasherModel;

public class DishwasherOn extends AbstractDishwasherEvent{

    private static final long serialVersionUID = 1L;

    public DishwasherOn(Time timeOfOccurrence) {
        super(timeOfOccurrence, null);
    }

    @Override
    public String eventAsString(){
        return "Dishwasher::SwitchOn" ;
    }

    @Override
    public void	executeOn(AtomicModel model){
        assert	model instanceof DishwasherModel;
        DishwasherModel m = (DishwasherModel)model;
        if(!m.isOnBreak()) {
            m.setState(DWState.ON);
        }
    }
}