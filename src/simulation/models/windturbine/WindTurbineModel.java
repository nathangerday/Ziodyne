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
import simulation.events.windturbine.WindTurbineOff;
import simulation.events.windturbine.WindTurbineOn;
import simulation.events.windturbine.WindReading;

@ModelExternalEvents(imported = {WindReading.class,WindTurbineOn.class,WindTurbineOff.class})
public class WindTurbineModel extends AtomicHIOAwithEquations {

    private static final long serialVersionUID = 1L;

    //States of the wind turbine
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

    /** Puissance en Watt
     * P = 0.5 * RHO * S * v**3 
     * RHO : masse volumique de l'air (kg/m^3)
     * S : surface balayee par les pales (m^2)(rayon**2 * pi)
     * v : vitesse du vent (m/s) */
    @ExportedVariable(type = Double.class)
    protected final Value<Double> currentPower = new Value<Double>(this, 0.0, 0) ;

    private static final double RHO = 1.23;
    private static final double R = 2;
    private static final double COEFF = 0.5 * RHO * R * R * Math.PI;


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
                        SimulationMain.ORIGIN_Y + SimulationMain.getPlotterHeight(),
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
        this.currentState = WindTurbineModel.State.OFF ;

        this.powerPlotter.initialise() ;
        this.powerPlotter.showPlotter() ;

        try {
            this.setDebugLevel(1) ;
        } catch (Exception e) {
            throw new RuntimeException(e) ;
        }

        super.initialiseState(initialTime) ;
    }


    @Override
    public Vector<EventI> output() {
        return null;
    }

    @Override
    public Duration timeAdvance() {
        if (this.componentRef == null) {
            return Duration.INFINITY ;
        } else {
            return new Duration(10.0, TimeUnit.SECONDS) ;
        }
    }


    @Override
    public void userDefinedExternalTransition(Duration elapsedTime) {
        Vector<EventI> currentEvents = this.getStoredEventAndReset();
        EventI e;
        for(int i=0;i<currentEvents.size();i++) {
            e = currentEvents.get(i);
            if (this.hasDebugLevel(2)) {
                this.logMessage("WindTurbineModel::userDefinedExternalTransition "
                        + e.getClass().getCanonicalName());
            }

            if (e instanceof WindReading) {
                if(getState() == State.ON) {
                    double speed = ((WindReading.Reading) e.getEventInformation()).value;
                    currentPower.v = COEFF * Math.pow(speed, 3);
                }
                this.powerPlotter.addData(
                        SERIES,
                        this.getCurrentStateTime().getSimulatedTime(),
                        this.getPower()
                        );
            } else if (e instanceof WindTurbineOff) {
                setState(State.OFF);
            } else if (e instanceof WindTurbineOn) {
                setState(State.ON);
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

    public State getState(){
        return this.currentState ;
    }

    public void setState(State s) {
        this.currentState = s;
        if(s == State.OFF) {
            this.currentPower.v = 0.0;
        }
    }

    public double getPower(){
        return this.currentPower.v ;
    }
}
