package components;

import connectors.LampConnector;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import interfaces.LampControllerI;
import ports.LampControllerOutboundPort;

//-----------------------------------------------------------------------------
/**
* The class <code>Controller</code> implements a component that can control a Lamp
* from Lamp component.
*
* <p><strong>Description</strong></p>
* 
* The component declares its required service through the required interface
* <code>LampI</code> which has a <code>isLampOn</code> requested service
* signature.  The internal method <code>switchLamp</code> implements the
* main task of the component, as it calls the provider component through the
* outbound port implementing the connection.  It switches the button On and Off. The <code>start</code> method initiates
* this process. 
* 
* <p><strong>Invariant</strong></p>
* 
* <pre>
* invariant		true
* </pre>
* 
* <p>Created on : 2018-10-18</p>
*
*/

public class Controller extends AbstractComponent{

    private LampControllerOutboundPort p;
    private String uri;
    private String uri_lamp;

    
    /**
	 * @param uri				URI of the component
	 * @param uri_lamp 			URI of the URI lamp_component outbound port
	 * @throws Exception			<i>todo.</i>
	 */
    protected Controller(String uri, String uri_lamp) throws Exception{
        super(uri, 1, 0);
        this.uri = uri;
        this.uri_lamp = uri_lamp;

        this.addRequiredInterface(LampControllerI.class);
        p = new LampControllerOutboundPort(uri,this);
        p.publishPort();
    }

    
    /**
	 * a component is always started by calling this method, so intercept the
	 * call and make sure the task of the component is executed.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true				// no more preconditions.
	 * post	true				// no more postconditions.
	 * </pre>
	 * 
	 * @see fr.sorbonne_u.components.AbstractComponent#start()
	 */
    @Override
    public void start() throws ComponentStartException{
        super.start();
        try {
            this.doPortConnection(uri, uri_lamp, LampConnector.class.getCanonicalName());
        }catch(Exception e) {
            throw new ComponentStartException(e);
        }
    }

    @Override
    public void execute() throws Exception{
        super.execute();
        System.out.print("Lampe état : ");
        switch(this.p.getState()) {
        	case 0 : System.out.println("éteint");break;
	        case 1 : System.out.println("tamisé"); break;
	        case 2 : System.out.println("normal"); break;
	        case 3 : System.out.println("fort"); break;
        }
        
    }

    @Override
    public void shutdown() throws ComponentShutdownException {
        try {
            p.unpublishPort();
        }catch(Exception e) {
            throw new ComponentShutdownException(e);
        }
        super.shutdown();
    }

    @Override
    public void shutdownNow() throws ComponentShutdownException {
        try {
            p.unpublishPort();
        }catch(Exception e) {
            throw new ComponentShutdownException(e);
        }
        super.shutdownNow();
    }

    @Override
    public void finalise() throws Exception{
        this.doPortDisconnection(p.getPortURI());
        super.finalise();
    }

}
