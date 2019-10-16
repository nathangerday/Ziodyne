package main;

import components.Controller;
import components.Lamp;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;



public class CVM extends AbstractCVM{
    public CVM() throws Exception{
        super();
    }

    
    public static void main(String[] args) throws Exception{
        CVM a = new CVM();
        a.startStandardLifeCycle(20000L);
        Thread.sleep(5000L);
        System.exit(1);
    }
    
    /**
	 * instantiate the components, publish their port and interconnect them.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	!this.deploymentDone()
	 * post	this.deploymentDone()
	 * </pre>
	 * 
	 * @see fr.sorbonne_u.components.cvm.AbstractCVM#deploy()
	 */
    @Override
    public void deploy() throws Exception{
    	
    	assert	!this.deploymentDone() ;
    	
        AbstractComponent.createComponent(Controller.class.getCanonicalName(),
                new Object[] {Constants.URI_OUTBOUND_LAMP, Constants.URI_INBOUND_LAMP});
        AbstractComponent.createComponent(Lamp.class.getCanonicalName(),
                new Object[] {Constants.URI_INBOUND_LAMP});
        super.deploy();
        
        assert this.deploymentDone();
    }

    
    /**
	 * @see fr.sorbonne_u.components.cvm.AbstractCVM#finalise()
	 */
    @Override
    public void finalise() throws Exception{
        super.finalise();
    }

    

}
