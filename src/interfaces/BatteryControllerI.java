package interfaces;

import components.Battery.BState;
import fr.sorbonne_u.components.interfaces.RequiredI;

/**
 * The interface <code>BatteryControllerI</code> defines the interface required by a
 * component that needs to get information from a battery component.
 */
public interface BatteryControllerI extends RequiredI {
	/**
	 * get max capacity
	 * 
	 * @return max capacity
	 * @throws Exception
	 */
    public double getMaxCapacity() throws Exception;
    /**
	 * get current capacity
	 * 
	 * @return current capacity
	 * @throws Exception
	 */
    public double getCurrentCapacity() throws Exception;
    /**
     * set Mode
     * 
     * @param mode
     * @throws Exception
     */
    public void setMode(BState mode) throws Exception;
    /**
     * get mode
     * @return mode
     * @throws Exception
     */
    public BState getMode() throws Exception;
}
