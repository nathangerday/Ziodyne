package simulation.models.battery;

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
import simulation.events.battery.BatteryCharging;
import simulation.events.battery.BatteryLevel;
import simulation.events.battery.BatteryOff;
import simulation.events.battery.BatteryOn;
import simulation.events.battery.BatteryProducing;

@ModelExternalEvents(imported = {BatteryLevel.class,BatteryOff.class,BatteryOn.class,BatteryProducing.class,BatteryCharging.class})
public class BatteryModel extends AtomicHIOAwithEquations{

    private static final long serialVersionUID = 1L;

    public static final String  URI = "WindTurbineModel" ;
    private static final String SERIES = "power" ;

    //States of the wind turbine
    public enum State{ON,OFF,PRODUCING,CHARGING}

    private static final double COEFF = 2.0;
    private static final double BATTERY_CHARGE = 0.5;
    private static final double BATTERY_PRODUCE = -0.5;
    public static final String MAX_CAPACITY = "max-capacity";
    public static final String CURRENT_CAPACITY = "current-capacity";
    public static final String ENERGY_PRODUCED = "energy-produced";


    public static class BatteryReport extends AbstractSimulationReport{
        private static final long serialVersionUID = 1L;

        public BatteryReport(String modelURI) {
            super(modelURI);
        }

        @Override
        public String toString(){
            return "BatteryReport("+ this.getModelURI()+")";
        }
    }

    @ExportedVariable(type = Double.class)
    protected final Value<Double> currentPower = new Value<Double>(this, 0.0, 0);



    /** current state (OFF, ON) of the battery                 */
    protected State currentState ;
    /** plotter for the power level over time.                          */
    protected XYPlotter powerPlotter ;
    /** reference on the object representing the component that holds the
     *  model; enables the model to access the state of this component.     */
    protected EmbeddingComponentStateAccessI componentRef ;


    public BatteryModel(
            String uri,
            TimeUnit simulatedTimeUnit,
            SimulatorI simulationEngine) throws Exception {
        super(uri, simulatedTimeUnit, simulationEngine);

        PlotterDescription pd =
                new PlotterDescription(
                        "Battery Model",
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
        setState(BatteryModel.State.OFF); ;
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
                this.logMessage("BatteryModel::userDefinedExternalTransition "
                        + e.getClass().getCanonicalName());
            }

            if (e instanceof BatteryLevel) {
                if(getState() == State.ON) {
                    double level = ((BatteryLevel.Reading) e.getEventInformation()).value;
                    currentPower.v = level * COEFF;
                }
                else if(getState() == State.PRODUCING) {
                    currentPower.v -= BATTERY_PRODUCE ;
                }
                else if(getState() == State.CHARGING) {
                    currentPower.v += BATTERY_CHARGE ;
                }
                this.powerPlotter.addData(
                        SERIES,
                        this.getCurrentStateTime().getSimulatedTime(),
                        this.getPower()
                        );
            } else if (e instanceof BatteryOff) {
                setState(State.OFF);
            } else if (e instanceof BatteryOn) {
                setState(State.ON);
            }
            else if (e instanceof BatteryProducing) {
                setState(State.PRODUCING);
            }
            else if (e instanceof BatteryCharging) {
                setState(State.CHARGING);
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
        return new BatteryReport(this.getURI()) ;
    }


    public State getState() {
        return this.getState();
    }

    public void setState(State s) {
        this.currentState = s;
        if(s == State.OFF) {
            this.currentPower.v = 0.0;
        }
    }

    public double getPower() {
        return this.currentPower.v;
    }

}
