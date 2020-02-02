package components;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentAccessI;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.PreconditionException;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.architectures.SimulationEngineCreationMode;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor;
import interfaces.BatteryI;
import ports.BatteryInboundPort;
import simulation.sil.battery.models.BatteryModel;
import simulation.sil.battery.plugin.BatterySimulatorPlugin;

/**
 *The class <code>Battery</code> implements a battery component that will
 * hold the battery simulation model.
 * 
  <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 *
 */
public class Battery extends AbstractCyPhyComponent implements BatteryI, EmbeddingComponentAccessI {

	/**
	 * States of the battery
	 */
	public enum BState {
		STANDBY, PRODUCING, CONSUMING
	}

	/** Current state of the battery  */
	protected BState mode;

	/**
	 * Port that exposes the offered interface of the battery with the given URI to ease the
	 * connection from controller components.
	 */
	protected BatteryInboundPort batteryInboundPort;

	/** 
	 * the plugin in order to access the model 
	 */
	protected BatterySimulatorPlugin asp;

	/**
	 * Create a battery.
	 * 
	 *  <p><strong>Contract</strong></p>
	 * <pre>
	 * pre uri != null
	 * </pre>
	 * 
	 * @param uri				URI of the component
	 * @param batteryInboundPortURI	URI of the battery inbound port
	 * @throws Exception			<i>todo.</i>
	 */
	protected Battery(String uri, String batteryInboundPortURI) throws Exception {
		super(uri, 1, 0);
		assert uri != null : new PreconditionException("uri can't be null!");
		this.mode = BState.STANDBY;
		this.addOfferedInterface(BatteryI.class);
		batteryInboundPort = new BatteryInboundPort(batteryInboundPortURI, this);
		batteryInboundPort.publishPort();
		this.initialise();
	}

	/**
	 * Initialise the battery by installing the plugin for accessing to the model.
	 * 
	 * @throws Exception
	 */
	private void initialise() throws Exception {
		Architecture localArchitecture = this.createLocalArchitecture(null);
		this.asp = new BatterySimulatorPlugin();
		this.asp.setPluginURI(localArchitecture.getRootModelURI());
		this.asp.setSimulationArchitecture(localArchitecture);
		this.installPlugin(this.asp);
		this.toggleLogging();
	}
	
	
//  @Override
//  public void execute() throws Exception {
//      // @remove A garder que en standalone
//      HashMap<String,Object> simParams = new HashMap<String,Object>();
//      simParams.put(
//              BatteryModel.URI + ":" + BatteryModel.SERIES_CAPACITY + PlotterDescription.PLOTTING_PARAM_NAME,
//              new PlotterDescription(
//                      "Battery Capacity",
//                      "Time (sec)",
//                      "Capacity (W)",
//                      SimulationMain.ORIGIN_X,
//                      SimulationMain.ORIGIN_Y + SimulationMain.getPlotterHeight(),
//                      SimulationMain.getPlotterWidth(),
//                      SimulationMain.getPlotterHeight())) ;
//      simParams.put(
//              BatteryModel.URI + ":" + BatteryModel.SERIES_CONSUMPTION + PlotterDescription.PLOTTING_PARAM_NAME,
//              new PlotterDescription(
//                      "Battery Consumption",
//                      "Time (sec)",
//                      "Power (watt)",
//                      SimulationMain.ORIGIN_X,
//                      SimulationMain.ORIGIN_Y + 2 * SimulationMain.getPlotterHeight(),
//                      SimulationMain.getPlotterWidth(),
//                      SimulationMain.getPlotterHeight())) ;
//
//      simParams.put(
//              BatteryModel.URI + ":" + BatteryModel.SERIES_PRODUCTION + PlotterDescription.PLOTTING_PARAM_NAME,
//              new PlotterDescription(
//                      "Battery Production",
//                      "Time (sec)",
//                      "Power (W)",
//                      SimulationMain.ORIGIN_X,
//                      SimulationMain.ORIGIN_Y + 3 * SimulationMain.getPlotterHeight(),
//                      SimulationMain.getPlotterWidth(),
//                      SimulationMain.getPlotterHeight())) ;
//
//      this.asp.setSimulationRunParameters(simParams);
//      asp.setDebugLevel(0);
//      asp.doStandAloneSimulation(0.0, 500.0);
//  }

	/**
	 * Shutdown the component
	 * 
	 * @throws ComponentShutdownException
	 */
	@Override
	public void shutdown() throws ComponentShutdownException {
		try {
			batteryInboundPort.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}

	/**
	 * Shutdown the component now.
	 *
	 * @throws ComponentShutdownException
	 */
	@Override
	public void shutdownNow() throws ComponentShutdownException {
		try {
			batteryInboundPort.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdownNow();
	}

	/**
	 * Return max capacity of the battery
	 * 
	 * @return max capacity of the battery
	 */
	@Override
	public double getMaxCapacity() throws Exception {
		return (double) asp.getModelStateValue(BatteryModel.URI, "max capacity");
	}

	/**
	 * Return current capacity of the battery
	 * 
	 * @return current capacity of the battery
	 */
	@Override
	public double getCurrentCapacity() throws Exception {
		return (double) asp.getModelStateValue(BatteryModel.URI, "capacity");
	}

	/**
	 * Replace the current mode of the battery with a new one.
	 * 
	 * @param new mode of the battery
	 */
	@Override
	public void setMode(BState mode) throws Exception {
		this.mode = mode;
	}

	/**
	 * Return  current mode of the battery
	 * 
	 * @return current mode of the battery
	 */
	@Override
	public BState getMode() throws Exception {
		return mode;
	}

	/**
	 * Create local architecture using battery URI
	 * 
	 * @param URI of the model
	 * @return local architecture of the battery
	 */
	@Override
	protected Architecture createLocalArchitecture(String modelURI) throws Exception {
		Map<String, AbstractAtomicModelDescriptor> atomicModelDescriptors = new HashMap<>();
		atomicModelDescriptors.put(BatteryModel.URI, AtomicModelDescriptor.create(BatteryModel.class, BatteryModel.URI,
				TimeUnit.SECONDS, null, SimulationEngineCreationMode.ATOMIC_ENGINE));
		Architecture localArchitecture = new Architecture(BatteryModel.URI, atomicModelDescriptors, new HashMap<>(),
				TimeUnit.SECONDS);
		return localArchitecture;
	}
	
	
	/**
	 * Return the embedding component state value.
	 * 
	 * @param name of the component
	 * @return mode of the battery
	 * 
	 */
	@Override
	public Object getEmbeddingComponentStateValue(String name) throws Exception {
		if (name.equals("state")) {
			return mode;
		} else {
			throw new RuntimeException();
		}
	}

	/**
	 * Set a new embedding component state value.
	 * 
	 * @param name of the component
	 * @param new state value
	 * 
	 */
	@Override
	public void setEmbeddingComponentStateValue(String name, Object value) {
		if (name.equals("state")) {
			this.mode = (BState) value;
		} else {
			throw new RuntimeException();
		}
	}
}
