package interfaces;

import fr.sorbonne_u.components.interfaces.RequiredI;

public interface LampControllerI extends RequiredI {
    
    /**
     * return the lamp's state
     * 
     * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition
	 * </pre>
	 *
	 * @return	int : the lamp's state
	 * @throws Exception	<i>todo.</i>
     * 
     */
    public int getState() throws Exception;
  
}
