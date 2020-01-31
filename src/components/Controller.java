package components;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import connectors.BatteryConnector;
import connectors.DishwasherConnector;
import connectors.ElectricMeterConnector;
import connectors.FridgeConnector;
import connectors.LampConnector;
import connectors.WindTurbineConnector;
import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentAccessI;
import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.architectures.SimulationEngineCreationMode;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor;
import interfaces.DishwasherControllerI;
import interfaces.FridgeControllerI;
import interfaces.LampControllerI;
import interfaces.WindTurbineControllerI;
import ports.BatteryControllerOutboundPort;
import ports.DishwasherControllerOutboundPort;
import ports.ElectricMeterControllerOutboundPort;
import ports.FridgeControllerOutboundPort;
import ports.LampControllerOutboundPort;
import ports.WindTurbineControllerOutboundPort;
import simulation.sil.controller.models.ControllerModel;

public class Controller extends AbstractCyPhyComponent implements EmbeddingComponentAccessI {
    //equipements ports
    private LampControllerOutboundPort lampOutboundPort;
    private FridgeControllerOutboundPort fridgeOutboundPort;
    private WindTurbineControllerOutboundPort windTurbineOutboundPort;
    private DishwasherControllerOutboundPort dishwasherOutboundPort;
    private ElectricMeterControllerOutboundPort electricMeterOutboundPort;
    private BatteryControllerOutboundPort batteryOutboundPort;

    //equipments ports uri
    private String lampInboundPortURI;
    private String fridgeInboundPortURI;
    private String windTurbineInboundPortURI;
    private String dishwasherInboundPortURI;
    private String electricMeterInboundPortURI;
    private String batteryInboundPortURI;

    protected AtomicSimulatorPlugin asp;

    protected Controller(
            String uri,
            String lampOutboundPortURI,
            String lampInboundPortURI,
            String fridgeOutboundPortURI,
            String fridgeInboundPortURI,
            String windTurbineOutboundPortURI,
            String windTurbineInboundPortURI,
            String dishwasherOutboundPortURI,
            String dishwasherInboundPortURI,
            String electricMeterOutboundPortURI,
            String electricMeterInboundPortURI,
            String batteryOutboundPortURI,
            String batteryInboundPortURI) throws Exception{
        super(uri, 1, 0);
        this.lampInboundPortURI = lampInboundPortURI;
        this.fridgeInboundPortURI = fridgeInboundPortURI;
        this.windTurbineInboundPortURI = windTurbineInboundPortURI;
        this.dishwasherInboundPortURI = dishwasherInboundPortURI;
        this.electricMeterInboundPortURI = electricMeterInboundPortURI;
        this.batteryInboundPortURI = batteryInboundPortURI;

        this.addRequiredInterface(LampControllerI.class);
        this.addRequiredInterface(FridgeControllerI.class);
        this.addRequiredInterface(WindTurbineControllerI.class);
        this.addRequiredInterface(DishwasherControllerI.class);
        this.addRequiredInterface(ElectricMeterControllerOutboundPort.class);
        this.addRequiredInterface(BatteryControllerOutboundPort.class);

        this.lampOutboundPort = new LampControllerOutboundPort(lampOutboundPortURI,this);
        this.lampOutboundPort.publishPort();
        this.fridgeOutboundPort = new FridgeControllerOutboundPort(fridgeOutboundPortURI,this);
        this.fridgeOutboundPort.publishPort();
        this.windTurbineOutboundPort = new WindTurbineControllerOutboundPort(windTurbineOutboundPortURI,this);
        this.windTurbineOutboundPort.publishPort();
        this.dishwasherOutboundPort = new DishwasherControllerOutboundPort(dishwasherOutboundPortURI, this);
        this.dishwasherOutboundPort.publishPort();
        this.electricMeterOutboundPort = new ElectricMeterControllerOutboundPort(electricMeterOutboundPortURI, this);
        this.electricMeterOutboundPort.publishPort();
        this.batteryOutboundPort = new BatteryControllerOutboundPort(batteryOutboundPortURI,this);
        this.batteryOutboundPort.publishPort();

        this.initialise();
    }

