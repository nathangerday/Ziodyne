package simulation.sil.lamp.events;

import components.Lamp;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulation.sil.lamp.models.LampModel;

public class LampLow extends AbstractLampEvent {

    private static final long serialVersionUID = 1L;

    public LampLow(Time timeOfOccurrence) {
        super(timeOfOccurrence, null);
    }

    @Override
    public String eventAsString() {
        return "Lamp::LampLow";
    }

    @Override
    public void	executeOn(AtomicModel model) {
        assert	model instanceof LampModel;
        LampModel m = (LampModel)model;
        m.setState(Lamp.State.LOW);
    }
}
