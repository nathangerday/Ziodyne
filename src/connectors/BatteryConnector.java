package connectors;

import components.Battery.BState;
import fr.sorbonne_u.components.connectors.AbstractConnector;
import interfaces.BatteryControllerI;
import interfaces.BatteryI;

/**
 * The class <code>BatteryConnector</code> implements a connector
 * for the <code>BatteryControllerI</code> interface.
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 */
public class BatteryConnector extends AbstractConnector implements BatteryControllerI {

	/**
	 * @see {@link interfaces.BatteryI#getMaxCapacity() }
	 */
    @Override
    public double getMaxCapacity() throws Exception {
        return ((BatteryI)this.offering).getMaxCapacity();
    }

    /**
	 * @see {@link interfaces.BatteryI#getCurrentCapacity() }
	 */
    @Override
    public double getCurrentCapacity() throws Exception {
        return ((BatteryI)this.offering).getCurrentCapacity();
    }

    /**
	 * @see {@link interfaces.BatteryI#setMode() }
	 */
    @Override
    public void setMode(BState mode) throws Exception {
        ((BatteryI)this.offering).setMode(mode);
    }
    
    /**
	 * @see {@link interfaces.BatteryI#getMode() }
	 */
    @Override
    public BState getMode() throws Exception {
        return ((BatteryI)this.offering).getMode();
    }
}
