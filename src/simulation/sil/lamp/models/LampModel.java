package simulation.sil.lamp.models;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import components.Lamp;
import components.Lamp.State;   
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOAwithEquations;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
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
import simulation.sil.lamp.events.AbstractLampEvent;
import simulation.sil.lamp.events.LampConsumption;
import simulation.sil.lamp.events.LampHigh;
import simulation.sil.lamp.events.LampLow;
import simulation.sil.lamp.events.LampMedium;
import simulation.sil.lamp.events.LampOff;

@ModelExternalEvents(imported = {
        LampOff.class,
        LampLow.class,
        LampMedium.class,
        LampHigh.class})
public class LampModel 	extends AtomicHIOAwithEquations {

    private static final long serialVersionUID = 1L;
    public static final String  URI = "SILLampModel";
    private static final String SERIES = "power";

    public static class LampReport extends AbstractSimulationReport{

        private static final long serialVersionUID = 1L;

        public LampReport(String modelURI) {
            super(modelURI);
        }

        @Override
        public String toString(){
            return "LampReport("+ this.getModelURI()+")";
        }
    }


    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------
    
    public static final String COMPONENT_REF = URI + ":componentRef";

    /** energy consumption (in Watts) of the lamp in LOW mode.		*/
    private static final double LOW_MODE_CONSUMPTION = 20.0; // Watts
    /** energy consumption (in Watts) of the lamp in MEDIUM mode.		*/
    private static final double MEDIUM_MODE_CONSUMPTION = 40.0; // Watts
    /** energy consumption (in Watts) of the lamp in HIGH mode.		*/
    private static final double HIGH_MODE_CONSUMPTION = 60.0; // Watts

    /** current power in watts.			*/
    private final Value<Double> currentPower = new Value<Double>(this, 0.0, 0);
    private boolean consumptionHasChanged;

    /** plotter for the power level over time.							*/
    protected XYPlotter powerPlotter;
    /** reference on the object representing the component that holds the
     *  model; enables the model to access the state of this component.		*/
    protected Lamp componentRef;


    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public LampModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
        super(uri, simulatedTimeUnit, simulationEngine);
        this.setLogger(new StandardLogger());
        //        PlotterDescription pd =
        //                new PlotterDescription(
        //                        "Lamp Power",
        //                        "Time (sec)",
        //                        "Power (Watt)",
        //                        SimulationMain.ORIGIN_X,
        //                        SimulationMain.ORIGIN_Y + SimulationMain.getPlotterHeight(),
        //                        SimulationMain.getPlotterWidth(),
        //                        SimulationMain.getPlotterHeight());
    }


    // -------------------------------------------------------------------------
    // Simulation's methods
    // -------------------------------------------------------------------------

    @Override
    public void	setSimulationRunParameters(Map<String, Object> simParams) throws Exception{
        String vname = this.getURI() + ":" + PlotterDescription.PLOTTING_PARAM_NAME;
        if(simParams.containsKey(vname)) {
            PlotterDescription pd = (PlotterDescription) simParams.get(vname);
            this.powerPlotter = new XYPlotter(pd);
            this.powerPlotter.createSeries(SERIES);
        }

        // The reference to the embedding component
        vname = COMPONENT_REF;
        if(simParams.containsKey(vname)) {
            this.componentRef = (Lamp) simParams.get(vname);
        }
    }

    @Override
    public void	initialiseState(Time initialTime) {
        if(this.powerPlotter != null) {
            this.powerPlotter.initialise();
            this.powerPlotter.showPlotter();
        }
        this.consumptionHasChanged = false;
        try {
            this.setDebugLevel(1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        super.initialiseState(initialTime);
    }


    @Override
    protected void initialiseVariables(Time startTime){
        try {
            State s = (State) this.componentRef.getEmbeddingComponentStateValue("state");
            switch(s) {
            case OFF:
                this.currentPower.v = 0.0;
                break;
            case LOW:
                this.currentPower.v = LOW_MODE_CONSUMPTION;
                break;
            case MEDIUM:
                this.currentPower.v = MEDIUM_MODE_CONSUMPTION;
                break;
            case HIGH:
                this.currentPower.v = HIGH_MODE_CONSUMPTION;
                break;
            }
        }catch (Exception e) {
            throw new RuntimeException(e);
        }

        //First dot in the plotter
        if(this.powerPlotter != null) {
            this.powerPlotter.addData(
                    SERIES,
                    this.getCurrentStateTime().getSimulatedTime(),
                    this.getPower());
        }
        super.initialiseVariables(startTime);
    }


    @Override
    public ArrayList<EventI> output() {
        if (this.consumptionHasChanged) {
            ArrayList<EventI> ret = new ArrayList<EventI>();
            Time currentTime = this.getCurrentStateTime().add(this.getNextTimeAdvance());
            ret.add(new LampConsumption(currentTime, this.getPower()));
            this.consumptionHasChanged = false;
            return ret;
        } else {
            return null;
        }
    }

    @Override
    public Duration timeAdvance() {
        if (this.consumptionHasChanged) {
            return Duration.zero(this.getSimulatedTimeUnit());
        } else {
            return Duration.INFINITY;
        }
    }


    @Override
    public void	userDefinedExternalTransition(Duration elapsedTime) {
        ArrayList<EventI> currentEvents = this.getStoredEventAndReset();
        assert currentEvents != null && currentEvents.size() == 1;
        Event ce = (Event) currentEvents.get(0);
        assert ce instanceof AbstractLampEvent;

        double last = this.getPower();
        ce.executeOn(this);

        if(last != this.getPower()) {
            this.consumptionHasChanged = true;
        }

        super.userDefinedExternalTransition(elapsedTime);
    }


    @Override
    public void	endSimulation(Time endTime) throws Exception {
        if(this.powerPlotter != null) {
            this.powerPlotter.addData(
                    SERIES,
                    endTime.getSimulatedTime(),
                    this.getPower());
            Thread.sleep(10000L);
            this.powerPlotter.dispose();
        }

        super.endSimulation(endTime);
    }

    @Override
    public SimulationReportI getFinalReport() throws Exception {
        return new LampReport(this.getURI());
    }


    // ------------------------------------------------------------------------
    // Model-specific methods
    // ------------------------------------------------------------------------

    public void	setState(State s){
        try {
            if(s != this.getState()) {
                this.componentRef.setEmbeddingComponentStateValue("state", s);
                switch (s){
                case OFF :
                    this.setPower(0);
                    break;
                case LOW :
                    this.setPower(LOW_MODE_CONSUMPTION);
                    break;
                case MEDIUM :
                    this.setPower(MEDIUM_MODE_CONSUMPTION);
                    break;
                case HIGH :
                    this.setPower(HIGH_MODE_CONSUMPTION);
                    break;
                }
            }
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public State getState() {
        try {
            return (State) this.componentRef.getEmbeddingComponentStateValue("state");
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public void setPower(double v) {
        if(powerPlotter != null) {
            this.powerPlotter.addData(
                    SERIES,
                    this.getCurrentStateTime().getSimulatedTime(),
                    this.getPower());
        }
        this.currentPower.v = v;
        if(powerPlotter != null) {
            this.powerPlotter.addData(
                    SERIES,
                    this.getCurrentStateTime().getSimulatedTime(),
                    this.getPower());
        }
    }


    public double getPower(){
        return this.currentPower.v;
    }
}
