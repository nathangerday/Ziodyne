package connectors;

import components.Lamp.State;
import fr.sorbonne_u.components.connectors.AbstractConnector;
import interfaces.LampControllerI;
import interfaces.LampI;

public class LampConnector extends AbstractConnector implements LampControllerI{

	@Override
	public State getState() throws Exception {
		return ((LampI)this.offering).getState();
	}
}
