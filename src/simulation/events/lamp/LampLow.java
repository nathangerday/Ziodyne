package simulation.events.lamp;

import fr.sorbonne_u.cyphy.examples.sg.equipments.hairdryer.models.events.SetHigh;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulation.models.lamp.LampModel;

public class LampLow extends AbstractLampEvent {

    private static final long serialVersionUID = 1L;

    public LampLow(Time timeOfOccurrence) {
        super(timeOfOccurrence, null);
    }

    @Override
    public String	eventAsString()
    {
        return "Lamp::SetLow" ;
    }

    @Override
    public boolean			hasPriorityOver(EventI e)
    {
        if (e instanceof LampOn  || e instanceof SetHigh || e instanceof LampMedium) {
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
        m.setState(LampModel.State.LOW) ;
    }
}
