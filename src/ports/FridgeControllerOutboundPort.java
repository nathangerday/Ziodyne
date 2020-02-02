package ports;

import components.Controller;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import interfaces.FridgeControllerI;

/**
 * The class <code>FridgeControllerOutboundPort</code> implements an outbound port for
 * the <code>FridgeControllerI</code> interface.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 */
public class FridgeControllerOutboundPort extends AbstractOutboundPort implements FridgeControllerI {

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
    public FridgeControllerOutboundPort(String uri, ComponentI owner) throws Exception {
        super(uri, FridgeControllerI.class, owner);

        assert uri != null && owner instanceof Controller;
    }

    public FridgeControllerOutboundPort(ComponentI owner) throws Exception {
        super(FridgeControllerI.class, owner);

        assert owner instanceof Controller;
    }
    
    /**
     * @see interfaces.FridgeControllerI#switchFridgeBreak()
     */
    @Override
    public void switchFridgeBreak() throws Exception{
        ((FridgeControllerI)this.connector).switchFridgeBreak();
    }
    
    /**
     * @see interfaces.FridgeControllerI#isFridgeOnBreak()
     */
    @Override
    public boolean isFridgeOnBreak() throws Exception{
        return ((FridgeControllerI)this.connector).isFridgeOnBreak();
    }
    
    /**
     * @see interfaces.FridgeControllerI#switchFreezerBreak()
     */
    @Override
    public void switchFreezerBreak() throws Exception{
        ((FridgeControllerI)this.connector).switchFreezerBreak();
    }
    
    /**
     * @see interfaces.FridgeControllerI#isFreezerOnBreak()
     */
    @Override
    public boolean isFreezerOnBreak() throws Exception{
        return ((FridgeControllerI)this.connector).isFreezerOnBreak();
    }
    
    /**
     * @see interfaces.FridgeControllerI#isFridgeOn()
     */
    @Override
    public boolean isFridgeOn() throws Exception{
        return ((FridgeControllerI)this.connector).isFridgeOn();
    }
    
    /**
     * @see interfaces.FridgeControllerI#isFreezerOn()
     */
    @Override
    public boolean isFreezerOn() throws Exception{
        return ((FridgeControllerI)this.connector).isFreezerOn();
    }
}
