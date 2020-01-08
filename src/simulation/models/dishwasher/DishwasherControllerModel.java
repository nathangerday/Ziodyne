package simulation.models.dishwasher;

import fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import org.apache.commons.math3.random.RandomDataGenerator;

import simulation.events.dishwasher.SetModeEco;
import simulation.events.dishwasher.SetModeStandard;
import simulation.events.dishwasher.SwitchOff;
import simulation.events.dishwasher.SwitchOn;

import java.util.Random;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

@ModelExternalEvents(exported = {SetModeEco.class,
        SetModeStandard.class,
        SwitchOn.class,
        SwitchOff.class})
public class DishwasherControllerModel extends AtomicES_Model {


    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------

    private static final long serialVersionUID = 1L ;
    public static final String	URI = "DishwasherUserModel" ;

    protected double	initialDelay ;
    protected double	interdayDelay ;
    protected double	meanTimeBetweenUsages ;
    protected double	ECOMODEDURATION;
    protected double 	STANDARDMODEDURATION;
    protected Class<?>	nextEvent ;

    protected final RandomDataGenerator rg ;
    protected DishwasherModel.State hds ;
    public DishwasherControllerModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
        super(uri, simulatedTimeUnit, simulationEngine);

        this.rg = new RandomDataGenerator() ;

        this.setLogger(new StandardLogger()) ;
    }
    @Override
    public void			initialiseState(Time initialTime)
    {
        this.initialDelay = 10.0 ;
        this.interdayDelay = 100.0 ;
        this.meanTimeBetweenUsages = 10.0 ;
        this.ECOMODEDURATION = 60.0;
        this.STANDARDMODEDURATION = 30.0;
        this.hds = DishwasherModel.State.OFF ;

        this.rg.reSeedSecure() ;

        super.initialiseState(initialTime) ;

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

        this.nextTimeAdvance = this.timeAdvance() ;
        this.timeOfNextEvent =
                this.getCurrentStateTime().add(this.nextTimeAdvance) ;

        try {
        } catch (Exception e) {
            throw new RuntimeException(e) ;
        }
    }

    @Override
    public Duration			timeAdvance()
    {
        Duration d = super.timeAdvance() ;
        this.logMessage("DishwasherUserModel::timeAdvance() 1 " + d +
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

        this.logMessage("Dishwasher::output() " +
                this.nextEvent.getCanonicalName()) ;
        return ret ;
    }

    @Override
    public void				userDefinedInternalTransition(
            Duration elapsedTime
    )
    {

        Duration d ;
        // See what is the type of event to be executed
        if (this.nextEvent.equals(SwitchOn.class)) {
            d = new Duration(2.0 * this.rg.nextBeta(1.75, 1.75),
                    this.getSimulatedTimeUnit()) ;
            Time t = this.getCurrentStateTime().add(d) ;
            if(new Random().nextBoolean()) {
            	this.scheduleEvent(new SetModeStandard(t)) ;            	
            }else {
            	this.scheduleEvent(new SetModeEco(t)) ;
            }
            d = new Duration(this.interdayDelay, this.getSimulatedTimeUnit()) ;
            this.scheduleEvent(
                    new SwitchOn(this.getCurrentStateTime().add(d))) ;
        }else if (this.nextEvent.equals(SetModeStandard.class)) {
            d =	new Duration(
                    this.STANDARDMODEDURATION,
                    this.getSimulatedTimeUnit()) ;
            this.scheduleEvent(
                    new SwitchOff(this.getCurrentStateTime().add(d))) ;
        }else if (this.nextEvent.equals(SetModeEco.class)) {
            d =	new Duration(
                    this.ECOMODEDURATION,
                    this.getSimulatedTimeUnit()) ;
            this.scheduleEvent(
                    new SwitchOff(this.getCurrentStateTime().add(d))) ;
        }
    }
}
