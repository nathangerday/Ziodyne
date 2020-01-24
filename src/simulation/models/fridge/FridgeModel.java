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
import simulation.events.fridge.FreezerOn;
import simulation.events.fridge.FreezerOpen;
import simulation.events.fridge.FridgeClose;
import simulation.events.fridge.FreezerClose;
import simulation.events.fridge.FreezerOff;
import simulation.events.fridge.FridgeOn;
import simulation.events.fridge.FridgeOpen;
import simulation.events.fridge.FridgeOff;


@ModelExternalEvents(imported = {
        FridgeOn.class,
        FridgeOff.class,
        FreezerOff.class,
        FreezerOn.class,
        FridgeOpen.class,
        FridgeClose.class,
        FreezerOpen.class,
        FreezerClose.class})
public class FridgeModel extends AtomicHIOAwithEquations{

    private static final long serialVersionUID = 1L;

    public enum State{ON,OFF}
    public enum DoorState{CLOSE,OPEN}

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
    private static final String SERIES_POWER = "power";

    /** energy consumption (in Watts) of the freezer.		*/
    protected static final double	FREEZER_ON_CONSUMPTION = 80.0 ;
    /** energy consumption (in Watts) of the fridge.		*/
    protected static final double	FRIDGE_ON_CONSUMPTION = 300.0 ;

    protected static final double INC_FREEZER_TEMP = 0.2 ; 
    protected static final double INC_FRIDGE_TEMP = 0.2 ;

    protected static final double FREEZER_TEMP_MIN = -13.0 ; 
    protected static final double FRIDGE_TEMP_MIN = 4.0 ; 

    protected static final double FREEZER_TEMP_MAX = 25.0 ; 
    protected static final double FRIDGE_TEMP_MAX = 25.0 ; 

    /** Fridge's temperature in Celsius		*/
    @ExportedVariable(type = Double.class)
    protected final Value<Double> currentFridgeTemperature = new Value<Double>(this, FRIDGE_TEMP_MIN, 0) ;

    /** Freezer's temperature in Celsius		*/
    @ExportedVariable(type = Double.class)
    protected final Value<Double> currentFreezerTemperature = new Value<Double>(this, FREEZER_TEMP_MIN, 0) ;

    /** Freezer's intensity in Celsius		*/
    protected final Value<Double> currentFreezerPower = new Value<Double>(this, 0.0, 0) ;

    /** Fridge's intensity in Celsius		*/
    protected final Value<Double> currentFridgePower = new Value<Double>(this, 0.0, 0) ;

    @ExportedVariable(type = Double.class)
    protected final Value<Double> currentPower = new Value<Double>(this, 0.0, 0) ;

    /** fridge's door */
    protected DoorState fridgeDoor ;
    
    /** freezer's door */
    protected DoorState freezerDoor ;
    
    /** current state (OFF,ON) of the fridge.					*/
    protected State	currentStateFridge ;

    /** current state (OFF,ON) of the freezer.					*/
    protected State	currentStateFreezer;

    /** Fridge's temperature plotter **/
    protected XYPlotter temperatureFridgePlotter;

    /** Freezer's temperature plotter **/
    protected XYPlotter temperatureFreezerPlotter;

