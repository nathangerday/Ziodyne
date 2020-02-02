package simulation.sil.electricmeter.models;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
import simulation.sil.dishwasher.events.DishwasherConsumption;
import simulation.sil.fridge.events.FridgeConsumption;
import simulation.sil.lamp.events.LampConsumption;
import simulation.sil.windturbine.events.WindTurbineProduction;

/**
 * The class <code>ElectricMeterModel</code> implements a simulation model
 * of a meter providing the overall consumption as a continuous variable
 *  
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 */
@ModelExternalEvents(imported = {
        BatteryConsumption.class,
        BatteryProduction.class,
        WindTurbineProduction.class,
        LampConsumption.class,
        FridgeConsumption.class,
        DishwasherConsumption.class})
public class ElectricMeterModel extends AtomicModel {

    private static final long serialVersionUID = 1L;

    public static class ElectricMeterReport extends AbstractSimulationReport {

        private static final long serialVersionUID = 1L;

        public ElectricMeterReport(String modelURI) {
            super(modelURI);
        }

        @Override
        public String toString() {
            return "ElectricMeterReport(" + this.getModelURI() + ")";
        }
    }

    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------

    /** uri of the electric meter model **/
    public static final String URI = "SILElectricMeterModel";
    /** series of the power usage **/
    public static final String SERIES_CONSUMPTION = "GlobalPowerUsage";
    /** series of the power production **/
    public static final String SERIES_PRODUCTION = "GlobalPowerProduction";
    /** series of the power production **/
    public static final String SERIES_AVAILABLE = "Available energie";

    /** energy consumption of the lamp */
    private double lampConsumption;
    /** energy consumption of the fridge */
    private double fridgeConsumption;
    /** energy consumption of the dishwasher */
    private double dishwasherConsumption;
    /** energy consumption of the battery */
    private double batteryConsumption;

    /** energy production of the battery */
    private double batteryProduction;
    /** energy production of the wind turbine */
    private double windTurbineProduction;

    /** plotter for the intensity over time. */
    protected XYPlotter consumptionPlotter;
    /** plotter for the produced energy over time. */
    protected XYPlotter productionPlotter;
    /** plotter for the available energy over time. */
    protected XYPlotter availablePlotter;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------


    /**
     * Creates an ElectricMeterModel
     * @param uri uri of the model
     * @param simulatedTimeUnit timeunit of the simulation
     * @param simulationEngine engine for the simulation
     * @throws Exception
     */
    public ElectricMeterModel(String uri,
            TimeUnit simulatedTimeUnit,
            SimulatorI simulationEngine) throws Exception {
        super(uri, simulatedTimeUnit, simulationEngine);
        this.setLogger(new StandardLogger());
        //        PlotterDescription pdPower = new PlotterDescription("Global Power Usage", "Time (sec)", "Power (Watt)",
        //                SimulationMain.ORIGIN_X, SimulationMain.ORIGIN_Y + SimulationMain.getPlotterHeight(),
        //                SimulationMain.getPlotterWidth(), SimulationMain.getPlotterHeight());
        //
        //        this.consumptionPlotter = new XYPlotter(pdPower);
        //        this.consumptionPlotter.createSeries(SERIES_GLOBAL_POWER_USAGE);
        //
        //        PlotterDescription pdProduction = new PlotterDescription("Global Power Production", "Time (sec)",
        //                "Power (Watt)", SimulationMain.ORIGIN_X, SimulationMain.ORIGIN_Y + SimulationMain.getPlotterHeight(),
        //                SimulationMain.getPlotterWidth(), SimulationMain.getPlotterHeight());
        //
        //        this.productionPlotter = new XYPlotter(pdProduction);
        //        this.productionPlotter.createSeries(SERIES_GLOBAL_POWER_PRODUCTION);
    }


    // -------------------------------------------------------------------------
    // Simulation's methods
    // -------------------------------------------------------------------------


