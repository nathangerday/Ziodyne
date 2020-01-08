package simulation.models.fridge;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentStateAccessI;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOAwithEquations;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
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
import simulation.events.fridge.AbstractFridgeEvent;
import simulation.events.fridge.LowerFreezerTemp;
import simulation.events.fridge.LowerFridgeTemp;
import simulation.events.fridge.RaiseFreezerTemp;
import simulation.events.fridge.RaiseFridgeTemp;
import simulation.events.fridge.SwitchOff;
import simulation.events.fridge.SwitchOn;



@ModelExternalEvents(imported = {SwitchOn.class,
        SwitchOff.class,
        RaiseFreezerTemp.class,
        RaiseFridgeTemp.class,
        LowerFridgeTemp.class,
        LowerFreezerTemp.class})
public class FridgeModel extends AtomicHIOAwithEquations{

	public enum State{ON,OFF}
	
    public static class FridgeReport extends AbstractSimulationReport{

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
 
    /** Fridge's temperature in Celsius		*/
    @ExportedVariable(type = Double.class)
    protected final Value<Double> currentFridgeTemperature = new Value<Double>(this, 4.0, 0) ;
    
    /** Freezer's temperature in Celsius		*/
    @ExportedVariable(type = Double.class)
    protected final Value<Double> currentFreezerTemperature = new Value<Double>(this, -15.0, 0) ;
    
    /** current state (OFF,ON) of the fridge.					*/
    protected State	currentState ;
    
    /** Fridge's temperature plotter **/
    protected XYPlotter temperatureFridgePlotter;
   
    /** Freezer's temperature plotter **/
    protected XYPlotter temperatureFreezerPlotter;

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
                        100,
                        0,
                        600,
                        400);
        
        PlotterDescription pd_freezer =
                new PlotterDescription(
                        "Freezer Temperature Model",
                        "Time (sec)",
                        "Celsius",
                        700,
                        0,
                        600,
                        400);
        
        		

        this.temperatureFridgePlotter = new XYPlotter(pd_fridge) ;
        this.temperatureFreezerPlotter = new XYPlotter(pd_freezer) ;
        this.temperatureFridgePlotter.createSeries(SERIES_FRIDGE);
        this.temperatureFreezerPlotter.createSeries(SERIES_FREEZER);

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
        this.currentState = FridgeModel.State.OFF ;
	    this.temperatureFreezerPlotter.initialise() ;
	    this.temperatureFridgePlotter.initialise() ;
	    this.temperatureFreezerPlotter.showPlotter() ;
	    this.temperatureFridgePlotter.showPlotter() ;

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
            this.logMessage("FridgeModel::userDefinedExternalTransition 1");
        }
        // get the vector of current external events
        Vector<EventI> currentEvents = this.getStoredEventAndReset();
        // when this method is called, there is at least one external event,
        // and for the lamp model, there will be exactly one by
        // construction.
        assert currentEvents != null && currentEvents.size() == 1;

        Event ce = (Event) currentEvents.get(0);
        assert ce instanceof AbstractFridgeEvent;
        if (this.hasDebugLevel(2)) {
            this.logMessage("FridgeModel::userDefinedExternalTransition 2 "
                    + ce.getClass().getCanonicalName());
        }

        this.temperatureFreezerPlotter.addData(
                SERIES_FREEZER,
                this.getCurrentStateTime().getSimulatedTime(),
                this.getFreezerTemperature());
        
        this.temperatureFridgePlotter.addData(
                SERIES_FRIDGE,
                this.getCurrentStateTime().getSimulatedTime(),
                this.getFridgeTemperature());

        if (this.hasDebugLevel(2)) {
            this.logMessage("FridgeModel::userDefinedExternalTransition 3 "
                    + this.getState());
        }

        // execute the current external event on this model, changing its state
        // and intensity level
        ce.executeOn(this);

        if (this.hasDebugLevel(1)) {
            this.logMessage("FridgeModel::userDefinedExternalTransition 4 "
                    + this.getState()) ;
        }

        // add a new data on the plotter; this data will open a new piece
        this.temperatureFreezerPlotter.addData(
                SERIES_FREEZER,
                this.getCurrentStateTime().getSimulatedTime(),
                this.getFreezerTemperature());
        
        this.temperatureFridgePlotter.addData(
                SERIES_FRIDGE,
                this.getCurrentStateTime().getSimulatedTime(),
                this.getFridgeTemperature());

        super.userDefinedExternalTransition(elapsedTime) ;
        if (this.hasDebugLevel(2)) {
            this.logMessage("FridgeModel::userDefinedExternalTransition 5") ;
        }
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
        Thread.sleep(10000L);
        this.temperatureFreezerPlotter.dispose();
        this.temperatureFridgePlotter.dispose();

        super.endSimulation(endTime);
    }

	public void setState(State state) {
		this.currentState = state;
	}
	
	public void lowerFridgeTemperature() {
		this.currentFridgeTemperature.v -=0.5;
	}
	
	public void lowerFreezerTemperature() {
		this.currentFreezerTemperature.v -=0.5;
	}
	
	public void raiseFridgeTemperature() {
		this.currentFridgeTemperature.v +=0.5;
	}
	
	public void raiseFreezerTemperature() {
		this.currentFreezerTemperature.v +=0.5;
	}
	
	public State getState() {
		return this.currentState;
	}
	
	public double getFreezerTemperature() {
		return this.currentFreezerTemperature.v;
	}

	public double getFridgeTemperature() {
		return this.currentFridgeTemperature.v;
	}	
}
