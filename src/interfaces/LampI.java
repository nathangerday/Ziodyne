package interfaces;

import components.Lamp.LampState;
import fr.sorbonne_u.components.interfaces.OfferedI;

/**
 * The interface <code>LampControllerI</code> defines the interface required by a
 * component that needs to get information from a lamp component.
 */
public interface LampI extends OfferedI{
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
