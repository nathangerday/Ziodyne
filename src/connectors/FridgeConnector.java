package connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import interfaces.FridgeControllerI;
import interfaces.FridgeI;

/**
 * The class <code>FridgeConnector</code> implements a connector
 * for the <code>FridgeControllerI</code> interface.
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 */
public class FridgeConnector extends AbstractConnector implements FridgeControllerI {

	/**
	 * see {@link interfaces.FridgeControllerI#switchFridgeBreak()}
	 */
    @Override
    public void switchFridgeBreak() throws Exception{
        ((FridgeI)this.offering).switchFridgeBreak();
    }

    /**
     * see {@link interfaces.FridgeControllerI#isFridgeOnBreak()}
     */
    @Override
    public boolean isFridgeOnBreak() throws Exception{
        return ((FridgeI)this.offering).isFridgeOnBreak();
    }

    /**
	 * see {@link interfaces.FridgeControllerI#switchFreezerBreak()}
	 */
    @Override
    public void switchFreezerBreak() throws Exception{
        ((FridgeI)this.offering).switchFreezerBreak();
    }

    /**
	 * see {@link interfaces.FridgeControllerI#isFreezerOnBreak()}
	 */
    @Override
    public boolean isFreezerOnBreak() throws Exception{
        return ((FridgeI)this.offering).isFreezerOnBreak();
    }

    /**
	 * see {@link interfaces.FridgeControllerI#isFridgeOn()}
	 */
    @Override
    public boolean isFridgeOn() throws Exception{
        return ((FridgeI)this.offering).isFridgeOn();
    }

    /**
	 * see {@link interfaces.FridgeControllerI#isFreezerOn()}
	 */
    @Override
    public boolean isFreezerOn() throws Exception{
        return ((FridgeI)this.offering).isFreezerOn();
    }
}
