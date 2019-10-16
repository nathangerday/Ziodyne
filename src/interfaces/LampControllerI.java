package interfaces;

import fr.sorbonne_u.components.interfaces.RequiredI;

public interface LampControllerI extends RequiredI {
	
	/**
	 * return a boolean on the lamp's state (On or Off)
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition
	 * </pre>
	 *
	 * @return	the lamp's state
	 * @throws Exception	<i>todo.</i>
	 */
    public boolean isLampOn() throws Exception;
    
	/**
	 * Change the lamp's  state
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true
	 * post	isOn() == !isOn()
	 * </pre>
	 *
	 * @throws Exception	<i>todo.</i> 
	 */
    public void switchLamp() throws Exception;
}
