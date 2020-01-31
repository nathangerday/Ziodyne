package ports;

import components.Dishwasher;
import components.Dishwasher.DWMode;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import interfaces.DishwasherI;

public class DishwasherInboundPort extends AbstractInboundPort implements DishwasherI {

    private static final long serialVersionUID = 4328509908271704575L;

    public DishwasherInboundPort(String uri, ComponentI dishwasher) throws Exception {
        super(uri, DishwasherI.class, dishwasher);
    }

    public DishwasherInboundPort(ComponentI dishwasher) throws Exception{
        super(DishwasherI.class, dishwasher);
    }

    @Override
    public boolean isOn() throws Exception {
        return this.getOwner().handleRequestSync(
                owner -> ((Dishwasher)owner).isOn());
    }

    @Override
    public double getTimeLeft() throws Exception {
        return this.getOwner().handleRequestSync(
                owner -> ((Dishwasher)owner).getTimeLeft());
    }

    @Override
    public DWMode getMode() throws Exception {
        return this.getOwner().handleRequestSync(
                owner -> ((Dishwasher)owner).getMode());
    }

    @Override
    public void setMode(DWMode mode) throws Exception {
        this.getOwner().handleRequestSync(
                new AbstractComponent.AbstractService<Void>() {
                    @Override
                    public Void call() throws Exception {
                        ((Dishwasher)this.getServiceOwner()).setMode(mode);
                        return null;
                    }
                }) ;
    }

    @Override
    public void switchBreak() throws Exception {
        this.getOwner().handleRequestSync(
                new AbstractComponent.AbstractService<Void>() {
                    @Override
                    public Void call() throws Exception {
                        ((Dishwasher)this.getServiceOwner()).switchBreak();
                        return null;
                    }
                }) ;
    }

    @Override
    public boolean isOnBreak() throws Exception {
        return this.getOwner().handleRequestSync(
                owner -> ((Dishwasher)owner).isOnBreak());
    }
}