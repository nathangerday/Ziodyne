package connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import interfaces.FridgeControllerI;
import interfaces.FridgeI;

public class FridgeConnector extends AbstractConnector implements FridgeControllerI {

    @Override
    public void switchFridge() throws Exception{
        ((FridgeI)this.offering).switchFridge();
    }

    @Override
    public void switchFreezer() throws Exception{
        ((FridgeI)this.offering).switchFreezer();
    }

    @Override
    public boolean isFridgeOn() throws Exception{
        return ((FridgeI)this.offering).isFreezerOn();
    }

    @Override
    public boolean isFreezerOn() throws Exception{
        return ((FridgeI)this.offering).isFreezerOn();
    }

    @Override
    public float getFridgeTemp() throws Exception{
        return ((FridgeI)this.offering).getFridgeTemp();
    }

    @Override
    public float getFreezerTemp() throws Exception{
        return ((FridgeI)this.offering).getFreezerTemp();
    }

    @Override
    public void setFridgeTemp(float t) throws Exception{
        ((FridgeI)this.offering).setFridgeTemp(t);
    }

    @Override
    public void setFreezerTemp(float t) throws Exception{
        ((FridgeI)this.offering).setFreezerTemp(t);
    }
}
