package ports;

import components.Fridge;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import interfaces.FridgeI;

/**
 * The class <code>FridgeInboundPort</code> implements an inbound port for
 * the <code>FridgeI</code> interface.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 */
public class FridgeInboundPort extends AbstractInboundPort implements FridgeI{

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
    public FridgeInboundPort(String uri, ComponentI owner) throws Exception {
        super(uri, FridgeI.class, owner);

        assert uri != null && owner instanceof Fridge;
    }

    public FridgeInboundPort(ComponentI owner) throws Exception {
        super(FridgeI.class, owner);

        assert owner instanceof Fridge;
    }
    
    
    /**
     * @see interfaces.FridgeI#switchFridgeBreak()
     */
    @Override
    public void switchFridgeBreak() throws Exception {
        this.getOwner().handleRequestSync(
                new AbstractComponent.AbstractService<Void>() {
                    @Override
                    public Void call() throws Exception {
                        ((Fridge)this.getServiceOwner()).switchFridgeBreak();
                        return null;
                    }
                }) ;
    }
    
    /**
     * @see interfaces.FridgeI#isFridgeOnBreak()
     */
    @Override
    public boolean isFridgeOnBreak() throws Exception {
        return this.getOwner().handleRequestSync(
                owner -> ((Fridge)owner).isFridgeOnBreak());
    }
    
    /**
     * @see interfaces.FridgeI#switchFreezerBreak()
     */
    @Override
    public void switchFreezerBreak() throws Exception {
        this.getOwner().handleRequestSync(
                new AbstractComponent.AbstractService<Void>() {
                    @Override
                    public Void call() throws Exception {
                        ((Fridge)this.getServiceOwner()).switchFreezerBreak();
                        return null;
                    }
                }) ;
    }
    
    /**
     * @see interfaces.FridgeI#isFreezerOnBreak()
     */
    @Override
    public boolean isFreezerOnBreak() throws Exception {
        return this.getOwner().handleRequestSync(
                owner -> ((Fridge)owner).isFreezerOnBreak());
    }
    
    /**
     * @see interfaces.FridgeI#isFridgeOn()
     */
    @Override
    public boolean isFridgeOn() throws Exception {
        return this.getOwner().handleRequestSync(
                owner -> ((Fridge)owner).isFridgeOn());
    }
    
    /**
     * @see interfaces.FridgeI#isFreezerOn()
     */
    @Override
    public boolean isFreezerOn() throws Exception {
        return this.getOwner().handleRequestSync(
                owner -> ((Fridge)owner).isFreezerOn());
    }


}
