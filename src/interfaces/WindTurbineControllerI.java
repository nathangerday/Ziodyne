package interfaces;

import fr.sorbonne_u.components.interfaces.RequiredI;

/**
 * The interface <code>WindTurbineControllerI</code> defines the interface required by a
 * component that needs to get information from a wind turbine component.
 */
public interface WindTurbineControllerI extends RequiredI{
	/**
	 * get wind turbine state
	 * 
	 * @return true if wind turbine is on, else false
	 * @throws Exception
	 */
    public boolean isOn() throws Exception;
    /**
     * Return isOnBreak value of the wind turbine
     * 
     * @return isOnBreak
     */
    public boolean isOnBreak() throws Exception;
	/**
     * Set the  wind turbine on break or not on break
	 * @throws Exception
	 */
    public void switchBreak() throws Exception;
    /**
     * get wind speed 
     * 
     * @return wind speed
     * @throws Exception
     */
    public double getWindSpeed() throws Exception;
}
