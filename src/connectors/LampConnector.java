package connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import interfaces.LampControllerI;
import interfaces.LampI;

public class LampConnector extends AbstractConnector implements LampControllerI{

    @Override
    public boolean isLampOn() throws Exception{
        return ((LampI)this.offering).isOn();
    }

    @Override
    public void switchLamp() throws Exception{
        ((LampI)this.offering).switchButton();
    }
}
