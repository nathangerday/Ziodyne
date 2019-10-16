package components;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.examples.basic_cs.interfaces.URIProviderI;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.PostconditionException;
import fr.sorbonne_u.components.exceptions.PreconditionException;
import interfaces.LampI;
import ports.LampInboundPort;

public class Lamp extends AbstractComponent implements LampI{


    //a string prefix that will identify the URI lamp
    protected String uri;
    //port that exposes the offered interface with the
	// given URI to ease the connection from controller components.
    protected LampInboundPort p;
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
    protected Lamp(String uri) throws Exception {
        super(uri, 1, 0);
        assert uri != null :  new PreconditionException("uri can't be null!") ;
        this.uri = uri;
        this.state = 0;
        this.addOfferedInterface(LampI.class);
        p = new LampInboundPort(uri, this);
        p.publishPort();
        
        assert this.uri.equals(uri) :
			new PostconditionException("The URI prefix has not been initialised!");
        
        assert this.state == 0 :
        	new PostconditionException("The lamp's state has not been initialised correctly !");
        assert this.isPortExisting(p.getPortURI()):
			new PostconditionException("The component must have a "
					+ "port with URI " + p.getPortURI()) ;
		
        assert	this.findPortFromURI(p.getPortURI()).
		getImplementedInterface().equals(URIProviderI.class) :
		new PostconditionException("The component must have a "
				+ "port with implemented interface URIProviderI") ;
		
		assert	this.findPortFromURI(p.getPortURI()).isPublished() :
		new PostconditionException("The component must have a "
				+ "port published with URI " + p.getPortURI()) ;
    }


    @Override
    public void shutdown() throws ComponentShutdownException {
        try {
            p.unpublishPort();
        }catch(Exception e) {
            throw new ComponentShutdownException(e);
        }
        super.shutdown();
    }

    
    @Override
    public void shutdownNow() throws ComponentShutdownException {
        try {
            p.unpublishPort();
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
