package ports;

import components.WindTurbine;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import interfaces.WindTurbineI;

/**
 * The class <code>WindTurbineInboundPort</code> implements an inbound port for
 * the <code>WindTurbineI</code> interface.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 */
public class WindTurbineInboundPort extends AbstractInboundPort implements WindTurbineI{

    private static final long serialVersionUID = 1L;
    
    /**
	 * create the port with the given URI and the given windTurbine.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	uri != null and windTurbine != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param uri		URI of the port.
	 * @param windTurbine		owner of the port.
	 * @throws Exception	<i>todo.</i>
	 */
    public WindTurbineInboundPort(String uri, ComponentI windTurbine) throws Exception {
        super(uri, WindTurbineI.class, windTurbine);

        assert uri != null && windTurbine instanceof WindTurbine;
    }


    public WindTurbineInboundPort(ComponentI windTurbine) throws Exception {
        super(WindTurbineI.class, windTurbine);

        assert windTurbine instanceof WindTurbine;
    }

    /**
     * @see interfaces.WindTurbineI#switchBreak()
     */
    @Override
    public boolean isOn() throws Exception {
        return this.getOwner().handleRequestSync(
                owner -> ((WindTurbine)owner).isOn());
    }

    /**
     * @see interfaces.WindTurbineI#switchBreak()
     */
    @Override
    public boolean isOnBreak() throws Exception {
        return this.getOwner().handleRequestSync(
                owner -> ((WindTurbine)owner).isOnBreak());
    }

    /**
     * @see interfaces.WindTurbineI#switchBreak()
     */
    @Override
    public void switchBreak() throws Exception {
        this.getOwner().handleRequestSync(
                new AbstractComponent.AbstractService<Void>() {
                    @Override
                    public Void call() throws Exception {
                        ((WindTurbine)this.getServiceOwner()).switchBreak();
                        return null;
                    }
                }) ;
    }

    /**
     * @see interfaces.WindTurbineI#getWindSpeed()
     */
    @Override
    public double getWindSpeed() throws Exception {
        return this.getOwner().handleRequestSync(
                owner -> ((WindTurbine)owner).getWindSpeed());
    }
}
