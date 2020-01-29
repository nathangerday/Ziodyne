package simulation.models.electricmeter;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentAccessI;
import fr.sorbonne_u.devs_simulation.examples.molene.SimulationMain;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ExportedVariable;
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
import simulation.events.electricmeter.BatteryConsumption;
import simulation.events.electricmeter.BatteryProduction;
import simulation.events.electricmeter.DishwasherConsumption;
import simulation.events.electricmeter.FridgeConsumption;
import simulation.events.electricmeter.LampConsumption;
import simulation.events.electricmeter.WindTurbineProduction;

// TODO : Internal transitions ?

@ModelExternalEvents(imported = { BatteryConsumption.class, BatteryProduction.class, WindTurbineProduction.class,
		LampConsumption.class, FridgeConsumption.class, DishwasherConsumption.class })
public class ElectricMeterModel extends AtomicHIOAwithEquations {

	private static final long serialVersionUID = 1L;

	public static class ElectricMeterReport extends AbstractSimulationReport {

		private static final long serialVersionUID = 1L;

		/**
		 * create a simulation report.
		 *
		 * <p>
		 * <strong>Contract</strong>
		 * </p>
		 *
		 * <pre>
		 * pre	modelURI != null
		 * post	this.getModelURI() != null
		 * post	this.getModelURI().equals(modelURI)
		 * </pre>
		 *
		 * @param modelURI URI of the model which report is defined.
		 */
		public ElectricMeterReport(String modelURI) {
			super(modelURI);
		}

		@Override
		public String toString() {
			return "ElectricMeterReport(" + this.getModelURI() + ")";
		}
	}

	/** uri of the electric meter model **/
	public static final String URI = "ElectricMeterModel";
	/** series of the power usage **/
	private static final String SERIES_GLOBAL_POWER_USAGE = "GlobalPowerUsage";
	/** series of the power production **/
	private static final String SERIES_GLOBAL_POWER_PRODUCTION = "GlobalPowerProduction";

	// Exported variables
	/** current global power usage in Watts */
	@ExportedVariable(type = Double.class)
	protected final Value<Double> currentGlobalPowerUsage = new Value<Double>(this, 0.0, 0);
	/** produced global power usage in Watts */
	@ExportedVariable(type = Double.class)
	protected final Value<Double> producedGlobalPowerUsage = new Value<Double>(this, 0.0, 0);

	// Constants and variable

	/** energy consumption (in Watts) of the electric meter */
	private static final double ELECTRICMETER_CONSUMPTION = 10; // Watt

	// Variables : levels of energy consumed in Watt

	/** energy consumption of the lamp */
	private double LAMP_CONSUMPTION;
	/** energy consumption of the fridge */
	private double FRIDGE_CONSUMPTION;
	/** energy consumption of the dishwasher */
	private double DISHWASHER_CONSUMPTION;
	/** energy consumption of the battery */
	private double BATTERY_CONSUMPTION;

	// Variables : levels of energy produced in Watt

	/** energy production of the battery */
	private double BATTERY_PRODUCTION;
	/** energy production of the wind turbine */
	private double WINDTURBINE_PRODUCTION;

	/** plotter for the intensity level over time. */
	protected XYPlotter powerPlotter;
	/** plotter for the produced level energy over time. */
	protected XYPlotter producedPlotter;
	/**
	 * reference on the object representing the component that holds the model;
	 * enables the model to access the state of this component.
	 */
	protected EmbeddingComponentAccessI componentRef;

