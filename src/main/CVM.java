package main;

import components.Controller;
import components.Fridge;
import components.Lamp;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;



public class CVM extends AbstractCVM{
    public CVM() throws Exception{
        super();
    }


    public static void main(String[] args) throws Exception{
        CVM a = new CVM();
        a.startStandardLifeCycle(1000L);
        Thread.sleep(5000L);
        System.exit(1);
    }

    /**
     * instantiate the components, publish their port and interconnect them.
     * 
     * <p><strong>Contract</strong></p>
     * 
     * <pre>
     * pre	!this.deploymentDone()
     * post	this.deploymentDone()
     * </pre>
     * 
     * @see fr.sorbonne_u.components.cvm.AbstractCVM#deploy()
     */
    @Override
    public void deploy() throws Exception{

        assert	!this.deploymentDone() ;

        // Create the controller
        AbstractComponent.createComponent(Controller.class.getCanonicalName(),
                new Object[] {
                        URI.COMPONENT_CONTROLLER,
                        URI.LAMP_CONTROLLER_OUTBOUND_PORT,
                        URI.LAMP_INBOUND_PORT,
                        URI.FRIDGE_CONTROLLER_OUTBOUND_PORT,
                        URI.FRIDGE_INBOUND_PORT,
                        URI.WINDTURBINE_CONTROLLER_OUTBOUND_PORT,
                        URI.WINDTURBINE_INBOUND_PORT});

        // Create the lamp
        AbstractComponent.createComponent(Lamp.class.getCanonicalName(),
                new Object[] {
                        URI.COMPONENT_LAMP,
                        URI.LAMP_INBOUND_PORT});

        // Create the fridge
        AbstractComponent.createComponent(Fridge.class.getCanonicalName(),
                new Object[] {
                        URI.COMPONENT_FRIDGE,
                        URI.FRIDGE_INBOUND_PORT});

        // Create the wind turbine
        AbstractComponent.createComponent(Fridge.class.getCanonicalName(),
                new Object[] {
                        URI.COMPONENT_WINDTURBINE,
                        URI.WINDTURBINE_INBOUND_PORT});
        super.deploy();

        assert this.deploymentDone();
    }


    /**
     * @see fr.sorbonne_u.components.cvm.AbstractCVM#finalise()
     */
    @Override
    public void finalise() throws Exception{
        super.finalise();
    }



}
