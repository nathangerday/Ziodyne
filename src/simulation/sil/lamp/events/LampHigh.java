package simulation.sil.lamp.events;

import components.Lamp;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulation.sil.lamp.models.LampModel;

/**
 * The class <code>LampHigh</code> represents the setting 
 * of the lamp on the high intensity.
 */
public class LampHigh extends AbstractLampEvent {

    private static final long serialVersionUID = 1L;

    public LampHigh(Time timeOfOccurrence) {
        super(timeOfOccurrence, null);
    }

    @Override
    public String eventAsString() {
        return "Lamp::LampHigh";
    }

    @Override
    public void	executeOn(AtomicModel model) {
        assert	model instanceof LampModel;

        LampModel m = (LampModel)model;
        m.setState(Lamp.LampState.HIGH);
    }
}
