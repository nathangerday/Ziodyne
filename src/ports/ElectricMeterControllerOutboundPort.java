package ports;

import components.Controller;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import interfaces.ElectricMeterControllerI;

public class ElectricMeterControllerOutboundPort extends AbstractOutboundPort implements ElectricMeterControllerI {

    private static final long serialVersionUID = 1L;

    public ElectricMeterControllerOutboundPort(String uri, ComponentI owner) throws Exception {
        super(uri, ElectricMeterControllerI.class, owner);

        assert uri != null && owner instanceof Controller;
    }

    public ElectricMeterControllerOutboundPort(ComponentI owner) throws Exception {
        super(ElectricMeterControllerI.class, owner);

        assert owner instanceof Controller;
    }

    @Override
    public int getConsommation() throws Exception{
        return ((ElectricMeterControllerI)this.connector).getConsommation();
    }
}
