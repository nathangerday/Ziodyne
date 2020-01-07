package simulation.models.windturbine;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.random.RandomDataGenerator;

import fr.sorbonne_u.devs_simulation.hioa.annotations.ExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOAwithDE;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.AbstractSimulationReport;
import fr.sorbonne_u.utils.PlotterDescription;
import fr.sorbonne_u.utils.XYPlotter;
import simulation.utils.DoublePiece;

public class WindModel extends AtomicHIOAwithDE{

    public static class WindReport extends AbstractSimulationReport{

        private static final long serialVersionUID = 1L ;
        public final Vector<DoublePiece> windFunction ;

        public WindReport(
                String modelURI,
                Vector<DoublePiece> windFunction
                ){
            super(modelURI) ;
            this.windFunction = windFunction;
        }

        @Override
        public String   toString()
        {
            String ret = "\n-----------------------------------------\n" ;
            ret += "Wind Report\n" ;
            ret += "-----------------------------------------\n" ;
            ret += "wind function = \n" ;
            for (int i = 0 ; i < this.windFunction.size() ; i++) {
                ret += "    " + this.windFunction.get(i) + "\n" ;
            }
            ret += "-----------------------------------------\n" ;
            return ret ;
        }
    }

    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------

    private static final long   serialVersionUID = 1L ;
    private static final String SERIES = "wind speed" ;
    public static final String  URI = "WindModel" ;

    // Run parameter names to be used when initialising them before each run
    /** name of the run parameter defining the maximum wind speed.           */
    public static final String  MAX_WIND = "max-wind" ;
    /** name of the run parameter defining the alpha parameter of the gamma
     *  probability distribution giving the wind.        */
    public static final String  WMASSF = "wind-mean-absolute-slope-scale-factor" ;
    /** name of the run parameter defining the integration step for the
     *  brownian motion followed by the bandwidth.                      */
    public static final String  WIS = "wind-integration-step" ;

    // Model implementation variables
    protected double maxWind;
    protected double windMeanAbsoluteSlopeScaleFactor;
    protected double windIntegrationStep;
    protected final RandomDataGenerator rgBrownianMotion1;
    protected final RandomDataGenerator rgBrownianMotion2;

    protected double nextWind;
    protected double nextDelay;

    protected final Vector<DoublePiece> windFunction ;
    protected XYPlotter plotter;

    /** Wind speed in m/s. */
    @ExportedVariable(type = Double.class)
    protected final Value<Double> wind = new Value<Double>(this, 7.0, 0);

    public WindModel(
            String uri,
            TimeUnit simulatedTimeUnit,
            SimulatorI simulationEngine
            ) throws Exception{
        super(uri, simulatedTimeUnit, simulationEngine) ;

        this.rgBrownianMotion1 = new RandomDataGenerator() ;
        this.rgBrownianMotion2 = new RandomDataGenerator() ;
        this.windFunction = new Vector<DoublePiece>() ;

        assert  this.wind != null ;
    }

    // ------------------------------------------------------------------------
    // Simulation protocol and related methods
    // ------------------------------------------------------------------------

    @Override
    public void setSimulationRunParameters(Map<String, Object> simParams) throws Exception{
        // Get the values of the run parameters in the map using their names
        // and set the model implementation variables accordingly
        String vname = this.getURI() + ":" + WindModel.MAX_WIND;
        this.maxWind = (double) simParams.get(vname);
        vname = this.getURI() + ":" + WindModel.WMASSF;
        this.windMeanAbsoluteSlopeScaleFactor = (double) simParams.get(vname) ;     
        vname = this.getURI() + ":" + WindModel.WIS;
        this.windIntegrationStep = (double) simParams.get(vname) ;

        // Initialise the look of the plotter
        vname = this.getURI() + ":" + PlotterDescription.PLOTTING_PARAM_NAME ;
        PlotterDescription pd = (PlotterDescription) simParams.get(vname) ;
        this.plotter = new XYPlotter(pd) ;
        this.plotter.createSeries(SERIES) ;
    }

