package components;

import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentAccessI;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import interfaces.DishwasherI;
import ports.DishwasherInboundPort;
import simulation.sil.dishwasher.models.DishwasherCoupledModel;
import simulation.sil.dishwasher.models.DishwasherModel;
import simulation.sil.dishwasher.plugin.DishWasherSimulatorPlugin;

/**
 *The class <code>Dishwasher</code> implements a dishwasher component that will
 * hold the dishwasher simulation model.
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 *
 */
public class Dishwasher extends AbstractCyPhyComponent implements DishwasherI,EmbeddingComponentAccessI{
	
	/**
	 * States of the battery
	 */
    public enum DWState{ON,OFF}
    /**
     * Mode of the dishwasher
     */
    public enum DWMode{STANDARD,ECO}

    /** Current state of the dishwasher */
    private DWState state;
    /** Current mode of the dishwasher */
    private DWMode mode;
    
    /** true if the dishwasher is on break, false if not*/
    private boolean isOnBreak;
    /**
	 * Port that exposes the offered interface of the dishwasher with the given URI to ease the
	 * connection from controller components.
	 */
    protected DishwasherInboundPort dishwasherInboundPort;
    /** the plugin in order to access the model 	 */
    protected DishWasherSimulatorPlugin asp;

    

	/**
	 * Create a dishwasher.
	 * 
	 * 
	 *  <p><strong>Contract</strong></p>
	 *  
	 * <pre>
	 * pre uri != null
	 * </pre>
	 * 
	 * @param uri				URI of the component
	 * @param dishwasherInboundPortURI	URI of the battery inbound port
	 * @throws Exception			<i>todo.</i>
	 */
    protected Dishwasher(String uri, String dishwasherInboundPortURI) throws Exception{
        super(uri, 1, 0);
        state = DWState.OFF;
        mode = DWMode.STANDARD;
        isOnBreak = false;
        this.addOfferedInterface(DishwasherI.class);
        dishwasherInboundPort = new DishwasherInboundPort(dishwasherInboundPortURI, this);
        dishwasherInboundPort.publishPort();

        this.initialise();
    }

    /**
	 * Initialise the dishwasher by installing the plugin for accessing to the model.
	 * 
	 * @throws Exception
	 */
    private void initialise() throws Exception{
        Architecture localArchitecture = this.createLocalArchitecture(null) ;
        this.asp = new DishWasherSimulatorPlugin() ;
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
//                        "DishWasher Consumption",
//                        "Time (sec)",
//                        "Power (W)",
//                        SimulationMain.ORIGIN_X,
//                        SimulationMain.ORIGIN_Y + SimulationMain.getPlotterHeight(),
//                        SimulationMain.getPlotterWidth()*2,
//                        SimulationMain.getPlotterHeight()*2);
//
//        HashMap<String,Object> simParams = new HashMap<String,Object>();
//        simParams.put(DishwasherModel.URI + ":" + PlotterDescription.PLOTTING_PARAM_NAME, pd);
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
            dishwasherInboundPort.unpublishPort();
        }catch(Exception e) {
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
            dishwasherInboundPort.unpublishPort();
        }catch(Exception e) {
            throw new ComponentShutdownException(e);
        }
        super.shutdownNow();
    }


    /**
     * Check whether the dishwasher is on
     * 
     * @return true if the state of the dishwasher is ON, false if it is OFF
     */
    @Override
    public boolean isOn(){
        return this.state == DWState.ON;
    }

    /**
     * Return the value of time left for completing a wash
     * 
     * @return time left
     */
    @Override
    public double getTimeLeft() throws Exception{
        return (double) asp.getModelStateValue(DishwasherModel.URI, "time");
    }

    /** 
     * Return the mode of dishwasher
     * 
     * @return mode
     *
     */
    @Override
    public DWMode getMode(){
        return mode;
    }

    /**
     * Replace the current mode of the dishwasher with a new one.
	 * 
	 * @param new mode of the dishwasher
     */
    @Override
    public void setMode(DWMode mode){
        this.mode = mode;
    }


    /**
     * Set the dishwasher on break or not on break
     */
    @Override
    public void switchBreak() {
        this.isOnBreak = !this.isOnBreak;
    }

    /**
     * Return isOnBreak value
     * 
     * @return isOnBreak
     */
    @Override
    public boolean isOnBreak() {
        return isOnBreak;
    }

	/**
	 * Return the embedding component state value.
	 * 
	 * @param name of the component
	 * @return state, or isOnBreak, or mode of the dishwasher
	 * 
	 */
    @Override
    public Object getEmbeddingComponentStateValue(String name) throws Exception{
        if(name.equals("state")) {
            return state;
        } else if(name.equals("break")){ 
            return isOnBreak;
        } else if(name.equals("mode")){ 
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
    public void setEmbeddingComponentStateValue(String name , Object value) {
        if(name.equals("state")) {
            this.state = (DWState) value;
        } else {
            throw new RuntimeException();
        }
    }

    /**
	 * Create local architecture 
	 * 
	 * @param URI of the model
	 * @return local architecture of the dishwasher
	 */
    @Override
    protected Architecture createLocalArchitecture(String modelURI) throws Exception {
        return DishwasherCoupledModel.build();
    }
}