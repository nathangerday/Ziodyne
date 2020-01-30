package components;

import java.util.HashMap;

import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentAccessI;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.PostconditionException;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.examples.molene.SimulationMain;
import fr.sorbonne_u.utils.PlotterDescription;
import interfaces.LampI;
import ports.LampInboundPort;
import simulation.sil.lamp.models.LampCoupledModel;
import simulation.sil.lamp.models.LampModel;
import simulation.sil.lamp.plugin.LampSimulatorPlugin;

public class Lamp extends AbstractCyPhyComponent implements LampI,EmbeddingComponentAccessI{

    public enum LampState{OFF,LOW,MEDIUM,HIGH}

    protected LampInboundPort lampInboundPort;
    protected LampState state;

    protected LampSimulatorPlugin asp ;

    protected Lamp(String uri, String lampInboundPortURI) throws Exception {
        super(uri, 1, 0);
        this.state = LampState.OFF;
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

    private void initialise() throws Exception{
        Architecture localArchitecture = this.createLocalArchitecture(null) ;
        this.asp = new LampSimulatorPlugin() ;
        this.asp.setPluginURI(localArchitecture.getRootModelURI()) ;
        this.asp.setSimulationArchitecture(localArchitecture) ;
        this.installPlugin(this.asp) ;
        this.toggleLogging() ;
    }

    @Override
    public void execute() throws Exception {
        // @remove A garder que en standalone
        PlotterDescription pd =
                new PlotterDescription(
                        "Lamp Power",
                        "Time (sec)",
                        "Power (Watt)",
                        SimulationMain.ORIGIN_X,
                        SimulationMain.ORIGIN_Y + SimulationMain.getPlotterHeight(),
                        SimulationMain.getPlotterWidth()*2,
                        SimulationMain.getPlotterHeight()*2);

        HashMap<String,Object> simParams = new HashMap<String,Object>();
        simParams.put(LampModel.URI + ":" + PlotterDescription.PLOTTING_PARAM_NAME, pd);
        this.asp.setSimulationRunParameters(simParams);
        asp.setDebugLevel(0);
        asp.doStandAloneSimulation(0.0, 500.0);
    }

    @Override
    public void shutdown() throws ComponentShutdownException {
        try {
            this.lampInboundPort.unpublishPort();
        }catch(Exception e) {
            throw new ComponentShutdownException(e);
        }
        super.shutdown();
    }

    @Override
    public void shutdownNow() throws ComponentShutdownException {
        try {
            this.lampInboundPort.unpublishPort();
        }catch(Exception e) {
            throw new ComponentShutdownException(e);
        }
        super.shutdownNow();
    }

    @Override
    public LampState getState() {
        return state;
    }

    @Override
    protected Architecture createLocalArchitecture(String modelURI) throws Exception{
        return LampCoupledModel.build();
    }

    @Override
    public Object getEmbeddingComponentStateValue(String name) throws Exception{
        if(name.equals("state")) {
            return state;
        } else {
            throw new RuntimeException();
        }
    }

    @Override
    public void setEmbeddingComponentStateValue(String name , Object value) {
        if(name.equals("state")) {
            this.state = (LampState) value;
        } else {
            throw new RuntimeException();
        }
    }
}
