package simulation.sil.battery.models;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import components.Battery;
import components.Battery.BState;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.AbstractSimulationReport;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import fr.sorbonne_u.utils.PlotterDescription;
import fr.sorbonne_u.utils.XYPlotter;
import simulation.sil.battery.events.BatteryConsumption;
import simulation.sil.battery.events.BatteryProduction;

/**
 * The class <code>BatteryModel</code> implements a simulation model
 * of a battery providing the current capacity as a continous variable 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 */
@ModelExternalEvents(exported = {BatteryConsumption.class,BatteryProduction.class})
public class BatteryModel extends AtomicModel {

    private static final long serialVersionUID = 1L;

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

    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------

    public static final String  URI = "SILBatteryModel" ;
    public static final String COMPONENT_REF = URI + ":componentRef";
    public static final String SERIES_CONSUMPTION = "consumption" ;
    public static final String SERIES_PRODUCTION = "production" ;
    public static final String SERIES_CAPACITY = "capacity" ;
    public static final double BATTERY_MODIF = 500;

    protected double currentCapacity;

    private double maxCapacity = 100000;
    private boolean consumptionHasChanged;
    private boolean productionHasChanged;
    private double lastConsumption;
    private double lastProduction;

    private XYPlotter consumptionPlotter ;
    private XYPlotter productionPlotter ;
    private XYPlotter capacityPlotter ;
    private Battery componentRef ;


    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public BatteryModel(
            String uri,
            TimeUnit simulatedTimeUnit,
            SimulatorI simulationEngine) throws Exception {
        super(uri, simulatedTimeUnit, simulationEngine);
        this.setLogger(new StandardLogger());
    }


    // -------------------------------------------------------------------------
    // Simulation's methods
    // -------------------------------------------------------------------------

    @Override
    public void setSimulationRunParameters(Map<String, Object> simParams) throws Exception{
        String vname = this.getURI() + ":" + SERIES_CAPACITY + PlotterDescription.PLOTTING_PARAM_NAME;
        if(simParams.containsKey(vname)) {
            PlotterDescription pd = (PlotterDescription) simParams.get(vname);
            this.capacityPlotter = new XYPlotter(pd);
            this.capacityPlotter.createSeries(SERIES_CAPACITY);
        }
        vname = this.getURI() + ":" + SERIES_CONSUMPTION + PlotterDescription.PLOTTING_PARAM_NAME;
        if(simParams.containsKey(vname)) {
            PlotterDescription pd = (PlotterDescription) simParams.get(vname);
            this.consumptionPlotter = new XYPlotter(pd);
            this.consumptionPlotter.createSeries(SERIES_CONSUMPTION);
        }
        vname = this.getURI() + ":" + SERIES_PRODUCTION + PlotterDescription.PLOTTING_PARAM_NAME;
        if(simParams.containsKey(vname)) {
            PlotterDescription pd = (PlotterDescription) simParams.get(vname);
            this.productionPlotter = new XYPlotter(pd);
            this.productionPlotter.createSeries(SERIES_PRODUCTION);
        }

        // The reference to the embedding component
        vname = COMPONENT_REF;
        if(simParams.containsKey(vname)) {
            this.componentRef = (Battery) simParams.get(vname);
        }
    }

    @Override
    public void initialiseState(Time initialTime){
        this.consumptionHasChanged = false;
        this.productionHasChanged = false;
        this.currentCapacity = 40000.0;
        this.lastConsumption = this.getConsumption();
        this.lastProduction = this.getProduction();

        if(this.capacityPlotter != null) {
            this.capacityPlotter.initialise();
            this.capacityPlotter.showPlotter();
            this.capacityPlotter.addData(
                    SERIES_CAPACITY,
                    initialTime.getSimulatedTime(),
                    this.getCapacity());
        }
        if(this.consumptionPlotter != null) {
            this.consumptionPlotter.initialise();
            this.consumptionPlotter.showPlotter();
            this.consumptionPlotter.addData(
                    SERIES_CONSUMPTION,
                    initialTime.getSimulatedTime(),
                    this.getConsumption());
        }
        if(this.productionPlotter != null) {
            this.productionPlotter.initialise();
            this.productionPlotter.showPlotter();
            this.productionPlotter.addData(
                    SERIES_PRODUCTION,
                    initialTime.getSimulatedTime(),
                    this.getProduction());
        }

        try {
            this.setDebugLevel(1) ;
        } catch (Exception e) {
            throw new RuntimeException(e) ;
        }
        super.initialiseState(initialTime) ;
    }


    @Override
    public ArrayList<EventI> output() {
        ArrayList<EventI> ret = new ArrayList<EventI>();
        Time currentTime = this.getCurrentStateTime().add(this.getNextTimeAdvance());
        if(this.consumptionHasChanged) {
            ret.add(new BatteryConsumption(currentTime, this.getConsumption()));
            this.consumptionHasChanged = false;
        }
        if(this.productionHasChanged) {
            ret.add(new BatteryProduction(currentTime, this.getProduction()));
            this.productionHasChanged = false;
        }
        if(ret.size() != 0) {
            return ret;
        } else {
            return null;
        }
    }

