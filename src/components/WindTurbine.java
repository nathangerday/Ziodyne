package components;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.PostconditionException;
import fr.sorbonne_u.components.exceptions.PreconditionException;
import interfaces.WindTurbineI;
import ports.WindTurbineInboundPort;

public class WindTurbine extends AbstractComponent implements WindTurbineI {

    //port that exposes the offered interface with the
    // given URI to ease the connection from controller components.
    protected WindTurbineInboundPort windTurbineInboundPort;
    //an integer value that reports the quantity of energy produced
    protected int energyProduced;
    //an integer value reporting the wind level sensed
    protected int windSpeed;
    //a boolean value On/Off
    protected boolean isOn;

    protected WindTurbine(String uri, String windTurbineInboundPortURI) throws Exception{
        super(uri,1,0);
        assert uri != null :new PreconditionException("uri can't be null!") ;
        this.energyProduced = 0;
        this.windSpeed = 0;
        this.isOn = false;
        this.addOfferedInterface(WindTurbineI.class);
        windTurbineInboundPort = new WindTurbineInboundPort(windTurbineInboundPortURI, this);
        windTurbineInboundPort.publishPort();

        assert this.energyProduced == 0 :
                new PostconditionException("The wind turbine's state has not been initialised correctly !");
        assert this.windSpeed == 0 :
                new PostconditionException("The wind turbine's state has not been initialised correctly !");
        assert this.isOn == false :
                new PostconditionException("The wind turbine's state has not been initialised correctly !");
        assert this.isPortExisting(windTurbineInboundPort.getPortURI()):
                new PostconditionException("The component must have a "
                        + "port with URI " + windTurbineInboundPort.getPortURI()) ;
        assert	this.findPortFromURI(windTurbineInboundPort.getPortURI()).
                getImplementedInterface().equals(WindTurbineI.class) :
                new PostconditionException("The component must have a "
                        + "port with implemented interface WindTurbineI") ;
        assert	this.findPortFromURI(windTurbineInboundPort.getPortURI()).isPublished() :
                new PostconditionException("The component must have a "
                        + "port published with URI " + windTurbineInboundPort.getPortURI()) ;
    }

    @Override
    public void switchOn() throws Exception {
        assert !isOn : new PreconditionException("wind turbine is already on") ;
    	isOn = true;
    }


    @Override
    public int getWindSpeed() throws Exception {
        return windSpeed;
    }

    @Override
    public int getEnergyProduced() throws Exception {
        return energyProduced;
    }

    @Override
    public void shutdown() throws ComponentShutdownException {
        try {
            windTurbineInboundPort.unpublishPort();
        }catch(Exception e) {
            throw new ComponentShutdownException(e);
        }
        super.shutdown();
    }


    @Override
    public void shutdownNow() throws ComponentShutdownException {
        try {
            windTurbineInboundPort.unpublishPort();
        }catch(Exception e) {
            throw new ComponentShutdownException(e);
        }
        super.shutdownNow();
    }


}
