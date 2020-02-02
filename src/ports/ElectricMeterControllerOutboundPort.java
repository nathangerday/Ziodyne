package ports;

import components.Controller;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import interfaces.ElectricMeterControllerI;

/**
 * The class <code>ElectricMeterControllerOutboundPort</code> implements an outbound port for
 * the <code>ElectricMeterControllerI</code> interface.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 */
public class ElectricMeterControllerOutboundPort extends AbstractOutboundPort implements ElectricMeterControllerI {

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
    public ElectricMeterControllerOutboundPort(String uri, ComponentI owner) throws Exception {
        super(uri, ElectricMeterControllerI.class, owner);

        assert uri != null && owner instanceof Controller;
    }

    public ElectricMeterControllerOutboundPort(ComponentI owner) throws Exception {
        super(ElectricMeterControllerI.class, owner);

        assert owner instanceof Controller;
    }
    
    /**
     * @see interfaces.ElectricMeterControllerI#getAvailableEnergy()
     */
    @Override
    public double getAvailableEnergy() throws Exception{
        return ((ElectricMeterControllerI)this.connector).getAvailableEnergy();
    }
    
    /**
     * @see interfaces.ElectricMeterControllerI#getProduction()
     */
    @Override
    public double getProduction() throws Exception{
        return ((ElectricMeterControllerI)this.connector).getProduction();
    }
    
    /**
     * @see interfaces.ElectricMeterControllerI#getConsumption()
     */
    @Override
    public double getConsumption() throws Exception{
        return ((ElectricMeterControllerI)this.connector).getConsumption();
    }
}
