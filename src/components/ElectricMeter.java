package components;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.PostconditionException;
import fr.sorbonne_u.components.exceptions.PreconditionException;
import interfaces.ElectricMeterI;
import ports.ElectricMeterInboundPort;

public class ElectricMeter extends AbstractComponent implements ElectricMeterI{
    protected int conso;
    protected ElectricMeterInboundPort electricMeterInboundPort;

    protected ElectricMeter(String uri, String electricMeterInboundPortURI) throws Exception{
        super(uri,1,0);
        assert uri != null :  new PreconditionException("uri can't be null!") ;
        this.conso = 0;
        this.addOfferedInterface(ElectricMeterI.class);
        this.electricMeterInboundPort = new ElectricMeterInboundPort(electricMeterInboundPortURI, this);
        this.electricMeterInboundPort.publishPort();

        assert this.conso == 0 :
            new PostconditionException("The electric meter's state has not been initialised correctly !");
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
    public int getConsommation() {
        return conso;
    }
}
