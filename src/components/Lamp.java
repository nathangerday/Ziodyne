package components;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import interfaces.LampI;
import ports.LampInboundPort;

public class Lamp extends AbstractComponent{

    protected boolean isOn;
    protected LampInboundPort p;

    protected Lamp(String uri) throws Exception {
        super(uri, 1, 0);
        this.isOn = false;
        this.addOfferedInterface(LampI.class);
        p = new LampInboundPort(uri, this);
        p.publishPort();
    }

    @Override
    public void shutdown() throws ComponentShutdownException {
        try {
            p.unpublishPort();
        }catch(Exception e) {
            throw new ComponentShutdownException(e);
        }
        super.shutdown();
    }

    @Override
    public void shutdownNow() throws ComponentShutdownException {
        try {
            p.unpublishPort();
        }catch(Exception e) {
            throw new ComponentShutdownException(e);
        }
        super.shutdownNow();
    }

    public boolean isOn() {
        return this.isOn;
    }

    public void switchButton() {
        this.isOn = !this.isOn;
    }

}