    @Override
    public void setSimulationRunParameters(Map<String, Object> simParams) throws Exception {
        String vname = this.getURI() + ":" + SERIES_CONSUMPTION + PlotterDescription.PLOTTING_PARAM_NAME;
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
        vname = this.getURI() + ":" + SERIES_AVAILABLE + PlotterDescription.PLOTTING_PARAM_NAME;
        if(simParams.containsKey(vname)) {
            PlotterDescription pd = (PlotterDescription) simParams.get(vname);
            this.availablePlotter = new XYPlotter(pd);
            this.availablePlotter.createSeries(SERIES_AVAILABLE);
        }
    }

    @Override
    public void initialiseState(Time initialTime) {
        this.lampConsumption = 0.0;
        this.fridgeConsumption = 0.0;
        this.dishwasherConsumption = 0.0;
        this.batteryConsumption = 0.0;

        this.batteryProduction = 0.0;
        this.windTurbineProduction = 0.0;

        if(this.consumptionPlotter != null) {
            this.consumptionPlotter.initialise();
            this.consumptionPlotter.showPlotter();
            this.consumptionPlotter.addData(
                    SERIES_CONSUMPTION,
                    initialTime.getSimulatedTime(),
                    0.0);
        }
        if(this.productionPlotter != null) {
            this.productionPlotter.initialise();
            this.productionPlotter.showPlotter();
            this.productionPlotter.addData(
                    SERIES_PRODUCTION,
                    initialTime.getSimulatedTime(),
                    0.0);
        }
        if(this.availablePlotter != null) {
            this.availablePlotter.initialise();
            this.availablePlotter.showPlotter();
            this.availablePlotter.addData(
                    SERIES_AVAILABLE,
                    initialTime.getSimulatedTime(),
                    0.0);
        }

        try {
            this.setDebugLevel(1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        super.initialiseState(initialTime);
    }


    @Override
    public ArrayList<EventI> output() {
        return null;
    }


    @Override
    public Duration timeAdvance() {
        return Duration.INFINITY;
    }


    @Override
    public void userDefinedExternalTransition(Duration elapsedTime) {
        ArrayList<EventI> currentEvents = this.getStoredEventAndReset();
        for(EventI e : currentEvents) {
            e.executeOn(this);
        }
        if(consumptionPlotter != null) {
            this.consumptionPlotter.addData(
                    SERIES_CONSUMPTION,
                    this.getCurrentStateTime().getSimulatedTime(),
                    this.getConsumption());
        }
        if(productionPlotter != null) {
            this.productionPlotter.addData(
                    SERIES_PRODUCTION,
                    this.getCurrentStateTime().getSimulatedTime(),
                    this.getProduction());
        }
        if(availablePlotter != null) {
            this.availablePlotter.addData(
                    SERIES_AVAILABLE,
                    this.getCurrentStateTime().getSimulatedTime(),
                    this.getAvailableEnergy());
        }
        super.userDefinedExternalTransition(elapsedTime) ;
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
        if(this.availablePlotter != null) {
            sleep = true;
            this.availablePlotter.addData(
                    SERIES_AVAILABLE,
                    endTime.getSimulatedTime(),
                    this.getAvailableEnergy());
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
        if(this.availablePlotter != null) {
            this.availablePlotter.dispose();
        }

        super.endSimulation(endTime);
    }


    @Override
    public SimulationReportI getFinalReport() throws Exception{
        return new ElectricMeterReport(this.getURI()) ;
    }


    // ------------------------------------------------------------------------
    // Model-specific methods
    // ------------------------------------------------------------------------

    public double getConsumption() {
        return
                this.lampConsumption +
                this.dishwasherConsumption +
                this.batteryConsumption + 
                this.fridgeConsumption;
    }

    public double getProduction() {
        return this.windTurbineProduction + this.batteryProduction;
    }

    public double getAvailableEnergy() {
        return this.getProduction() - this.getConsumption();
    }

    public void setLampConsumption(double value) {
        this.lampConsumption = value;
    }

    public void setBatteryConsumption(double value) {
        this.batteryConsumption = value;
    }

    public void setFridgeConsumption(double value) {
        this.fridgeConsumption = value;

    }

    public void setDishwasherConsumption(double value) {
        this.dishwasherConsumption = value;
    }

    public void setBatteryProduction(double value) {
        this.batteryProduction = value;
    }

    public void setWindTurbineProduction(double value) {
        this.windTurbineProduction = value;
    }
}
