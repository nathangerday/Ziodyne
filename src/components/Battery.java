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
import fr.sorbonne_u.devs_simulation.examples.molene.SimulationMain;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor;
import fr.sorbonne_u.utils.PlotterDescription;
import interfaces.BatteryI;
import ports.BatteryInboundPort;
import simulation.sil.battery.models.BatteryModel;
import simulation.sil.battery.plugin.BatterySimulatorPlugin;

public class Battery extends AbstractCyPhyComponent implements BatteryI,EmbeddingComponentAccessI {

    public enum BState {STANDBY,PRODUCING,CONSUMING}

    /** mode of the battery : idle(0), producing energy(1) or charging(2) */
    protected BState mode;

    /** port that exposes the offered interface with the
     *  given URI to ease the connection from controller components.*/
    protected BatteryInboundPort batteryInboundPort;

    /** the plugin in order to acces the model*/
    protected BatterySimulatorPlugin asp;


    protected Battery(String uri, String batteryInboundPortURI) throws Exception{
        super(uri,1,0);
        assert uri != null :new PreconditionException("uri can't be null!");
        this.mode = BState.STANDBY;
        this.addOfferedInterface(BatteryI.class);
        batteryInboundPort = new BatteryInboundPort(batteryInboundPortURI, this);
        batteryInboundPort.publishPort();

        //        assert this.mode == BState.STANDBY :
        //            new PostconditionException("The battery's state has not been initialised correctly !");
        //        assert this.isPortExisting(batteryInboundPort.getPortURI()):
        //            new PostconditionException("The component must have a "
        //                    + "port with URI " + batteryInboundPort.getPortURI());
        //        assert	this.findPortFromURI(batteryInboundPort.getPortURI()).
        //        getImplementedInterface().equals(BatteryI.class) :
        //            new PostconditionException("The component must have a "
        //                    + "port with implemented interface BatteryI");
        //        assert	this.findPortFromURI(batteryInboundPort.getPortURI()).isPublished() :
        //            new PostconditionException("The component must have a "
        //                    + "port published with URI " + batteryInboundPort.getPortURI());

        this.initialise();
    }

    private void initialise() throws Exception{
        Architecture localArchitecture = this.createLocalArchitecture(null);
        this.asp = new BatterySimulatorPlugin();
        this.asp.setPluginURI(localArchitecture.getRootModelURI()) ;
        this.asp.setSimulationArchitecture(localArchitecture) ;
        this.installPlugin(this.asp) ;
        this.toggleLogging();
    }

    @Override
    public void execute() throws Exception {
        // @remove A garder que en standalone
        HashMap<String,Object> simParams = new HashMap<String,Object>();
        simParams.put(
                BatteryModel.URI + ":" + BatteryModel.SERIES_CAPACITY + PlotterDescription.PLOTTING_PARAM_NAME,
                new PlotterDescription(
                        "Battery Capacity",
                        "Time (sec)",
                        "Capacity (W)",
                        SimulationMain.ORIGIN_X,
                        SimulationMain.ORIGIN_Y + SimulationMain.getPlotterHeight(),
                        SimulationMain.getPlotterWidth(),
                        SimulationMain.getPlotterHeight())) ;
        simParams.put(
                BatteryModel.URI + ":" + BatteryModel.SERIES_CONSUMPTION + PlotterDescription.PLOTTING_PARAM_NAME,
                new PlotterDescription(
                        "Battery Consumption",
                        "Time (sec)",
                        "Power (watt)",
                        SimulationMain.ORIGIN_X,
                        SimulationMain.ORIGIN_Y + 2 * SimulationMain.getPlotterHeight(),
                        SimulationMain.getPlotterWidth(),
                        SimulationMain.getPlotterHeight())) ;

        simParams.put(
                BatteryModel.URI + ":" + BatteryModel.SERIES_PRODUCTION + PlotterDescription.PLOTTING_PARAM_NAME,
                new PlotterDescription(
                        "Battery Production",
                        "Time (sec)",
                        "Power (W)",
                        SimulationMain.ORIGIN_X,
                        SimulationMain.ORIGIN_Y + 3 * SimulationMain.getPlotterHeight(),
                        SimulationMain.getPlotterWidth(),
                        SimulationMain.getPlotterHeight())) ;

        this.asp.setSimulationRunParameters(simParams);
        asp.setDebugLevel(0);
        asp.doStandAloneSimulation(0.0, 500.0);
    }

    @Override
    public double getMaxCapacity() throws Exception {
        return (double) asp.getModelStateValue(BatteryModel.URI, "max capacity");
    }

    @Override
    public double getCurrentCapacity() throws Exception {
        return (double) asp.getModelStateValue(BatteryModel.URI, "capacity");
    }

    @Override
    public void setMode(BState mode) throws Exception {
        this.mode = mode;
    }

    @Override
    public void shutdown() throws ComponentShutdownException {
        try {
            batteryInboundPort.unpublishPort();
        }catch(Exception e) {
            throw new ComponentShutdownException(e);
        }
        super.shutdown();
    }


    @Override
    public void shutdownNow() throws ComponentShutdownException {
        try {
            batteryInboundPort.unpublishPort();
        }catch(Exception e) {
            throw new ComponentShutdownException(e);
        }
        super.shutdownNow();
    }

    @Override
    protected Architecture createLocalArchitecture(String modelURI) throws Exception{
        Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
                new HashMap<>() ;
        atomicModelDescriptors.put(
                BatteryModel.URI,
                AtomicModelDescriptor.create(
                        BatteryModel.class,
                        BatteryModel.URI,
                        TimeUnit.SECONDS,
                        null,
                        SimulationEngineCreationMode.ATOMIC_ENGINE)) ;
        Architecture localArchitecture =
                new Architecture(
                        BatteryModel.URI,
                        atomicModelDescriptors,
                        new HashMap<>(),
                        TimeUnit.SECONDS) ;
        return localArchitecture ;  
    }

    @Override
    public Object getEmbeddingComponentStateValue(String name) throws Exception{
        if(name.equals("state")) {
            return mode;
        } else {
            throw new RuntimeException();
        }
    }

    @Override
    public void setEmbeddingComponentStateValue(String name , Object value) {
        if(name.equals("state")) {
            this.mode = (BState) value;
        } else {
            throw new RuntimeException();
        }
    }
}
