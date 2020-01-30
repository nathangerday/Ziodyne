package simulation.sil.windturbine.models;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import components.WindTurbine;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOAwithEquations;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
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

@ModelExternalEvents(imported = {WindReading.class},
exported = {WindTurbineProduction.class})
public class WindTurbineModel extends AtomicHIOAwithEquations {

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
    private static final double R = 2;
    private static final double COEFF = 0.5 * RHO * R * R * Math.PI;

    /** Puissance en Watt
     * P = 0.5 * RHO * S * v**3 
     * RHO : masse volumique de l'air (kg/m^3)
     * S : surface balayee par les pales (m^2)(rayon**2 * pi)
     * v : vitesse du vent (m/s) */
    private final Value<Double> currentPower = new Value<Double>(this, 0.0, 0);

    private boolean productionHasChanged;
    private double maxSpeed;
    private double minSpeed;
    private double speed;


    private XYPlotter powerPlotter;
    private WindTurbine componentRef;




    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

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
        if(this.powerPlotter != null) {
            this.powerPlotter.initialise();
            this.powerPlotter.showPlotter();
        }
        this.productionHasChanged = false;
        try {
            this.setDebugLevel(1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        super.initialiseState(initialTime);
    }


    @Override
    protected void initialiseVariables(Time startTime){
        this.speed = 0;
        this.currentPower.v = 0.0;
        if(this.powerPlotter != null) {
            //First dot
            this.powerPlotter.addData(
                    SERIES,
                    this.getCurrentStateTime().getSimulatedTime(),
                    this.getPower());
        }
        super.initialiseVariables(startTime);
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
        return Duration.INFINITY;
    }


    @Override
    public void userDefinedInternalTransition(Duration elapsedTime) {
        if(elapsedTime.greaterThan(Duration.zero(getSimulatedTimeUnit()))) {
            if(this.isOnBreak() && this.getPower() != 0) {
                setPower(0);
            }else if(!this.isOnBreak() && this.isOn()) {
                setPower(COEFF * Math.pow(speed, 3));
            }
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
                    this.setPower(0);
                }else {
                    this.setPower(COEFF * Math.pow(speed, 3));
                }
            }
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setPower(double v) {
        if(this.currentPower.v != v) {
            this.currentPower.v = v;
            this.productionHasChanged = true;
        }
        this.powerPlotter.addData(
                SERIES,
                this.getCurrentStateTime().getSimulatedTime(),
                this.getPower()
                );
    }

    public double getPower() {
        return this.currentPower.v;
    }

    private void checkSpeed() {
        boolean on = this.isOn();
        if(!on && speed >= minSpeed && speed <= maxSpeed) {
            this.setState(true);
        }else if(on && (speed < minSpeed || speed > maxSpeed)) {
            this.setState(false);
        }
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double s) {
        this.speed = s;
        this.checkSpeed();
    }

    /**
     * Return true if the wind turbine has to be off (order from the controller/user)
     * @return
     */
    private boolean isOnBreak() {
        try {
            return (boolean) this.componentRef.getEmbeddingComponentStateValue("break");
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
