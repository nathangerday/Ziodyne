package ports;

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
    public boolean isDishwasherOn() throws Exception {
        return ((DishwasherControllerI)this.connector).isDishwasherOn();
    }

    @Override
    public boolean isDishwasherModeEco() throws Exception {
        return ((DishwasherControllerI)this.connector).isDishwasherModeEco();
    }

    @Override
    public void setDishwasherModeEco(boolean on) throws Exception {
        ((DishwasherControllerI)this.connector).setDishwasherModeEco(on);
    }

    @Override
    public int getDishwasherTimeLeft() throws Exception {
        return ((DishwasherControllerI)this.connector).getDishwasherTimeLeft();
    }

    @Override
    public void startDishwasherProgram() throws Exception {
        ((DishwasherControllerI)this.connector).startDishwasherProgram();
    }

}