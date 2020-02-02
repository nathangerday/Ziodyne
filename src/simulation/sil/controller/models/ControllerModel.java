package simulation.sil.controller.models;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import components.Controller;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;

/**
 * The class <code>ControllerModel</code> implements a simulation model
 * of a controller
 *  
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 */
public class ControllerModel extends AtomicModel{
    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------

    private static final long serialVersionUID = 1L;

    public static final String	URI = "SILController";
    public static final String COMPONENT_REF = URI + ":componentRef";
    protected Controller componentRef ;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------
    /**
     * Creates a ControllerModel
     * @param uri uri of the model
     * @param simulatedTimeUnit timeunit of the simulation
     * @param simulationEngine engine for the simulation
     * @throws Exception
     */
    public	ControllerModel(
            String uri,
            TimeUnit simulatedTimeUnit,
            SimulatorI simulationEngine
            ) throws Exception{
        super(uri, simulatedTimeUnit, simulationEngine);
    }

    // -------------------------------------------------------------------------
    // Methods
    // -------------------------------------------------------------------------

    @Override
    public void	setSimulationRunParameters(Map<String, Object> simParams) throws Exception{
        // The reference to the embedding component
        if(simParams.containsKey(COMPONENT_REF)) {
            this.componentRef = (Controller) simParams.get(COMPONENT_REF);
        }
    }

    public Duration	timeAdvance(){
        assert	this.componentRef != null ;
        double d = this.componentRef.getControlPeriod() ;
        return new Duration(d, this.getSimulatedTimeUnit()) ;
    }

    @Override
    public ArrayList<EventI> output(){
        return null;
    }

    @Override
    public void	userDefinedInternalTransition(Duration elapsedTime){
        super.userDefinedInternalTransition(elapsedTime) ;
        try {
            this.componentRef.controlTask(this.getCurrentStateTime().getSimulatedTime()) ;
        } catch (Exception e) {
            throw new RuntimeException(e) ;
        }
    }
}
