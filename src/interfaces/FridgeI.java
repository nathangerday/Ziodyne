package interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;

/**
 * The interface <code>FridgeControllerI</code> defines the interface offered by a
 * component that needs to get information from a fridge component.
 */
public interface FridgeI extends OfferedI {
	/**
     * Set the  fridge on break or not on break
	 * @throws Exception
	 */
    public void switchFridgeBreak() throws Exception ;
    /**
     * Return isFridgeOnBreak value
     * 
     * @return isFreezerOnBreak
     */
    public boolean isFridgeOnBreak() throws Exception ;
	/**
     * Set the  freezer on break or not on break
	 * @throws Exception
	 */
    public void switchFreezerBreak() throws Exception ;
    /**
     * Return isFreezerOnBreak value
     * 
     * @return isFreezerOnBreak
     */
    public boolean isFreezerOnBreak() throws Exception ;
    /**
     * get fridge state
     * 
     * @return true if fridge is on, false if fridge is off
     * @throws Exception
     */
    public boolean isFridgeOn() throws Exception ;
    /**
     * get freezer state 
     * 
     * @return true if freezer is on, false if freezer is off
     * @throws Exception
     */
    public boolean isFreezerOn() throws Exception ;
}
