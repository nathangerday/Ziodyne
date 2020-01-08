package simulation.models.fridge;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentStateAccessI;
import fr.sorbonne_u.devs_simulation.examples.molene.SimulationMain;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOAwithEquations;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.AbstractSimulationReport;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import fr.sorbonne_u.utils.PlotterDescription;
import fr.sorbonne_u.utils.XYPlotter;
import simulation.events.fridge.SwitchFreezerOn;
import simulation.events.fridge.SwitchFreezerOff;
import simulation.events.fridge.SwitchFridgeOn;
import simulation.events.fridge.TicEvent;
import simulation.events.fridge.SwitchFridgeOff;


@ModelExternalEvents(imported = {
        SwitchFridgeOn.class,
        SwitchFridgeOff.class,
        SwitchFreezerOff.class,
        SwitchFreezerOn.class,
        TicEvent.class})
public class FridgeModel extends AtomicHIOAwithEquations{

    private static final long serialVersionUID = 1L;

    public enum State{ON,OFF}

    public static class FridgeReport extends AbstractSimulationReport{

        private static final long serialVersionUID = 1L;

        public FridgeReport(String modelURI) {
            super(modelURI);
        }

        @Override
        public String toString(){
            return "FridgeReport("+ this.getModelURI()+")";
        }
    }

    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------

    public static final String	URI = "FridgeModel" ;
    private static final String	SERIES_FREEZER = "temperature-freezer" ;
    private static final String	SERIES_FRIDGE = "temperature-fridge" ;
    private static final String SERIES_INTENSITY = "intensity";

    /** energy consumption (in Watts) of the freezer.		*/
    protected static final double	FREEZER_ON_CONSUMPTION = 20.0 ;
    /** energy consumption (in Watts) of the fridge.		*/
    protected static final double	FRIDGE_ON_CONSUMPTION = 40.0 ;
    /** nominal tension of fridge **/
    protected static final double TENSION = 12.0 ; // Volts

    protected static final double INC_FREEZER_TEMP = 1 ; 
    protected static final double INC_FRIDGE_TEMP = 1 ;

    protected static final double FREEZER_TEMP_MIN = -15.0 ; 
    protected static final double FRIDGE_TEMP_MIN = 0.0 ; 

    protected static final double FREEZER_TEMP_MAX = 25.0 ; 
    protected static final double FRIDGE_TEMP_MAX = 25.0 ; 

    /** Fridge's temperature in Celsius		*/
    @ExportedVariable(type = Double.class)
    protected final Value<Double> currentFridgeTemperature = new Value<Double>(this, 0.0, 0) ;

    /** Freezer's temperature in Celsius		*/
    @ExportedVariable(type = Double.class)
    protected final Value<Double> currentFreezerTemperature = new Value<Double>(this, -15.0, 0) ;

    /** Freezer's intensity in Celsius		*/
    protected final Value<Double> currentFreezerIntensity = new Value<Double>(this, 0.0, 0) ;

    /** Fridge's intensity in Celsius		*/
    protected final Value<Double> currentFridgeIntensity = new Value<Double>(this, 0.0, 0) ;

    @ExportedVariable(type = Double.class)
    protected final Value<Double> currentIntensity = new Value<Double>(this, 0.0, 0) ;

    /** current state (OFF,ON) of the fridge.					*/
    protected State	currentStateFridge ;

    /** current state (OFF,ON) of the freezer.					*/
    protected State	currentStateFreezer;

    /** Fridge's temperature plotter **/
    protected XYPlotter temperatureFridgePlotter;

    /** Freezer's temperature plotter **/
    protected XYPlotter temperatureFreezerPlotter;

    /** Global intensity plotter **/
    protected XYPlotter intensityPlotter;

    /** reference on the object representing the component that holds the
     *  model; enables the model to access the state of this component.		*/
    protected EmbeddingComponentStateAccessI componentRef ;


    public FridgeModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
        super(uri, simulatedTimeUnit, simulationEngine);

        PlotterDescription pd_fridge =
                new PlotterDescription(
                        "Fridge Temperature Model",
                        "Time (sec)",
                        "Celsius",
                        SimulationMain.ORIGIN_X,
                        SimulationMain.ORIGIN_Y + SimulationMain.getPlotterHeight(),
                        SimulationMain.getPlotterWidth(),
                        SimulationMain.getPlotterHeight());

        PlotterDescription pd_freezer =
                new PlotterDescription(
                        "Freezer Temperature Model",
                        "Time (sec)",
                        "Celsius",
                        SimulationMain.ORIGIN_X,
                        SimulationMain.ORIGIN_Y + 2*SimulationMain.getPlotterHeight(),
                        SimulationMain.getPlotterWidth(),
                        SimulationMain.getPlotterHeight());

        PlotterDescription pd_intensity =
                new PlotterDescription(
                        "Fridge Intensity Model",
                        "Time (sec)",
                        "Ampere",
                        SimulationMain.ORIGIN_X,
                        SimulationMain.ORIGIN_Y + 3*SimulationMain.getPlotterHeight(),
                        SimulationMain.getPlotterWidth(),
                        SimulationMain.getPlotterHeight());


