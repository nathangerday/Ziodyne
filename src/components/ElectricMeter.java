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

public class ElectricMeter extends AbstractCyPhyComponent implements ElectricMeterI,EmbeddingComponentAccessI{

    protected ElectricMeterInboundPort electricMeterInboundPort;
    protected ElectricMeterSimulatorPlugin asp;

    protected ElectricMeter(String uri, String electricMeterInboundPortURI) throws Exception{
        super(uri,1,0);
        assert uri != null :  new PreconditionException("uri can't be null!") ;
        this.addOfferedInterface(ElectricMeterI.class);
        this.electricMeterInboundPort = new ElectricMeterInboundPort(electricMeterInboundPortURI, this);
        this.electricMeterInboundPort.publishPort();

        this.initialise();

        assert this.isPortExisting(electricMeterInboundPort.getPortURI()):
            new PostconditionException("The component must have a "
                    + "port with URI " + electricMeterInboundPort.getPortURI()) ;

        assert  this.findPortFromURI(electricMeterInboundPort.getPortURI()).
        getImplementedInterface().equals(ElectricMeterI.class) :
            new PostconditionException("The component must have a "
                    + "port with implemented interface ElectricMeterI") ;

        assert  this.findPortFromURI(electricMeterInboundPort.getPortURI()).isPublished() :
            new PostconditionException("The component must have a "
                    + "port published with URI " + electricMeterInboundPort.getPortURI()) ;
    }

    private void initialise() throws Exception{
        Architecture localArchitecture = this.createLocalArchitecture(null) ;
        this.asp = new ElectricMeterSimulatorPlugin() ;
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
    //                ElectricMeterModel.URI + ":" + ElectricMeterModel.SERIES_AVAILABLE + PlotterDescription.PLOTTING_PARAM_NAME,
    //                new PlotterDescription(
    //                        "Available Energy",
    //                        "Time (sec)",
    //                        "Power (W)",
    //                        SimulationMain.ORIGIN_X,
    //                        SimulationMain.ORIGIN_Y + SimulationMain.getPlotterHeight(),
    //                        SimulationMain.getPlotterWidth(),
    //                        SimulationMain.getPlotterHeight())) ;
    //        simParams.put(
    //                ElectricMeterModel.URI + ":" + ElectricMeterModel.SERIES_CONSUMPTION + PlotterDescription.PLOTTING_PARAM_NAME,
    //                new PlotterDescription(
    //                        "Total Consumption",
    //                        "Time (sec)",
    //                        "Power (W)",
    //                        SimulationMain.ORIGIN_X,
    //                        SimulationMain.ORIGIN_Y + 2 * SimulationMain.getPlotterHeight(),
    //                        SimulationMain.getPlotterWidth(),
    //                        SimulationMain.getPlotterHeight())) ;
    //
    //        simParams.put(
    //                ElectricMeterModel.URI + ":" + ElectricMeterModel.SERIES_PRODUCTION + PlotterDescription.PLOTTING_PARAM_NAME,
    //                new PlotterDescription(
    //                        "Total Production",
    //                        "Time (sec)",
    //                        "Power (W)",
    //                        SimulationMain.ORIGIN_X,
    //                        SimulationMain.ORIGIN_Y + 3 * SimulationMain.getPlotterHeight(),
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
            this.electricMeterInboundPort.unpublishPort();
        }catch(Exception e) {
            throw new ComponentShutdownException(e);
        }
        super.shutdown();
    }

    @Override
    public void shutdownNow() throws ComponentShutdownException {
        try {
            this.electricMeterInboundPort.unpublishPort();
        }catch(Exception e) {
            throw new ComponentShutdownException(e);
        }
        super.shutdownNow();
    }

    @Override
    public double getAvailableEnergy() throws Exception {
        return (double) asp.getModelStateValue(ElectricMeterModel.URI, "available");
    }

    @Override
    public double getProduction() throws Exception {
        return (double) asp.getModelStateValue(ElectricMeterModel.URI, "production");
    }

    @Override
    public double getConsumption() throws Exception {
        return (double) asp.getModelStateValue(ElectricMeterModel.URI, "consumption");
    }

    @Override
    protected Architecture createLocalArchitecture(String modelURI) throws Exception{
        Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
                new HashMap<>() ;
        atomicModelDescriptors.put(
                ElectricMeterModel.URI,
                AtomicModelDescriptor.create(
                        ElectricMeterModel.class,
                        ElectricMeterModel.URI,
                        TimeUnit.SECONDS,
                        null,
                        SimulationEngineCreationMode.ATOMIC_ENGINE)) ;
        Architecture localArchitecture =
                new Architecture(
                        ElectricMeterModel.URI,
                        atomicModelDescriptors,
                        new HashMap<>(),
                        TimeUnit.SECONDS) ;
        return localArchitecture ;  
    }
}
