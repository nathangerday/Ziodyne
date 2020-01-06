package simulation.models.windturbine;

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
import fr.sorbonne_u.utils.PlotterDescription;
import fr.sorbonne_u.utils.XYPlotter;
import simulation.events.windturbine.TicEvent;
import simulation.events.windturbine.WindReading;

@ModelExternalEvents(imported = {TicEvent.class},
exported = {WindReading.class})

public class WindSensorModel extends AtomicHIOAwithEquations{

    public static class WindSensorReport extends AbstractSimulationReport {
        private static final long serialVersionUID = 1L ;
        public final Vector<WindReading>   readings ;

        public WindSensorReport(String modelURI, Vector<WindReading> readings){
            super(modelURI);
            this.readings = readings ;
        }

        @Override
        public String toString(){
            String ret = "\n-----------------------------------------\n" ;
            ret += "Wind Sensor Report\n" ;
            ret += "-----------------------------------------\n" ;
            ret += "number of readings = " + this.readings.size() + "\n" ;
            ret += "Readings:\n" ;
            for (int i = 0 ; i < this.readings.size() ; i++) {
                ret += "    " + this.readings.get(i).eventAsString() + "\n" ;
            }
            ret += "-----------------------------------------\n" ;
            return ret ;
        }
    }

    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------

    private static final long serialVersionUID = 1L ;
    private static final String SERIES = "wind speed" ;
    public static final String URI = "windSensorModel-1" ;

    /** true when a external event triggered a reading.                     */
    protected boolean triggerReading ;
    /** the last value emitted as a reading of the wind speed.              */
    protected double lastReading;
    /** the simulation time at the last reading.                            */
    protected double lastReadingTime ;
    /** history of readings, for the simulation report.                     */
    protected final Vector<WindReading> readings ;

    /** frame used to plot the wind speed readings during the simulation.    */
    protected XYPlotter plotter ;

    /** Wind speed in m/s                         */
    @ImportedVariable(type = Double.class)
    protected Value<Double> wind;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public WindSensorModel(
            String uri,
            TimeUnit simulatedTimeUnit,
            SimulatorI simulationEngine
            ) throws Exception{
        super(uri, simulatedTimeUnit, simulationEngine) ;

        // Model implementation variable initialisation
        this.lastReading = -1.0 ;

        // Create the representation of the sensor wind function
        this.readings = new Vector<WindReading>() ;
    }

    @Override
    public void setSimulationRunParameters(Map<String, Object> simParams) throws Exception{
        String vname = this.getURI() + ":" + PlotterDescription.PLOTTING_PARAM_NAME ;
        // Initialise the look of the plotter
        PlotterDescription pd = (PlotterDescription) simParams.get(vname) ;
        this.plotter = new XYPlotter(pd) ;
        this.plotter.createSeries(SERIES) ;
    }

    @Override
    public void initialiseState(Time initialTime){
        this.triggerReading = false;

        this.lastReadingTime = initialTime.getSimulatedTime();
        this.readings.clear();
        if (this.plotter != null) {
            this.plotter.initialise();
            this.plotter.showPlotter();
        }

        super.initialiseState(initialTime);
    }

    @Override
    public Duration timeAdvance(){
        if (this.triggerReading){
            return Duration.zero(this.getSimulatedTimeUnit()) ;
        } else {
            return Duration.INFINITY ;
        }
    }

    @Override
    public Vector<EventI> output(){
        if (this.triggerReading) {
            if (this.plotter != null) {
                this.plotter.addData(SERIES,this.lastReadingTime,this.wind.v);
                this.plotter.addData(SERIES,this.getCurrentStateTime().getSimulatedTime(),this.wind.v);
            }
            this.lastReading = this.wind.v ;
            this.lastReadingTime = this.getCurrentStateTime().getSimulatedTime() ;

            Vector<EventI> ret = new Vector<EventI>(1) ;
            Time currentTime = this.getCurrentStateTime().add(this.getNextTimeAdvance()) ;
            WindReading wr = new WindReading(currentTime, this.wind.v) ;
            ret.add(wr) ;

            this.readings.addElement(wr) ;
            this.logMessage(this.getCurrentStateTime() +
                    "|output|wind speed reading " +
                    this.readings.size() + " with value = " +
                    this.wind.v) ;

            this.triggerReading = false ;
            return ret ;
        } else {
            return null ;
        }
    }

    @Override
    public void userDefinedInternalTransition(Duration elapsedTime){
        super.userDefinedInternalTransition(elapsedTime) ;
        this.logMessage(this.getCurrentStateTime() +
                "|internal|wind speed = " +
                this.wind.v + " m/s.") ;
    }

    @Override
    public void userDefinedExternalTransition(Duration elapsedTime)
    {
        super.userDefinedExternalTransition(elapsedTime);
        Vector<EventI> current = this.getStoredEventAndReset() ;
        boolean ticReceived = false ;
        for (int i = 0 ; !ticReceived && i < current.size() ; i++) {
            if (current.get(i) instanceof TicEvent) {
                ticReceived = true ;
            }
        }
        if (ticReceived) {
            this.triggerReading = true ;
            this.logMessage(this.getCurrentStateTime() +
                    "|external|tic event received.") ;
        }
    }

    @Override
    public void endSimulation(Time endTime) throws Exception{
        if (this.plotter != null) {
            this.plotter.addData(SERIES,
                    endTime.getSimulatedTime(),
                    this.lastReading) ;
        }

        super.endSimulation(endTime);
    }

    @Override
    public SimulationReportI getFinalReport() throws Exception{
        return new WindSensorReport(this.getURI(), this.readings) ;
    }
}
