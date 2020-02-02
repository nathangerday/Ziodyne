package components;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentAccessI;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.PostconditionException;
import fr.sorbonne_u.components.exceptions.PreconditionException;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.architectures.SimulationEngineCreationMode;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor;
import interfaces.ElectricMeterI;
import ports.ElectricMeterInboundPort;
import simulation.sil.electricmeter.models.ElectricMeterModel;
import simulation.sil.electricmeter.plugin.ElectricMeterSimulatorPlugin;


/**
 *The class <code>ElectricMeter</code> implements a electric meter component that will
 * hold the electric meter simulation model.
 * 
  <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 *
 */
public class ElectricMeter extends AbstractCyPhyComponent implements ElectricMeterI, EmbeddingComponentAccessI {

	/**
	 * Port that exposes the offered interface of the electric meter with the given URI to ease the
	 * connection from controller components.
	 */
	protected ElectricMeterInboundPort electricMeterInboundPort;
	/** the plugin in order to access the model  */
	protected ElectricMeterSimulatorPlugin asp;

	/**
	 * Create a electric meter component
	 * 
	 *  <p><strong>Contract</strong></p>
	 *  
	 * <pre>
	 * pre uri != null
	 * </pre>
	 * 
	 * <post>
	 * post isPortExisting(electricMeterInboundPort.portUri) == true
	 *  </post>
	 *  
	 *   <post>
	 * post findPortFromURI(electricMeterInboundPort.portURI).implementedInterface == ElectricMeterI.class 
	 *  </post>
	 *  
	 *   <post>
	 * post findPortFromURI(electricMeterInboundPort.portURI()).isPublished() == true
	 *  </post>
	 *  
	 * @param uri
	 * @param electricMeterInboundPortURI
	 * @throws Exception
	 */
	protected ElectricMeter(String uri, String electricMeterInboundPortURI) throws Exception {
		super(uri, 1, 0);
		assert uri != null : new PreconditionException("uri can't be null!");
		this.addOfferedInterface(ElectricMeterI.class);
		this.electricMeterInboundPort = new ElectricMeterInboundPort(electricMeterInboundPortURI, this);
		this.electricMeterInboundPort.publishPort();

		this.initialise();

		assert this.isPortExisting(electricMeterInboundPort.getPortURI()) : new PostconditionException(
				"The component must have a " + "port with URI " + electricMeterInboundPort.getPortURI());

		assert this.findPortFromURI(electricMeterInboundPort.getPortURI()).getImplementedInterface()
				.equals(ElectricMeterI.class) : new PostconditionException(
						"The component must have a " + "port with implemented interface ElectricMeterI");

		assert this.findPortFromURI(electricMeterInboundPort.getPortURI()).isPublished() : new PostconditionException(
				"The component must have a " + "port published with URI " + electricMeterInboundPort.getPortURI());
	}

    /**
	 * Initialise the electric meter by installing the plugin for accessing to the model.
	 * 
	 * @throws Exception
	 */
	private void initialise() throws Exception {
		Architecture localArchitecture = this.createLocalArchitecture(null);
		this.asp = new ElectricMeterSimulatorPlugin();
		this.asp.setPluginURI(localArchitecture.getRootModelURI());
		this.asp.setSimulationArchitecture(localArchitecture);
		this.installPlugin(this.asp);
		this.toggleLogging();
	}

	// @Override
	// public void execute() throws Exception {
	// // @remove A garder que en standalone
	// HashMap<String,Object> simParams = new HashMap<String,Object>();
	// simParams.put(
	// ElectricMeterModel.URI + ":" + ElectricMeterModel.SERIES_AVAILABLE +
	// PlotterDescription.PLOTTING_PARAM_NAME,
	// new PlotterDescription(
	// "Available Energy",
	// "Time (sec)",
	// "Power (W)",
	// SimulationMain.ORIGIN_X,
	// SimulationMain.ORIGIN_Y + SimulationMain.getPlotterHeight(),
	// SimulationMain.getPlotterWidth(),
	// SimulationMain.getPlotterHeight())) ;
	// simParams.put(
	// ElectricMeterModel.URI + ":" + ElectricMeterModel.SERIES_CONSUMPTION +
	// PlotterDescription.PLOTTING_PARAM_NAME,
	// new PlotterDescription(
	// "Total Consumption",
	// "Time (sec)",
	// "Power (W)",
	// SimulationMain.ORIGIN_X,
	// SimulationMain.ORIGIN_Y + 2 * SimulationMain.getPlotterHeight(),
	// SimulationMain.getPlotterWidth(),
	// SimulationMain.getPlotterHeight())) ;
	//
	// simParams.put(
	// ElectricMeterModel.URI + ":" + ElectricMeterModel.SERIES_PRODUCTION +
	// PlotterDescription.PLOTTING_PARAM_NAME,
	// new PlotterDescription(
	// "Total Production",
	// "Time (sec)",
	// "Power (W)",
	// SimulationMain.ORIGIN_X,
	// SimulationMain.ORIGIN_Y + 3 * SimulationMain.getPlotterHeight(),
	// SimulationMain.getPlotterWidth(),
	// SimulationMain.getPlotterHeight())) ;
	//
	// this.asp.setSimulationRunParameters(simParams);
	// asp.setDebugLevel(0);
	// asp.doStandAloneSimulation(0.0, 500.0);
	// }

	
	/**
	 * Shutdown the component
	 * 
	 * @throws ComponentShutdownException
	 */
	@Override
	public void shutdown() throws ComponentShutdownException {
		try {
			this.electricMeterInboundPort.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}

	/**
	 * Shutdown the component now
	 * 
	 * @throws ComponentShutdownException
	 */
	@Override
	public void shutdownNow() throws ComponentShutdownException {
		try {
			this.electricMeterInboundPort.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdownNow();
	}

	/**
	 * Return available energy  shown in the model
	 * 
	 * @return available energy
	 */
	@Override
	public double getAvailableEnergy() throws Exception {
		return (double) asp.getModelStateValue(ElectricMeterModel.URI, "available");
	}

	/**
	 * Return energy produced shown in the model
	 * 
	 * @return produced energy
	 */
	@Override
	public double getProduction() throws Exception {
		return (double) asp.getModelStateValue(ElectricMeterModel.URI, "production");
	}

	/**
	 * Return energy produced shown in the model
	 * 
	 * @return produced energy
	 */
	@Override
	public double getConsumption() throws Exception {
		return (double) asp.getModelStateValue(ElectricMeterModel.URI, "consumption");
	}


	/**
	 * Create local architecture 
	 * 
	 * @param URI of the model
	 * @return local architecture of the electric meter
	 */
	@Override
	protected Architecture createLocalArchitecture(String modelURI) throws Exception {
		Map<String, AbstractAtomicModelDescriptor> atomicModelDescriptors = new HashMap<>();
		atomicModelDescriptors.put(ElectricMeterModel.URI, AtomicModelDescriptor.create(ElectricMeterModel.class,
				ElectricMeterModel.URI, TimeUnit.SECONDS, null, SimulationEngineCreationMode.ATOMIC_ENGINE));
		Architecture localArchitecture = new Architecture(ElectricMeterModel.URI, atomicModelDescriptors,
				new HashMap<>(), TimeUnit.SECONDS);
		return localArchitecture;
	}
}
