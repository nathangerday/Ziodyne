package simulation.models.fridge;

import java.util.Vector;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.random.RandomDataGenerator;

import fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import simulation.events.fridge.FreezerOn;
import simulation.events.fridge.FreezerOff;
import simulation.events.fridge.FridgeOn;
import simulation.events.fridge.FridgeOff;


@ModelExternalEvents(exported = {
        FridgeOn.class,
        FridgeOff.class,
        FreezerOff.class,
        FreezerOn.class})
public class FridgeControllerModel extends AtomicES_Model {

    private static final long serialVersionUID = 1L;

    public static final String	URI = "FridgeUserModel" ;

    /** initial delay before sending the first switch on event.				*/
    protected double	initialDelay ;
    /** delay between uses of the fridge from one day to another.		*/
    protected double	interdayDelay ;
    /** mean time between uses of the fridge in the same day.			*/
    protected double	meanTimeBetweenUsages ;
    /** during one use, mean time the fridge/freezer is lowering or raising temperature.	*/
    protected double	meanTimeTempAction ;
    /** next event to be sent.												*/
    protected Class<?>	nextEvent ;

    /**	a random number generator from common math library.					*/
    protected final RandomDataGenerator rg ;
    /** the current state of the fridge simulation model.				*/
    protected FridgeModel.State fridge_s ;
    /** the current state of the freezer simulation model.				*/
    protected FridgeModel.State freezer_s ;

    public FridgeControllerModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
        super(uri, simulatedTimeUnit, simulationEngine);
        this.rg = new RandomDataGenerator() ;

        // create a standard logger (logging on the terminal)
        this.setLogger(new StandardLogger()) ;
    }

    @Override
    public void			initialiseState(Time initialTime)
    {
        this.initialDelay = 10.0 ;
        this.interdayDelay = 100.0 ;
        this.meanTimeBetweenUsages = 10.0 ;
        this.meanTimeTempAction = 5.0;
        this.fridge_s = FridgeModel.State.OFF ;
        this.freezer_s = FridgeModel.State.OFF;
        this.rg.reSeedSecure() ;

        // Initialise to get the correct current time.
        super.initialiseState(initialTime) ;

        // Schedule the first SwitchOn event.
        Duration d1 = new Duration(
                this.initialDelay,
                this.getSimulatedTimeUnit()) ;
        Duration d2 =
                new Duration(
                        2.0 * this.meanTimeBetweenUsages *
                        this.rg.nextBeta(1.75, 1.75),
                        this.getSimulatedTimeUnit()) ;
        Time t = this.getCurrentStateTime().add(d1).add(d2) ;
        this.scheduleEvent(new FridgeOn(t)) ;

        // Redo the initialisation to take into account the initial event
        // just scheduled.
        this.nextTimeAdvance = this.timeAdvance() ;
        this.timeOfNextEvent = this.getCurrentStateTime().add(this.nextTimeAdvance) ;
    }

    @Override
    public Duration timeAdvance()
    {
        Duration d = super.timeAdvance() ;
        this.logMessage("FridgeUserModel::timeAdvance() 1 " + d +
                " " + this.eventListAsString()) ;
        return d ;
    }

    @Override
    public Vector<EventI> output()
    {
        assert	!this.eventList.isEmpty() ;
        Vector<EventI> ret = super.output() ;
        assert	ret.size() == 1 ;

        this.nextEvent = ret.get(0).getClass() ;

        this.logMessage("FridgeUserModel::output() " +
                this.nextEvent.getCanonicalName()) ;
        return ret ;
    }


    @Override
    public void	userDefinedInternalTransition(Duration elapsedTime){
        Duration d ;
        if (this.nextEvent.equals(FridgeOn.class)) {
            d = new Duration(50 * this.rg.nextBeta(1.75, 1.75),
                    this.getSimulatedTimeUnit()) ;
            Time t = this.getCurrentStateTime().add(d) ;
            this.scheduleEvent(new FreezerOn(t)) ;

            d = new Duration(this.interdayDelay, this.getSimulatedTimeUnit()) ;
            this.scheduleEvent(new FridgeOff(this.getCurrentStateTime().add(d))) ;
        } else if (this.nextEvent.equals(FridgeOff.class)) {
            d =	new Duration(
                    50 * this.meanTimeTempAction* this.rg.nextBeta(1.75, 1.75),
                    this.getSimulatedTimeUnit()) ;
            this.scheduleEvent(new FreezerOff(this.getCurrentStateTime().add(d))) ;

            d = new Duration(this.interdayDelay, this.getSimulatedTimeUnit()) ;
            this.scheduleEvent(new FridgeOn(this.getCurrentStateTime().add(d))) ;
        }
    }
}