	/**
	 * create an atomic hybrid input/output model based on an algebraic equations
	 * solver with the given URI (if null, one will be generated) and to be run by
	 * the given simulator (or by the one of an ancestor coupled model if null)
	 * using the given time unit for its clock.
	 *
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
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
	 * post	!isDebugModeOn()
	 * </pre>
	 *
	 * @param uri               unique identifier of the model.
	 * @param simulatedTimeUnit time unit used for the simulation clock.
	 * @param simulationEngine  simulation engine enacting the model.
	 * @throws Exception <i>TODO</i>.
	 */
	public ElectricMeterModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);

		PlotterDescription pdPower = new PlotterDescription("Global Power Usage", "Time (sec)", "Power (Watt)",
				SimulationMain.ORIGIN_X, SimulationMain.ORIGIN_Y + SimulationMain.getPlotterHeight(),
				SimulationMain.getPlotterWidth(), SimulationMain.getPlotterHeight());

		this.powerPlotter = new XYPlotter(pdPower);
		this.powerPlotter.createSeries(SERIES_GLOBAL_POWER_USAGE);

		PlotterDescription pdProduction = new PlotterDescription("Global Power Production", "Time (sec)",
				"Power (Watt)", SimulationMain.ORIGIN_X, SimulationMain.ORIGIN_Y + SimulationMain.getPlotterHeight(),
				SimulationMain.getPlotterWidth(), SimulationMain.getPlotterHeight());

		this.producedPlotter = new XYPlotter(pdProduction);
		this.producedPlotter.createSeries(SERIES_GLOBAL_POWER_PRODUCTION);

		// create a standard logger (logging on the terminal)
		this.setLogger(new StandardLogger());
	}

	@Override
	public void setSimulationRunParameters(Map<String, Object> simParams) throws Exception {

		// The reference to the embedding component
		this.componentRef = (EmbeddingComponentAccessI) simParams.get("componentRef");
	}

	@Override
	public void initialiseState(Time initialTime) {
		// initialisation of the intensity plotter
		this.powerPlotter.initialise();
		// show the plotter on the screen
		this.powerPlotter.showPlotter();

		// initialisation of the in plotter
		this.producedPlotter.initialise();
		// show the plotter on the screen
		this.producedPlotter.showPlotter();

		try {
			// set the debug level triggering the production of log messages.
			this.setDebugLevel(1);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		super.initialiseState(initialTime);
	}

	@Override
	protected void initialiseVariables(Time startTime) {
		// as the lamp starts in mode OFF, its power consumption is 0
		this.currentGlobalPowerUsage.v = 0.0;
		this.producedGlobalPowerUsage.v = 0.0;

		// first data in the plotter to start the plot.
		this.powerPlotter.addData(SERIES_GLOBAL_POWER_USAGE, this.getCurrentStateTime().getSimulatedTime(),
				getCurrentGlobalPowerUsage());
		this.producedPlotter.addData(SERIES_GLOBAL_POWER_PRODUCTION,this.getCurrentStateTime().getSimulatedTime(),
				getGlobalProducedPower());

		super.initialiseVariables(startTime);
	}

	@Override
	public void userDefinedExternalTransition(Duration elapsedTime) {
		  Vector<EventI> currentEvents = this.getStoredEventAndReset();
	        EventI e;
	        for(int i=0;i<currentEvents.size();i++) {
	            e = currentEvents.get(i);
	            if (this.hasDebugLevel(2)) {
	                this.logMessage("ElectricMeterModel::userDefinedExternalTransition "
	                        + e.getClass().getCanonicalName());
	            }
	            
	            double value = 0.0;
	            if(e instanceof BatteryConsumption) {
	            	value = ((BatteryConsumption.Reading) e.getEventInformation()).value;
	            	this.currentGlobalPowerUsage.v += value;
	            }
	            else if (e instanceof DishwasherConsumption) {
	            	value = ((DishwasherConsumption.Reading) e.getEventInformation()).value;
	            	this.currentGlobalPowerUsage.v += value;
	            }
	            else if (e instanceof FridgeConsumption) {
	            	value = ((FridgeConsumption.Reading) e.getEventInformation()).value;
	            	this.currentGlobalPowerUsage.v += value;
	            }
	            else if (e instanceof LampConsumption) {
	            	value = ((LampConsumption.Reading) e.getEventInformation()).value;
	            	this.currentGlobalPowerUsage.v += value;
	            }
	            else if (e instanceof WindTurbineProduction) {
	            	value = ((WindTurbineProduction.Reading) e.getEventInformation()).value;
	            	
	            }
	            else if(e instanceof BatteryProduction) {
	            	value = ((BatteryProduction.Reading) e.getEventInformation()).value;
	            	this.producedGlobalPowerUsage.v += value;
	            }
	            e.executeOn(this);
	        }
	        
	        super.userDefinedExternalTransition(elapsedTime) ;
	}
	
	
	   @Override
	    public void endSimulation(Time endTime) throws Exception {
	        this.powerPlotter.addData(
	                SERIES_GLOBAL_POWER_USAGE,
	                endTime.getSimulatedTime(),
	                this.getCurrentGlobalPowerUsage());
	        this.producedPlotter.addData(SERIES_GLOBAL_POWER_PRODUCTION, endTime.getSimulatedTime(), this.getGlobalProducedPower());
	        Thread.sleep(10000L);
	        this.powerPlotter.dispose();

	        super.endSimulation(endTime);
	    }

	@Override
	public Vector<EventI> output() {
		return null;
	}
	
	@Override
    public SimulationReportI getFinalReport() throws Exception
    {
        return new ElectricMeterReport(this.getURI()) ;
    }


	@Override
	public Duration timeAdvance() {
		return new Duration(9.0, TimeUnit.SECONDS);
	}

	public double getCurrentGlobalPowerUsage() {
		return currentGlobalPowerUsage.v;
	}

	public double getGlobalProducedPower() {
		return producedGlobalPowerUsage.v;
	}

	public void setLampConsumption(double value) {
		this.LAMP_CONSUMPTION = value;
	}

	public void setBatteryConsumption(double value) {
		this.BATTERY_CONSUMPTION = value;
	}

	public void setFridgeConsumption(double value) {
		this.FRIDGE_CONSUMPTION = value;
	}

	public void setDishwasherConsumption(double value) {
		this.DISHWASHER_CONSUMPTION = value;
	}

	public void setBatteryProduction(double value) {
		this.BATTERY_PRODUCTION = value;
	}

	public void setWindTurbineProduction(double value) {
		this.WINDTURBINE_PRODUCTION = value;
	}

}
