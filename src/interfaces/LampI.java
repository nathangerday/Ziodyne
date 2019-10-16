package interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;

/**
 * The interface <code>LampI</code>
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>Created on : 2018-10-16</p>
 * 
 * 
 */
public interface LampI extends OfferedI{
	
	/**
	 * return a boolean whether the lamp is On or Off. (false if Off, true if On)
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true
	 * post	true
	 * </pre>
	 *
	 * @return boolean.
	 * @throws Exception	<i>todo.</i>
	 */
    public boolean isOn() throws Exception;
    

	/**
	 * service called in order to change the lamp state
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true
	 * post	true
	 * </pre>
	 *
	 * @throws Exception	<i>todo.</i> 
	 */
    public void switchButton() throws Exception;
}
