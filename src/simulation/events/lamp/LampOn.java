package simulation.events.lamp;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulation.models.lamp.LampModel;

public class LampOn extends AbstractLampEvent{

    private static final long serialVersionUID = 1L;

    public LampOn(Time timeOfOccurrence) {
        super(timeOfOccurrence, null);
    }

    @Override
    public String eventAsString()
    {
        return "Lamp::SwitchOn" ;
    }

    @Override
    public boolean	hasPriorityOver(EventI e)
    {
        return true ;
    }

    @Override
    public void	executeOn(AtomicModel model)
    {
        assert	model instanceof LampModel;

        ((LampModel)model).setState(LampModel.State.LOW) ;
    }
}
