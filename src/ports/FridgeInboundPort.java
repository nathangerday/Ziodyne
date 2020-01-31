package ports;

import components.Fridge;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import interfaces.FridgeI;

public class FridgeInboundPort extends AbstractInboundPort implements FridgeI{

    private static final long serialVersionUID = 1L;

    public FridgeInboundPort(String uri, ComponentI owner) throws Exception {
        super(uri, FridgeI.class, owner);

        assert uri != null && owner instanceof Fridge;
    }

    public FridgeInboundPort(ComponentI owner) throws Exception {
        super(FridgeI.class, owner);

        assert owner instanceof Fridge;
    }
    
    @Override
    public void switchFridgeBreak() throws Exception {
        this.getOwner().handleRequestSync(
                new AbstractComponent.AbstractService<Void>() {
                    @Override
                    public Void call() throws Exception {
                        ((Fridge)this.getServiceOwner()).switchFridgeBreak();
                        return null;
                    }
                }) ;
    }
    
    @Override
    public boolean isFridgeOnBreak() throws Exception {
        return this.getOwner().handleRequestSync(
                owner -> ((Fridge)owner).isFridgeOnBreak());
    }
    
    @Override
    public void switchFreezerBreak() throws Exception {
        this.getOwner().handleRequestSync(
                new AbstractComponent.AbstractService<Void>() {
                    @Override
                    public Void call() throws Exception {
                        ((Fridge)this.getServiceOwner()).switchFreezerBreak();
                        return null;
                    }
                }) ;
    }
    
    @Override
    public boolean isFreezerOnBreak() throws Exception {
        return this.getOwner().handleRequestSync(
                owner -> ((Fridge)owner).isFreezerOnBreak());
    }

    @Override
    public boolean isFridgeOn() throws Exception {
        return this.getOwner().handleRequestSync(
                owner -> ((Fridge)owner).isFridgeOn());
    }

    @Override
    public boolean isFreezerOn() throws Exception {
        return this.getOwner().handleRequestSync(
                owner -> ((Fridge)owner).isFreezerOn());
    }


}
