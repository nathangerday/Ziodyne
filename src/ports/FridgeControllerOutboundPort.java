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
    public void switchFridgeBreak() throws Exception{
        ((FridgeControllerI)this.connector).switchFridgeBreak();
    }

    @Override
    public boolean isFridgeOnBreak() throws Exception{
        return ((FridgeControllerI)this.connector).isFridgeOnBreak();
    }

    @Override
    public void switchFreezerBreak() throws Exception{
        ((FridgeControllerI)this.connector).switchFreezerBreak();
    }

    @Override
    public boolean isFreezerOnBreak() throws Exception{
        return ((FridgeControllerI)this.connector).isFreezerOnBreak();
    }

    @Override
    public boolean isFridgeOn() throws Exception{
        return ((FridgeControllerI)this.connector).isFridgeOn();
    }

    @Override
    public boolean isFreezerOn() throws Exception{
        return ((FridgeControllerI)this.connector).isFreezerOn();
    }
}
