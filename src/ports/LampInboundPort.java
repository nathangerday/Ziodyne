package ports;

import components.Lamp;
import components.Lamp.LampState;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import interfaces.LampI;

public class LampInboundPort extends AbstractInboundPort implements LampI{

    private static final long serialVersionUID = 1L;

    public LampInboundPort(String uri, ComponentI lamp) throws Exception {
        super(uri, LampI.class, lamp);

        assert uri != null && lamp instanceof Lamp;
    }

    public LampInboundPort(ComponentI lamp) throws Exception {
        super(LampI.class, lamp);

        assert lamp instanceof Lamp;
    }

    @Override
    public LampState getState() throws Exception {
        return this.getOwner().handleRequestSync(
                owner -> ((Lamp)owner).getState());
    }
}
