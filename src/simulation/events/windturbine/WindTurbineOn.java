package simulation.events.windturbine;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulation.models.windturbine.WindTurbineModel;

public class WindTurbineOn extends Event{

    private static final long serialVersionUID = 1L;

    public WindTurbineOn(Time timeOfOccurrence){
        super(timeOfOccurrence, null);
    }
    public String eventAsString(){
        return "WindTurbineOn(" + this.getTimeOfOccurrence().getSimulatedTime() + ")";
    }

    @Override
    public void executeOn(AtomicModel model) {
        WindTurbineModel m = (WindTurbineModel)model ;
        m.setState(WindTurbineModel.State.ON);
    }
}
