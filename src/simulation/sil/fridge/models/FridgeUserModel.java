package simulation.sil.fridge.models;

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
import simulation.sil.fridge.events.FreezerClose;
import simulation.sil.fridge.events.FreezerOpen;
import simulation.sil.fridge.events.FridgeClose;
import simulation.sil.fridge.events.FridgeOpen;


@ModelExternalEvents(exported = {
        FridgeOpen.class,
        FridgeClose.class,
        FreezerOpen.class,
        FreezerClose.class})
public class FridgeUserModel extends AtomicES_Model {

    private static final long serialVersionUID = 1L;

    public static final String	URI = "FridgeUserModel" ;

    /** initial delay before sending the first switch on event.				*/
    protected double	initialDelay ;
    /** delay between uses of the fridge from one day to another.		*/
    protected double	interdayDelay ;
    /** mean time between the closing and the opening of the fridge door */
    protected double	meanTimeFridgeOpen ;
    /** mean time between the opening and the closing of the fridge door */
    protected double    meanTimeFridgeClose ;
    /** mean time between the closing and the opening of the freezer door */
    protected double    meanTimeFreezerOpen ;
    /** mean time between the opening and the closing of the freezer door */
    protected double    meanTimeFreezerClose ;
    /** next event.												*/
    protected Class<?>	nextEvent ;
    /**	a random number generator from common math library.					*/
    protected final RandomDataGenerator rg ;

    public FridgeUserModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
        super(uri, simulatedTimeUnit, simulationEngine);
        this.rg = new RandomDataGenerator() ;
        this.setLogger(new StandardLogger()) ;
    }

    @Override
    public void	initialiseState(Time initialTime){
        this.initialDelay = 1.0 ;
        this.meanTimeFridgeOpen = 30.0 ;
        this.meanTimeFridgeClose = 15.0 ;
        this.meanTimeFreezerOpen = 40.0 ;
        this.meanTimeFreezerClose = 10.0 ;
        this.rg.reSeedSecure() ;

        super.initialiseState(initialTime) ;

        //Initialize first FridgeOpen
        Duration d1 = new Duration(
                this.initialDelay,
                this.getSimulatedTimeUnit()) ;
        Duration d2 =
                new Duration(
                        2.0 * this.meanTimeFridgeOpen *
                        this.rg.nextBeta(1.75, 1.75),
                        this.getSimulatedTimeUnit()) ;
        Time t = this.getCurrentStateTime().add(d1).add(d2) ;
        this.scheduleEvent(new FreezerOpen(t));
        

        //Initialize first FreezerOpen
        d1 = new Duration(
                this.initialDelay,
                this.getSimulatedTimeUnit()) ;
        d2 =
                new Duration(
                        2.0 * this.meanTimeFreezerOpen *
                        this.rg.nextBeta(1.75, 1.75),
                        this.getSimulatedTimeUnit()) ;
        t = this.getCurrentStateTime().add(d1).add(d2) ;
        this.scheduleEvent(new FridgeOpen(t));

        try {
            this.setDebugLevel(1) ;
        } catch (Exception e) {
            throw new RuntimeException(e) ;
        }
        // Redo the initialisation to take into account the initial event
        // just scheduled.
        this.nextTimeAdvance = this.timeAdvance() ;
        this.timeOfNextEvent = this.getCurrentStateTime().add(this.nextTimeAdvance) ;

        
    }
    
    @Override
    public Duration timeAdvance() {
        return super.timeAdvance();
    }


    @Override
    public ArrayList<EventI> output(){
        assert	!this.eventList.isEmpty() ;
        ArrayList<EventI> ret = super.output() ;
        assert ret.size() == 1 ;
        this.nextEvent = ret.get(0).getClass();
        return ret ;
    }


    @Override
    public void	userDefinedInternalTransition(Duration elapsedTime){
        Duration d ;
        if (this.nextEvent.equals(FridgeOpen.class)) {
            d = new Duration(2 * this.meanTimeFridgeClose * this.rg.nextBeta(1.75, 1.75),
                    this.getSimulatedTimeUnit()) ;
            Time t = this.getCurrentStateTime().add(d) ;
            this.scheduleEvent(new FridgeClose(t)) ;
        } else if (this.nextEvent.equals(FridgeClose.class)) {
            d = new Duration(2 * this.meanTimeFridgeOpen * this.rg.nextBeta(1.75, 1.75),
                    this.getSimulatedTimeUnit()) ;
            Time t = this.getCurrentStateTime().add(d) ;
            this.scheduleEvent(new FridgeOpen(t)) ;
        } else if (this.nextEvent.equals(FreezerOpen.class)) {
            d = new Duration(2 * this.meanTimeFreezerClose * this.rg.nextBeta(1.75, 1.75),
                    this.getSimulatedTimeUnit()) ;
            Time t = this.getCurrentStateTime().add(d) ;
            this.scheduleEvent(new FreezerClose(t)) ;
        } else if (this.nextEvent.equals(FreezerClose.class)) {
            d = new Duration(2 * this.meanTimeFreezerOpen * this.rg.nextBeta(1.75, 1.75),
                    this.getSimulatedTimeUnit()) ;
            Time t = this.getCurrentStateTime().add(d) ;
            this.scheduleEvent(new FreezerOpen(t)) ;
        }
    }
}
