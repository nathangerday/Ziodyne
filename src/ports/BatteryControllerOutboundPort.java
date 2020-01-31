package ports;

import components.Battery.BState;
import components.Controller;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import interfaces.BatteryControllerI;

public class BatteryControllerOutboundPort extends AbstractOutboundPort implements BatteryControllerI {

    private static final long serialVersionUID = 1L;

    public BatteryControllerOutboundPort(String uri, ComponentI battery) throws Exception {
        super(uri, BatteryControllerI.class, battery);
        assert uri != null && battery instanceof Controller;
    }

    public BatteryControllerOutboundPort(ComponentI owner) throws Exception {
        super(BatteryControllerI.class, owner);
        assert owner instanceof Controller;
    }

    @Override
    public double getMaxCapacity() throws Exception {
        return ((BatteryControllerI)this.connector).getMaxCapacity();
    }

    @Override
    public double getCurrentCapacity() throws Exception {
        return ((BatteryControllerI)this.connector).getCurrentCapacity();
    }

    @Override
    public void setMode(BState mode) throws Exception {
        ((BatteryControllerI)this.connector).setMode(mode);
    }
}
