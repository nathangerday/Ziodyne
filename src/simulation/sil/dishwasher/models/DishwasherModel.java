package simulation.sil.dishwasher.models;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import components.Dishwasher;
import components.Dishwasher.DWMode;
import components.Dishwasher.DWState;
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
import simulation.sil.dishwasher.events.DishwasherConsumption;
import simulation.sil.dishwasher.events.DishwasherOn;

@ModelExternalEvents(imported = {DishwasherOn.class},
exported = {DishwasherConsumption.class})
public class DishwasherModel extends AtomicModel{

    private static final long serialVersionUID = 1L;

    public static class DishwasherReport extends AbstractSimulationReport{

        private static final long serialVersionUID = 1L;

        public DishwasherReport(String modelURI) {
            super(modelURI);
        }

        @Override
        public String toString(){
            return "DishwasherReport("+ this.getModelURI()+")";
        }
    }

    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------

    public static final String	URI = "SILDishwasherModel" ;
    public static final String COMPONENT_REF = URI + ":componentRef";
    private static final String	SERIES = "power" ;

    private static final double ECO_MODE_DURATION = 60;
    private static final double STANDARD_MODE_DURATION = 30;
    private static final double	ECO_MODE_CONSUMPTION = 700.0 ; // Watts
    private static final double	STANDARD_MODE_CONSUMPTION = 1600.0 ; // Watts

    private double currentPower;
    private double lastPower;
    private double endCycle;
    private boolean consumptionHasChanged;

    protected XYPlotter powerPlotter ;
    protected Dishwasher componentRef ;


    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------


    public DishwasherModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
        super(uri, simulatedTimeUnit, simulationEngine);
        this.setLogger(new StandardLogger()) ;
        //        PlotterDescription pd =
        //                new PlotterDescription(
        //                        "Dishwasher power",
        //                        "Time (sec)",
        //                        "Power (Watt)",
        //                        SimulationMain.ORIGIN_X,
        //                        SimulationMain.ORIGIN_Y + SimulationMain.getPlotterHeight(),
        //                        SimulationMain.getPlotterWidth(),
        //                        SimulationMain.getPlotterHeight()) ;
    }


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
            this.componentRef = (Dishwasher) simParams.get(vname);
        }
    }

    @Override
    public void	initialiseState(Time initialTime){
        this.currentPower = 0.0;
        this.lastPower = 0.0;
        this.endCycle = 0.0;
        this.consumptionHasChanged = false;
        if(this.powerPlotter != null) {
            this.powerPlotter.initialise();
            this.powerPlotter.showPlotter();
            this.powerPlotter.addData(
                    SERIES,
                    initialTime.getSimulatedTime(),
                    this.getPower());
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
        if(this.consumptionHasChanged) {
            ArrayList<EventI> ret = new ArrayList<EventI>();
            ret.add(new DishwasherConsumption(
                    this.getCurrentStateTime(),
                    this.getPower()));
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
            return new Duration(1.0, TimeUnit.SECONDS);
        }
    }


    @Override
    public void	userDefinedInternalTransition(Duration elapsedTime){
        super.userDefinedInternalTransition(elapsedTime);
        if(elapsedTime.greaterThan(Duration.zero(getSimulatedTimeUnit()))){
            DWState state = this.getState();
            double currentTime = this.getCurrentStateTime().getSimulatedTime();

            //Change State to OFF if the dishwasher if on break
            //or it's the end of the cycle
            if(this.isOnBreak() && state == DWState.ON ||
                    state == DWState.ON && currentTime >= endCycle) {
                this.setState(DWState.OFF);
            }

            //Plotter
            if(this.powerPlotter != null) {
                if(this.lastPower != this.getPower()) {
                    this.powerPlotter.addData(
                            SERIES,
                            currentTime,
                            this.lastPower);
                }
                this.powerPlotter.addData(
                        SERIES,
                        currentTime,
                        this.getPower());
            }

            //Check if consumption has changed
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
    public SimulationReportI getFinalReport() throws Exception{
        return new DishwasherReport(this.getURI()) ;
    }


    // ------------------------------------------------------------------------
    // Model-specific methods
    // ------------------------------------------------------------------------

    private DWState getState(){
        try {
            return (DWState) this.componentRef.getEmbeddingComponentStateValue("state");
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setState(DWState s){
        try {
            if(this.getState() != s) {
                this.componentRef.setEmbeddingComponentStateValue("state", s);
                switch(s) {
                case ON :
                    if(getMode() == DWMode.ECO) {
                        this.setPower(ECO_MODE_CONSUMPTION);
                        this.endCycle =
                                this.getCurrentStateTime().getSimulatedTime() +
                                ECO_MODE_DURATION;
                    } else if(getMode() == DWMode.STANDARD) {
                        this.setPower(STANDARD_MODE_CONSUMPTION);
                        this.endCycle =
                                this.getCurrentStateTime().getSimulatedTime() +
                                STANDARD_MODE_DURATION;
                    }
                    break;
                case OFF :
                    this.setPower(0.0);
                    break;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private DWMode getMode() {
        try {
            return (DWMode) this.componentRef.getEmbeddingComponentStateValue("mode");
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isOnBreak() {
        try {
            return (boolean) this.componentRef.getEmbeddingComponentStateValue("break");
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public double getPower(){
        return this.currentPower;
    }

    private void setPower(double v) {
        this.currentPower = v;
    }
    
    public double getTimeLeft() {
        if(this.getState() == DWState.ON) {
            return endCycle - this.getCurrentStateTime().getSimulatedTime();
        } else {
            return 0.0;
        }
    }
}
