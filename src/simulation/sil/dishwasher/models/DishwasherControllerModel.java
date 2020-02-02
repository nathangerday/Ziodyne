package simulation.sil.dishwasher.models;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import simulation.sil.dishwasher.events.DishwasherOn;

/**
 * The class <code>DishwasherControllerModel</code> implements a simulation model
 * of a controller for the dishwasher
 *  
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 */
@ModelExternalEvents(exported = {DishwasherOn.class})
public class DishwasherControllerModel extends AtomicES_Model {

    private static final long serialVersionUID = 1L ;
    public static final String  URI = "SILDishwasherUserModel" ;

    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------

    /** initial delay before sending the first switch on event.             */
    protected double initialDelay;
    /** delay between uses of the lamp from one day to another.     */
    protected double interdayDelay;


    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public DishwasherControllerModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
        super(uri, simulatedTimeUnit, simulationEngine);
        this.setLogger(new StandardLogger()) ;
    }


    @Override
    public void	initialiseState(Time initialTime){
        this.initialDelay = 50.0 ;
        this.interdayDelay = 500.0 ;
        
        try {
            this.setDebugLevel(3);
        } catch (Exception e) {
            throw new RuntimeException(e) ;
        }
        
        super.initialiseState(initialTime) ;

        Duration d = new Duration(
                this.initialDelay,
                this.getSimulatedTimeUnit()) ;
        Time t = this.getCurrentStateTime().add(d);
        this.scheduleEvent(new DishwasherOn(t)) ;
       
        this.nextTimeAdvance = this.timeAdvance() ;
        this.timeOfNextEvent = this.getCurrentStateTime().add(this.nextTimeAdvance) ;

        
    }
    

    @Override
    public ArrayList<EventI> output(){
        assert	!this.eventList.isEmpty() ;
        ArrayList<EventI> ret = super.output() ;
        assert	ret.size() == 1 ;
        return ret ;
    }


    @Override
    public void userDefinedInternalTransition(Duration elapsedTime) {
        Duration d = new Duration(this.interdayDelay, this.getSimulatedTimeUnit()) ;
        this.scheduleEvent(new DishwasherOn(this.getCurrentStateTime().add(d))) ;
    }
}
