package ports;

import components.BatteryState;
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
    public void switchOn() throws Exception {
        ((BatteryControllerI)this.connector).switchOn();
    }

    @Override
    public int getEnergyProduced() throws Exception {
        return ((BatteryControllerI)this.connector).getEnergyProduced();
    }
    @Override
    public int getMaxCapacity() throws Exception {
        return ((BatteryControllerI)this.connector).getMaxCapacity();
    }

    @Override
    public int getCurrentCapacity() throws Exception {
        return ((BatteryControllerI)this.connector).getCurrentCapacity();
    }

    @Override
    public void setMode(BatteryState mode) throws Exception {
        ((BatteryControllerI)this.connector).setMode(mode);
    }
}
