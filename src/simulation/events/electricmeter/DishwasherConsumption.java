package simulation.events.electricmeter;

import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

public class DishwasherConsumption extends Event {

	private static final long serialVersionUID = 1L;

	public static class Reading implements EventInformationI {
		private static final long serialVersionUID = 1L;
		public final double value;

		public Reading(double value) {
			super();
			this.value = value;
		}
	}

	public DishwasherConsumption(Time timeOfOccurrence, double power) {
		super(timeOfOccurrence, new Reading(power));
	}

	@Override
	public String eventAsString() {
		return "PowerReadingDishwasher(" + this.getTimeOfOccurrence().getSimulatedTime() + ")";
	}

	@Override
	public String eventContentAsString() {
		return "power = " + ((Reading)this.getEventInformation()).value +" Watts ";
	}
}
