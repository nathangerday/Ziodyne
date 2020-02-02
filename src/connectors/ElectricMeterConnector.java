package connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import interfaces.ElectricMeterControllerI;
import interfaces.ElectricMeterI;

/**
 * The class <code>ElectricMeterConnector</code> implements a connector
 * for the <code>ElectricMeterControllerI</code> interface.
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 */
public class ElectricMeterConnector extends AbstractConnector implements ElectricMeterControllerI {

	/**
	 * @see {@link interfaces.ElectricMeterControllerI#getAvailableEnergy()}
	 */
    @Override
    public double getAvailableEnergy() throws Exception {
        return ((ElectricMeterI)this.offering).getAvailableEnergy();
    }

    /**
	 * @see {@link interfaces.ElectricMeterControllerI#getProduction() }
	 */
    @Override
    public double getProduction() throws Exception {
        return ((ElectricMeterI)this.offering).getProduction();
    }

    /**
	 * @see {@link interfaces.ElectricMeterControllerI#getConsumption() }
	 */
    @Override
    public double getConsumption() throws Exception{
        return ((ElectricMeterI)this.offering).getConsumption();
    }
}
