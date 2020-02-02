package ports;

import components.Lamp;
import components.Lamp.LampState;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import interfaces.LampI;

/**
 * The class <code>LampInboundPort</code> implements an inbound port for
 * the <code>LampI</code> interface.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 */
public class LampInboundPort extends AbstractInboundPort implements LampI{

    private static final long serialVersionUID = 1L;
    
    /**
	 * create the port with the given URI and the given lamp.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	uri != null and lamp != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param uri		URI of the port.
	 * @param lamp		owner of the port.
	 * @throws Exception	<i>todo.</i>
	 */
    public LampInboundPort(String uri, ComponentI lamp) throws Exception {
        super(uri, LampI.class, lamp);

        assert uri != null && lamp instanceof Lamp;
    }

    public LampInboundPort(ComponentI lamp) throws Exception {
        super(LampI.class, lamp);

        assert lamp instanceof Lamp;
    }

    /**
     * @see interfaces.LampI#getState()
     */
    @Override
    public LampState getState() throws Exception {
        return this.getOwner().handleRequestSync(
                owner -> ((Lamp)owner).getState());
    }
    
    /**
     * @see interfaces.LampI#switchBreak()
     */
    @Override
    public void switchBreak() throws Exception {
        this.getOwner().handleRequestSync(
                new AbstractComponent.AbstractService<Void>() {
                    @Override
                    public Void call() throws Exception {
                        ((Lamp)this.getServiceOwner()).switchBreak();
                        return null;
                    }
                }) ;
    }
    
    /**
     * @see interfaces.LampI#isOnBreak()
     */
    @Override
    public boolean isOnBreak() throws Exception {
        return this.getOwner().handleRequestSync(
                owner -> ((Lamp)owner).isOnBreak());
    }
}
