package simulation;

import simulation.components.dishwasher.Dishwasher;
import simulation.components.fridge.Fridge;
import simulation.components.lamp.Lamp;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;

public class			CVM
        extends AbstractCVM
{
    public				CVM() throws Exception
    {
        super() ;
        SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 10L ;
    }

    /**
     * @see fr.sorbonne_u.components.cvm.AbstractCVM#deploy()
     */
    @Override
    public void			deploy() throws Exception
    {
        @SuppressWarnings("unused")
        String componentLampURI =
                AbstractComponent.createComponent(
                        Lamp.class.getCanonicalName(),
                        new Object[]{}) ;
        
        String componentFridgeURI =
                AbstractComponent.createComponent(
                        Fridge.class.getCanonicalName(),
                        new Object[]{}) ;
        
        /*
        String componentDishwasherURI =
                AbstractComponent.createComponent(
                        Dishwasher.class.getCanonicalName(),
                        new Object[]{}) ;*/
        
        super.deploy();
    }

    public static void	main(String[] args)
    {
        try {
            CVM c = new CVM() ;
            c.startStandardLifeCycle(10000L) ;
            System.exit(0) ;
        } catch (Exception e) {
            throw new RuntimeException(e) ;
        }
    }
}