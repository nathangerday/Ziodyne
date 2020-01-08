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
import simulation.events.fridge.SwitchFreezerOn;
import simulation.events.fridge.SwitchFreezerOff;
import simulation.events.fridge.SwitchFridgeOn;
import simulation.events.fridge.SwitchFridgeOff;


@ModelExternalEvents(imported = {
        SwitchFridgeOn.class,
        SwitchFridgeOff.class,
        SwitchFreezerOff.class,
        SwitchFreezerOn.class})
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
    private static final String SERIES_INTENSITY = "intensity";
    
    /** energy consumption (in Watts) of the freezer.		*/
    protected static final double	FREEZER_ON_CONSUMPTION = 20.0 ; // Celsius
    /** energy consumption (in Watts) of the fridge.		*/
    protected static final double	FRIDGE_ON_CONSUMPTION = 40.0 ; // Celsius
    /** nominal tension of fridge **/
    protected static final double	TENSION = 12.0 ; // Volts
    
    protected static final double INC_FREEZER_TEMP = 0.5 ; 
    protected static final double INC_FRIDGE_TEMP = 0.5 ;
    
    protected static final double FREEZER_TEMP = -15.0 ; 
    protected static final double FRIDGE_TEMP = 0.0 ; 
    
    /** Fridge's temperature in Celsius		*/
    @ExportedVariable(type = Double.class)
    protected final Value<Double> currentFridgeTemperature = new Value<Double>(this, 0.0, 0) ;
    
    /** Freezer's temperature in Celsius		*/
    @ExportedVariable(type = Double.class)
    protected final Value<Double> currentFreezerTemperature = new Value<Double>(this, 0.0, 0) ;
    
    /** Freezer's intensity in Celsius		*/
    @ExportedVariable(type = Double.class)
    protected final Value<Double> currentFreezerIntensity = new Value<Double>(this, 0.0, 0) ;
    
    /** Fridge's intensity in Celsius		*/
    @ExportedVariable(type = Double.class)
    protected final Value<Double> currentFridgeIntensity = new Value<Double>(this, 0.0, 0) ;
    
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
        
        PlotterDescription pd_intensity =
                new PlotterDescription(
                        "Fridge Intensity Model",
                        "Time (sec)",
                        "Ampere",
                        700,
                        800,
                        600,
                        400);
        		

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
                
             // add a new data on the plotter; this data will open a new piece
                this.temperatureFreezerPlotter.addData(
                        SERIES_FREEZER,
                        this.getCurrentStateTime().getSimulatedTime(),
                        this.getFreezerTemperature());
                
                this.temperatureFridgePlotter.addData(
                        SERIES_FRIDGE,
                        this.getCurrentStateTime().getSimulatedTime(),
                        this.getFridgeTemperature());
                
                this.intensityPlotter.addData(
                		SERIES_FRIDGE,
                		this.getCurrentStateTime().getSimulatedTime(),
                		this.getIntensity());
                
            if(currentStateFreezer == FridgeModel.State.OFF) {
            	this.currentFreezerTemperature.v += INC_FREEZER_TEMP;
            }
            else {
            	while(this.currentFreezerTemperature.v > FREEZER_TEMP) {
            		this.currentFreezerTemperature.v -= INC_FREEZER_TEMP;
            	}
            
            }
            
            
            if(currentStateFridge == FridgeModel.State.OFF) {
            	this.currentFridgeTemperature.v += INC_FRIDGE_TEMP;
            }
            else {
            	while(this.currentFreezerTemperature.v > FRIDGE_TEMP) {
            		this.currentFreezerTemperature.v -= INC_FRIDGE_TEMP;
            	}
            }
            
            /*
            this.temperatureFreezerPlotter.addData(
                    SERIES_FREEZER,
                    this.getCurrentStateTime().getSimulatedTime(),
                    this.getFreezerTemperature());
            
            this.temperatureFridgePlotter.addData(
                    SERIES_FRIDGE,
                    this.getCurrentStateTime().getSimulatedTime(),
                    this.getFridgeTemperature());
            
            this.intensityPlotter.addData(
            		SERIES_FRIDGE,
            		this.getCurrentStateTime().getSimulatedTime(),
            		this.getIntensity());
            */
           
                
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
        
        for(EventI e : currentEvents) {
        	e.executeOn(this);
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
	}
	
	public void setStateFridge(State state) {
		this.currentStateFridge = state;
		switch(state) {
	        case OFF :
	            this.currentFridgeIntensity.v = 0.0 ;
	            break ;
	        case ON :
	            this.currentFridgeIntensity.v = FRIDGE_ON_CONSUMPTION/TENSION;
	            break ;
		}
	}
	
	public State getStateFreezer() {
		return this.currentStateFreezer;
	}
	
	public double getIntensity() {
		return this.currentFridgeIntensity.v + this.currentFreezerIntensity.v;
	}
	
	public State getStateFridge() {
		return this.currentStateFridge;
	}
	
	public double getFreezerTemperature() {
		return this.currentFreezerTemperature.v;
	}

	public double getFridgeTemperature() {
		return this.currentFridgeTemperature.v;
	}	
}
