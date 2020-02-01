package connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import interfaces.FridgeControllerI;
import interfaces.FridgeI;

public class FridgeConnector extends AbstractConnector implements FridgeControllerI {

    @Override
    public void switchFridgeBreak() throws Exception{
        ((FridgeI)this.offering).switchFridgeBreak();
    }

    @Override
    public boolean isFridgeOnBreak() throws Exception{
        return ((FridgeI)this.offering).isFridgeOnBreak();
    }

    @Override
    public void switchFreezerBreak() throws Exception{
        ((FridgeI)this.offering).switchFreezerBreak();
    }

    @Override
    public boolean isFreezerOnBreak() throws Exception{
        return ((FridgeI)this.offering).isFreezerOnBreak();
    }

    @Override
    public boolean isFridgeOn() throws Exception{
        return ((FridgeI)this.offering).isFridgeOn();
    }

    @Override
    public boolean isFreezerOn() throws Exception{
        return ((FridgeI)this.offering).isFreezerOn();
    }
}
