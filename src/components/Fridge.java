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

public class Fridge extends AbstractCyPhyComponent implements FridgeI,EmbeddingComponentAccessI{

    public enum DoorState{OPEN,CLOSE}
    public enum FState{ON,OFF}

    protected FState fridgeState;
    protected FState freezerState;
    protected DoorState fridgeDoor;
    protected DoorState freezerDoor;
    protected boolean isFridgeOnBreak;
    protected boolean isFreezerOnBreak;
    protected FridgeSimulatorPlugin asp;

    protected FridgeInboundPort fridgeInboundPort;

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

    @Override
    public void shutdown() throws ComponentShutdownException {
        try {
            fridgeInboundPort.unpublishPort();
        }catch(Exception e) {
            throw new ComponentShutdownException(e);
        }
        super.shutdown();
    }

    @Override
    public void shutdownNow() throws ComponentShutdownException {
        try {
            fridgeInboundPort.unpublishPort();
        }catch(Exception e) {
            throw new ComponentShutdownException(e);
        }
        super.shutdownNow();
    }

    @Override
    public void switchFridgeBreak(){
        this.isFridgeOnBreak = !this.isFridgeOnBreak;
    }

    @Override
    public boolean isFridgeOnBreak() throws Exception {
        return isFridgeOnBreak;

    }

    @Override
    public void switchFreezerBreak() {
        this.isFreezerOnBreak = !this.isFreezerOnBreak;
    }

    @Override
    public boolean isFreezerOnBreak() throws Exception {
        return isFreezerOnBreak;
    }

    @Override
    public boolean isFridgeOn(){
        return fridgeState == FState.ON;
    }

    @Override
    public boolean isFreezerOn(){
        return freezerState == FState.ON;
    }

    @Override
    protected Architecture createLocalArchitecture(String modelURI) throws Exception{
        return FridgeCoupledModel.build();
    }

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
