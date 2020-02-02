package simulation.sil.lamp.events;

import components.Lamp;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulation.sil.lamp.models.LampModel;


/**
 * The class <code>LampOff</code> represents the switching 
 * off of the lamp.
 */
public class LampOff extends AbstractLampEvent {

    private static final long serialVersionUID = 1L;

    public LampOff(Time timeOfOccurrence) {
        super(timeOfOccurrence, null);
    }

    @Override
    public String eventAsString() {
        return "Lamp::LampOff";
    }

    @Override
    public void	executeOn(AtomicModel model) {
        assert	model instanceof LampModel;
        ((LampModel)model).setState(Lamp.LampState.OFF);
    }
}
