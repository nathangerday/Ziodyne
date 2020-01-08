package simulation.models.battery;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.examples.molene.bsm.BatteryLevel;
import fr.sorbonne_u.devs_simulation.examples.molene.pcsm.PortableComputerStateModel;
import fr.sorbonne_u.devs_simulation.examples.molene.tic.TicEvent;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ImportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOAwithEquations;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.AbstractSimulationReport;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import fr.sorbonne_u.utils.PlotterDescription;
import fr.sorbonne_u.utils.XYPlotter;

public class BatterySensorModel extends AtomicHIOAwithEquations{



	public static class	BatterySensorReport
	extends		AbstractSimulationReport
	{
		private static final long				serialVersionUID = 1L ;
		protected final Vector<BatteryLevel>	readings ;

		public			BatterySensorReport(
			String modelURI,
			Vector<BatteryLevel> readings
			)
		{
			super(modelURI) ;

			this.readings = readings ;
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String	toString()
		{
			String ret = "\n-----------------------------------------\n" ;
			ret += "Battery Level Sensor Report\n" ;
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

	private static final long				serialVersionUID = 1L ;
	/** an URI to be used when create an instance of the model.				*/
	public static final String				URI = "BatterySensorModel" ;

	/** true when a external event triggered a reading.						*/
	protected boolean						triggerReading ;
	/** the last value emitted as a reading of the battery level.		 	*/
	protected double						lastReading ;
	/** the simulation time at the last reading.							*/
	protected double						lastReadingTime ;
	/** history of readings, for the simulation report.						*/
	protected final Vector<BatteryLevel>	readings ;

	/** frame used to plot the battery level readings during the
	 *  simulation.															*/
	protected XYPlotter						plotter ;

	// -------------------------------------------------------------------------
	// HIOA model variables
	// -------------------------------------------------------------------------

	/** the continuous variable imported from the PC model.					*/
	@ImportedVariable(type = Double.class)
	protected Value<Double>					remainingCapacity ;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * creating a battery level sensor model instance.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	simulatedTimeUnit != null
	 * pre	simulationEngine == null ||
	 * 		    	simulationEngine instanceof HIOA_AtomicEngine
	 * post	this.getURI() != null
	 * post	uri != null implies this.getURI().equals(uri)
	 * post	this.getSimulatedTimeUnit().equals(simulatedTimeUnit)
	 * post	simulationEngine != null implies
	 * 			this.getSimulationEngine().equals(simulationEngine)
	 * </pre>
	 *
	 * @param uri					unique identifier of the model.
	 * @param simulatedTimeUnit		time unit used for the simulation clock.
	 * @param simulationEngine		simulation engine enacting the model.
	 * @throws Exception				<i>todo.</i>
	 */
	public				BatterySensorModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		SimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine) ;

		this.setLogger(new StandardLogger()) ;
		this.readings = new Vector<BatteryLevel>() ;
		this.lastReading = -1.0 ;
	}

	// -------------------------------------------------------------------------
	// Simulation protocol and related methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#setSimulationRunParameters(java.util.Map)
	 */
	@Override
	public void			setSimulationRunParameters(
		Map<String, Object> simParams
		) throws Exception
	{
		String vname =
			this.getURI() + ":" + PlotterDescription.PLOTTING_PARAM_NAME ;
		PlotterDescription pd = (PlotterDescription) simParams.get(vname) ;
		this.plotter = new XYPlotter(pd) ;
		this.plotter.createSeries("standard") ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			initialiseState(Time initialTime)
	{
		this.triggerReading = false ;
		this.lastReadingTime = initialTime.getSimulatedTime() ;
		this.readings.clear() ;
		if (this.plotter != null) {
			this.plotter.initialise() ;
			this.plotter.showPlotter() ;
		}

		super.initialiseState(initialTime) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseVariables(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	protected void		initialiseVariables(Time startTime)
	{
		this.remainingCapacity.v = PortableComputerStateModel.INITIAL_CAPACITY ;

		super.initialiseVariables(startTime);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#timeAdvance()
	 */
	@Override
	public Duration		timeAdvance()
	{
		if (this.triggerReading) {
			// immediate internal event when a reading is triggered.
			return Duration.zero(this.getSimulatedTimeUnit()) ;
		} else {
			return Duration.INFINITY ;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI#output()
	 */
	@Override
	public Vector<EventI>	output()
	{
		if (this.triggerReading) {
			// Plotting, plays no role in the simulation
			if (this.plotter != null) {
				this.plotter.addData(
						"standard",
						this.lastReadingTime,
						this.remainingCapacity.v) ;
				this.plotter.addData(
						"standard",
						this.getCurrentStateTime().getSimulatedTime(),
						this.remainingCapacity.v) ;
			}
			// Memorise a new last reading
			this.lastReading = this.remainingCapacity.v ;
			this.lastReadingTime =
					this.getCurrentStateTime().getSimulatedTime() ;

			// Create and emit the battery level event.
			Vector<EventI> ret = new Vector<EventI>(1) ;
			Time t = this.getCurrentStateTime().add(this.getNextTimeAdvance()) ;
			BatteryLevel bl = new BatteryLevel(t, this.remainingCapacity.v) ;
			ret.add(bl) ;

			// Memorise the reading for the simulation report.
			this.readings.add(bl) ;
			// Trace the execution
			this.logMessage(this.getCurrentStateTime() +
					"|output|battery reading " +
					this.readings.size() + " with value = " +
					this.remainingCapacity.v) ;

			// The reading that was triggered has now been processed.
			this.triggerReading = false ;
			return ret ;
		} else {
			return null ;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		super.userDefinedInternalTransition(elapsedTime) ;
		if (this.hasDebugLevel(1)) {
			this.logMessage(this.getCurrentStateTime() +
							"|internal|battery = " +
							this.remainingCapacity.v + " mAh.") ;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedExternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedExternalTransition(Duration elapsedTime)
	{
		super.userDefinedExternalTransition(elapsedTime) ;

		Vector<EventI> current = this.getStoredEventAndReset() ;
		boolean	ticReceived = false ;
		for (int i = 0 ; !ticReceived && i < current.size() ; i++) {
			if (current.get(i) instanceof TicEvent) {
				ticReceived = true ;
			}
		}
		if (ticReceived) {
			this.triggerReading = true ;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getFinalReport()
	 */
	@Override
	public SimulationReportI		getFinalReport() throws Exception
	{
		return new BatterySensorReport(this.getURI(), this.readings) ;
	}
}
