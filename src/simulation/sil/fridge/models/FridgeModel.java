package simulation.sil.fridge.models;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import components.Fridge;
import components.Fridge.DoorState;
import components.Fridge.FState;
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
import simulation.sil.fridge.events.FreezerClose;
import simulation.sil.fridge.events.FreezerOpen;
import simulation.sil.fridge.events.FridgeClose;
import simulation.sil.fridge.events.FridgeConsumption;
import simulation.sil.fridge.events.FridgeOpen;


@ModelExternalEvents(imported = {
        FridgeOpen.class,
        FridgeClose.class,
        FreezerOpen.class,
        FreezerClose.class},
exported = {FridgeConsumption.class})
public class FridgeModel extends AtomicHIOAwithEquations{

    private static final long serialVersionUID = 1L;

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

    public static final String URI = "SILFridgeModel" ;
    public static final String SERIES_FREEZER = "temperature-freezer" ;
    public static final String SERIES_FRIDGE = "temperature-fridge" ;
    public static final String SERIES_POWER = "power";
    public static final String COMPONENT_REF = URI + ":componentRef";

    /** energy consumption (in Watts) of the freezer.		*/
    public static final double	FREEZER_ON_CONSUMPTION = 80.0 ;
    /** energy consumption (in Watts) of the fridge.		*/
    public static final double	FRIDGE_ON_CONSUMPTION = 300.0 ;

    protected static final double FREEZER_TEMP_MODIF = 0.01 ; 
    protected static final double FRIDGE_TEMP_MODIF = 0.005 ;

    /** Freezer's temperature min threshold */
    protected static final double FREEZER_TEMP_MIN = -15.0 ;
    /** Freezer's temperature max threshold */
    protected static final double FREEZER_TEMP_MAX = -13.0 ;
    /** Fridge's temperature min threshold */
    protected static final double FRIDGE_TEMP_MIN = 4.0 ;
    /** Fridge's temperature max threshold */
    protected static final double FRIDGE_TEMP_MAX = 6.0 ; 
    /** Ambient temperature */ 
    protected static final double AMBIENT_TEMP = 25.0 ; 

    /** Fridge's temperature in Celsius		*/
    private final Value<Double> currentFridgeTemperature = new Value<Double>(this, FRIDGE_TEMP_MIN, 0) ;
    /** Freezer's temperature in Celsius		*/
    private final Value<Double> currentFreezerTemperature = new Value<Double>(this, FREEZER_TEMP_MIN, 0) ;
    /** Freezer's intensity in Celsius		*/
    private final Value<Double> currentFreezerPower = new Value<Double>(this, 0.0, 0) ;
    /** Fridge's intensity in Celsius		*/
    private final Value<Double> currentFridgePower = new Value<Double>(this, 0.0, 0) ;

    /** Check if the consumption has changed since last time */
    private boolean consumptionHasChanged;
    /** Last power consumption */
    private double lastPower;

    /** Fridge's temperature plotter **/
    private XYPlotter temperatureFridgePlotter;
    /** Freezer's temperature plotter **/
    private XYPlotter temperatureFreezerPlotter;
    /** Global intensity plotter **/
    private XYPlotter powerPlotter;

