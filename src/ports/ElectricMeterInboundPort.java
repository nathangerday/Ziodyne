package ports;

import components.ElectricMeter;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import interfaces.ElectricMeterI;

public class ElectricMeterInboundPort extends AbstractInboundPort implements ElectricMeterI {
    private static final long serialVersionUID = 1L;

    public ElectricMeterInboundPort(String uri, ComponentI owner) throws Exception {
        super(uri, ElectricMeterI.class, owner);

        assert uri != null && owner instanceof ElectricMeter;
    }

    public ElectricMeterInboundPort(ComponentI owner) throws Exception {
        super(ElectricMeter.class, owner);

        assert owner instanceof ElectricMeter;
    }

    @Override
    public int getConsommation() throws Exception {
        return this.getOwner().handleRequestSync(
                owner -> ((ElectricMeter)owner).getConsommation());
    }
}
