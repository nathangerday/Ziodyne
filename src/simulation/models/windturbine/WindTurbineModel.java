package simulation.models.windturbine;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentStateAccessI;
import fr.sorbonne_u.devs_simulation.examples.molene.SimulationMain;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOAwithEquations;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.AbstractSimulationReport;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import fr.sorbonne_u.utils.PlotterDescription;
import fr.sorbonne_u.utils.XYPlotter;
import simulation.events.windturbine.WindReading;

@ModelExternalEvents(imported = {WindReading.class})
public class WindTurbineModel extends AtomicHIOAwithEquations {

    private static final long serialVersionUID = 1L;

    public enum State{ON,OFF}

    public static class WindTurbineReport extends AbstractSimulationReport{
        private static final long serialVersionUID = 1L;

        public WindTurbineReport(String modelURI) {
            super(modelURI);
        }

        @Override
        public String toString(){
            return "WindReport("+ this.getModelURI()+")";
        }
    }

    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------

    public static final String  URI = "WindTurbineModel" ;
    private static final String SERIES = "power" ;

    private static final double RHO = 1.23;
    private static final double R = 2;
    private static final double COEFF = 0.5 * RHO * R * R * Math.PI;
    /** Puissance en Watt
     * P = 0.5 * RHO * S * v**3 
     * RHO : masse volumique de l'air (kg/m^3)
     * S : surface balayee par les pales (m^2)(rayon**2 * pi)
     * v : vitesse du vent (m/s) */
    @ExportedVariable(type = Double.class)
    protected final Value<Double> currentPower = new Value<Double>(this, 0.0, 0) ;
    /** current state (OFF, ON) of the wind turbine                 */
    protected State currentState ;
    /** plotter for the power level over time.                          */
    protected XYPlotter powerPlotter ;
    /** reference on the object representing the component that holds the
     *  model; enables the model to access the state of this component.     */
    protected EmbeddingComponentStateAccessI componentRef ;

    public WindTurbineModel(
            String uri,
            TimeUnit simulatedTimeUnit,
            SimulatorI simulationEngine) throws Exception {
        super(uri, simulatedTimeUnit, simulationEngine);

        PlotterDescription pd =
                new PlotterDescription(
                        "Wind Turnbine Model",
                        "Time (sec)",
                        "Power (watt)",
                        SimulationMain.ORIGIN_X,
                        SimulationMain.ORIGIN_Y + 3*SimulationMain.getPlotterHeight(),
                        SimulationMain.getPlotterWidth(),
                        SimulationMain.getPlotterHeight());

        this.powerPlotter = new XYPlotter(pd) ;
        this.powerPlotter.createSeries(SERIES) ;

        this.setLogger(new StandardLogger()) ;
    }

    @Override
    public void setSimulationRunParameters(Map<String, Object> simParams) throws Exception{
        this.componentRef =
                (EmbeddingComponentStateAccessI) simParams.get("componentRef") ;
    }

    @Override
    public void initialiseState(Time initialTime){
        // the lamp starts in mode ON
        this.currentState = WindTurbineModel.State.ON ;

        // initialisation of the power plotter
        this.powerPlotter.initialise() ;
        // show the plotter on the screen
        this.powerPlotter.showPlotter() ;

        try {
            // set the debug level triggering the production of log messages.
            this.setDebugLevel(1) ;
        } catch (Exception e) {
            throw new RuntimeException(e) ;
        }

        super.initialiseState(initialTime) ;
    }


    @Override
    protected void initialiseVariables(Time startTime){
        //        // as the lamp starts in mode OFF, its power consumption is 0
        //        this.currentPower.v = 0.0 ;
        //
        //        // first data in the plotter to start the plot.
        //        this.powerPlotter.addData(
        //                SERIES,
        //                this.getCurrentStateTime().getSimulatedTime(),
        //                this.getIntensity());

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
    public void userDefinedInternalTransition(Duration elapsedTime){
        if (this.componentRef != null) {
            try {
                this.logMessage("component state = " +
                        componentRef.getEmbeddingComponentStateValue("state")) ;
            } catch (Exception e) {
                throw new RuntimeException(e) ;
            }
        }
    }


    @Override
    public void userDefinedExternalTransition(Duration elapsedTime) {
        if (this.hasDebugLevel(2)) {
            this.logMessage("WindTurbine::userDefinedExternalTransition 1");
        }
        Vector<EventI> currentEvents = this.getStoredEventAndReset();

        EventI e;
        for(int i=0;i<currentEvents.size();i++) {
            e = currentEvents.get(i);
            if (this.hasDebugLevel(2)) {
                this.logMessage("WindModel::userDefinedExternalTransition 2 "
                        + e.getClass().getCanonicalName());
            }
            if (e instanceof WindReading) {
                double speed = ((WindReading.Reading) e.getEventInformation()).value;
                currentPower.v = COEFF * Math.pow(speed, 3);
                this.powerPlotter.addData(
                        SERIES,
                        e.getTimeOfOccurrence().getSimulatedTime(),
                        this.getPower()
                        );
                this.logMessage("speed = " + speed + ", power = " + this.getPower());
            }
        }
        super.userDefinedExternalTransition(elapsedTime) ;
    }

    @Override
    public void endSimulation(Time endTime) throws Exception {
        this.powerPlotter.addData(
                SERIES,
                endTime.getSimulatedTime(),
                this.getPower());
        Thread.sleep(10000L);
        this.powerPlotter.dispose();

        super.endSimulation(endTime);
    }

    @Override
    public SimulationReportI getFinalReport() throws Exception
    {
        return new WindTurbineReport(this.getURI()) ;
    }


    // ------------------------------------------------------------------------
    // Model-specific methods
    // ------------------------------------------------------------------------

    public void         setState(State s)
    {
        //        this.currentState = s ;
        //        switch (s)
        //        {
        //        case OFF :
        //            this.currentPower.v = 0.0 ;
        //            break ;
        //        case LOW :
        //            this.currentPower.v = LOW_MODE_CONSUMPTION/TENSION;
        //            break ;
        //        case MEDIUM :
        //            this.currentPower.v = MEDIUM_MODE_CONSUMPTION/TENSION;
        //            break;
        //        case HIGH :
        //            this.currentPower.v = HIGH_MODE_CONSUMPTION/TENSION;
        //            break;
        //        }
    }

    public State getState(){
        return this.currentState ;
    }

    public double getPower(){
        return this.currentPower.v ;
    }
}
