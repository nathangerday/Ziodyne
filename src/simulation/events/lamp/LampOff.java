package simulation.events.lamp;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulation.models.lamp.LampModel;

public class LampOff extends AbstractLampEvent {

    private static final long serialVersionUID = 1L;

    public LampOff(Time timeOfOccurrence) {
        super(timeOfOccurrence, null);
    }

    @Override
    public String eventAsString()
    {
        return "Lamp::SwitchOff" ;
    }

    @Override
    public boolean	hasPriorityOver(EventI e)
    {
        return false;
    }

    @Override
    public void	executeOn(AtomicModel model)
    {
        assert	model instanceof LampModel;

        ((LampModel)model).setState(LampModel.State.OFF) ;
    }
}
