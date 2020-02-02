package ports;

import components.ElectricMeter;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import interfaces.ElectricMeterI;


/**
 * The class <code>FridgeControllerOutboundPort</code> implements an inbound port for
 * the <code>ElectricMeterI</code> interface.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 */
public class ElectricMeterInboundPort extends AbstractInboundPort implements ElectricMeterI {
    private static final long serialVersionUID = 1L;
    
    /**
	 * create the port with the given URI and the given owner.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	uri != null and owner != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param uri		URI of the port.
	 * @param owner		owner of the port.
	 * @throws Exception	<i>todo.</i>
	 */
    public ElectricMeterInboundPort(String uri, ComponentI owner) throws Exception {
        super(uri, ElectricMeterI.class, owner);

        assert uri != null && owner instanceof ElectricMeter;
    }

    public ElectricMeterInboundPort(ComponentI owner) throws Exception {
        super(ElectricMeter.class, owner);

        assert owner instanceof ElectricMeter;
    }
    
    /**
     * @see interfaces.ElectricMeterI#getAvailableEnergy()
     */
    @Override
    public double getAvailableEnergy() throws Exception {
        return this.getOwner().handleRequestSync(
                owner -> ((ElectricMeter)owner).getAvailableEnergy());
    }
    
    /**
     * @see interfaces.ElectricMeterI#getProduction()
     */
    @Override
    public double getProduction() throws Exception {
        return this.getOwner().handleRequestSync(
                owner -> ((ElectricMeter)owner).getProduction());
    }
    
    /**
     * @see interfaces.ElectricMeterI#getConsumption()
     */
    @Override
    public double getConsumption() throws Exception {
        return this.getOwner().handleRequestSync(
                owner -> ((ElectricMeter)owner).getConsumption());
    }
}
