package interfaces;

import components.Dishwasher.DWMode;
import fr.sorbonne_u.components.interfaces.OfferedI;

/**
 * The interface <code>DishwasherControllerI</code> defines the interface offered by a
 * component that needs to get information from a dishwasher component.
 */
public interface DishwasherI extends OfferedI{
	/**
	 * get isOn value
	 * 
	 * @return true if is dishwasher is on, false if it is off
	 * @throws Exception
	 */
    public boolean isOn() throws Exception;
    /**
     * get time left
    
     * @return time left
     * @throws Exception
     */
    public double getTimeLeft() throws Exception;
    /**
     * get mode
     * 
     * @return dishwasher current mode
     * @throws Exception
     */
    public DWMode getMode() throws Exception;
    /**
     * set mode
     * 
     * @param mode
     * @throws Exception
     */
    public void setMode(DWMode mode) throws Exception;
    /**
     * switch break state
     * 
     * @throws Exception
     */
    public void switchBreak() throws Exception;
    /**
     * get break state
     * 
     * @return break state
     * @throws Exception
     */
    public boolean isOnBreak() throws Exception;
}