package connectors;

import components.Dishwasher.DWMode;
import fr.sorbonne_u.components.connectors.AbstractConnector;
import interfaces.DishwasherControllerI;
import interfaces.DishwasherI;

/**
 * The class <code>DishwasherConnector</code> implements a connector
 * for the <code>DishwasherControllerI</code> interface.
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 */
public class DishwasherConnector extends AbstractConnector implements DishwasherControllerI{

	/**
	 * @see {@link interfaces.DishwasherControllerI#isOn() }
	 */
    @Override
    public boolean isOn() throws Exception {
        return ((DishwasherI)this.offering).isOn();
    }

	/**
	 * @see {@link interfaces.DishwasherControllerI#getTimeLeft() }
	 */
    @Override
    public double getTimeLeft() throws Exception {
        return ((DishwasherI)this.offering).getTimeLeft();
    }

	/**
	 * @see {@link interfaces.DishwasherControllerI#getMode() }
	 */
    @Override
    public DWMode getMode() throws Exception {
        return ((DishwasherI)this.offering).getMode();
    }

	/**
	 * @see {@link interfaces.DishwasherControllerI#setMode(DWMode) }
	 */
    @Override
    public void setMode(DWMode mode) throws Exception {
        ((DishwasherI)this.offering).setMode(mode);        
    }

	/**
	 * @see {@link interfaces.DishwasherControllerI#switchBreak() }
	 */
    @Override
    public void switchBreak() throws Exception {
        ((DishwasherI)this.offering).switchBreak();      
    }

	/**
	 * @see  {@link interfaces.DishwasherControllerI#isOnBreak() }
	 */
    @Override
    public boolean isOnBreak() throws Exception {
        return ((DishwasherI)this.offering).isOnBreak();
    }
}
