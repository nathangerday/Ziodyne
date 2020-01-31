package ports;

import components.Controller;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import interfaces.WindTurbineControllerI;

public class WindTurbineControllerOutboundPort extends AbstractOutboundPort implements WindTurbineControllerI {

    private static final long serialVersionUID = 1L;

    public WindTurbineControllerOutboundPort(String uri, ComponentI owner) throws Exception {
        super(uri, WindTurbineControllerI.class, owner);

        assert uri != null && owner instanceof Controller;
    }


    public WindTurbineControllerOutboundPort(ComponentI owner) throws Exception {
        super(WindTurbineControllerI.class, owner);
        assert owner instanceof Controller;
    }


    @Override
    public boolean isOn() throws Exception {
        return ((WindTurbineControllerI)this.connector).isOn();
    }


    @Override
    public boolean isOnBreak() throws Exception {
        return ((WindTurbineControllerI)this.connector).isOnBreak();
    }


    @Override
    public void switchBreak() throws Exception {
        ((WindTurbineControllerI)this.connector).switchBreak();
    }


    @Override
    public double getWindSpeed() throws Exception {
        return ((WindTurbineControllerI)this.connector).getWindSpeed();
    }
}
