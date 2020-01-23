package simulation.events.electricmeter;

import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

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
}