    /** reference on the object representing the component that holds the
     *  model; enables the model to access the state of this component.		*/
    protected Fridge componentRef ;


    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public FridgeModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
        super(uri, simulatedTimeUnit, simulationEngine);
        this.setLogger(new StandardLogger()) ;
    }


    // -------------------------------------------------------------------------
    // Simulation's methods
    // -------------------------------------------------------------------------

    @Override
    public void	setSimulationRunParameters(Map<String, Object> simParams) throws Exception {
        String vname = this.getURI() + ":" + SERIES_FREEZER + PlotterDescription.PLOTTING_PARAM_NAME;
        if(simParams.containsKey(vname)) {
            PlotterDescription pd = (PlotterDescription) simParams.get(vname);
            this.temperatureFreezerPlotter = new XYPlotter(pd);
            this.temperatureFreezerPlotter.createSeries(SERIES_FREEZER);
        }
        vname = this.getURI() + ":" + SERIES_FRIDGE + PlotterDescription.PLOTTING_PARAM_NAME;
        if(simParams.containsKey(vname)) {
            PlotterDescription pd = (PlotterDescription) simParams.get(vname);
            this.temperatureFridgePlotter = new XYPlotter(pd);
            this.temperatureFridgePlotter.createSeries(SERIES_FRIDGE);
        }
        vname = this.getURI() + ":" + SERIES_POWER + PlotterDescription.PLOTTING_PARAM_NAME;
        if(simParams.containsKey(vname)) {
            PlotterDescription pd = (PlotterDescription) simParams.get(vname);
            this.powerPlotter = new XYPlotter(pd);
            this.powerPlotter.createSeries(SERIES_POWER);
        }

        // The reference to the embedding component
        vname = COMPONENT_REF;
        if(simParams.containsKey(vname)) {
            this.componentRef = (Fridge) simParams.get(vname);
        }
    }


    @Override
    public void	initialiseState(Time initialTime) {
        if(this.temperatureFreezerPlotter != null) {
            this.temperatureFreezerPlotter.initialise() ;
            this.temperatureFreezerPlotter.showPlotter() ;
        }
        if(this.temperatureFridgePlotter != null) {
            this.temperatureFridgePlotter.initialise() ;
            this.temperatureFridgePlotter.showPlotter() ;
        }
        if(this.powerPlotter != null) {
            this.powerPlotter.initialise();
            this.powerPlotter.showPlotter();
        }
        this.consumptionHasChanged = false;
        try {
            this.setDebugLevel(1) ;
        } catch (Exception e) {
            throw new RuntimeException(e) ;
        }
        super.initialiseState(initialTime) ;
    }


    @Override
    protected void initialiseVariables(Time startTime){
        if(this.powerPlotter != null) {
            this.powerPlotter.addData(
                    SERIES_POWER,
                    this.getCurrentStateTime().getSimulatedTime(),
                    this.getPower());
        }
        if(this.temperatureFreezerPlotter != null) {
            this.temperatureFreezerPlotter.addData(
                    SERIES_FREEZER,
                    this.getCurrentStateTime().getSimulatedTime(),
                    this.getFreezerTemperature());
        }
        if(this.temperatureFridgePlotter != null) {
            this.temperatureFridgePlotter.addData(
                    SERIES_FRIDGE,
                    this.getCurrentStateTime().getSimulatedTime(),
                    this.getFridgeTemperature());
        }
        this.lastPower = getPower();
        super.initialiseVariables(startTime);
    }


    @Override
    public ArrayList<EventI> output() {
        if(this.consumptionHasChanged) {
            ArrayList<EventI> ret = new ArrayList<EventI>();
            Time currentTime = this.getCurrentStateTime().add(this.getNextTimeAdvance());
            ret.add(new FridgeConsumption(currentTime, this.getPower()));
            this.consumptionHasChanged = false;
            return ret;
        } else {
            return null;
        }
    }

    @Override
    public Duration timeAdvance() {
        if(this.consumptionHasChanged) {
            return Duration.zero(this.getSimulatedTimeUnit());
        } else {
            return new Duration(1.0, TimeUnit.SECONDS);
        }
    }

    @Override
    public void userDefinedInternalTransition(Duration elapsedTime) {
        super.userDefinedInternalTransition(elapsedTime);
        if(elapsedTime.greaterThan(Duration.zero(getSimulatedTimeUnit()))){
            FState fridgeState = this.getFridgeState();
            FState freezerState = this.getFreezerState();

            //Coefficients for when the fridge or the freezer is open
            double doorFreezerOpenCoefficient = 1;
            double doorFridgeOpenCoefficient = 1;
            if(this.getFridgeDoorState() == DoorState.OPEN) {
                doorFridgeOpenCoefficient = 1.5;
            }
            if(this.getFreezerDoorState() == DoorState.OPEN) {
                doorFreezerOpenCoefficient = 2;
            }

            //Change freezer's temperature
            if(freezerState == FState.OFF || this.isFreezerOnBreak()) {
                this.currentFreezerTemperature.v =
                        Math.min(AMBIENT_TEMP, this.currentFreezerTemperature.v +
                                FREEZER_TEMP_MODIF * doorFreezerOpenCoefficient *
                                (elapsedTime.getSimulatedDuration()/timeAdvance().getSimulatedDuration()));
            } else {
                this.currentFreezerTemperature.v =
                        this.currentFreezerTemperature.v -
                        FREEZER_TEMP_MODIF * doorFreezerOpenCoefficient *
                        (elapsedTime.getSimulatedDuration()/timeAdvance().getSimulatedDuration());
            }
            if(this.temperatureFreezerPlotter != null) {
                this.temperatureFreezerPlotter.addData(
                        SERIES_FREEZER,
                        this.getCurrentStateTime().getSimulatedTime(),
                        this.getFreezerTemperature());
            }
            //Change fridge's temperature
            if(fridgeState == FState.OFF || this.isFridgeOnBreak()) {
                this.currentFridgeTemperature.v =
                        Math.min(AMBIENT_TEMP, this.currentFridgeTemperature.v +
                                FRIDGE_TEMP_MODIF * doorFridgeOpenCoefficient *
                                (elapsedTime.getSimulatedDuration()/timeAdvance().getSimulatedDuration()));
            } else {
                this.currentFridgeTemperature.v =
                        this.currentFridgeTemperature.v -
                        FRIDGE_TEMP_MODIF * doorFridgeOpenCoefficient *
                        (elapsedTime.getSimulatedDuration()/timeAdvance().getSimulatedDuration());
            }
            if(this.temperatureFridgePlotter != null) {
                this.temperatureFridgePlotter.addData(
                        SERIES_FRIDGE,
                        this.getCurrentStateTime().getSimulatedTime(),
                        this.getFridgeTemperature());
            }


            //Change fridge's state if temperature exceed thresholds
            if(fridgeState == FState.ON &&
                    this.getFridgeTemperature() <= FRIDGE_TEMP_MIN) {
                this.setFridgeState(FState.OFF);
            } else if(fridgeState == FState.OFF &&
                    this.getFridgeTemperature() >= FRIDGE_TEMP_MAX) {
                this.setFridgeState(FState.ON);
            }
            //Change freezer's state if temperature exceed thresholds
            if(freezerState == FState.ON &&
                    this.getFreezerTemperature() <= FREEZER_TEMP_MIN) {
                this.setFreezerState(FState.OFF);
            } else if(freezerState == FState.OFF &&
                    this.getFreezerTemperature() >= FREEZER_TEMP_MAX) {
                this.setFreezerState(FState.ON);
            }


            //Power consumption
            if(this.powerPlotter != null) {
                if(this.lastPower != getPower()) {
                    this.powerPlotter.addData(
                            SERIES_POWER,
                            this.getCurrentStateTime().getSimulatedTime(),
                            this.lastPower);
                }
                this.powerPlotter.addData(
                        SERIES_POWER,
                        this.getCurrentStateTime().getSimulatedTime(),
                        this.getPower());
            }
            if(this.lastPower != this.getPower()) {
                this.consumptionHasChanged = true;
                this.lastPower = this.getPower();
            }
        }
    }

    @Override
    public void	userDefinedExternalTransition(Duration elapsedTime) {
        ArrayList<EventI> currentEvents = this.getStoredEventAndReset();
        for(EventI e : currentEvents) {
            e.executeOn(this);
        }
        super.userDefinedExternalTransition(elapsedTime) ;
    }

    @Override
    public void	endSimulation(Time endTime) throws Exception {
        boolean sleep = false;
        if(this.powerPlotter != null) {
            sleep = true;
            this.powerPlotter.addData(
                    SERIES_POWER,
                    endTime.getSimulatedTime(),
                    this.getPower());
        }
        if(this.temperatureFreezerPlotter != null) {
            sleep = true;
            this.temperatureFreezerPlotter.addData(
                    SERIES_FREEZER,
                    endTime.getSimulatedTime(),
                    this.getFreezerTemperature());
        }
        if(this.temperatureFridgePlotter != null) {
            sleep = true;
            this.temperatureFridgePlotter.addData(
                    SERIES_FRIDGE,
                    endTime.getSimulatedTime(),
                    this.getFridgeTemperature());
        }
        if(sleep) {
            Thread.sleep(10000L);
        }
        if(this.temperatureFreezerPlotter != null) {
            this.temperatureFreezerPlotter.dispose();
        }
        if(this.temperatureFridgePlotter != null) {
            this.temperatureFridgePlotter.dispose();
        }
        if(this.powerPlotter != null) {
            this.powerPlotter.dispose();
        }

        super.endSimulation(endTime);
    }


    // ------------------------------------------------------------------------
    // Model-specific methods
    // ------------------------------------------------------------------------

    private DoorState getFridgeDoorState() {
        try {
            return (DoorState) this.componentRef.getEmbeddingComponentStateValue("fridge door");
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setFridgeDoor(DoorState state) {
        try {
            if(this.getFridgeDoorState() != state) {
                this.componentRef.setEmbeddingComponentStateValue("fridge door", state);
            }
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private DoorState getFreezerDoorState() {
        try {
            return (DoorState) this.componentRef.getEmbeddingComponentStateValue("freezer door");
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setFreezerDoor(DoorState state) {
        try {
            if(this.getFreezerDoorState() != state) {
                this.componentRef.setEmbeddingComponentStateValue("freezer door", state);
            }
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setFridgePower(double v) {
        this.currentFridgePower.v = v;
    }

    private void setFreezerPower(double v) {
        this.currentFreezerPower.v = v;
    }

    private double getFridgePower() {
        if(this.isFridgeOnBreak()) {
            return 0.0;
        } else {
            return this.currentFridgePower.v;
        }
    }

    private double getFreezerPower() {
        if(this.isFreezerOnBreak()) {
            return 0.0;
        } else {
            return this.currentFreezerPower.v;
        }
    }

    public double getPower() {
        return this.getFridgePower() + this.getFreezerPower();
    }

    public FState getFridgeState() {
        try {
            return (FState) this.componentRef.getEmbeddingComponentStateValue("fridge state");
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setFridgeState(FState state) {
        try {
            if(this.getFridgeState() != state) {
                this.componentRef.setEmbeddingComponentStateValue("fridge state", state);
                switch(state) {
                case OFF :
                    this.setFridgePower(0.0);
                    break ;
                case ON :
                    this.setFridgePower(FRIDGE_ON_CONSUMPTION);
                    break ;
                }
            }
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public FState getFreezerState() {
        try {
            return (FState) this.componentRef.getEmbeddingComponentStateValue("freezer state");
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setFreezerState(FState state) {
        try {
            if(this.getFreezerState() != state) {
                this.componentRef.setEmbeddingComponentStateValue("freezer state", state);
                switch(state) {
                case OFF :
                    this.setFreezerPower(0.0);
                    break ;
                case ON :
                    this.setFreezerPower(FREEZER_ON_CONSUMPTION);
                    break ;
                }
            }
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public double getFreezerTemperature() {
        return this.currentFreezerTemperature.v;
    }

    public double getFridgeTemperature() {
        return this.currentFridgeTemperature.v;
    }

    /**
     * Return true if the Fridge has to be off (order from the controller)
     * @return
     */
    private boolean isFridgeOnBreak() {
        try {
            return (boolean) this.componentRef.getEmbeddingComponentStateValue("fridge break");
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Return true if the Freezer has to be off (order from the controller)
     * @return
     */
    private boolean isFreezerOnBreak() {
        try {
            return (boolean) this.componentRef.getEmbeddingComponentStateValue("freezer break");
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
