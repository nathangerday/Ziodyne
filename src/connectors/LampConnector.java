package connectors;

import components.Lamp.LampState;
import fr.sorbonne_u.components.connectors.AbstractConnector;
import interfaces.LampControllerI;
import interfaces.LampI;

public class LampConnector extends AbstractConnector implements LampControllerI{

    @Override
    public LampState getState() throws Exception {
        return ((LampI)this.offering).getState();
    }

    @Override
    public boolean isOnBreak() throws Exception {
        return ((LampI)this.offering).isOnBreak();
    }

    @Override
    public void switchBreak() throws Exception {
        ((LampI)this.offering).switchBreak();
    }
}
