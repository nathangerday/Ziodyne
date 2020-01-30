package ports;

import components.WindTurbine;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import interfaces.WindTurbineI;

public class WindTurbineInboundPort extends AbstractInboundPort implements WindTurbineI{

    private static final long serialVersionUID = 1L;

    public WindTurbineInboundPort(String uri, ComponentI windTurbine) throws Exception {
        super(uri, WindTurbineI.class, windTurbine);

        assert uri != null && windTurbine instanceof WindTurbine;
    }


    public WindTurbineInboundPort(ComponentI windTurbine) throws Exception {
        super(WindTurbineI.class, windTurbine);

        assert windTurbine instanceof WindTurbine;
    }


    @Override
    public boolean isOn() throws Exception {
        return this.getOwner().handleRequestSync(
                owner -> ((WindTurbine)owner).isOn());
    }


    @Override
    public boolean isOnBreak() throws Exception {
        return this.getOwner().handleRequestSync(
                owner -> ((WindTurbine)owner).isOnBreak());
    }


    @Override
    public void switchBreak() throws Exception {
        this.getOwner().handleRequestSync(
                new AbstractComponent.AbstractService<Void>() {
                    @Override
                    public Void call() throws Exception {
                        ((WindTurbine)this.getServiceOwner()).switchBreak();
                        return null;
                    }
                }) ;
    }


    @Override
    public double getWindSpeed() throws Exception {
        return this.getOwner().handleRequestSync(
                owner -> ((WindTurbine)owner).getWindSpeed());
    }
}
