package simulation.sil.battery.events;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulation.sil.electricmeter.models.ElectricMeterModel;

/**
 * The class <code>BatteryProduction</code> represents the fact that
 * the battery production changed
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 */
public class BatteryProduction extends Event {

    private static final long serialVersionUID = 1L;

    public static class Reading implements EventInformationI {
        private static final long serialVersionUID = 1L;
        public final double value;

        public Reading(double value) {
            super();
            this.value = value;
        }
    }
    
    /**
     * Creates a BatteryProduction event
     * @param timeOfOccurrence time of the event
     * @param value value of the consumption at the time of the event
     */
    public BatteryProduction(Time timeOfOccurrence, double value) {
        super(timeOfOccurrence, new Reading(value));
    }

    @Override
    public String eventAsString() {
        return "BatteryProduction(" + this.getTimeOfOccurrence().getSimulatedTime() + ")";
    }

    @Override
    public String eventContentAsString() {
        return "power = " + ((Reading)this.getEventInformation()).value +" Watts "+
                "\nproduced power = "+((Reading) this.getEventInformation()).value+ " Watts";
    }

    @Override
    public void executeOn(AtomicModel model) {
        assert model instanceof ElectricMeterModel;
        ElectricMeterModel m = (ElectricMeterModel) model;
        m.setBatteryProduction(((Reading)this.getEventInformation()).value);
    }
}