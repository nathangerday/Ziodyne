package connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import interfaces.LampControllerI;
import interfaces.LampI;

public class LampConnector extends AbstractConnector implements LampControllerI{

	/**
   	 * @see interfaces.LampControllerI#getState
   	 */
	@Override
	public int getState() throws Exception {
		return ((LampI)this.offering).getState();
	}
}
