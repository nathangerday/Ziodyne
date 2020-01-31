package simulation;

import components.Dishwasher;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;
import main.URI;

public class			CVM
extends AbstractCVM
{
    public				CVM() throws Exception
    {
        super() ;
        SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 10L ;
    }

    @Override
    public void			deploy() throws Exception
    {
        @SuppressWarnings("unused")
        //        String componentLampURI =
        //        AbstractComponent.createComponent(
        //                Lamp.class.getCanonicalName(),
        //                new Object[]{URI.COMPONENT_LAMP,URI.LAMP_INBOUND_PORT}) ;

        //        String componentWindTurbineURI =
        //                AbstractComponent.createComponent(
        //                        WindTurbine.class.getCanonicalName(),
        //                        new Object[]{URI.COMPONENT_WINDTURBINE,URI.WINDTURBINE_INBOUND_PORT}) ;

        //        String componentFridgeURI =
        //                AbstractComponent.createComponent(
        //                        Fridge.class.getCanonicalName(),
        //                        new Object[]{URI.COMPONENT_FRIDGE,URI.FRIDGE_INBOUND_PORT}) ;

        //        String componentBatteryURI =
        //        AbstractComponent.createComponent(
        //                Battery.class.getCanonicalName(),
        //                new Object[]{URI.COMPONENT_BATTERY,URI.BATTERY_INBOUND_PORT}) ;

        //        String componentElectricMeterURI =
        //        AbstractComponent.createComponent(
        //                ElectricMeter.class.getCanonicalName(),
        //                new Object[]{URI.COMPONENT_ELECTRICMETER,URI.ELECTRICMETER_INBOUND_PORT}) ;

        String componentDishWasherURI =
        AbstractComponent.createComponent(
                Dishwasher.class.getCanonicalName(),
                new Object[]{URI.COMPONENT_DISHWASHER,URI.DISHWASHER_INBOUND_PORT}) ;

        super.deploy();
    }

    public static void	main(String[] args)
    {
        try {
            CVM c = new CVM() ;
            c.startStandardLifeCycle(20000L) ;
            System.exit(0) ;
        } catch (Exception e) {
            throw new RuntimeException(e) ;
        }
    }
}