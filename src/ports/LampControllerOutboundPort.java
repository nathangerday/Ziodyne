package ports;

import components.Controller;
import components.Lamp.LampState;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import interfaces.LampControllerI;

/**
 * The class <code>LampControllerOutboundPort</code> implements an outbound port for
 * the <code>LampControllerI</code> interface.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 */
public class LampControllerOutboundPort extends AbstractOutboundPort implements LampControllerI{

    private static final long serialVersionUID = 1L;
    
    /**
	 * create the port with the given URI and the given owner.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	uri != null and owner != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param uri		URI of the port.
	 * @param owner		owner of the port.
	 * @throws Exception	<i>todo.</i>
	 */
    public LampControllerOutboundPort(String uri, ComponentI owner) throws Exception {
        super(uri, LampControllerI.class, owner);

        assert uri != null && owner instanceof Controller;
    }

    public LampControllerOutboundPort(ComponentI owner) throws Exception {
        super(LampControllerI.class, owner);

        assert owner instanceof Controller;
    }
    
    /**
     * @see interfaces.LampControllerI#getState()
     */
    @Override
    public LampState getState() throws Exception{
        return ((LampControllerI)this.connector).getState();
    }
    
    /**
     * @see interfaces.LampControllerI#isOnBreak()
     */
    @Override
    public boolean isOnBreak() throws Exception{
        return ((LampControllerI)this.connector).isOnBreak();
    }
    
    /**
     * @see interfaces.LampControllerI#switchBreak()
     */
    @Override
    public void switchBreak() throws Exception{
        ((LampControllerI)this.connector).switchBreak();
    }
}
