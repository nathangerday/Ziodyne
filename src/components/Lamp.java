package components;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.PostconditionException;
import fr.sorbonne_u.components.exceptions.PreconditionException;
import interfaces.LampI;
import ports.LampInboundPort;

public class Lamp extends AbstractComponent implements LampI{

    //port that exposes the offered interface with the
	// given URI to ease the connection from controller components.
    protected LampInboundPort lampInboundPort;
    //integer indicating the lamp's state
    protected int state;
    
    /**
	 * create a component Lamp with a given uri prefix and that will expose its
	 * service through a port of the given URI.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	uri != null
	 * post	this.uri.equals(uri)
	 * post this.isOn = false
	 * post	this.p(providerPortURI)
	 * post	this.findPortFromURI(providerPortURI).getImplementedInterface().equals(URIProviderI.class)
	 * post	this.findPortFromURI(providerPortURI).isPublished()
	 * </pre>
	 *
	 * @param uriPrefix			the URI prefix of the lamp
	 * @throws Exception			<i>todo.</i>
	 */
    protected Lamp(String uri, String lampInboundPortURI) throws Exception {
        super(uri, 1, 0);
        assert uri != null :  new PreconditionException("uri can't be null!") ;
        this.state = 0;
        this.addOfferedInterface(LampI.class);
        this.lampInboundPort = new LampInboundPort(lampInboundPortURI, this);
        this.lampInboundPort.publishPort();
        
        assert this.state == 0 :
        	new PostconditionException("The lamp's state has not been initialised correctly !");
        assert this.isPortExisting(lampInboundPort.getPortURI()):
			new PostconditionException("The component must have a "
					+ "port with URI " + lampInboundPort.getPortURI()) ;
		
        assert	this.findPortFromURI(lampInboundPort.getPortURI()).
		getImplementedInterface().equals(LampI.class) :
		new PostconditionException("The component must have a "
				+ "port with implemented interface LampI") ;
		
		assert	this.findPortFromURI(lampInboundPort.getPortURI()).isPublished() :
		new PostconditionException("The component must have a "
				+ "port published with URI " + lampInboundPort.getPortURI()) ;
    }

    @Override
    public void shutdown() throws ComponentShutdownException {
        try {
            this.lampInboundPort.unpublishPort();
        }catch(Exception e) {
            throw new ComponentShutdownException(e);
        }
        super.shutdown();
    }

    @Override
    public void shutdownNow() throws ComponentShutdownException {
        try {
            this.lampInboundPort.unpublishPort();
        }catch(Exception e) {
            throw new ComponentShutdownException(e);
        }
        super.shutdownNow();
    }

	@Override
	public int getState() {
		return state;
	}
}
