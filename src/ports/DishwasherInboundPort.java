package ports;

import components.Dishwasher;
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
    public boolean isModeEco() throws Exception {
        return this.getOwner().handleRequestSync(
            owner -> ((Dishwasher)owner).isModeEco());
    }

    @Override
    public void setModeEco(boolean on) throws Exception {
        this.getOwner().handleRequestSync(
            new AbstractComponent.AbstractService<Void>() {
                @Override
                public Void call() throws Exception {
                    ((Dishwasher)this.getServiceOwner()).setModeEco(on);
                    return null;
                }
            }) ;
    }

    @Override
    public int getTimeLeft() throws Exception {
        return this.getOwner().handleRequestSync(
            owner -> ((Dishwasher)owner).getTimeLeft());
    }

    @Override
    public void startProgram() throws Exception {
        this.getOwner().handleRequestSync(
            new AbstractComponent.AbstractService<Void>() {
                @Override
                public Void call() throws Exception {
                    ((Dishwasher)this.getServiceOwner()).startProgram();
                    return null;
                }
            }) ;
    }
    
}