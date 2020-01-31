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

public class Dishwasher extends AbstractCyPhyComponent implements DishwasherI,EmbeddingComponentAccessI{

    public enum DWState{ON,OFF}
    public enum DWMode{STANDARD,ECO}

    private DWState state;
    private DWMode mode;
    private boolean isOnBreak;
    protected DishwasherInboundPort dishwasherInboundPort;
    protected DishWasherSimulatorPlugin asp;

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

    @Override
    public void shutdown() throws ComponentShutdownException {
        try {
            dishwasherInboundPort.unpublishPort();
        }catch(Exception e) {
            throw new ComponentShutdownException(e);
        }
        super.shutdown();
    }


    @Override
    public void shutdownNow() throws ComponentShutdownException {
        try {
            dishwasherInboundPort.unpublishPort();
        }catch(Exception e) {
            throw new ComponentShutdownException(e);
        }
        super.shutdownNow();
    }


    @Override
    public boolean isOn(){
        return this.state == DWState.ON;
    }

    @Override
    public double getTimeLeft() throws Exception{
        return (double) asp.getModelStateValue(DishwasherModel.URI, "time");
    }

    @Override
    public DWMode getMode(){
        return mode;
    }

    @Override
    public void setMode(DWMode mode){
        this.mode = mode;
    }


    @Override
    public void switchBreak() {
        this.isOnBreak = !this.isOnBreak;
    }

    @Override
    public boolean isOnBreak() {
        return isOnBreak;
    }

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

    @Override
    public void setEmbeddingComponentStateValue(String name , Object value) {
        if(name.equals("state")) {
            this.state = (DWState) value;
        } else {
            throw new RuntimeException();
        }
    }

    @Override
    protected Architecture createLocalArchitecture(String modelURI) throws Exception {
        return DishwasherCoupledModel.build();
    }
}