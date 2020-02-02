package ports;

import components.Dishwasher.DWMode;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import interfaces.DishwasherControllerI;



/**
 * The class <code>DishwasherControllerOutboundPort</code> implements an outbound port for
 * the <code>DishwasherControllerI</code> interface.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 */
public class DishwasherControllerOutboundPort extends AbstractOutboundPort implements DishwasherControllerI{

    private static final long serialVersionUID = -4235186469238174935L;
    
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
    public DishwasherControllerOutboundPort(String uri, ComponentI owner)
            throws Exception {
        super(uri, DishwasherControllerI.class, owner);
    }

    public DishwasherControllerOutboundPort(ComponentI owner) throws Exception{
        super(DishwasherControllerI.class, owner);
    }
    
    
    /**
     * @see interfaces.DishwasherControllerI#isOn()
     */
    @Override
    public boolean isOn() throws Exception {
        return ((DishwasherControllerI)this.connector).isOn();
    }
    
    /**
     * @see interfaces.DishwasherControllerI#getTimeLeft()
     */
    @Override
    public double getTimeLeft() throws Exception {
        return ((DishwasherControllerI)this.connector).getTimeLeft();
    }
    
    /**
     * @see interfaces.DishwasherControllerI#getMode()
     */
    @Override
    public DWMode getMode() throws Exception {
        return ((DishwasherControllerI)this.connector).getMode();
    }
    
    /**
     * @see interfaces.DishwasherControllerI#setMode(components.Dishwasher.DWMode)
     */
    @Override
    public void setMode(DWMode mode) throws Exception {
        ((DishwasherControllerI)this.connector).setMode(mode);        
    }
    
    /**
     * @see interfaces.DishwasherControllerI#switchBreak()
     */
    @Override
    public void switchBreak() throws Exception {
        ((DishwasherControllerI)this.connector).switchBreak();
    }
    
    /**
     * @see interfaces.DishwasherControllerI#isOnBreak()
     */
    @Override
    public boolean isOnBreak() throws Exception {
        return ((DishwasherControllerI)this.connector).isOnBreak();
    }
}