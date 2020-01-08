package simulation.models.lamp;

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
import simulation.events.lamp.*;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;


// -----------------------------------------------------------------------------
@ModelExternalEvents(imported = {SwitchOn.class,
        SwitchOff.class,
        SetLow.class,
        SetMedium.class,
        SetHigh.class})
// -----------------------------------------------------------------------------
public class LampModel 	extends AtomicHIOAwithEquations {


    public enum State{ ON,OFF,LOW, MEDIUM,HIGH}

    public static class LampReport extends AbstractSimulationReport{

        /**
         * create a simulation report.
         *
         * <p><strong>Contract</strong></p>
         *
         * <pre>
         * pre	modelURI != null
         * post	this.getModelURI() != null
         * post	this.getModelURI().equals(modelURI)
         * </pre>
         *
         * @param modelURI URI of the model which report is defined.
         */
        public LampReport(String modelURI) {
            super(modelURI);
        }

        @Override
        public String toString(){
            return "LampReport("+ this.getModelURI()+")";
        }
    }


    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------

    public static final String	URI = "LampModel" ;
    private static final String	SERIES = "intensity" ;

    /** energy consumption (in Watts) of the lamp in LOW mode.		*/
    protected static final double	LOW_MODE_CONSUMPTION = 20.0 ; // Watts
    /** energy consumption (in Watts) of the lamp in MEDIUM mode.		*/
    protected static final double	MEDIUM_MODE_CONSUMPTION = 40.0 ; // Watts
    /** energy consumption (in Watts) of the lamp in HIGH mode.		*/
    protected static final double	HIGH_MODE_CONSUMPTION = 60.0 ; // Watts
    /** nominal tension (in Volts) of the lamp.						*/
    protected static final double	TENSION = 12.0 ; // Volts


    /** current intensity in Amperes; intensity is power/tension.			*/
    @ExportedVariable(type = Double.class)
    protected final Value<Double> currentIntensity =
    new Value<Double>(this, 0.0, 0) ;
    /** current state (OFF, LOW, HIGH) of the lamp.					*/
    protected State					currentState ;
    /** plotter for the intensity level over time.							*/
    protected XYPlotter intensityPlotter ;
    /** reference on the object representing the component that holds the
     *  model; enables the model to access the state of this component.		*/
    protected EmbeddingComponentStateAccessI componentRef ;


    /**
     * create an atomic hybrid input/output model based on an algebraic
     * equations solver with the given URI (if null, one will be generated)
     * and to be run by the given simulator (or by the one of an ancestor
     * coupled model if null) using the given time unit for its clock.
     *
     * <p><strong>Contract</strong></p>
     *
     * <pre>
     * pre	simulatedTimeUnit != null
     * pre	simulationEngine == null ||
     * 		    	simulationEngine instanceof HIOA_AtomicEngine
     * post	this.getURI() != null
     * post	uri != null implies this.getURI().equals(uri)
     * post	this.getSimulatedTimeUnit().equals(simulatedTimeUnit)
     * post	simulationEngine != null implies
     * 			this.getSimulationEngine().equals(simulationEngine)
     * post	!isDebugModeOn()
     * </pre>
     *
     * @param uri               unique identifier of the model.
     * @param simulatedTimeUnit time unit used for the simulation clock.
     * @param simulationEngine  simulation engine enacting the model.
     * @throws Exception <i>TODO</i>.
     */
    public LampModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
        super(uri, simulatedTimeUnit, simulationEngine);

        PlotterDescription pd =
                new PlotterDescription(
                        "Lamp intensity",
                        "Time (sec)",
                        "Intensity (Amp)",
                        100,
                        500,
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
        // the lamp starts in mode OFF
        this.currentState = LampModel.State.OFF ;

        // initialisation of the intensity plotter
        this.intensityPlotter.initialise() ;
        // show the plotter on the screen
        this.intensityPlotter.showPlotter() ;

        try {
            // set the debug level triggering the production of log messages.
            this.setDebugLevel(1) ;
        } catch (Exception e) {
            throw new RuntimeException(e) ;
        }

        super.initialiseState(initialTime) ;
    }


    @Override
    protected void		initialiseVariables(Time startTime)
    {
        // as the lamp starts in mode OFF, its power consumption is 0
        this.currentIntensity.v = 0.0 ;

        // first data in the plotter to start the plot.
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
            this.logMessage("LampModel::userDefinedExternalTransition 1");
        }
        // get the vector of current external events
        Vector<EventI> currentEvents = this.getStoredEventAndReset();
        // when this method is called, there is at least one external event,
        // and for the lamp model, there will be exactly one by
        // construction.
        assert currentEvents != null && currentEvents.size() == 1;

        Event ce = (Event) currentEvents.get(0);
        assert ce instanceof AbstractLampEvent;
        if (this.hasDebugLevel(2)) {
            this.logMessage("LampModel::userDefinedExternalTransition 2 "
                    + ce.getClass().getCanonicalName());
        }

        this.intensityPlotter.addData(
                SERIES,
                this.getCurrentStateTime().getSimulatedTime(),
                this.getIntensity());

        if (this.hasDebugLevel(2)) {
            this.logMessage("LampModel::userDefinedExternalTransition 3 "
                    + this.getState());
        }

        // execute the current external event on this model, changing its state
        // and intensity level
        ce.executeOn(this);

        if (this.hasDebugLevel(1)) {
            this.logMessage("LampModel::userDefinedExternalTransition 4 "
                    + this.getState()) ;
        }

        // add a new data on the plotter; this data will open a new piece
        this.intensityPlotter.addData(
                SERIES,
                this.getCurrentStateTime().getSimulatedTime(),
                this.getIntensity());

        super.userDefinedExternalTransition(elapsedTime) ;
        if (this.hasDebugLevel(2)) {
            this.logMessage("LampModel::userDefinedExternalTransition 5") ;
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
        return new LampReport(this.getURI()) ;
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
        switch (s)
        {
        case OFF :
            this.currentIntensity.v = 0.0 ;
            break ;
        case LOW :
            this.currentIntensity.v = LOW_MODE_CONSUMPTION/TENSION;
            break ;
        case MEDIUM :
            this.currentIntensity.v = MEDIUM_MODE_CONSUMPTION/TENSION;
            break;
        case HIGH :
            this.currentIntensity.v = HIGH_MODE_CONSUMPTION/TENSION;
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
