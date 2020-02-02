package connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;

import interfaces.WindTurbineControllerI;
import interfaces.WindTurbineI;


/**
 * The class <code>WindTurbineConnector</code> implements a connector
 * for the <code>WindTurbineControllerI</code> interface.
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 */
public class WindTurbineConnector extends AbstractConnector implements WindTurbineControllerI {

	/**
	 * @see {@link interfaces.WindTurbineControllerI#isOn()}
	 */
	@Override
    public boolean isOn() throws Exception {
        return ((WindTurbineI)this.offering).isOn();
    }
	
	/**
	 * @see {@link interfaces.WindTurbineControllerI#isOnBreak()}
	 */
	@Override
    public boolean isOnBreak() throws Exception {
        return ((WindTurbineI)this.offering).isOnBreak();
    }
	
	/**
	 * @see {@link interfaces.WindTurbineControllerI#switchBreak()}
	 */
	@Override
    public void switchBreak() throws Exception {
        ((WindTurbineI)this.offering).switchBreak();
    }

	/**
	 * @see {@link interfaces.WindTurbineControllerI#getWindSpeed()}
	 */
	@Override
	public double getWindSpeed() throws Exception {
		return ((WindTurbineI)this.offering).getWindSpeed();
	}
}
