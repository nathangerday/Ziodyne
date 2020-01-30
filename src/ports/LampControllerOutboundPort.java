package ports;

import components.Controller;
import components.Lamp.LampState;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import interfaces.LampControllerI;

public class LampControllerOutboundPort extends AbstractOutboundPort implements LampControllerI{

    private static final long serialVersionUID = 1L;

    public LampControllerOutboundPort(String uri, ComponentI owner) throws Exception {
        super(uri, LampControllerI.class, owner);

        assert uri != null && owner instanceof Controller;
    }

    public LampControllerOutboundPort(ComponentI owner) throws Exception {
        super(LampControllerI.class, owner);

        assert owner instanceof Controller;
    }

    @Override
    public LampState getState() throws Exception{
        return ((LampControllerI)this.connector).getState();
    }

    @Override
    public boolean isOnBreak() throws Exception{
        return ((LampControllerI)this.connector).isOnBreak();
    }

    @Override
    public void switchBreak() throws Exception{
        ((LampControllerI)this.connector).switchBreak();
    }
}
