package interfaces;

import components.Lamp.LampState;
import fr.sorbonne_u.components.interfaces.RequiredI;

/**
 * The interface <code>LampControllerI</code> defines the interface required by a
 * component that needs to get information from a lamp component.
 */
public interface LampControllerI extends RequiredI {
	/**
	 * get current lamp state
	 * @return lamp state
	 * @throws Exception
	 */
    public LampState getState() throws Exception;
    /**
     * Set the lamp on break or not on break
	 * @throws Exception
	 */
    public void switchBreak() throws Exception;
    /**
     * Return isOnBreak value for the lamp
     * 
     * @return isOnBreak
     */
    public boolean isOnBreak() throws Exception;
}
