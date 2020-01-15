package simulation.models.battery;

import java.util.Random;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import simulation.events.battery.BatteryCharging;
import simulation.events.battery.BatteryProducing;
import simulation.events.battery.BatteryStandby;

@ModelExternalEvents(exported = {BatteryCharging.class,BatteryStandby.class,BatteryProducing.class})
public class BatteryControllerModel extends AtomicES_Model{

    private static final long serialVersionUID = 1L;

    public static final String  URI = "BatteryControllerModel" ;

    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------

    private double delay = 50;
    protected Class<?>  nextEvent ;

    public BatteryControllerModel(
            String uri,
            TimeUnit simulatedTimeUnit,
            SimulatorI simulationEngine) throws Exception {
        super(uri, simulatedTimeUnit, simulationEngine);

        this.setLogger(new StandardLogger()) ;
    }
    @Override
    public void initialiseState(Time initialTime){
        super.initialiseState(initialTime) ;

        Duration d = new Duration(10,this.getSimulatedTimeUnit()) ;
        Time t = this.getCurrentStateTime().add(d);
        this.scheduleEvent(new BatteryCharging(t)) ;

        this.nextTimeAdvance = this.timeAdvance() ;
        this.timeOfNextEvent =this.getCurrentStateTime().add(this.nextTimeAdvance) ;

        try {
        } catch (Exception e) {
            throw new RuntimeException(e) ;
        }
    }

    @Override
    public Duration timeAdvance(){
        Duration d = super.timeAdvance() ;
        return d ;
    }

    @Override
    public Vector<EventI> output(){
        assert  !this.eventList.isEmpty() ;
        Vector<EventI> ret = super.output() ;
        assert  ret.size() == 1 ;

        this.nextEvent = ret.get(0).getClass() ;
        return ret ;
    }

    @Override
    public void userDefinedInternalTransition(Duration elapsedTime){

        Duration d ;
        // See what is the type of event to be executed
        if (this.nextEvent.equals(BatteryCharging.class)) {
            d = new Duration(delay,this.getSimulatedTimeUnit()) ;
            Time t = this.getCurrentStateTime().add(d) ;
            if(new Random().nextBoolean()) {
                this.scheduleEvent(new BatteryStandby(t)) ;
            }else {
                this.scheduleEvent(new BatteryProducing(t)) ;
            }
        }else if (this.nextEvent.equals(BatteryStandby.class)) {
            d = new Duration(delay,this.getSimulatedTimeUnit()) ;
            Time t = this.getCurrentStateTime().add(d) ;
            if(new Random().nextBoolean()) {
                this.scheduleEvent(new BatteryCharging(t)) ;               
            }else {
                this.scheduleEvent(new BatteryProducing(t)) ;
            }
        }else if (this.nextEvent.equals(BatteryProducing.class)) {
            d = new Duration(delay,this.getSimulatedTimeUnit()) ;
            Time t = this.getCurrentStateTime().add(d) ;
            if(new Random().nextBoolean()) {
                this.scheduleEvent(new BatteryStandby(t)) ;               
            }else {
                this.scheduleEvent(new BatteryCharging(t)) ;
            }
        }
    }
}