    /** Global intensity plotter **/
    protected XYPlotter powerPlotter;

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
                        "Fridge Power Model",
                        "Time (sec)",
                        "Power (Watt)",
                        SimulationMain.ORIGIN_X,
                        SimulationMain.ORIGIN_Y + 3*SimulationMain.getPlotterHeight(),
                        SimulationMain.getPlotterWidth(),
                        SimulationMain.getPlotterHeight());


        this.temperatureFridgePlotter = new XYPlotter(pd_fridge) ;
        this.temperatureFreezerPlotter = new XYPlotter(pd_freezer) ;
        this.powerPlotter = new XYPlotter(pd_intensity) ;
        this.temperatureFridgePlotter.createSeries(SERIES_FRIDGE);
        this.temperatureFreezerPlotter.createSeries(SERIES_FREEZER);
        this.powerPlotter.createSeries(SERIES_POWER);

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
        this.powerPlotter.initialise();
        this.temperatureFreezerPlotter.showPlotter() ;
        this.temperatureFridgePlotter.showPlotter() ;
        this.freezerDoor = DoorState.CLOSE;
        this.fridgeDoor = DoorState.CLOSE;
        this.powerPlotter.showPlotter();

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
        return new Duration(10.0, TimeUnit.SECONDS) ;
    }

    @Override
    public void userDefinedInternalTransition(Duration elapsedTime) {
        super.userDefinedInternalTransition(elapsedTime);
        if(elapsedTime.greaterThan(Duration.zero(getSimulatedTimeUnit()))){
            //Change freezer temperature
        	
        	int doorFreezerOpenCoefficient = 1; 
        	int doorFridgeOpenCoefficient = 1;
        	//TODO Maybe do dynamic coef depening on the temperature
        	if(this.fridgeDoor == DoorState.OPEN) {
        		doorFridgeOpenCoefficient = 3;
        	}
        	if(this.freezerDoor == DoorState.OPEN) {
        		doorFreezerOpenCoefficient = 5;
        	}
        	
        	
        	
            if(currentStateFreezer == FridgeModel.State.OFF) {
                this.currentFreezerTemperature.v =
                        Math.min(FREEZER_TEMP_MAX, this.currentFreezerTemperature.v +
                                INC_FREEZER_TEMP * doorFreezerOpenCoefficient * (elapsedTime.getSimulatedDuration()/timeAdvance().getSimulatedDuration()));
            }
            else {
                this.currentFreezerTemperature.v =
                        Math.max(FREEZER_TEMP_MIN, this.currentFreezerTemperature.v -
                                INC_FREEZER_TEMP * doorFreezerOpenCoefficient * (elapsedTime.getSimulatedDuration()/timeAdvance().getSimulatedDuration()));
            }
            //Change fridge temperature
            if(currentStateFridge == FridgeModel.State.OFF) {
                this.currentFridgeTemperature.v =
                        Math.min(FRIDGE_TEMP_MAX, this.currentFridgeTemperature.v +
                                INC_FRIDGE_TEMP * doorFridgeOpenCoefficient * (elapsedTime.getSimulatedDuration()/timeAdvance().getSimulatedDuration()));
            }
            else {
                this.currentFridgeTemperature.v =
                        Math.max(FRIDGE_TEMP_MIN, this.currentFridgeTemperature.v -
                                INC_FRIDGE_TEMP * doorFridgeOpenCoefficient * (elapsedTime.getSimulatedDuration()/timeAdvance().getSimulatedDuration()));
            }
            this.temperatureFreezerPlotter.addData(
                    SERIES_FREEZER,
                    this.getCurrentStateTime().getSimulatedTime(),
                    this.getFreezerTemperature());

            this.temperatureFridgePlotter.addData(
                    SERIES_FRIDGE,
                    this.getCurrentStateTime().getSimulatedTime(),
                    this.getFridgeTemperature());
        }
    }

    @Override
    public void	userDefinedExternalTransition(Duration elapsedTime) {
        if (this.hasDebugLevel(2)) {
            this.logMessage("FridgeModel::userDefinedExternalTransition 1");
        }
        Vector<EventI> currentEvents = this.getStoredEventAndReset();

        for(EventI e : currentEvents) {
            e.executeOn(this);
        }
        super.userDefinedExternalTransition(elapsedTime) ;
    }

    @Override
    public void	endSimulation(Time endTime) throws Exception {
        this.powerPlotter.addData(
                SERIES_POWER,
                endTime.getSimulatedTime(),
                this.getPower());
        Thread.sleep(10000L);
        this.temperatureFreezerPlotter.dispose();
        this.temperatureFridgePlotter.dispose();
        this.powerPlotter.dispose();

        super.endSimulation(endTime);
    }

    public void setStateFreezer(State state) {
        if(this.currentStateFreezer != state) {
            this.currentStateFreezer = state;
            switch(state) {
            case OFF :
                this.currentFreezerPower.v = 0.0 ;
                break ;
            case ON :
                this.currentFreezerPower.v = FREEZER_ON_CONSUMPTION;
                break ;
            }
            this.setPower(this.currentFreezerPower.v + this.currentFridgePower.v);
        }
    }

    public void setStateFridge(State state) {
        if(this.currentStateFridge != state) {
            this.currentStateFridge = state;
            switch(state) {
            case OFF :
                this.currentFridgePower.v = 0.0;
                break ;
            case ON :
                this.currentFridgePower.v = FRIDGE_ON_CONSUMPTION;
                break ;
            }
            this.setPower(this.currentFreezerPower.v + this.currentFridgePower.v);
        }
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
    
    public void setStateFridgeDoor(DoorState state) {
    	this.fridgeDoor = state;
    }
    
    public void setStateFreezerDoor(DoorState state) {
    	this.freezerDoor = state;
    }

    public double getPower() {
        return this.currentPower.v;
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
    
    public DoorState getFridgeDoorState() {
    	return this.fridgeDoor;
    }
    
    public DoorState getFreezerDoorState() {
    	return this.freezerDoor;
    }
}
