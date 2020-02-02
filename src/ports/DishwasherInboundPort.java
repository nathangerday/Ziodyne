package ports;

import components.Dishwasher;
import components.Dishwasher.DWMode;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import interfaces.DishwasherI;


/**
 * The class <code>DishwasherInboundPort</code> implements an inbound port for
 * the <code>DishwasherI</code> interface.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 */
public class DishwasherInboundPort extends AbstractInboundPort implements DishwasherI {

    private static final long serialVersionUID = 4328509908271704575L;
    
    /**
	 * create the port with the given URI and the given dishwasher.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	uri != null and dishwasher != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param uri		URI of the port.
	 * @param dishwasher		owner of the port.
	 * @throws Exception	<i>todo.</i>
	 */
    public DishwasherInboundPort(String uri, ComponentI dishwasher) throws Exception {
        super(uri, DishwasherI.class, dishwasher);
    }

    public DishwasherInboundPort(ComponentI dishwasher) throws Exception{
        super(DishwasherI.class, dishwasher);
    }
    
    /**
     * @see interfaces.DishwasherI#isOn()
     */
    @Override
    public boolean isOn() throws Exception {
        return this.getOwner().handleRequestSync(
                owner -> ((Dishwasher)owner).isOn());
    }
    
    /**
     * @see interfaces.DishwasherI#getTimeLeft()
     */
    @Override
    public double getTimeLeft() throws Exception {
        return this.getOwner().handleRequestSync(
                owner -> ((Dishwasher)owner).getTimeLeft());
    }
    
    /**
     * @see interfaces.DishwasherI#getMode()
     */
    @Override
    public DWMode getMode() throws Exception {
        return this.getOwner().handleRequestSync(
                owner -> ((Dishwasher)owner).getMode());
    }
    
    /**
     * @see interfaces.DishwasherI#setMode(components.Dishwasher.DWMode)
     */
    @Override
    public void setMode(DWMode mode) throws Exception {
        this.getOwner().handleRequestSync(
                new AbstractComponent.AbstractService<Void>() {
                    @Override
                    public Void call() throws Exception {
                        ((Dishwasher)this.getServiceOwner()).setMode(mode);
                        return null;
                    }
                }) ;
    }
    
    /**
     * @see interfaces.DishwasherI#switchBreak()
     */
    @Override
    public void switchBreak() throws Exception {
        this.getOwner().handleRequestSync(
                new AbstractComponent.AbstractService<Void>() {
                    @Override
                    public Void call() throws Exception {
                        ((Dishwasher)this.getServiceOwner()).switchBreak();
                        return null;
                    }
                }) ;
    }
    
    /**
     * @see interfaces.DishwasherI#isOnBreak()
     */
    @Override
    public boolean isOnBreak() throws Exception {
        return this.getOwner().handleRequestSync(
                owner -> ((Dishwasher)owner).isOnBreak());
    }
}