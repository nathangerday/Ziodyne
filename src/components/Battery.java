package components;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.PostconditionException;
import fr.sorbonne_u.components.exceptions.PreconditionException;
import interfaces.BatteryI;
import ports.BatteryInboundPort;

public class Battery extends AbstractComponent implements BatteryI {

    //port that exposes the offered interface with the
    // given URI to ease the connection from controller components.
    protected BatteryInboundPort batteryInboundPort;
    //an integer value that reports the quantity of energy produced
    protected int energyProduced;
    //an integer value indicating the maximum value of energy the battery can stock
    protected int maxCapacity;
    //an integer value indicating the current value of energy inside the battery
    protected int currentCapacity;
    //a boolean value On/Off
    protected boolean isOn;
    //mode of the battery : idle(0), producing energy(1) or charging(2)
    protected BatteryState mode;


    protected Battery(String uri, String batteryInboundPortURI) throws Exception{
        super(uri,1,0);
        assert uri != null :new PreconditionException("uri can't be null!");
        this.energyProduced =0;
        this.maxCapacity =1000;
        this.currentCapacity = 0;
        this.isOn = false;
        this.mode = BatteryState.Idle;
        this.addOfferedInterface(BatteryI.class);
        batteryInboundPort = new BatteryInboundPort(batteryInboundPortURI, this);
        batteryInboundPort.publishPort();

        assert this.energyProduced == 0 :
                new PostconditionException("The battery's state has not been initialised correctly !");
        assert this.maxCapacity == 1000 :
                new PostconditionException("The battery's state has not been initialised correctly !");
        assert this.currentCapacity == 0 :
                new PostconditionException("The battery's state has not been initialised correctly !");
        assert this.isOn == false :
                new PostconditionException("The battery's state has not been initialised correctly !");
        assert this.mode == BatteryState.Idle :
                new PostconditionException("The battery's state has not been initialised correctly !");
        assert this.isPortExisting(batteryInboundPort.getPortURI()):
                new PostconditionException("The component must have a "
                        + "port with URI " + batteryInboundPort.getPortURI()) ;
        assert	this.findPortFromURI(batteryInboundPort.getPortURI()).
                getImplementedInterface().equals(BatteryI.class) :
                new PostconditionException("The component must have a "
                        + "port with implemented interface BatteryI") ;
        assert	this.findPortFromURI(batteryInboundPort.getPortURI()).isPublished() :
                new PostconditionException("The component must have a "
                        + "port published with URI " + batteryInboundPort.getPortURI()) ;

    }

    @Override
    public void switchOn() throws Exception {
        assert !isOn : new PreconditionException("battery is already on") ;
        isOn = true;
    }

    @Override
    public int getEnergyProduced() throws Exception {
        return energyProduced;
    }

    @Override
    public int getMaxCapacity() throws Exception {
        return maxCapacity;
    }

    @Override
    public int getCurrentCapacity() throws Exception {
        return currentCapacity;
    }

    @Override
    public void setMode(BatteryState mode) throws Exception {
        switch (mode){
            case Idle : System.out.println("Now idling"); break;
            case Producing : System.out.println("Now producing"); break;
            case Charging : System.out.println("Now charging"); break;
            default : System.out.println("Mode doesn't exist");
        }
        this.mode = mode;

        assert this.mode == mode : new PostconditionException("Mode hasn't been correctly affected");
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
}
