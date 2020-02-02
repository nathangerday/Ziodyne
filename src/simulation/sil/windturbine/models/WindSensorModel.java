package simulation.sil.windturbine.models;

import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.hioa.annotations.ImportedVariable;
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
import simulation.events.common.TicEvent;
import simulation.sil.windturbine.events.WindReading;

/**
 * The class <code>WindSensorModel</code> implements a simulation model
 * for the wind providing a reading of the wind speed as a continuous variable
 *  
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 */
@ModelExternalEvents(imported = {TicEvent.class},
exported = {WindReading.class})
public class WindSensorModel extends AtomicHIOAwithEquations{

    public static class WindSensorReport extends AbstractSimulationReport{
        private static final long serialVersionUID = 1L;
        public final Vector<WindReading> readings;

        public WindSensorReport(String modelURI, Vector<WindReading> readings) {
            super(modelURI);
            this.readings = readings;
        }

        @Override
        public String toString() {
            String ret = "\n-----------------------------------------\n";
            ret += "Wind Sensor Report\n";
            ret += "-----------------------------------------\n";
            ret += "number of readings = " + this.readings.size() + "\n";
            ret += "Readings:\n";
            for (int i = 0; i < this.readings.size(); i++) {
                ret += "    " + this.readings.get(i).eventAsString() + "\n";
            }
            ret += "-----------------------------------------\n";
            return ret;
        }
    }

    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------

    private static final long serialVersionUID = 1L;
    private static final String SERIES = "wind speed";
    public static final String URI = "SILWindSensorModel";

    /** true when a external event triggered a reading.                     */
    private boolean triggerReading;
    /** the last value emitted as a reading of the wind speed.              */
    private double lastReading;
    /** the simulation time at the last reading.                            */
    private double lastReadingTime;
    /** history of readings, for the simulation report.                     */
    private final Vector<WindReading> readings;

    /** frame used to plot the wind speed readings during the simulation.    */
    private XYPlotter plotter;

    /** Wind speed in m/s                         */
    @ImportedVariable(type = Double.class)
    protected Value<Double> wind;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
    
    /**
     * Creates a WindSensorModel
     * @param uri uri of the model
     * @param simulatedTimeUnit timeunit of the simulation
     * @param simulationEngine engine for the simulation
     * @throws Exception
     */
    public WindSensorModel(
            String uri,
            TimeUnit simulatedTimeUnit,
            SimulatorI simulationEngine
            ) throws Exception{
        super(uri, simulatedTimeUnit, simulationEngine);

        this.lastReading = -1.0;
        this.readings = new Vector<WindReading>();
        this.setLogger(new StandardLogger());
    }

    @Override
    public void setSimulationRunParameters(Map<String, Object> simParams) throws Exception{
        String vname = this.getURI() + ":" + PlotterDescription.PLOTTING_PARAM_NAME;
        if(simParams.containsKey(vname)) {
            PlotterDescription pd = (PlotterDescription) simParams.get(vname);
            this.plotter = new XYPlotter(pd);
            this.plotter.createSeries(SERIES);
        }
    }

    @Override
    public void initialiseState(Time initialTime) {
        this.triggerReading = false;
        this.lastReadingTime = initialTime.getSimulatedTime();
        this.readings.clear();
        if (this.plotter != null) {
            this.plotter.initialise();
            this.plotter.showPlotter();
        }
        try {
            this.setDebugLevel(1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        super.initialiseState(initialTime);
    }

    @Override
    public Duration timeAdvance() {
        if (this.triggerReading) {
            return Duration.zero(this.getSimulatedTimeUnit());
        } else {
            return Duration.INFINITY;
        }
    }

    @Override
    public ArrayList<EventI> output() {
        if (this.triggerReading) {
            if (this.plotter != null) {
                this.plotter.addData(SERIES,this.lastReadingTime,this.wind.v);
                this.plotter.addData(SERIES,this.getCurrentStateTime().getSimulatedTime(),this.wind.v);
            }
            this.lastReading = this.wind.v;
            this.lastReadingTime = this.getCurrentStateTime().getSimulatedTime();
            ArrayList<EventI> ret = new ArrayList<EventI>(1);
            Time currentTime = this.getCurrentStateTime().add(this.getNextTimeAdvance());
            WindReading wr = new WindReading(currentTime, this.wind.v);
            ret.add(wr);
            this.readings.addElement(wr);
            this.triggerReading = false;
            return ret;
        } else {
            return null;
        }
    }

    @Override
    public void userDefinedExternalTransition(Duration elapsedTime) {
        this.triggerReading = true;
        super.userDefinedExternalTransition(elapsedTime);
    }

    @Override
    public void endSimulation(Time endTime) throws Exception{
        if (this.plotter != null) {
            this.plotter.addData(SERIES,
                    endTime.getSimulatedTime(),
                    this.lastReading);
            Thread.sleep(10000L);
            this.plotter.dispose();
        }
        super.endSimulation(endTime);
    }

    @Override
    public SimulationReportI getFinalReport() throws Exception{
        return new WindSensorReport(this.getURI(), this.readings);
    }
}