    private void initialise() throws Exception {
        Architecture localArchitecture = this.createLocalArchitecture(null);
        Controller ref = this;
        this.asp = new AtomicSimulatorPlugin() {
            private static final long serialVersionUID = 1L;

            @Override
            public void setSimulationRunParameters(
                    Map<String, Object> simParams
                    ) throws Exception{
                simParams.put(ControllerModel.COMPONENT_REF,ref);
                super.setSimulationRunParameters(simParams);
                simParams.remove(ControllerModel.COMPONENT_REF);
            }
        };
        this.asp.setPluginURI(localArchitecture.getRootModelURI());
        this.asp.setSimulationArchitecture(localArchitecture);
        this.installPlugin(this.asp);
        this.tracer.setTitle("Controller");
        this.tracer.setRelativePosition(1, 0);
        this.toggleTracing();
    }


    /**
     * a component is always started by calling this method, so intercept the
     * call and make sure the task of the component is executed.
     * 
     * <p><strong>Contract</strong></p>
     * 
     * <pre>
     * pre	true				// no more preconditions.
     * post	true				// no more postconditions.
     * </pre>
     * 
     * @see fr.sorbonne_u.components.AbstractComponent#start()
     */
    @Override
    public void start() throws ComponentStartException{
        super.start();
        try {
            //connect with the lamp
            this.doPortConnection(
                    this.lampOutboundPort.getPortURI(),
                    this.lampInboundPortURI,
                    LampConnector.class.getCanonicalName());
            //connect with the fridge
            this.doPortConnection(
                    this.fridgeOutboundPort.getPortURI(),
                    this.fridgeInboundPortURI,
                    FridgeConnector.class.getCanonicalName());
            //connect with the wind turbine
            this.doPortConnection(
                    this.windTurbineOutboundPort.getPortURI(),
                    this.windTurbineInboundPortURI,
                    WindTurbineConnector.class.getCanonicalName());
            this.doPortConnection(
                    this.dishwasherOutboundPort.getPortURI(),
                    this.dishwasherInboundPortURI,
                    DishwasherConnector.class.getCanonicalName());
            this.doPortConnection(
                    this.electricMeterOutboundPort.getPortURI(),
                    this.electricMeterInboundPortURI,
                    ElectricMeterConnector.class.getCanonicalName());
            this.doPortConnection(
                    this.batteryOutboundPort.getPortURI(),
                    this.batteryInboundPortURI,
                    BatteryConnector.class.getCanonicalName());
        }catch(Exception e) {
            throw new ComponentStartException(e);
        }
    }




    @Override
    public void shutdown() throws ComponentShutdownException {
        try {
            this.lampOutboundPort.unpublishPort();
            this.fridgeOutboundPort.unpublishPort();
            this.windTurbineOutboundPort.unpublishPort();
            this.dishwasherOutboundPort.unpublishPort();
            this.electricMeterOutboundPort.unpublishPort();
            this.batteryOutboundPort.unpublishPort();
        }catch(Exception e) {
            throw new ComponentShutdownException(e);
        }
        super.shutdown();
    }

    @Override
    public void shutdownNow() throws ComponentShutdownException {
        try {
            this.lampOutboundPort.unpublishPort();
            this.fridgeOutboundPort.unpublishPort();
            this.windTurbineOutboundPort.unpublishPort();
            this.dishwasherOutboundPort.unpublishPort();
            this.electricMeterOutboundPort.unpublishPort();
            this.batteryOutboundPort.unpublishPort();
        }catch(Exception e) {
            throw new ComponentShutdownException(e);
        }
        super.shutdownNow();
    }

    @Override
    public void finalise() throws Exception{
        this.doPortDisconnection(this.lampOutboundPort.getPortURI());
        this.doPortDisconnection(this.fridgeOutboundPort.getPortURI());
        this.doPortDisconnection(this.windTurbineOutboundPort.getPortURI());
        this.doPortDisconnection(this.dishwasherOutboundPort.getPortURI());
        this.doPortDisconnection(this.electricMeterOutboundPort.getPortURI());
        this.doPortDisconnection(this.batteryOutboundPort.getPortURI());
        super.finalise();
    }

    public double getControlPeriod() {
        return 1.0;
    }

    public void controlTask(double simulatedTime) throws Exception {
    }

    @Override
    protected Architecture createLocalArchitecture(String modelURI) throws Exception {
        Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
                new HashMap<>() ;

        atomicModelDescriptors.put(
                ControllerModel.URI,
                AtomicModelDescriptor.create(
                        ControllerModel.class,
                        ControllerModel.URI,
                        TimeUnit.SECONDS,
                        null,
                        SimulationEngineCreationMode.ATOMIC_ENGINE)) ;
        Architecture localArchitecture =
                new Architecture(
                        ControllerModel.URI,
                        atomicModelDescriptors,
                        new HashMap<>(),
                        TimeUnit.SECONDS) ;
        return localArchitecture ;
    }
}
