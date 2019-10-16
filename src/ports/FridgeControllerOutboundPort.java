package ports;

import components.Controller;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import interfaces.FridgeControllerI;

public class FridgeControllerOutboundPort extends AbstractOutboundPort implements FridgeControllerI {

    private static final long serialVersionUID = 1L;

    public FridgeControllerOutboundPort(String uri, ComponentI owner) throws Exception {
        super(uri, FridgeControllerI.class, owner);

        assert uri != null && owner instanceof Controller;
    }

    public FridgeControllerOutboundPort(ComponentI owner) throws Exception {
        super(FridgeControllerI.class, owner);

        assert owner instanceof Controller;
    }

    @Override
    public void switchFridge() throws Exception{
        ((FridgeControllerI)this.connector).switchFridge();
    }

    @Override
    public void switchFreezer() throws Exception{
        ((FridgeControllerI)this.connector).switchFreezer();
    }

    @Override
    public boolean isFridgeOn() throws Exception{
        return ((FridgeControllerI)this.connector).isFridgeOn();
    }

    @Override
    public boolean isFreezerOn() throws Exception{
        return ((FridgeControllerI)this.connector).isFreezerOn();
    }

    @Override
    public float getFridgeTemp() throws Exception{
        return ((FridgeControllerI)this.connector).getFridgeTemp();
    }

    @Override
    public float getFreezerTemp() throws Exception{
        return ((FridgeControllerI)this.connector).getFreezerTemp();
    }

    @Override
    public void setFridgeTemp(float t) throws Exception{
        ((FridgeControllerI)this.connector).setFridgeTemp(t);
    }

    @Override
    public void setFreezerTemp(float t) throws Exception{
        ((FridgeControllerI)this.connector).setFreezerTemp(t);
    }
}
