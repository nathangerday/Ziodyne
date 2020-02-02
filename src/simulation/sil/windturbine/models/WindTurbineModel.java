package simulation.sil.windturbine.models;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import components.WindTurbine;
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
import simulation.sil.windturbine.events.WindReading;
import simulation.sil.windturbine.events.WindTurbineProduction;


/**
 * The class <code>WindTurbineModel</code> implements a simulation model
 * for a windturbine providing the production as a continuous variable
 *  
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 */
@ModelExternalEvents(imported = {WindReading.class},
exported = {WindTurbineProduction.class})
public class WindTurbineModel extends AtomicModel {

    private static final long serialVersionUID = 1L;

    public static class WindTurbineReport extends AbstractSimulationReport{
        private static final long serialVersionUID = 1L;

        public WindTurbineReport(String modelURI) {
            super(modelURI);
        }

        @Override
        public String toString() {
            return "WindReport("+ this.getModelURI()+")";
        }
    }

    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------

    public static final String  URI = "SILWindTurbineModel";
    private static final String SERIES = "power";
    public static final String COMPONENT_REF = URI + ":componentRef";
    public static final String MAX_SPEED = "max-speed";
    public static final String MIN_SPEED = "min-speed";

    private static final double RHO = 1.23;
    private static final double R = 0.8;
    private static final double COEFF = 0.5 * RHO * R * R * Math.PI;

    /** Puissance en Watt
     * P = 0.5 * RHO * S * v**3 
     * RHO : masse volumique de l'air (kg/m^3)
     * S : surface balayee par les pales (m^2)(rayon**2 * pi)
     * v : vitesse du vent (m/s) */
    private double currentPower;

    private boolean productionHasChanged;
    private double lastPower;
    private double maxSpeed;
    private double minSpeed;
    private double speed;

    /** plotter for the power level over time.                          */
    private XYPlotter powerPlotter;
    private WindTurbine componentRef;


    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------
    
    /**
     * Creates a WindTurbineModel
     * @param uri uri of the model
     * @param simulatedTimeUnit timeunit of the simulation
     * @param simulationEngine engine for the simulation
     * @throws Exception
     */
    public WindTurbineModel(
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
        String vname = this.getURI() + ":" + PlotterDescription.PLOTTING_PARAM_NAME;
        if(simParams.containsKey(vname)) {
            PlotterDescription pd = (PlotterDescription) simParams.get(vname);
            this.powerPlotter = new XYPlotter(pd);
            this.powerPlotter.createSeries(SERIES);
        }
        vname = this.getURI() + ":" + MAX_SPEED;
        if (simParams.containsKey(vname)) {
            this.maxSpeed = (Double) simParams.get(vname) ;
        }
        vname = this.getURI() + ":" + MIN_SPEED;
        if (simParams.containsKey(vname)) {
            this.minSpeed = (Double) simParams.get(vname) ;
        }

        // The reference to the embedding component
        vname = COMPONENT_REF;
        if(simParams.containsKey(vname)) {
            this.componentRef = (WindTurbine) simParams.get(vname);
        }
    }


    @Override
    public void initialiseState(Time initialTime) {
        this.currentPower = 0.0;
        this.productionHasChanged = false;
        this.lastPower = 0.0;
        this.speed = 0.0;
        if(this.powerPlotter != null) {
            this.powerPlotter.initialise();
            this.powerPlotter.showPlotter();
            this.powerPlotter.addData(
                    SERIES,
                    initialTime.getSimulatedTime(),
                    this.getPower());
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
        if (this.productionHasChanged) {
            ArrayList<EventI> ret = new ArrayList<EventI>();
            Time currentTime = this.getCurrentStateTime().add(this.getNextTimeAdvance());
            ret.add(new WindTurbineProduction(currentTime, this.getPower()));
            this.productionHasChanged = false;
            return ret;
        } else {
            return null;
        }
    }


    @Override
    public Duration timeAdvance() {
        if (this.productionHasChanged) {
            return Duration.zero(this.getSimulatedTimeUnit());
        } else {
            return new Duration(2.0, TimeUnit.SECONDS);
        }
    }


    @Override
    public void userDefinedInternalTransition(Duration elapsedTime) {
        if(this.isOnBreak()) {
            if(this.isOn()) {
                this.setState(false);
            }
        } else {
            this.checkSpeed();
            if(this.isOn()) {
                this.setPower(COEFF * Math.pow(speed, 3));
            }
        }
        if(this.powerPlotter != null) {
            this.powerPlotter.addData(
                    SERIES,
                    this.getCurrentStateTime().getSimulatedTime(),
                    this.getPower()
                    );
        }
        if(this.lastPower != this.getPower()) {
            this.lastPower = this.getPower();
            this.productionHasChanged = true;
        }

        super.userDefinedInternalTransition(elapsedTime);
    }


    @Override
    public void userDefinedExternalTransition(Duration elapsedTime) {
        ArrayList<EventI> currentEvents = this.getStoredEventAndReset();
        for(EventI e : currentEvents) {
            e.executeOn(this);
        }
        super.userDefinedExternalTransition(elapsedTime);
    }


    @Override
    public void endSimulation(Time endTime) throws Exception {
        this.powerPlotter.addData(
                SERIES,
                endTime.getSimulatedTime(),
                this.getPower());
        Thread.sleep(10000L);
        this.powerPlotter.dispose();
        super.endSimulation(endTime);
    }


    @Override
    public SimulationReportI getFinalReport() throws Exception {
        return new WindTurbineReport(this.getURI());
    }


    // ------------------------------------------------------------------------
    // Model-specific methods
    // ------------------------------------------------------------------------

    private boolean isOn() {
        try {
            return (boolean) this.componentRef.getEmbeddingComponentStateValue("state");
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setState(boolean state) {
        try {
            if(this.isOn() != state) {
                this.componentRef.setEmbeddingComponentStateValue("state", state);
                if(state) {
                    this.setPower(COEFF * Math.pow(speed, 3));
                }else {
                    this.setPower(0);
                }
            }
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setPower(double v) {
        this.currentPower = v;
    }

    private double getPower() {
        return this.currentPower;
    }

    public void setSpeed(double s) {
        this.speed = s;
    }

    public double getSpeed() {
        return speed;
    }

    private void checkSpeed() {
        boolean on = this.isOn();
        if(!on && speed >= minSpeed && speed <= maxSpeed) {
            this.setState(true);
        }else if(on && (speed < minSpeed || speed > maxSpeed)) {
            this.setState(false);
        }
    }

    private boolean isOnBreak() {
        try {
            return (boolean) this.componentRef.getEmbeddingComponentStateValue("break");
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
