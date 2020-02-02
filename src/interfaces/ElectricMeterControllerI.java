package interfaces;

import fr.sorbonne_u.components.interfaces.RequiredI;

/**
 * The interface <code>ElectricMeterControllerI</code> defines the interface required by a
 * component that needs to get information from a electric meter component.
 */
public interface ElectricMeterControllerI extends RequiredI{
	/**
	 * get available energy
	 * @return available energy
	 * @throws Exception
	 */
    public double getAvailableEnergy() throws Exception;
    /**
     * get production
     * 
     * @return production
     * @exception exception
     */
    public double getProduction() throws Exception;
    /**
     * get consumption
     * 
     * @return consumption
     * @throws Exception
     */
    public double getConsumption() throws Exception;
}
