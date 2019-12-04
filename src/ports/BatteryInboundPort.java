package ports;

import components.Battery;
import components.BatteryState;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import interfaces.BatteryI;

public class BatteryInboundPort extends AbstractInboundPort implements BatteryI {

    private static final long serialVersionUID = 1L;

    public BatteryInboundPort(String uri,ComponentI battery) throws Exception {
        super(uri,BatteryI.class, battery);
        assert uri != null && battery instanceof Battery;
    }

    public BatteryInboundPort(ComponentI battery) throws Exception {
        super(BatteryI.class, battery);

        assert battery instanceof Battery;
    }

    @Override
    public void switchOn() throws Exception {
        this.getOwner().handleRequestSync(
                new AbstractComponent.AbstractService<Void>() {
                    @Override
                    public Void call() throws Exception {
                        ((Battery)this.getServiceOwner()).switchOn();
                        return null;
                    }
                }) ;
    }

    @Override
    public int getEnergyProduced() throws Exception {
        return this.getOwner().handleRequestSync(
                owner -> ((Battery)owner).getEnergyProduced());
    }

    @Override
    public int getMaxCapacity() throws Exception {
        return this.getOwner().handleRequestSync(
                owner -> ((Battery)owner).getMaxCapacity());
    }

    @Override
    public int getCurrentCapacity() throws Exception {
        return this.getOwner().handleRequestSync(
                owner -> ((Battery)owner).getCurrentCapacity());
    }

    @Override
    public void setMode(BatteryState mode) throws Exception {
        this.getOwner().handleRequestSync(
                new AbstractComponent.AbstractService<Void>() {
                    @Override
                    public Void call() throws Exception {
                        ((Battery)this.getServiceOwner()).setMode(mode);
                        return null;
                    }
                }) ;
    }
}
