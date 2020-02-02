package components;

import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentAccessI;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.PostconditionException;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import interfaces.LampI;
import ports.LampInboundPort;
import simulation.sil.lamp.models.LampCoupledModel;
import simulation.sil.lamp.plugin.LampSimulatorPlugin;

/**
 *The class <code>Lamp</code> implements a lamp component that will
 * hold the lamp simulation model.
 * 
  <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 *
 */
public class Lamp extends AbstractCyPhyComponent implements LampI,EmbeddingComponentAccessI{

	/** State of the lamp*/
    public enum LampState{OFF,LOW,MEDIUM,HIGH}
    /**
  	 * Port that exposes the offered interface of the dishwasher with the given URI to ease the
  	 * connection from controller components.
  	 */
    protected LampInboundPort lampInboundPort;
    /** Current state of the lamp */
    protected LampState state;
    /** true if the dishwasher is on break, false if not*/
    protected boolean isOnBreak;
    /** the plugin in order to access the model  */
    protected LampSimulatorPlugin asp ;

    /**
     * Create a lamp component
     * 
     * <p><strong>Contract</strong></p>
	 *  
	 * <pre>
	 * pre uri != null
	 * </pre>
	 * 
	 * <post> 
	 * post state == LampState.OFF
	 * post findPortFromURI(lampInboundPort.portURI).implementedInterface == LampI.class
	 * post isPortExisting(lampInboundPort.portURI()) == true
	 * post findPortFromURI(lampInboundPort.getportURI).isPublished == true
	 * </post>
	 * 
	 * 
     * @param uri
     * @param lampInboundPortURI
     * @throws Exception
     */
    protected Lamp(String uri, String lampInboundPortURI) throws Exception {
        super(uri, 1, 0);
        this.state = LampState.OFF;
        this.isOnBreak = false;
        this.addOfferedInterface(LampI.class);
        this.lampInboundPort = new LampInboundPort(lampInboundPortURI, this);
        this.lampInboundPort.publishPort();

        this.initialise();

        assert this.state == LampState.OFF :
            new PostconditionException("The lamp's state has not been initialised correctly !");
        assert this.isPortExisting(lampInboundPort.getPortURI()):
            new PostconditionException("The component must have a "
                    + "port with URI " + lampInboundPort.getPortURI()) ;

        assert	this.findPortFromURI(lampInboundPort.getPortURI()).
        getImplementedInterface().equals(LampI.class) :
            new PostconditionException("The component must have a "
                    + "port with implemented interface LampI") ;

        assert	this.findPortFromURI(lampInboundPort.getPortURI()).isPublished() :
            new PostconditionException("The component must have a "
                    + "port published with URI " + lampInboundPort.getPortURI()) ;
    }

    /**
 	 * Initialise the lamp by installing the plugin for accessing to the model.
 	 * 
 	 * @throws Exception
 	 */
    private void initialise() throws Exception{
        Architecture localArchitecture = this.createLocalArchitecture(null) ;
        this.asp = new LampSimulatorPlugin() ;
        this.asp.setPluginURI(localArchitecture.getRootModelURI()) ;
        this.asp.setSimulationArchitecture(localArchitecture) ;
        this.installPlugin(this.asp) ;
        this.toggleLogging() ;
    }

    //    @Override
    //    public void execute() throws Exception {
    //        // @remove A garder que en standalone
    //        PlotterDescription pd =
    //                new PlotterDescription(
    //                        "Lamp Power",
    //                        "Time (sec)",
    //                        "Power (Watt)",
    //                        SimulationMain.ORIGIN_X,
    //                        SimulationMain.ORIGIN_Y + SimulationMain.getPlotterHeight(),
    //                        SimulationMain.getPlotterWidth()*2,
    //                        SimulationMain.getPlotterHeight()*2);
    //
    //        HashMap<String,Object> simParams = new HashMap<String,Object>();
    //        simParams.put(LampModel.URI + ":" + PlotterDescription.PLOTTING_PARAM_NAME, pd);
    //        this.asp.setSimulationRunParameters(simParams);
    //        asp.setDebugLevel(0);
    //        asp.doStandAloneSimulation(0.0, 500.0);
    //    }

    /**
 	 * Shutdown the component
 	 * 
 	 * @throws ComponentShutdownException
 	 */
    @Override
    public void shutdown() throws ComponentShutdownException {
        try {
            this.lampInboundPort.unpublishPort();
        }catch(Exception e) {
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
            this.lampInboundPort.unpublishPort();
        }catch(Exception e) {
            throw new ComponentShutdownException(e);
        }
        super.shutdownNow();
    }

    /**
     * Return lamp state
     * 
     * @return lamp current state
     */
    @Override
    public LampState getState() {
        return state;
    }

    /**
     * Set the lamp on break or not on break
     */
    @Override
    public void switchBreak() throws Exception{
        this.isOnBreak = !this.isOnBreak;
    }

    /**
     * Return isOnBreak value
     * 
     * @return isOnBreak
     */
    @Override
    public boolean isOnBreak() throws Exception{
        return this.isOnBreak;
    }

    /**
	 * Create local architecture 
	 * 
	 * @param URI of the model
	 * @return local architecture of the dishwasher
	 */
    @Override
    protected Architecture createLocalArchitecture(String modelURI) throws Exception{
        return LampCoupledModel.build();
    }


	/**
	 * Return the embedding component state value.
	 * 
	 * @param name of the component
	 * @return state, or isOnBreak
	 */
    @Override
    public Object getEmbeddingComponentStateValue(String name) throws Exception{
        if(name.equals("state")) {
            return state;
        } else if(name.equals("break")){ 
            return isOnBreak;
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
    public void setEmbeddingComponentStateValue(String name , Object value) {
        if(name.equals("state")) {
            this.state = (LampState) value;
        } else {
            throw new RuntimeException();
        }
    }
}
