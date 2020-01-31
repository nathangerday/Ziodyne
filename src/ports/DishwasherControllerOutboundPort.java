package ports;

import components.Dishwasher.DWMode;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import interfaces.DishwasherControllerI;

public class DishwasherControllerOutboundPort extends AbstractOutboundPort implements DishwasherControllerI{

    private static final long serialVersionUID = -4235186469238174935L;

    public DishwasherControllerOutboundPort(String uri, ComponentI owner)
            throws Exception {
        super(uri, DishwasherControllerI.class, owner);
    }

    public DishwasherControllerOutboundPort(ComponentI owner) throws Exception{
        super(DishwasherControllerI.class, owner);
    }

    @Override
    public boolean isOn() throws Exception {
        return ((DishwasherControllerI)this.connector).isOn();
    }

    @Override
    public double getTimeLeft() throws Exception {
        return ((DishwasherControllerI)this.connector).getTimeLeft();
    }

    @Override
    public DWMode getMode() throws Exception {
        return ((DishwasherControllerI)this.connector).getMode();
    }

    @Override
    public void setMode(DWMode mode) throws Exception {
        ((DishwasherControllerI)this.connector).setMode(mode);        
    }

    @Override
    public void switchBreak() throws Exception {
        ((DishwasherControllerI)this.connector).switchBreak();
    }

    @Override
    public boolean isOnBreak() throws Exception {
        return ((DishwasherControllerI)this.connector).isOnBreak();
    }
}