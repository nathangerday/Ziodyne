package ports;

import components.Battery.BState;
import components.Controller;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import interfaces.BatteryControllerI;


/**
 * The class <code>BatteryControllerOutboundPort</code> implements an outbound port for
 * the <code>BatteryControllerI</code> interface.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 */
public class BatteryControllerOutboundPort extends AbstractOutboundPort implements BatteryControllerI {

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
    public BatteryControllerOutboundPort(String uri, ComponentI battery) throws Exception {
        super(uri, BatteryControllerI.class, battery);
        assert uri != null && battery instanceof Controller;
    }
    
    public BatteryControllerOutboundPort(ComponentI owner) throws Exception {
        super(BatteryControllerI.class, owner);
        assert owner instanceof Controller;
    }
    
    /**
     * @see interfaces.BatteryControllerI#getMaxCapacity()
     */
    @Override
    public double getMaxCapacity() throws Exception {
        return ((BatteryControllerI)this.connector).getMaxCapacity();
    }
    
    /**
     * @see interfaces.BatteryControllerI#getCurrentCapacity()
     */
    @Override
    public double getCurrentCapacity() throws Exception {
        return ((BatteryControllerI)this.connector).getCurrentCapacity();
    }
    
    /**
     * @see interfaces.BatteryControllerI#setMode(components.Battery.BState)
     */
    @Override
    public void setMode(BState mode) throws Exception {
        ((BatteryControllerI)this.connector).setMode(mode);
    }
    
    /**
     * @see interfaces.BatteryControllerI#setMode()
     */
    @Override
    public BState getMode() throws Exception {
        return ((BatteryControllerI)this.connector).getMode();
    }
}
