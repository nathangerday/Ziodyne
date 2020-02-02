package connectors;

import components.Lamp.LampState;
import fr.sorbonne_u.components.connectors.AbstractConnector;
import interfaces.LampControllerI;
import interfaces.LampI;

/**
 * The class <code>LampConnector</code> implements a connector
 * for the <code>LampControllerI</code> interface.
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 */
public class LampConnector extends AbstractConnector implements LampControllerI{

	/**
	 * @see {@link interfaces.LampControllerI#getState()}
	 */
    @Override
    public LampState getState() throws Exception {
        return ((LampI)this.offering).getState();
    }

    /**
	 * @see {@link interfaces.LampControllerI#isOnBreak()}
	 */
    @Override
    public boolean isOnBreak() throws Exception {
        return ((LampI)this.offering).isOnBreak();
    }

    /**
	 * @see {@link interfaces.LampControllerI#switchBreak()}
	 */
    @Override
    public void switchBreak() throws Exception {
        ((LampI)this.offering).switchBreak();
    }
}
