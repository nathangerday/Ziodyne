package main;

import components.Battery;
import components.Controller;
import components.Dishwasher;
import components.ElectricMeter;
import components.Fridge;
import components.Lamp;
import components.WindTurbine;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractDistributedCVM;

public class DistributedCVM extends AbstractDistributedCVM{

    public DistributedCVM(String[] args,int xLayout, int yLayout) throws Exception {
        super(args,xLayout,yLayout);
    }

    @Override
    public void initialise() throws Exception{
        super.initialise();
    }

    public static void	main(String[] args)
    {
        try {
            DistributedCVM da  = new DistributedCVM(args, 2, 5) ;
            da.startStandardLifeCycle(6000L) ;
            Thread.sleep(10000L) ;
            System.exit(0) ;
        } catch (Exception e) {
            throw new RuntimeException(e) ;
        }
    }

    @Override
    public void instantiateAndPublish() throws Exception{
        if(thisJVMURI.equals(URI.JVM_CONTROLLER)) {
            // Create the controller
            AbstractComponent.createComponent(Controller.class.getCanonicalName(),
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

            assert this.isDeployedComponent(URI.COMPONENT_CONTROLLER);
            this.toggleLogging(URI.COMPONENT_CONTROLLER);
            this.toggleTracing(URI.COMPONENT_CONTROLLER);

        }
        else if (thisJVMURI.equals(URI.JVM_ELECTRICMETER)) {
            AbstractComponent.createComponent(ElectricMeter.class.getCanonicalName(),
                    new Object[] {
                            URI.COMPONENT_ELECTRICMETER,
                            URI.ELECTRICMETER_INBOUND_PORT});

            assert this.isDeployedComponent(URI.COMPONENT_ELECTRICMETER);
            this.toggleLogging(URI.COMPONENT_ELECTRICMETER);
            this.toggleTracing(URI.COMPONENT_ELECTRICMETER);
        }
        else if (thisJVMURI.equals(URI.JVM_COMPONENTS)) {
            AbstractComponent.createComponent(Lamp.class.getCanonicalName(),
                    new Object[] {
                            URI.COMPONENT_LAMP,
                            URI.LAMP_INBOUND_PORT});
            assert this.isDeployedComponent(URI.COMPONENT_LAMP);
            this.toggleLogging(URI.COMPONENT_LAMP);
            this.toggleTracing(URI.COMPONENT_LAMP);

            AbstractComponent.createComponent(Fridge.class.getCanonicalName(),
                    new Object[] {
                            URI.COMPONENT_FRIDGE,
                            URI.FRIDGE_INBOUND_PORT});
            assert this.isDeployedComponent(URI.COMPONENT_FRIDGE);
            this.toggleLogging(URI.COMPONENT_FRIDGE);
            this.toggleTracing(URI.COMPONENT_FRIDGE);

            AbstractComponent.createComponent(WindTurbine.class.getCanonicalName(),
                    new Object[] {
                            URI.COMPONENT_WINDTURBINE,
                            URI.WINDTURBINE_INBOUND_PORT});
            assert this.isDeployedComponent(URI.COMPONENT_WINDTURBINE);
            this.toggleLogging(URI.COMPONENT_WINDTURBINE);
            this.toggleTracing(URI.COMPONENT_WINDTURBINE);

            AbstractComponent.createComponent(Dishwasher.class.getCanonicalName(),
                    new Object[] {
                            URI.COMPONENT_DISHWASHER,
                            URI.DISHWASHER_INBOUND_PORT});
            assert this.isDeployedComponent(URI.COMPONENT_DISHWASHER);
            this.toggleLogging(URI.COMPONENT_DISHWASHER);
            this.toggleTracing(URI.COMPONENT_DISHWASHER);

            AbstractComponent.createComponent(Battery.class.getCanonicalName(),
                    new Object[] {
                            URI.COMPONENT_BATTERY,
                            URI.BATTERY_INBOUND_PORT});
            assert this.isDeployedComponent(URI.COMPONENT_BATTERY);
            this.toggleLogging(URI.COMPONENT_BATTERY);
            this.toggleTracing(URI.COMPONENT_BATTERY);
        }
        else {
            System.out.println("Unknown JVM URI... " + thisJVMURI) ;
        }
        super.instantiateAndPublish();
    }

    @Override
    public void	interconnect() throws Exception{
        assert	this.isIntantiatedAndPublished() ;
        super.interconnect();
    }

    @Override
    public void start() throws Exception{
        super.start();
    }

    @Override
    public void finalise() throws Exception{
        super.finalise();
    }
}
