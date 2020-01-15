package simulation.models.dishwasher;

import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentStateAccessI;
import fr.sorbonne_u.devs_simulation.examples.molene.SimulationMain;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ExportedVariable;
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
import simulation.events.dishwasher.AbstractDishwasherEvent;
import simulation.events.dishwasher.ModeEco;
import simulation.events.dishwasher.ModeStandard;
import simulation.events.dishwasher.DishwasherOn;
import simulation.events.dishwasher.DishwasherOff;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;


// -----------------------------------------------------------------------------
@ModelExternalEvents(imported = {ModeEco.class,
        ModeStandard.class,
        DishwasherOn.class,
        DishwasherOff.class})
// -----------------------------------------------------------------------------
public class DishwasherModel extends AtomicHIOAwithEquations {

    private static final long serialVersionUID = 1L;

    public enum State{ ON,OFF,ECO, STD}

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

    public static final String	URI = "DishwasherModel" ;
    private static final String	SERIES = "power" ;

    protected static final double	ECO_MODE_CONSUMPTION = 700.0 ; // Watts
    protected static final double	STANDARD_MODE_CONSUMPTION = 1600.0 ; // Watts

    @ExportedVariable(type = Double.class)
    protected final Value<Double> currentPower = new Value<Double>(this, 0.0, 0) ;
    protected State	currentState ;
    protected XYPlotter powerPlotter ;
    protected EmbeddingComponentStateAccessI componentRef ;

    public DishwasherModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
        super(uri, simulatedTimeUnit, simulationEngine);

        PlotterDescription pd =
                new PlotterDescription(
                        "Dishwasher power",
                        "Time (sec)",
                        "Power (Watt)",
                        SimulationMain.ORIGIN_X,
                        SimulationMain.ORIGIN_Y + SimulationMain.getPlotterHeight(),
                        SimulationMain.getPlotterWidth(),
                        SimulationMain.getPlotterHeight()) ;

        this.powerPlotter = new XYPlotter(pd) ;
        this.powerPlotter.createSeries(SERIES) ;

        this.setLogger(new StandardLogger()) ;
    }

    @Override
    public void	setSimulationRunParameters(Map<String, Object> simParams) throws Exception
    {

        this.componentRef =
                (EmbeddingComponentStateAccessI) simParams.get("componentRef") ;
    }

    @Override
    public void	initialiseState(Time initialTime)
    {
        this.currentState = DishwasherModel.State.OFF ;

        this.powerPlotter.initialise() ;
        this.powerPlotter.showPlotter() ;

        try {
            this.setDebugLevel(1) ;
        } catch (Exception e) {
            throw new RuntimeException(e) ;
        }

        super.initialiseState(initialTime) ;
    }


    @Override
    protected void		initialiseVariables(Time startTime)
    {
        this.currentPower.v = 0.0 ;

        this.powerPlotter.addData(
                SERIES,
                this.getCurrentStateTime().getSimulatedTime(),
                this.getPower());

        super.initialiseVariables(startTime);
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
            this.logMessage("DiishwasherModel::userDefinedExternalTransition 1");
        }
        Vector<EventI> currentEvents = this.getStoredEventAndReset();
        assert currentEvents != null && currentEvents.size() == 1;

        Event ce = (Event) currentEvents.get(0);
        assert ce instanceof AbstractDishwasherEvent;
        if (this.hasDebugLevel(2)) {
            this.logMessage("DishwasherModel::userDefinedExternalTransition 2 "
                    + ce.getClass().getCanonicalName());
        }

        this.powerPlotter.addData(
                SERIES,
                this.getCurrentStateTime().getSimulatedTime(),
                this.getPower());

        if (this.hasDebugLevel(2)) {
            this.logMessage("DishwasherModel::userDefinedExternalTransition 3 "
                    + this.getState());
        }

        ce.executeOn(this) ;

        if (this.hasDebugLevel(2)) {
            this.logMessage("DishwasherModel::userDefinedExternalTransition 4 "
                    + this.getState()) ;
        }

        this.powerPlotter.addData(
                SERIES,
                this.getCurrentStateTime().getSimulatedTime(),
                this.getPower());

        super.userDefinedExternalTransition(elapsedTime) ;
        if (this.hasDebugLevel(2)) {
            this.logMessage("DishwasherModel::userDefinedExternalTransition 5") ;
        }
    }

    @Override
    public void	endSimulation(Time endTime) throws Exception {
        this.powerPlotter.addData(
                SERIES,
                endTime.getSimulatedTime(),
                this.getPower());
        Thread.sleep(10000L);
        this.powerPlotter.dispose();

        super.endSimulation(endTime);
    }

    @Override
    public SimulationReportI getFinalReport() throws Exception
    {
        return new DishwasherReport(this.getURI()) ;
    }


    // ------------------------------------------------------------------------
    // Model-specific methods
    // ------------------------------------------------------------------------

    public void			setState(State s)
    {
        this.currentState = s ;
        switch (s){
        case OFF :
            this.currentPower.v = 0.0 ;
            break ;
        case ECO :
            this.currentPower.v = ECO_MODE_CONSUMPTION;
            break ;
        case STD :
            this.currentPower.v = STANDARD_MODE_CONSUMPTION;
            break;
        case ON :
            break;
        }
    }

    public State getState(){
        return this.currentState ;
    }

    public double getPower()
    {
        return this.currentPower.v ;
    }

}
