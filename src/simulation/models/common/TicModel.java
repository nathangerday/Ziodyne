package simulation.models.common;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import simulation.events.common.TicEvent;

@ModelExternalEvents(exported = {TicEvent.class})
public class TicModel extends AtomicModel{
    private static final long	serialVersionUID = 1L;
    /** name of the run parameter defining the delay between tic events.	*/
    public static final String	DELAY_PARAMETER_NAME = "delay";
    /** the standard delay between tic events.								*/
    public static Duration STANDARD_DURATION = new Duration(60.0, TimeUnit.SECONDS) ;
    /** the URI to be used when creating the instance of the model.			*/
    public static final String URI_WINDTURBINE = "TicModelWindTurbine";
    public static final String URI_FRIDGE = "TicModelFridge";

    /** the value of the delay between tic events during the current
     *  simulation run.														*/
    protected Duration delay ;

    public TicModel(
            String uri,
            TimeUnit simulatedTimeUnit,
            SimulatorI simulationEngine
            ) throws Exception{
        super(uri, simulatedTimeUnit, simulationEngine);
        this.delay = TicModel.STANDARD_DURATION;
        this.setLogger(new StandardLogger());
        this.toggleDebugMode();
    }

    @Override
    public void	setSimulationRunParameters(Map<String, Object> simParams) throws Exception{
        super.setSimulationRunParameters(simParams);
        String varName = this.getURI() + ":" + TicModel.DELAY_PARAMETER_NAME;
        if (simParams.containsKey(varName)){
            this.delay = (Duration) simParams.get(varName) ;
        }
    }

    @Override
    public Vector<EventI> output(){
        Vector<EventI> ret = new Vector<EventI>() ;
        Time t = this.getCurrentStateTime().add(this.getNextTimeAdvance()) ;
        TicEvent e = new TicEvent(t) ;
        this.logMessage("output " + e.eventAsString()) ;
        ret.add(e);
        return ret;
    }

    @Override
    public void	userDefinedInternalTransition(Duration elapsedTime){
        this.logMessage("at internal transition " +
                this.getCurrentStateTime().getSimulatedTime() +
                " " + elapsedTime.getSimulatedDuration()) ;
    }

    @Override
    public Duration	timeAdvance(){
        return this.delay;
    }

    @Override
    public SimulationReportI getFinalReport() throws Exception{
        final String uri = this.getURI() ;
        return new SimulationReportI() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getModelURI() { return uri ; }

            @Override
            public String toString() {
                return "TicModelReport()|" + this.getModelURI();
            }
        };
    }
}