    @Override
    public void initialiseState(Time initialTime){
        // initialisation of the random number generators
        this.rgBrownianMotion1.reSeedSecure() ;
        this.rgBrownianMotion2.reSeedSecure() ;

        // initialisation of the wind function for the report
        this.windFunction.clear() ;
        // initialisation of the wind function plotter on the screen
        if (this.plotter != null) {
            this.plotter.initialise() ;
            this.plotter.showPlotter() ;
        }

        // standard initialisation
        super.initialiseState(initialTime) ;
    }

    @Override
    protected void initialiseDerivatives(){
        this.computeDerivatives() ;
    }

    @Override
    public Duration timeAdvance(){
        return new Duration(this.nextDelay, this.getSimulatedTimeUnit()) ;
    }

    @Override
    protected void computeDerivatives(){
        double delta_t = this.windIntegrationStep ;
        double uniform1 = this.rgBrownianMotion1.nextUniform(0.0, 1.0) ;
        double quantum = -Math.log(1 - uniform1) / delta_t ;
        quantum = quantum * this.windMeanAbsoluteSlopeScaleFactor ;
        double uniform2 = this.rgBrownianMotion2.nextUniform(0.0, 1.0) ;
        double threshold =
                (this.maxWind - this.wind.v)/this.maxWind ;
        if (Math.abs(uniform2 - threshold) < 0.000001) {
            this.nextWind = this.wind.v ;
            this.nextDelay = delta_t ;
        } else if (uniform2 < threshold) {
            double limit = this.maxWind - this.wind.v ;
            if (quantum > limit) {
                this.nextWind = this.maxWind ;
                this.nextDelay = -Math.log(1 - uniform1) / quantum ;
            } else {
                this.nextWind = this.wind.v + quantum ;
                this.nextDelay = delta_t ;
            }
        } else {
            assert  uniform2 > threshold ;
            double limit = this.wind.v ;
            if (quantum > limit) {
                this.nextWind = 0.0 ;
                this.nextDelay = -Math.log(1 - uniform1) / quantum ;
            } else {
                this.nextWind = this.wind.v - quantum ;
                this.nextDelay = delta_t ;
            }
        }
    }

    @Override
    public void userDefinedInternalTransition(Duration elapsedTime){
        if (this.hasDebugLevel(1)) {
            this.logMessage("WindModel#userDefinedInternalTransition "
                    + elapsedTime) ;
        }
        if (elapsedTime.greaterThan(Duration.zero(getSimulatedTimeUnit()))) {
            super.userDefinedInternalTransition(elapsedTime) ;

            double oldWind = this.wind.v;
            this.wind.v = this.nextWind;
            this.wind.time = this.getCurrentStateTime() ;

            this.windFunction.add(
                    new DoublePiece(this.wind.time.getSimulatedTime(),
                            oldWind,
                            this.getCurrentStateTime().getSimulatedTime(),
                            this.wind.v)) ;
            if (this.plotter != null) {
                this.plotter.addData(
                        SERIES,
                        this.getCurrentStateTime().getSimulatedTime(),
                        this.wind.v) ;
            }
            this.logMessage(this.getCurrentStateTime() +
                    "|internal|wind = " + this.wind.v + " m/s") ;
        }
    }

    @Override
    public Vector<EventI> output(){
        return null ;
    }

    @Override
    public void endSimulation(Time endTime) throws Exception {
        if(this.plotter != null) {
            Thread.sleep(10000L);
            this.plotter.dispose();
        }
        super.endSimulation(endTime);
    }

    @Override
    public SimulationReportI getFinalReport() throws Exception{
        Time end = this.getSimulationEngine().getSimulationEndTime();
        this.windFunction.add(
                new DoublePiece(
                        end.getSimulatedTime(),
                        this.wind.v,
                        end.getSimulatedTime(),
                        this.wind.v));
        return new WindReport(this.getURI(), this.windFunction);
    }
}
