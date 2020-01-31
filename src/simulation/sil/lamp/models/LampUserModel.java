package simulation.sil.lamp.models;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.random.RandomDataGenerator;

import fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import simulation.sil.lamp.events.LampHigh;
import simulation.sil.lamp.events.LampLow;
import simulation.sil.lamp.events.LampMedium;
import simulation.sil.lamp.events.LampOff;


@ModelExternalEvents(exported = {
        LampOff.class,
        LampHigh.class,
        LampMedium.class,
        LampLow.class})
public class LampUserModel extends AtomicES_Model {

    private static final long serialVersionUID = 1L;
    public static final String URI = "SILLampUserModel";

    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------

    /** initial delay before sending the first switch on event.				*/
    protected double	initialDelay;
    /** delay between uses of the lamp from one day to another.		*/
    protected double	interdayDelay;
    /** mean time between uses of the lamp in the same day.			*/
    protected double	meanTimeBetweenUsages;
    /** during one use, mean time the lamp is at high setting.	*/
    protected double	meanTimeAtHigh;
    /** during one use, mean time the lamp is at medium setting.	*/
    protected double	meanTimeAtMedium;
    /** during one use, mean time the lamp is at low setting.		*/
    protected double	meanTimeAtLow;
    /** next event to be sent.												*/
    protected Class<?>	nextEvent;
    /**	a random number generator from common math library.					*/
    protected final RandomDataGenerator rg;


    public LampUserModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
        super(uri, simulatedTimeUnit, simulationEngine);
        this.rg = new RandomDataGenerator();
        this.setLogger(new StandardLogger());
    }


    @Override
    public void	initialiseState(Time initialTime) {
        this.initialDelay = 10.0;
        this.interdayDelay = 100.0;
        this.meanTimeBetweenUsages = 10.0;
        this.meanTimeAtHigh = 8.0;
        this.meanTimeAtMedium = 4.0;
        this.meanTimeAtLow = 2.0;
        this.rg.reSeedSecure();

        super.initialiseState(initialTime);

        // Schedule the first LampLow event.
        Duration d1 = new Duration(
                this.initialDelay,
                this.getSimulatedTimeUnit());
        Duration d2 = new Duration(
                2.0 * this.meanTimeBetweenUsages *
                this.rg.nextBeta(1.75, 1.75),
                this.getSimulatedTimeUnit());
        Time t = this.getCurrentStateTime().add(d1).add(d2);
        this.scheduleEvent(new LampLow(t));

        // Redo the initialisation to take into account the initial event
        // just scheduled.
        this.nextTimeAdvance = this.timeAdvance();
        this.timeOfNextEvent = this.getCurrentStateTime().add(this.nextTimeAdvance);
    }


    @Override
    public ArrayList<EventI> output() {
        ArrayList<EventI> ret = super.output();
        this.nextEvent = ret.get(0).getClass();
        return ret;
    }


    @Override
    public void	userDefinedInternalTransition(Duration elapsedTime) {
        Duration d;
        if (this.nextEvent.equals(LampLow.class)) {
            d = new Duration(2.0 * this.meanTimeAtLow *
                    this.rg.nextBeta(1.75, 1.75),this.getSimulatedTimeUnit());
            Time t = this.getCurrentStateTime().add(d);
            this.scheduleEvent(new LampHigh(t));

            // also, plan the next switch on for the next day
            d = new Duration(this.interdayDelay, this.getSimulatedTimeUnit());
            this.scheduleEvent(new LampLow(this.getCurrentStateTime().add(d)));
        } else if (this.nextEvent.equals(LampHigh.class)) {
            d =	new Duration(2.0 * this.meanTimeAtHigh *
                    this.rg.nextBeta(1.75, 1.75),this.getSimulatedTimeUnit());
            this.scheduleEvent(new LampMedium(this.getCurrentStateTime().add(d)));
        } else if (this.nextEvent.equals(LampMedium.class)) {
            d = new Duration(2.0 * this.meanTimeAtMedium *
                    this.rg.nextBeta(1.75, 1.75),this.getSimulatedTimeUnit());
            this.scheduleEvent(new LampOff(this.getCurrentStateTime().add(d)));
        } else if (this.nextEvent.equals(LampOff.class)) {
            //Nothing
        }
    }
}
