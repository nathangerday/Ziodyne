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
import simulation.events.fridge.LowerFreezerTemp;
import simulation.events.fridge.LowerFridgeTemp;
import simulation.events.fridge.RaiseFreezerTemp;
import simulation.events.fridge.RaiseFridgeTemp;
import simulation.events.fridge.SwitchOff;
import simulation.events.fridge.SwitchOn;

@ModelExternalEvents(exported = {SwitchOn.class,
        SwitchOff.class,
        RaiseFreezerTemp.class,
        RaiseFridgeTemp.class,
        LowerFridgeTemp.class,
        LowerFreezerTemp.class})
public class FridgeUserModel extends AtomicES_Model {
	
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
	    protected FridgeModel.State fs ;

	public FridgeUserModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
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
        this.fs = FridgeModel.State.OFF ;

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
        this.scheduleEvent(new SwitchOn(t)) ;

        // Redo the initialisation to take into account the initial event
        // just scheduled.
        this.nextTimeAdvance = this.timeAdvance() ;
        this.timeOfNextEvent =
                this.getCurrentStateTime().add(this.nextTimeAdvance) ;

        try {
            // set the debug level triggering the production of log messages.
            //this.setDebugLevel(1) ;
        } catch (Exception e) {
            throw new RuntimeException(e) ;
        }
    }
	
	@Override
	 public Duration timeAdvance()
	    {
	        // This is just for debugging purposes; the time advance for an ES
	        // model is given by the earliest time among the currently scheduled
	        // events.
	        Duration d = super.timeAdvance() ;
	        this.logMessage("FridgeUserModel::timeAdvance() 1 " + d +
	                " " + this.eventListAsString()) ;
	        return d ;
	    }
	
	 @Override
	    public Vector<EventI> output()
	    {
	        // output is called just before executing an internal transition
	        // in ES models, this corresponds to having at least one event in
	        // the event list which time of occurrence corresponds to the current
	        // simulation time when performing the internal transition.

	        // when called, there must be an event to be executed and it will
	        // be sent to other models when they are external events.
	        assert	!this.eventList.isEmpty() ;
	        // produce the set of such events by calling the super method
	        Vector<EventI> ret = super.output() ;
	        // by construction, there will be only one such event
	        assert	ret.size() == 1 ;

	        // remember which external event was sent (in ES model, events are
	        // either internal or external, hence an external event is removed
	        // from the event list to be sent and it will not be accessible to
	        // the internal transition method; hence, we store the information
	        // to keep it for the internal transition)
	        this.nextEvent = ret.get(0).getClass() ;

	        this.logMessage("FridgeUserModel::output() " +
	                this.nextEvent.getCanonicalName()) ;
	        return ret ;
	    }
	 
	 
	  @Override
	    public void				userDefinedInternalTransition(
	            Duration elapsedTime
	    )
	    {
	        // This method implements a usage scenario for the lamp.
	        // Here, we assume that the lamp is used once each cycle (day)
	        // and then it starts in low mode, is set in high mode shortly after,
	        // used for a while in high mode and then set back in low mode.

	        Duration d ;
	        // See what is the type of event to be executed
	        if (this.nextEvent.equals(SwitchOn.class)) {
	            // when a switch on event has been issued, plan the next event as
	            // a set high (the lamp is switched on in low mode
	            d = new Duration(2.0 * this.rg.nextBeta(1.75, 1.75),
	                    this.getSimulatedTimeUnit()) ;
	            // compute the time of occurrence (in the future)
	            Time t = this.getCurrentStateTime().add(d) ;
	            // schedule the event
	            this.scheduleEvent(new RaiseFreezerTemp(t)) ;
	            // also, plan the next switch on for the next day
	            d = new Duration(this.interdayDelay, this.getSimulatedTimeUnit()) ;
	            this.scheduleEvent(
	                    new SwitchOn(this.getCurrentStateTime().add(d))) ;
	        } else if (this.nextEvent.equals(RaiseFreezerTemp.class)) {
	            // when a set high event has been issued, plan the next set low
	            // after some time of usage
	            d =	new Duration(
	                    2.0 * this.meanTimeTempAction * this.rg.nextBeta(1.75, 1.75),
	                    this.getSimulatedTimeUnit()) ;
	            this.scheduleEvent(new RaiseFridgeTemp(this.getCurrentStateTime().add(d))) ;
	        } else if (this.nextEvent.equals(RaiseFridgeTemp.class)) {
	            // when a set high event has been issued, plan the next switch off
	            // after some time of usage
	            d =	new Duration(
	                    2.0 * this.meanTimeTempAction* this.rg.nextBeta(1.75, 1.75),
	                    this.getSimulatedTimeUnit()) ;
	            this.scheduleEvent(
	                    new LowerFreezerTemp(this.getCurrentStateTime().add(d))) ;
	        } else if (this.nextEvent.equals(LowerFreezerTemp.class)) {
	        	d =	new Duration(
	                    2.0 * this.meanTimeTempAction * this.rg.nextBeta(1.75, 1.75),
	                    this.getSimulatedTimeUnit()) ;
	            this.scheduleEvent(
	                    new LowerFridgeTemp(this.getCurrentStateTime().add(d))) ;
	        }
	        else if (this.nextEvent.equals(LowerFridgeTemp.class)) {
	        	d =	new Duration(
	                    2.0 * this.meanTimeTempAction * this.rg.nextBeta(1.75, 1.75),
	                    this.getSimulatedTimeUnit()) ;
	            this.scheduleEvent(
	                    new SwitchOff(this.getCurrentStateTime().add(d))) ;
	        }
	    }

}
