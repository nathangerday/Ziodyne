package components;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ports.PortI;
import ports.LampInboundPort;

public class Lamp extends AbstractComponent{
	
	protected final String uri;
	protected final String port;
	protected boolean isOn;
	
	protected Lamp(String uri, String port) throws Exception {
		super(uri, 1, 0);
		
		this.uri = uri;
		this.port = port;
		
		this.isOn = false;
		
		PortI p = new LampInboundPort(this.port, this);
		p.publishPort();
		
		//TODO addOfferedInterface ???
		
		
	}
	
	public boolean isOn() {
		return this.isOn;
	}
	
	public void switchButton() {
		this.isOn = !this.isOn;
	}
	
}
