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
    public void switchFridge() throws Exception {
        this.getOwner().handleRequestSync(
                new AbstractComponent.AbstractService<Void>() {
                    @Override
                    public Void call() throws Exception {
                        ((Fridge)this.getServiceOwner()).switchFridge();
                        return null;
                    }
                }) ;
    }

    @Override
    public void switchFreezer() throws Exception {
        this.getOwner().handleRequestSync(
                new AbstractComponent.AbstractService<Void>() {
                    @Override
                    public Void call() throws Exception {
                        ((Fridge)this.getServiceOwner()).switchFreezer();
                        return null;
                    }
                }) ;
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

    @Override
    public float getFridgeTemp() throws Exception {
        return this.getOwner().handleRequestSync(
                owner -> ((Fridge)owner).getFridgeTemp());
    }

    @Override
    public float getFreezerTemp() throws Exception {
        return this.getOwner().handleRequestSync(
                owner -> ((Fridge)owner).getFreezerTemp());
    }

    @Override
    public void setFridgeTemp(float t) throws Exception {
        this.getOwner().handleRequestSync(
                new AbstractComponent.AbstractService<Void>() {
                    @Override
                    public Void call() throws Exception {
                        ((Fridge)this.getServiceOwner()).setFridgeTemp(t);
                        return null;
                    }
                }) ;
    }

    @Override
    public void setFreezerTemp(float t) throws Exception {
        this.getOwner().handleRequestSync(
                new AbstractComponent.AbstractService<Void>() {
                    @Override
                    public Void call() throws Exception {
                        ((Fridge)this.getServiceOwner()).setFreezerTemp(t);
                        return null;
                    }
                }) ;
    }
}
