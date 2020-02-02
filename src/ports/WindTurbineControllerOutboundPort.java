package ports;

import components.Controller;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import interfaces.WindTurbineControllerI;


/**
 * The class <code>WindTurbineControllerOutboundPort</code> implements an outbound port for
 * the <code>WindTurbineControllerI</code> interface.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 */
public class WindTurbineControllerOutboundPort extends AbstractOutboundPort implements WindTurbineControllerI {

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
    public WindTurbineControllerOutboundPort(String uri, ComponentI owner) throws Exception {
        super(uri, WindTurbineControllerI.class, owner);

        assert uri != null && owner instanceof Controller;
    }


    public WindTurbineControllerOutboundPort(ComponentI owner) throws Exception {
        super(WindTurbineControllerI.class, owner);
        assert owner instanceof Controller;
    }

    /**
     * @see interfaces.WindTurbineControllerI#isOn()
     */
    @Override
    public boolean isOn() throws Exception {
        return ((WindTurbineControllerI)this.connector).isOn();
    }

    /**
     * @see interfaces.WindTurbineControllerI#isOnBreak()
     */
    @Override
    public boolean isOnBreak() throws Exception {
        return ((WindTurbineControllerI)this.connector).isOnBreak();
    }

    /**
     * @see interfaces.WindTurbineControllerI#switchBreak()
     */
    @Override
    public void switchBreak() throws Exception {
        ((WindTurbineControllerI)this.connector).switchBreak();
    }

    /**
     * @see interfaces.WindTurbineControllerI#getWindSpeed()
     */
    @Override
    public double getWindSpeed() throws Exception {
        return ((WindTurbineControllerI)this.connector).getWindSpeed();
    }
}
