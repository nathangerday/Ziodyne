package simulation.models.battery;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentAccessI;
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
import simulation.events.battery.BatteryProducing;
import simulation.events.battery.BatteryStandby;

@ModelExternalEvents(imported =
{BatteryCharging.class, BatteryStandby.class, BatteryProducing.class})
public class BatteryModel extends AtomicHIOAwithEquations{

    private static final long serialVersionUID = 1L;

    //States of the wind turbine
    public enum State{PRODUCING,CHARGING,STANDBY}

    public static final String  URI = "BatteryModel" ;
    private static final String SERIES_POWER = "power" ;
    private static final String SERIES_CAPACITY = "capacity" ;

    //Exported variables
    @ExportedVariable(type = Double.class)
    protected final Value<Double> currentPower = new Value<Double>(this, 0.0, 0);

    @ExportedVariable(type = Double.class)
    protected final Value<Double> currentCapacity = new Value<Double>(this, 0.0, 0);


    //Constants and variable
    private static final double BATTERY_CONSUMPTION = 1000; //Watt

    protected double maxCapacity = 100000;
    protected State currentState ;
    protected XYPlotter powerPlotter ;
    protected XYPlotter capacityPlotter ;
    protected EmbeddingComponentAccessI componentRef ;


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


    public BatteryModel(
            String uri,
            TimeUnit simulatedTimeUnit,
            SimulatorI simulationEngine) throws Exception {
        super(uri, simulatedTimeUnit, simulationEngine);

        PlotterDescription pd =
                new PlotterDescription(
                        "Battery Consumption Model",
                        "Time (sec)",
                        "Power (watt)",
                        SimulationMain.ORIGIN_X,
                        SimulationMain.ORIGIN_Y + SimulationMain.getPlotterHeight(),
                        SimulationMain.getPlotterWidth(),
                        SimulationMain.getPlotterHeight());

        this.powerPlotter = new XYPlotter(pd) ;
        this.powerPlotter.createSeries(SERIES_POWER) ;

        pd = new PlotterDescription(
                "Battery Capacity Model",
                "Time (sec)",
                "Capacity (Wh)",
                SimulationMain.ORIGIN_X,
                SimulationMain.ORIGIN_Y + 2 * SimulationMain.getPlotterHeight(),
                SimulationMain.getPlotterWidth(),
                SimulationMain.getPlotterHeight());

        this.capacityPlotter = new XYPlotter(pd) ;
        this.capacityPlotter.createSeries(SERIES_CAPACITY) ;

        this.setLogger(new StandardLogger()) ;
    }


    @Override
    public void setSimulationRunParameters(Map<String, Object> simParams) throws Exception{
        this.componentRef = (EmbeddingComponentAccessI) simParams.get("componentRef") ;
    }

    @Override
    public void initialiseState(Time initialTime){
        this.powerPlotter.initialise() ;
        this.powerPlotter.showPlotter() ;

        this.capacityPlotter.initialise() ;
        this.capacityPlotter.showPlotter() ;

        super.initialiseState(initialTime) ;
        setState(State.STANDBY);

        try {
            this.setDebugLevel(1) ;
        } catch (Exception e) {
            throw new RuntimeException(e) ;
        }
    }

    @Override
    public ArrayList<EventI> output() {
        return null;
    }

    @Override
    public Duration timeAdvance() {
        return new Duration(10.0, TimeUnit.SECONDS) ;
    }

    @Override
    public void userDefinedInternalTransition(Duration elapsedTime) {
        super.userDefinedInternalTransition(elapsedTime);

        if(elapsedTime.greaterThan(Duration.zero(getSimulatedTimeUnit()))){
            //change capacity
            if(currentState == State.CHARGING) {
                currentCapacity.v = Math.min(maxCapacity, currentCapacity.v +
                        BATTERY_CONSUMPTION * (elapsedTime.getSimulatedDuration()/timeAdvance().getSimulatedDuration()));
            }else if(currentState == State.PRODUCING){
                currentCapacity.v = Math.max(0, currentCapacity.v -
                        BATTERY_CONSUMPTION * (elapsedTime.getSimulatedDuration()/timeAdvance().getSimulatedDuration()));
            }

            this.capacityPlotter.addData(
                    SERIES_CAPACITY,
                    this.getCurrentStateTime().getSimulatedTime(),
                    this.getCapacity());

            //Change state if max or 0
            if(currentState == State.CHARGING && getCapacity() == maxCapacity ||
                    currentState == State.PRODUCING && getCapacity() == 0) {
                setState(State.STANDBY);
            }
        }
    }

    @Override
    public void userDefinedExternalTransition(Duration elapsedTime) {
        ArrayList<EventI> currentEvents = this.getStoredEventAndReset();
        EventI e;
        for(int i=0;i<currentEvents.size();i++) {
            e = currentEvents.get(i);
            if (this.hasDebugLevel(2)) {
                this.logMessage("BatteryModel::userDefinedExternalTransition "
                        + e.getClass().getCanonicalName());
            }
            e.executeOn(this);
        }
        super.userDefinedExternalTransition(elapsedTime) ;
    }

    @Override
    public void endSimulation(Time endTime) throws Exception {
        this.powerPlotter.addData(
                SERIES_POWER,
                endTime.getSimulatedTime(),
                this.getPower());
        this.capacityPlotter.addData(
                SERIES_CAPACITY,
                endTime.getSimulatedTime(),
                this.getCapacity());
        Thread.sleep(10000L);
        this.powerPlotter.dispose();
        this.capacityPlotter.dispose();

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
        if(this.currentState != s) {
            this.currentState = s;
            if(s == State.STANDBY || s == State.PRODUCING) {
                setPower(0);
            }else {
                setPower(BATTERY_CONSUMPTION);
            }
        }
    }

    public double getPower() {
        return this.currentPower.v;
    }

    public void setPower(double v) {
        this.powerPlotter.addData(
                SERIES_POWER,
                this.getCurrentStateTime().getSimulatedTime(),
                this.getPower());

        this.currentPower.v = v;

        this.powerPlotter.addData(
                SERIES_POWER,
                this.getCurrentStateTime().getSimulatedTime(),
                this.getPower());
    }

    public double getCapacity() {
        return this.currentCapacity.v;
    }
}
