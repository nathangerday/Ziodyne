package components;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import components.Battery.BState;
import components.Dishwasher.DWMode;
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
import simulation.sil.battery.models.BatteryModel;
import simulation.sil.controller.models.ControllerModel;
import simulation.sil.dishwasher.models.DishwasherModel;
import simulation.sil.fridge.models.FridgeModel;
import simulation.sil.lamp.models.LampModel;

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
        this.toggleTracing();
        this.tracer.setTitle("Controller");
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
            //connect with the dish washer
            this.doPortConnection(
                    this.dishwasherOutboundPort.getPortURI(),
                    this.dishwasherInboundPortURI,
                    DishwasherConnector.class.getCanonicalName());
            //connect with the electric meter
            this.doPortConnection(
                    this.electricMeterOutboundPort.getPortURI(),
                    this.electricMeterInboundPortURI,
                    ElectricMeterConnector.class.getCanonicalName());
            //connect with the battery
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
        double energy = this.electricMeterOutboundPort.getAvailableEnergy();
        if(energy > 0) {
            positiveEnergy(simulatedTime, energy);
        } else {
            negativeEnergy(simulatedTime, energy);
        }
    }

    /**
     * Method called when we have enough energy.
     * The order of the rule is important. Greater the number,
     * more will be the added consumption.
     * @param simulatedTime
     * @param energy
     * @throws Exception
     */
    private void positiveEnergy(double simulatedTime, double energy) throws Exception {
        int rule = 0;
        double nrj = energy;
        while(nrj > 0) {
            switch(rule) {
            case 0:
                //If the dishwasher was on break,
                //we can switch it because it won't consume power when restarting
                //The test of isOn(), is just in the worst case
                if(dishwasherOutboundPort.isOnBreak() &&
                        !dishwasherOutboundPort.isOn()) {
                    dishwasherOutboundPort.switchBreak();
                }
                rule++;
                break;

            case 1:
                //Same for the lamp
                //But we need to be carefull of the consumption
                if(lampOutboundPort.isOnBreak()) {
                    double consumption = 0.0;
                    switch(lampOutboundPort.getState()) {
                    case OFF:
                        break;
                    case LOW:
                        consumption = LampModel.LOW_MODE_CONSUMPTION;
                        break;
                    case MEDIUM:
                        consumption = LampModel.MEDIUM_MODE_CONSUMPTION;
                        break;
                    case HIGH:
                        consumption = LampModel.HIGH_MODE_CONSUMPTION;
                        break;
                    }
                    if(nrj >= consumption) {
                        lampOutboundPort.switchBreak();
                        nrj -= consumption;
                    }
                }
                rule++;
                break;

            case 2:
                //Same for the freezer
                if(fridgeOutboundPort.isFreezerOnBreak()) {
                    double consumption = 0.0;
                    if(fridgeOutboundPort.isFreezerOn()) {
                        consumption = FridgeModel.FREEZER_ON_CONSUMPTION;
                    }
                    if (nrj >= consumption) {
                        fridgeOutboundPort.switchFreezerBreak();
                        nrj -= consumption;
                    }
                }
                rule++;
                break;

            case 3:
                //Same for fridge
                if(fridgeOutboundPort.isFridgeOnBreak()) {
                    double consumption = 0.0;
                    if(fridgeOutboundPort.isFridgeOn()) {
                        consumption = FridgeModel.FRIDGE_ON_CONSUMPTION;
                    }
                    if (nrj >= consumption) {
                        fridgeOutboundPort.switchFridgeBreak();
                        nrj -= consumption;
                    }
                }
                rule++;
                break;

            case 4:
                //If the battery is PRODUCING, isn't full and we can switch to STANDBY
                if(batteryOutboundPort.getMode() == BState.PRODUCING
                && batteryOutboundPort.getCurrentCapacity() < batteryOutboundPort.getMaxCapacity()
                && nrj >= BatteryModel.BATTERY_MODIF) {
                    nrj -= BatteryModel.BATTERY_MODIF;
                    batteryOutboundPort.setMode(BState.STANDBY);
                }
                rule++;
                break;

            case 5:
                //If the dishwasher's mode is ECO and we can switch to STANDARD
                if(dishwasherOutboundPort.getMode() == DWMode.ECO) {
                    double consumption = 0.0;
                    if(dishwasherOutboundPort.isOn()) {
                        consumption = DishwasherModel.STANDARD_MODE_CONSUMPTION -
                                DishwasherModel.ECO_MODE_CONSUMPTION;
                    }
                    if(nrj >= consumption) {
                        dishwasherOutboundPort.setMode(DWMode.STANDARD);
                        nrj -= consumption;
                    }
                }
                rule++;
                break;

            case 6:
                //If the battery is STANDBY, isn't full and we can switch to CONSUMING
                if(batteryOutboundPort.getMode() != BState.CONSUMING
                && batteryOutboundPort.getCurrentCapacity() < batteryOutboundPort.getMaxCapacity()
                && nrj >= BatteryModel.BATTERY_MODIF) {
                    nrj -= BatteryModel.BATTERY_MODIF;
                    batteryOutboundPort.setMode(BState.CONSUMING);
                }
                rule++;
                break;

            default:
                //We can't use the available energy
                //We can sell it
                //this.logMessage("SELL : " + nrj + " Watts");
                nrj = 0;
                break;
            }
        }
    }

    /**
     * Method called when we don't have enough energy.
     * The order of the rule is important. Greater the number,
     * less will be the removed consumption.
     * @param simulatedTime
     * @param energy
     * @throws Exception
     */
    private void negativeEnergy(double simulatedTime, double energy) throws Exception {
        int rule = 0;
        double nrj = energy;
        while(nrj < 0) {
            switch(rule) {
            case 0:
                //We look if the battery was charging.
                //If yes we can set its mode to STANDBY
                if(batteryOutboundPort.getMode() == BState.CONSUMING) {
                    batteryOutboundPort.setMode(BState.STANDBY);
                    nrj += BatteryModel.BATTERY_MODIF; 
                }
                rule++;
                break;

            case 1:
                //We check if we set the battery mode to producing
                if(batteryOutboundPort.getMode() != BState.PRODUCING &&
                batteryOutboundPort.getCurrentCapacity() > 0) {
                    batteryOutboundPort.setMode(BState.PRODUCING);
                    nrj += BatteryModel.BATTERY_MODIF;
                }
                rule++;
                break;

            case 2:
                //If the dishwasher is not on break, ON and STANDARD, we switch to ECO
                if(!dishwasherOutboundPort.isOnBreak() &&
                        dishwasherOutboundPort.isOn() &&
                        dishwasherOutboundPort.getMode() == DWMode.STANDARD) {
                    dishwasherOutboundPort.setMode(DWMode.ECO);
                    nrj += DishwasherModel.STANDARD_MODE_CONSUMPTION -
                            DishwasherModel.ECO_MODE_CONSUMPTION;
                }
                rule++;
                break;

            case 3:
                //If the dishwasher is not on break and ON,we switch it
                if(!dishwasherOutboundPort.isOnBreak() && dishwasherOutboundPort.isOn()) {
                    double consumption = DishwasherModel.STANDARD_MODE_CONSUMPTION;
                    if(dishwasherOutboundPort.getMode() == DWMode.ECO) {
                        consumption = DishwasherModel.ECO_MODE_CONSUMPTION;
                    }
                    nrj += consumption;
                    dishwasherOutboundPort.switchBreak();
                }
                rule++;
                break;

            case 4:
                //If the fridge is not on break and is ON, we switch it
                if(!fridgeOutboundPort.isFridgeOnBreak() && fridgeOutboundPort.isFridgeOn()) {
                    nrj += FridgeModel.FRIDGE_ON_CONSUMPTION;
                    fridgeOutboundPort.switchFridgeBreak();
                }
                rule++;
                break;

            case 5:
                //If the freezer is not on break and is ON, we switch it
                if(!fridgeOutboundPort.isFreezerOnBreak() && fridgeOutboundPort.isFreezerOn()) {
                    nrj += FridgeModel.FREEZER_ON_CONSUMPTION;
                    fridgeOutboundPort.switchFreezerBreak();
                }
                rule++;
                break;

            case 6:
                //If the lamp is not on break, we switch it
                if(!lampOutboundPort.isOnBreak()) {
                    double consumption = 0.0;
                    switch(lampOutboundPort.getState()) {
                    case OFF:
                        break;
                    case LOW:
                        consumption = LampModel.LOW_MODE_CONSUMPTION;
                        break;
                    case MEDIUM:
                        consumption = LampModel.MEDIUM_MODE_CONSUMPTION;
                        break;
                    case HIGH:
                        consumption = LampModel.HIGH_MODE_CONSUMPTION;
                        break;
                    }
                    if(consumption != 0.0) {
                        nrj += consumption;
                        lampOutboundPort.switchBreak();
                    }
                }
                rule++;
                break;

            default:
                //Should be impossible, but because of delays, maybe
                this.logMessage("energy = " + nrj);
                nrj = 0;
                break;
            }
        }
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
