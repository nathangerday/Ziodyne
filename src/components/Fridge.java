package components;

import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentAccessI;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.PreconditionException;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import interfaces.FridgeI;
import ports.FridgeInboundPort;
import simulation.sil.fridge.models.FridgeCoupledModel;
import simulation.sil.fridge.plugin.FridgeSimulatorPlugin;

/**
 *The class <code>Fridge</code> implements a fridge component that will
 * hold the fridge simulation model. The fridge is composed of two compartments : 
 * the fridge and freezer
 * 
  <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 *
 */
public class Fridge extends AbstractCyPhyComponent implements FridgeI,EmbeddingComponentAccessI{

	/** State of the fridge's door*/
    public enum DoorState{OPEN,CLOSE}
    /** State of the fridge*/
    public enum FState{ON,OFF}
    
    /** Current state of the fridge */
    protected FState fridgeState;
    /** Current state of the freezer */
    protected FState freezerState;
    /** Current state of the fridge door */
    protected DoorState fridgeDoor;
    /** Current state of the freezer door */
    protected DoorState freezerDoor;
    /** true if fridge is on break, false if not */
    protected boolean isFridgeOnBreak;
    /** true if freezer is on break, false if not */
    protected boolean isFreezerOnBreak;
    /** the plugin in order to access the model 	 */
    protected FridgeSimulatorPlugin asp;
    /**
	 * Port that exposes the offered interface of the fridge with the given URI to ease the
	 * connection from controller components.
	 */
    protected FridgeInboundPort fridgeInboundPort;

    /** 
     * Construct a fridge component.
     * 
     * <p><strong>Contract</strong></p>
     * 
     * <pre>
     * pre  uri != null
     * </pre>
     * @param uri
     * @param fridgeInboundPortURI
     * @throws Exception
     */
    protected Fridge(String uri, String fridgeInboundPortURI) throws Exception {
        super(uri, 1, 0);
        assert uri != null :  new PreconditionException("uri can't be null!") ;
        fridgeState = FState.OFF;
        freezerState = FState.OFF;
        fridgeDoor = DoorState.CLOSE;
        freezerDoor = DoorState.CLOSE;
        isFridgeOnBreak = false;
        isFreezerOnBreak = false;

        this.addOfferedInterface(FridgeI.class);
        fridgeInboundPort = new FridgeInboundPort(fridgeInboundPortURI, this);
        fridgeInboundPort.publishPort();

        initialise();
    }

    /**
	 * Initialise the fridge by installing the plugin for accessing to the model.
	 * 
	 * @throws Exception
	 */
    private void initialise() throws Exception{
        Architecture localArchitecture = this.createLocalArchitecture(null) ;
        this.asp = new FridgeSimulatorPlugin();
        this.asp.setPluginURI(localArchitecture.getRootModelURI()) ;
        this.asp.setSimulationArchitecture(localArchitecture) ;
        this.installPlugin(this.asp) ;
        this.toggleLogging() ;
    }