        this.temperatureFridgePlotter = new XYPlotter(pd_fridge) ;
        this.temperatureFreezerPlotter = new XYPlotter(pd_freezer) ;
        this.intensityPlotter = new XYPlotter(pd_intensity) ;
        this.temperatureFridgePlotter.createSeries(SERIES_FRIDGE);
        this.temperatureFreezerPlotter.createSeries(SERIES_FREEZER);
        this.intensityPlotter.createSeries(SERIES_INTENSITY);

        // create a standard logger (logging on the terminal)
        this.setLogger(new StandardLogger()) ;
    }

    @Override
    public void	setSimulationRunParameters(Map<String, Object> simParams) throws Exception
    {
        // The reference to the embedding component
        this.componentRef =(EmbeddingComponentStateAccessI) simParams.get("componentRef") ;
    }

    @Override
    public void	initialiseState(Time initialTime)
    {
        this.currentStateFreezer = FridgeModel.State.OFF ;
        this.currentStateFridge = FridgeModel.State.OFF;
        this.temperatureFreezerPlotter.initialise() ;
        this.temperatureFridgePlotter.initialise() ;
        this.intensityPlotter.initialise();
        this.temperatureFreezerPlotter.showPlotter() ;
        this.temperatureFridgePlotter.showPlotter() ;
        this.intensityPlotter.showPlotter();

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
    public void	userDefinedExternalTransition(Duration elapsedTime) {
        if (this.hasDebugLevel(2)) {
            this.logMessage("FridgeModel::userDefinedExternalTransition 1");
        }
        Vector<EventI> currentEvents = this.getStoredEventAndReset();

        for(EventI e : currentEvents) {
            if (e instanceof TicEvent) {
                this.temperatureFreezerPlotter.addData(
                        SERIES_FREEZER,
                        this.getCurrentStateTime().getSimulatedTime(),
                        this.getFreezerTemperature());

                this.temperatureFridgePlotter.addData(
                        SERIES_FRIDGE,
                        this.getCurrentStateTime().getSimulatedTime(),
                        this.getFridgeTemperature());

                //Change freezer temperature
                if(currentStateFreezer == FridgeModel.State.OFF) {
                    this.currentFreezerTemperature.v =
                            Math.min(FREEZER_TEMP_MAX, this.currentFreezerTemperature.v + INC_FREEZER_TEMP);
                }
                else {
                    this.currentFreezerTemperature.v =
                            Math.max(FREEZER_TEMP_MIN, this.currentFreezerTemperature.v - INC_FREEZER_TEMP);
                }
                //Change fridge temperature
                if(currentStateFridge == FridgeModel.State.OFF) {
                    this.currentFridgeTemperature.v =
                            Math.min(FRIDGE_TEMP_MAX, this.currentFridgeTemperature.v + INC_FRIDGE_TEMP);
                }
                else {
                    this.currentFridgeTemperature.v =
                            Math.max(FRIDGE_TEMP_MIN, this.currentFridgeTemperature.v - INC_FRIDGE_TEMP);
                }
            } else {
                this.intensityPlotter.addData(
                        SERIES_INTENSITY,
                        this.getCurrentStateTime().getSimulatedTime(),
                        this.getIntensity());
                e.executeOn(this);
                this.intensityPlotter.addData(
                        SERIES_INTENSITY,
                        this.getCurrentStateTime().getSimulatedTime(),
                        this.getIntensity());
            }
        }
        super.userDefinedExternalTransition(elapsedTime) ;
    }

    @Override
    public void	endSimulation(Time endTime) throws Exception {
        this.temperatureFreezerPlotter.addData(
                SERIES_FREEZER,
                endTime.getSimulatedTime(),
                this.getFreezerTemperature());
        this.temperatureFridgePlotter.addData(
                SERIES_FRIDGE,
                endTime.getSimulatedTime(),
                this.getFridgeTemperature());
        this.intensityPlotter.addData(
                SERIES_INTENSITY,
                endTime.getSimulatedTime(),
                this.getIntensity());
        Thread.sleep(10000L);
        this.temperatureFreezerPlotter.dispose();
        this.temperatureFridgePlotter.dispose();
        this.intensityPlotter.dispose();

        super.endSimulation(endTime);
    }

    public void setStateFreezer(State state) {
        this.currentStateFreezer = state;
        switch(state) {
        case OFF :
            this.currentFreezerIntensity.v = 0.0 ;
            break ;
        case ON :
            this.currentFreezerIntensity.v = FREEZER_ON_CONSUMPTION/TENSION;
            break ;
        }
        this.currentIntensity.v = this.currentFreezerIntensity.v + this.currentFridgeIntensity.v;
    }

    public void setStateFridge(State state) {
        this.currentStateFridge = state;
        switch(state) {
        case OFF :
            this.currentFridgeIntensity.v = 0.0;
            break ;
        case ON :
            this.currentFridgeIntensity.v = FRIDGE_ON_CONSUMPTION/TENSION;
            break ;
        }
        this.currentIntensity.v = this.currentFreezerIntensity.v + this.currentFridgeIntensity.v;
    }

    public double getIntensity() {
        return this.currentIntensity.v;
    }

    public State getStateFridge() {
        return this.currentStateFridge;
    }

    public State getStateFreezer() {
        return this.currentStateFreezer;
    }

    public double getFreezerTemperature() {
        return this.currentFreezerTemperature.v;
    }

    public double getFridgeTemperature() {
        return this.currentFridgeTemperature.v;
    }	
}
