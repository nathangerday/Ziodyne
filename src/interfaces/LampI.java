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