    //    @Override
    //    public void execute() throws Exception {
    //        // @remove A garder que en standalone
    //        HashMap<String,Object> simParams = new HashMap<String,Object>();
    //        simParams.put(
    //                FridgeModel.URI + ":" + FridgeModel.SERIES_FRIDGE + PlotterDescription.PLOTTING_PARAM_NAME,
    //                new PlotterDescription(
    //                        "Fridge Temperature Model",
    //                        "Time (sec)",
    //                        "Celsius",
    //                        SimulationMain.ORIGIN_X,
    //                        SimulationMain.ORIGIN_Y + SimulationMain.getPlotterHeight(),
    //                        SimulationMain.getPlotterWidth(),
    //                        SimulationMain.getPlotterHeight())) ;
    //        simParams.put(
    //                FridgeModel.URI + ":" + FridgeModel.SERIES_FREEZER + PlotterDescription.PLOTTING_PARAM_NAME,
    //                new PlotterDescription(
    //                        "Freezer Temperature Model",
    //                        "Time (sec)",
    //                        "Celsius",
    //                        SimulationMain.ORIGIN_X,
    //                        SimulationMain.ORIGIN_Y + 2*SimulationMain.getPlotterHeight(),
    //                        SimulationMain.getPlotterWidth(),
    //                        SimulationMain.getPlotterHeight())) ;
    //        simParams.put(
    //                FridgeModel.URI + ":" + FridgeModel.SERIES_POWER + PlotterDescription.PLOTTING_PARAM_NAME,
    //                new PlotterDescription(
    //                        "Fridge Power Model",
    //                        "Time (sec)",
    //                        "Power (Watt)",
    //                        SimulationMain.ORIGIN_X,
    //                        SimulationMain.ORIGIN_Y + 3*SimulationMain.getPlotterHeight(),
    //                        SimulationMain.getPlotterWidth(),
    //                        SimulationMain.getPlotterHeight())) ;
    //
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
            fridgeInboundPort.unpublishPort();
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
            fridgeInboundPort.unpublishPort();
        }catch(Exception e) {
            throw new ComponentShutdownException(e);
        }
        super.shutdownNow();
    }
    
    /**
     * Set the fridge on break or not on break
     */
    @Override
    public void switchFridgeBreak(){
        this.isFridgeOnBreak = !this.isFridgeOnBreak;
    }

    
    /**
     * Return isFridgeOnBreak value
     * 
     * @return isFridgeOnBreak
     */
    @Override
    public boolean isFridgeOnBreak() throws Exception {
        return isFridgeOnBreak;

    }

    /**
     * Set the  freezer on break or not on break
     */
    @Override
    public void switchFreezerBreak() {
        this.isFreezerOnBreak = !this.isFreezerOnBreak;
    }

    
    /**
     * Return isFreezerOnBreak value
     * 
     * @return isFreezerOnBreak
     */
    @Override
    public boolean isFreezerOnBreak() throws Exception {
        return isFreezerOnBreak;
    }

    /**
     * Check whether the fridge is on
     * 
     * @return true if the state of the fridge is ON, false if it is OFF
     */
    @Override
    public boolean isFridgeOn(){
        return fridgeState == FState.ON;
    }

    /**
     * Check whether the freezer is on
     * 
     * @return true if the state of the fridge is ON, false if it is OFF
     */
    @Override
    public boolean isFreezerOn(){
        return freezerState == FState.ON;
    }

    /**
 	 * Create local architecture 
 	 * 
 	 * @param URI of the model
 	 * @return local architecture of the fridge
 	 */
    @Override
    protected Architecture createLocalArchitecture(String modelURI) throws Exception{
        return FridgeCoupledModel.build();
    }

	/**
	 * Return the embedding component state value.
	 * 
	 * @param name of the component
	 * @return freezer's state, or fridge's state, or  freezer's door state, or fridge's door
	 *  state, or if freezer is on break, or if fridge is on break
	 * 
	 */
    @Override
    public Object getEmbeddingComponentStateValue(String name) throws Exception{
        if(name.equals("freezer state")) {
            return freezerState;
        } else if (name.equals("fridge state")){
            return fridgeState;
        } else if (name.equals("freezer door")){
            return freezerDoor;
        } else if (name.equals("fridge door")){
            return fridgeDoor;
        } else if (name.equals("freezer break")){
            return isFreezerOnBreak;
        } else if (name.equals("fridge break")){
            return isFridgeOnBreak;
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
        if(name.equals("freezer door")) {
            freezerDoor = (DoorState) value;
        } else if (name.equals("fridge door")) {
            fridgeDoor = (DoorState) value;
        } else if (name.equals("freezer state")) {
            freezerState = (FState) value;
        } else if (name.equals("fridge state")) {
            fridgeState = (FState) value;
        } else {
            throw new RuntimeException();
        }
    }
}
