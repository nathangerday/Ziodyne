package simulation.sil.fridge.events;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulation.sil.electricmeter.models.ElectricMeterModel;

public class FridgeConsumption extends Event {

    private static final long serialVersionUID = 1L;

    public static class Reading implements EventInformationI {
        private static final long serialVersionUID = 1L;
        public final double value;

        public Reading(double value) {
            super();
            this.value = value;
        }
    }

    public FridgeConsumption(Time timeOfOccurrence, double value) {
        super(timeOfOccurrence, new Reading(value));
    }

    @Override
    public String eventAsString() {
        return "FridgeConsumption(" + this.getTimeOfOccurrence().getSimulatedTime() + ")";
    }

    @Override
    public String eventContentAsString() {
        return "power = " +  ((Reading)this.getEventInformation()).value +" Watts ";
    }

    @Override
    public void executeOn(AtomicModel model) {
        assert model instanceof ElectricMeterModel;
        ElectricMeterModel m = (ElectricMeterModel) model;
        m.setFridgeConsumption(((Reading)this.getEventInformation()).value);
    }
}
