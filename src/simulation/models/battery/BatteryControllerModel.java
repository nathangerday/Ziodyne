package simulation.models.battery;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import simulation.events.battery.BatteryCharging;
import simulation.events.battery.BatteryLevel;
import simulation.events.battery.BatteryOff;
import simulation.events.battery.BatteryOn;
import simulation.events.battery.BatteryProducing;
import simulation.models.battery.BatteryModel.State;

@ModelExternalEvents(imported = {BatteryLevel.class},
exported = {BatteryOn.class,BatteryOff.class,BatteryCharging.class,BatteryProducing.class})
public class BatteryControllerModel extends AtomicModel  {
	
    public static final String  URI = "BatteryControllerModel" ;
    
  //Parameters names
    public static final String MAX_CAPACITY = "max-capacity";
    public static final String CURRENT_CAPACITY = "current-capacity";
    public static final String ENERGY_PRODUCED = "energy-produced";
    
 // Model variables
    protected State state;
    protected boolean triggerAction;
    protected double maxCapacity;
    protected double currentCapacity;
    protected double energyProduced;


	public BatteryControllerModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine)
			throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);
        state = State.OFF;
        triggerAction = false;
        this.maxCapacity= 10;
        this.setLogger(new StandardLogger()) ;
	}
	

    @Override
    public void setSimulationRunParameters(Map<String, Object> simParams) throws Exception{
        super.setSimulationRunParameters(simParams) ;
        String varName = this.getURI() + ":" + BatteryControllerModel.MAX_CAPACITY;
        if (simParams.containsKey(varName)) {
            this.maxCapacity = (Double) simParams.get(varName) ;
        }
    }
    
    @Override
    public Vector<EventI> output(){
        if (this.triggerAction) {
            this.triggerAction = false ;

            Time currentTime = this.getCurrentStateTime().add(this.getNextTimeAdvance()) ;
            EventI e = null;
            if(state == State.OFF){
                e = new BatteryOn(currentTime);
                state = State.ON;
            }else if(state == State.ON && currentCapacity< maxCapacity) {
                e = new BatteryCharging(currentTime);
                state = State.CHARGING;
            }
            else if(state == State.ON && currentCapacity == maxCapacity) {
                e = new BatteryProducing(currentTime);
                state = State.PRODUCING;
                
            }else if (state == State.CHARGING && currentCapacity< maxCapacity) {
            	e = new BatteryCharging(currentTime);
                state = State.CHARGING;
            }
            else if (state == State.PRODUCING && currentCapacity>0) {
            	e = new BatteryProducing(currentTime);
                state = State.PRODUCING;
            }

            if (e != null) {
                Vector<EventI> ret = new Vector<EventI>(1);
                ret.add(e) ;
                this.logMessage(this.getCurrentStateTime() +
                        "|output|controller action = " + e.getClass().getCanonicalName());
                return ret;
            }else {
                return null;
            }
        } else {
            return null ;
        }
    }

    @Override
    public void userDefinedExternalTransition(Duration elapsedTime){
        super.userDefinedExternalTransition(elapsedTime);
        Vector<EventI> current = this.getStoredEventAndReset() ;
        for(EventI e : current) {
            currentCapacity = ((BatteryLevel.Reading) e.getEventInformation()).value;
            if(state == State.OFF){
                triggerAction = true;
            }
        }
    }

	  @Override
	    public Duration timeAdvance(){
	        if (this.triggerAction){
	            return Duration.zero(this.getSimulatedTimeUnit()) ;
	        } else {
	            return Duration.INFINITY ;
	        }
	    }



}
