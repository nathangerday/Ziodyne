package simulation.models.dishwasher;

import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentStateAccessI;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOAwithEquations;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.AbstractSimulationReport;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import fr.sorbonne_u.utils.PlotterDescription;
import fr.sorbonne_u.utils.XYPlotter;
import simulation.events.dishwasher.AbstractDishwasherEvent;
import simulation.events.dishwasher.SetModeEco;
import simulation.events.dishwasher.SetModeStandard;
import simulation.events.dishwasher.SwitchOn;
import simulation.events.dishwasher.SwitchOff;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;


// -----------------------------------------------------------------------------
@ModelExternalEvents(imported = {SetModeEco.class,
        SetModeStandard.class,
        SwitchOn.class,
        SwitchOff.class})
// -----------------------------------------------------------------------------
public class DishwasherModel extends AtomicHIOAwithEquations {


    public enum State{OFF,ECO, STD}

    public static class DishwasherReport extends AbstractSimulationReport{

        public DishwasherReport(String modelURI) {
            super(modelURI);
        }

        @Override
        public String toString(){
            return "DishwasherReport("+ this.getModelURI()+")";
        }
    }


    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------

    public static final String	URI = "DishwasherModel" ;
    private static final String	SERIES = "intensity" ;

    protected static final double	ECO_MODE_CONSUMPTION = 20.0 ; // Watts
    protected static final double	STANDARD_MODE_CONSUMPTION = 40.0 ; // Watts
    protected static final double	TENSION = 12.0 ; // Volts

    @ExportedVariable(type = Double.class)
    protected final Value<Double> currentIntensity =
    new Value<Double>(this, 0.0, 0) ;
    protected State					currentState ;
    protected XYPlotter intensityPlotter ;
    protected EmbeddingComponentStateAccessI componentRef ;

    public DishwasherModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
        super(uri, simulatedTimeUnit, simulationEngine);

        PlotterDescription pd =
                new PlotterDescription(
                        "Dishwasher intensity",
                        "Time (sec)",
                        "Intensity (Amp)",
                        100,
                        0,
                        600,
                        400) ;

        this.intensityPlotter = new XYPlotter(pd) ;
        this.intensityPlotter.createSeries(SERIES) ;

        // create a standard logger (logging on the terminal)
        this.setLogger(new StandardLogger()) ;
    }

    @Override
    public void	setSimulationRunParameters(Map<String, Object> simParams) throws Exception
    {

        // The reference to the embedding component
        this.componentRef =
                (EmbeddingComponentStateAccessI) simParams.get("componentRef") ;
    }

    @Override
    public void	initialiseState(Time initialTime)
    {
        this.currentState = DishwasherModel.State.OFF ;

        this.intensityPlotter.initialise() ;
        this.intensityPlotter.showPlotter() ;

        try {
            this.setDebugLevel(1) ;
        } catch (Exception e) {
            throw new RuntimeException(e) ;
        }

        super.initialiseState(initialTime) ;
    }


    @Override
    protected void		initialiseVariables(Time startTime)
    {
        this.currentIntensity.v = 0.0 ;

        this.intensityPlotter.addData(
                SERIES,
                this.getCurrentStateTime().getSimulatedTime(),
                this.getIntensity());

        super.initialiseVariables(startTime);
    }


    @Override
    public Vector<EventI> output() {
        return null;
    }

    @Override
    public Duration timeAdvance() {
        if (this.componentRef == null) {
            // the model has no internal event, however, its state will evolve
            // upon reception of external events.
            return Duration.INFINITY ;
        } else {
            // This is to test the embedding component access facility.
            return new Duration(10.0, TimeUnit.SECONDS) ;
        }
    }


    @Override
    public void	userDefinedInternalTransition(Duration elapsedTime)
    {
        if (this.componentRef != null) {
            // This is an example showing how to access the component state
            // from a simulation model; this must be done with care and here
            // we are not synchronising with other potential component threads
            // that may access the state of the component object at the same
            // time.
            try {
                this.logMessage("component state = " +
                        componentRef.getEmbeddingComponentStateValue("state")) ;
            } catch (Exception e) {
                throw new RuntimeException(e) ;
            }
        }
    }


    @Override
    public void	userDefinedExternalTransition(Duration elapsedTime) {
        if (this.hasDebugLevel(2)) {
            this.logMessage("DiishwasherModel::userDefinedExternalTransition 1");
        }
        // get the vector of current external events
        Vector<EventI> currentEvents = this.getStoredEventAndReset();
        // when this method is called, there is at least one external event,
        // and for the hair dryer model, there will be exactly one by
        // construction.
        assert currentEvents != null && currentEvents.size() == 1;

        Event ce = (Event) currentEvents.get(0);
        assert ce instanceof AbstractDishwasherEvent;
        if (this.hasDebugLevel(2)) {
            this.logMessage("DishwasherModel::userDefinedExternalTransition 2 "
                    + ce.getClass().getCanonicalName());
        }

        this.intensityPlotter.addData(
                SERIES,
                this.getCurrentStateTime().getSimulatedTime(),
                this.getIntensity());

        if (this.hasDebugLevel(2)) {
            this.logMessage("DishwasherModel::userDefinedExternalTransition 3 "
                    + this.getState());
        }

        // execute the current external event on this model, changing its state
        // and intensity level
        ce.executeOn(this) ;

        if (this.hasDebugLevel(1)) {
            this.logMessage("DishwasherModel::userDefinedExternalTransition 4 "
                    + this.getState()) ;
        }

        // add a new data on the plotter; this data will open a new piece
        this.intensityPlotter.addData(
                SERIES,
                this.getCurrentStateTime().getSimulatedTime(),
                this.getIntensity());

        super.userDefinedExternalTransition(elapsedTime) ;
        if (this.hasDebugLevel(2)) {
            this.logMessage("DishwasherModel::userDefinedExternalTransition 5") ;
        }
    }

    @Override
    public void	endSimulation(Time endTime) throws Exception {
        this.intensityPlotter.addData(
                SERIES,
                endTime.getSimulatedTime(),
                this.getIntensity());
        Thread.sleep(10000L);
        this.intensityPlotter.dispose();

        super.endSimulation(endTime);
    }

    @Override
    public SimulationReportI getFinalReport() throws Exception
    {
        return new DishwasherReport(this.getURI()) ;
    }


    // ------------------------------------------------------------------------
    // Model-specific methods
    // ------------------------------------------------------------------------

    /**
     * set the state of the lamp.
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     * pre	s != null
     * post	true			// no postcondition.
     * </pre>
     *
     * @param s		the new state.
     */
    public void			setState(State s)
    {
        this.currentState = s ;
        System.out.println("set state");
        switch (s)
        {
        case OFF :
            this.currentIntensity.v = 0.0 ;
            break ;
        case ECO :
            this.currentIntensity.v = ECO_MODE_CONSUMPTION/TENSION;
            break ;
        case STD :
            this.currentIntensity.v = STANDARD_MODE_CONSUMPTION/TENSION;
            break;
        }
    }

    /**
     * return the state of the lamp.
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     * pre	true			// no precondition.
     * post	ret != null
     * </pre>
     *
     * @return	the state of the lamp.
     */
    public State		getState()
    {
        return this.currentState ;
    }

    /**
     * return the current intensity of electricity consumption in amperes.
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     * pre	true			// no precondition.
     * post	{@code ret >= 0.0 and ret <= 1200.0/220.0}
     * </pre>
     *
     * @return	the current intensity of electricity consumption in amperes.
     */
    public double	getIntensity()
    {
        return this.currentIntensity.v ;
    }

}

