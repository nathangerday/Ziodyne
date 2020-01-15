package simulation.events.lamp;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulation.models.lamp.LampModel;

public class LampMedium extends AbstractLampEvent {

    private static final long serialVersionUID = 1L;

    public LampMedium(Time timeOfOccurrence) {
        super(timeOfOccurrence, null);
    }


    @Override
    public String	eventAsString()
    {
        return "Lamp::SetMedium" ;
    }


    @Override
    public boolean			hasPriorityOver(EventI e)
    {
        if (e instanceof LampOn || e instanceof LampLow ) {
            return false ;
        } else {
            return true ;
        }
    }


    @Override
    public void	executeOn(AtomicModel model)
    {
        assert	model instanceof LampModel;

        LampModel m = (LampModel)model ;
        m.setState(LampModel.State.MEDIUM) ;
    }
}
