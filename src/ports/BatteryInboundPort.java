package ports;

import components.Battery;
import components.Battery.BState;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import interfaces.BatteryI;


/**
 * The class <code>BatteryInboundPort</code> implements an inbound port for
 * the <code>BatteryI</code> interface.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 */
public class BatteryInboundPort extends AbstractInboundPort implements BatteryI {

    private static final long serialVersionUID = 1L;
    
    /**
	 * create the port with the given URI and the given battery.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	uri != null and battery != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param uri		URI of the port.
	 * @param battery		owner of the port.
	 * @throws Exception	<i>todo.</i>
	 */
    public BatteryInboundPort(String uri,ComponentI battery) throws Exception {
        super(uri,BatteryI.class, battery);
        assert uri != null && battery instanceof Battery;
    }

    public BatteryInboundPort(ComponentI battery) throws Exception {
        super(BatteryI.class, battery);

        assert battery instanceof Battery;
    }
    
    /**
     * @see interfaces.BatteryI#getMaxCapacity()
     */
    @Override
    public double getMaxCapacity() throws Exception {
        return this.getOwner().handleRequestSync(
                owner -> ((Battery)owner).getMaxCapacity());
    }
    
    /**
     * @see interfaces.BatteryI#getCurrentCapacity()
     */
    @Override
    public double getCurrentCapacity() throws Exception {
        return this.getOwner().handleRequestSync(
                owner -> ((Battery)owner).getCurrentCapacity());
    }
    
    /**
     * @see interfaces.BatteryI#setMode(components.Battery.BState)
     */
    @Override
    public void setMode(BState mode) throws Exception {
        this.getOwner().handleRequestSync(
                new AbstractComponent.AbstractService<Void>() {
                    @Override
                    public Void call() throws Exception {
                        ((Battery)this.getServiceOwner()).setMode(mode);
                        return null;
                    }
                }) ;
    }
    
    /**
     * @see interfaces.BatteryI#getMode()
     */
    @Override
    public BState getMode() throws Exception {
        return this.getOwner().handleRequestSync(
                owner -> ((Battery)owner).getMode());
    }
}
