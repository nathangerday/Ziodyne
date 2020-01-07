package simulation.models.windturbine;

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
import simulation.events.windturbine.SwitchOff;
import simulation.events.windturbine.SwitchOn;
import simulation.events.windturbine.WindReading;
import simulation.models.windturbine.WindTurbineModel.State;

@ModelExternalEvents(imported = {WindReading.class},
exported = {SwitchOn.class,SwitchOff.class})
public class WindTurbineControllerModel extends AtomicModel{

    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------

    private static final long   serialVersionUID = 1L ;
    public static final String  URI = "WindTurbineControllerModel" ;

    //Parameters names
    public static final String MAX_SPEED = "max-speed";
    public static final String MIN_SPEED = "min-speed";

    // Model variables
    protected State state;
    protected boolean triggerAction;
    protected double maxSpeed;
    protected double minSpeed;
    private double speed;

    public WindTurbineControllerModel(
            String uri,
            TimeUnit simulatedTimeUnit,
            SimulatorI simulationEngine) throws Exception {
        super(uri, simulatedTimeUnit, simulationEngine);

        //At the beginning, the wind turbine is off
        state = State.OFF;
        triggerAction = false;
        this.minSpeed = 4;
        this.maxSpeed = 10;
        this.setLogger(new StandardLogger()) ;
    }

    @Override
    public void setSimulationRunParameters(Map<String, Object> simParams) throws Exception{
        super.setSimulationRunParameters(simParams) ;
        String varName = this.getURI() + ":" + WindTurbineControllerModel.MAX_SPEED;
        if (simParams.containsKey(varName)) {
            this.maxSpeed = (Double) simParams.get(varName) ;
        }
        varName = this.getURI() + ":" + WindTurbineControllerModel.MIN_SPEED;
        if (simParams.containsKey(varName)) {
            this.minSpeed = (Double) simParams.get(varName) ;
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

    @Override
    public Vector<EventI> output(){
        if (this.triggerAction) {
            this.triggerAction = false ;

            Time currentTime = this.getCurrentStateTime().add(this.getNextTimeAdvance()) ;
            EventI e = null;
            if(state == State.OFF && speed >= minSpeed && speed <= maxSpeed){
                e = new SwitchOn(currentTime);
                state = State.ON;
            }else if(state == State.ON && (speed < minSpeed || speed > maxSpeed)) {
                e = new SwitchOff(currentTime);
                state = State.OFF;
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
            speed = ((WindReading.Reading) e.getEventInformation()).value;
            if((state == State.OFF && (speed >= minSpeed && speed <= maxSpeed)) ||
                    (state == State.ON && (speed < minSpeed || speed > maxSpeed))){
                triggerAction = true;
            }
        }
    }
}
