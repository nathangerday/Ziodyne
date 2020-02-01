package main;

import java.util.HashMap;

import components.Battery;
import components.Controller;
import components.Coordinator;
import components.Dishwasher;
import components.ElectricMeter;
import components.Fridge;
import components.Lamp;
import components.Supervisor;
import components.WindTurbine;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;
import simulation.overall.SGCoupledModel;
import simulation.sil.battery.models.BatteryModel;
import simulation.sil.controller.models.ControllerModel;
import simulation.sil.dishwasher.models.DishwasherCoupledModel;
import simulation.sil.electricmeter.models.ElectricMeterModel;
import simulation.sil.fridge.models.FridgeCoupledModel;
import simulation.sil.lamp.models.LampCoupledModel;
import simulation.sil.windturbine.models.WindTurbineCoupledModel;



public class CVM extends AbstractCVM{
    public CVM() throws Exception{
        super();
        SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 2L ;
    }

    @Override
    public void deploy() throws Exception{

        assert	!this.deploymentDone() ;

        HashMap<String,String> hm = new HashMap<>() ;

        // Create the controller
        String controllerComponent = AbstractComponent.createComponent(Controller.class.getCanonicalName(),
                new Object[] {
                        URI.COMPONENT_CONTROLLER,
                        URI.LAMP_CONTROLLER_OUTBOUND_PORT,
                        URI.LAMP_INBOUND_PORT,
                        URI.FRIDGE_CONTROLLER_OUTBOUND_PORT,
                        URI.FRIDGE_INBOUND_PORT,
                        URI.WINDTURBINE_CONTROLLER_OUTBOUND_PORT,
                        URI.WINDTURBINE_INBOUND_PORT,
                        URI.DISHWASHER_CONTROLLER_OUTBOUND_PORT,
                        URI.DISHWASHER_INBOUND_PORT,
                        URI.ELECTRICMETER_CONTROLLER_OUTBOUND_PORT,
                        URI.ELECTRICMETER_INBOUND_PORT,
                        URI.BATTERY_CONTROLLER_OUTBOUND_PORT,
                        URI.BATTERY_INBOUND_PORT});
        hm.put(ControllerModel.URI, controllerComponent);

        // Create the lamp
        String lampComponent = AbstractComponent.createComponent(Lamp.class.getCanonicalName(),
                new Object[] {
                        URI.COMPONENT_LAMP,
                        URI.LAMP_INBOUND_PORT});
        hm.put(LampCoupledModel.URI, lampComponent);

        // Create the fridge
        String fridgeComponent = AbstractComponent.createComponent(Fridge.class.getCanonicalName(),
                new Object[] {
                        URI.COMPONENT_FRIDGE,
                        URI.FRIDGE_INBOUND_PORT});
        hm.put(FridgeCoupledModel.URI, fridgeComponent);

        // Create the wind turbine
        String WTComponent = AbstractComponent.createComponent(WindTurbine.class.getCanonicalName(),
                new Object[] {
                        URI.COMPONENT_WINDTURBINE,
                        URI.WINDTURBINE_INBOUND_PORT});
        hm.put(WindTurbineCoupledModel.URI, WTComponent);

        // Create the dishwasher
        String DWComponent = AbstractComponent.createComponent(Dishwasher.class.getCanonicalName(),
                new Object[] {
                        URI.COMPONENT_DISHWASHER,
                        URI.DISHWASHER_INBOUND_PORT});
        hm.put(DishwasherCoupledModel.URI, DWComponent);

        // Create the electric meter
        String EMComponent = AbstractComponent.createComponent(ElectricMeter.class.getCanonicalName(),
                new Object[] {
                        URI.COMPONENT_ELECTRICMETER,
                        URI.ELECTRICMETER_INBOUND_PORT});
        hm.put(ElectricMeterModel.URI, EMComponent);

        //Create the battery
        String batteryComponent = AbstractComponent.createComponent(Battery.class.getCanonicalName(),
                new Object[] {
                        URI.COMPONENT_BATTERY,
                        URI.BATTERY_INBOUND_PORT});
        hm.put(BatteryModel.URI, batteryComponent);

        //Create the coordinator
        String coordinatorComponent = AbstractComponent.createComponent(Coordinator.class.getCanonicalName(),
                new Object[] {});
        hm.put(SGCoupledModel.URI, coordinatorComponent);

        //Create the supervisor
        AbstractComponent.createComponent(
                Supervisor.class.getCanonicalName(),
                new Object[]{hm}) ;

        super.deploy();
    }

    public static void main(String[] args) throws Exception{
        try {
            CVM c = new CVM() ;
            c.startStandardLifeCycle(75000L) ;
            Thread.sleep(10000L) ;
            System.exit(0) ;
        } catch (Exception e) {
            throw new RuntimeException(e) ;
        }
    }
}