    @Override
    public Duration timeAdvance() {
        if (this.consumptionHasChanged || this.productionHasChanged) {
            return Duration.zero(this.getSimulatedTimeUnit());
        } else {
            return new Duration(1.0, TimeUnit.SECONDS);
        }
    }

    @Override
    public void userDefinedInternalTransition(Duration elapsedTime) {
        super.userDefinedInternalTransition(elapsedTime);
        if(elapsedTime.greaterThan(Duration.zero(getSimulatedTimeUnit()))){
            BState state = this.getState();

            //change capacity
            if(state == BState.CONSUMING) {
                currentCapacity = Math.min(maxCapacity, currentCapacity +
                        BATTERY_MODIF * (elapsedTime.getSimulatedDuration()/timeAdvance().getSimulatedDuration()));
            }else if(state == BState.PRODUCING){
                currentCapacity = Math.max(0, currentCapacity -
                        BATTERY_MODIF * (elapsedTime.getSimulatedDuration()/timeAdvance().getSimulatedDuration()));
            }

            if(this.capacityPlotter != null) {
                this.capacityPlotter.addData(
                        SERIES_CAPACITY,
                        this.getCurrentStateTime().getSimulatedTime(),
                        this.getCapacity());
            }

            //Change state to STANDBY if max capacity or zero
            if(state == BState.CONSUMING && this.getCapacity() == maxCapacity ||
                    state == BState.PRODUCING && this.getCapacity() == 0) {
                setState(BState.STANDBY);
            }

            //Check consumption
            if(this.consumptionPlotter != null) {
                if(this.lastConsumption != this.getConsumption()) {
                    this.consumptionPlotter.addData(
                            SERIES_CONSUMPTION,
                            this.getCurrentStateTime().getSimulatedTime(),
                            lastConsumption);
                }
                this.consumptionPlotter.addData(
                        SERIES_CONSUMPTION,
                        this.getCurrentStateTime().getSimulatedTime(),
                        this.getConsumption());
            }
            if(this.lastConsumption != this.getConsumption()) {
                this.lastConsumption = this.getConsumption();
                this.consumptionHasChanged = true;
            }

            //Check production
            if(this.productionPlotter != null) {
                if(this.lastProduction != this.getProduction()) {
                    this.productionPlotter.addData(
                            SERIES_PRODUCTION,
                            this.getCurrentStateTime().getSimulatedTime(),
                            lastProduction);
                }
                this.productionPlotter.addData(
                        SERIES_PRODUCTION,
                        this.getCurrentStateTime().getSimulatedTime(),
                        this.getProduction());
            }
            if(this.lastProduction != this.getProduction()) {
                this.lastProduction = this.getProduction();
                this.productionHasChanged = true;
            }
        }
    }

    @Override
    public void endSimulation(Time endTime) throws Exception {
        boolean sleep = false;
        if(this.consumptionPlotter != null) {
            sleep = true;
            this.consumptionPlotter.addData(
                    SERIES_CONSUMPTION,
                    endTime.getSimulatedTime(),
                    this.getConsumption());
        }
        if(this.productionPlotter != null) {
            sleep = true;
            this.productionPlotter.addData(
                    SERIES_PRODUCTION,
                    endTime.getSimulatedTime(),
                    this.getProduction());
        }
        if(this.capacityPlotter != null) {
            sleep = true;
            this.capacityPlotter.addData(
                    SERIES_CAPACITY,
                    endTime.getSimulatedTime(),
                    this.getCapacity());
        }

        if(sleep) {
            Thread.sleep(10000L);
        }

        if(this.consumptionPlotter != null) {
            this.consumptionPlotter.dispose();
        }
        if(this.productionPlotter != null) {
            this.productionPlotter.dispose();
        }
        if(this.capacityPlotter != null) {
            this.capacityPlotter.dispose();
        }

        super.endSimulation(endTime);
    }

    @Override
    public SimulationReportI getFinalReport() throws Exception {
        return new BatteryReport(this.getURI()) ;
    }


    // ------------------------------------------------------------------------
    // Model-specific methods
    // ------------------------------------------------------------------------

    public double getMaxCapacity() {
        return maxCapacity;
    }

    public double getCapacity() {
        return currentCapacity;
    }

    private double getProduction() {
        if(this.getState() == BState.PRODUCING) {
            return BATTERY_MODIF;
        } else {
            return 0.0;
        }
    }


    private double getConsumption() {
        if(this.getState() == BState.CONSUMING) {
            return BATTERY_MODIF;
        } else {
            return 0.0;
        }
    }


    private BState getState() {
        try {
            return (BState) this.componentRef.getEmbeddingComponentStateValue("state");
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setState(BState s) {
        try {
            if(this.getState() != s) {
                this.componentRef.setEmbeddingComponentStateValue("state", s);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
