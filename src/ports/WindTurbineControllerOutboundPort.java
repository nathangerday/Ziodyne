package ports;

import components.Controller;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import interfaces.WindTurbineControllerI;

public class WindTurbineControllerOutboundPort extends AbstractOutboundPort implements WindTurbineControllerI {

	 private static final long serialVersionUID = 1L;

	    public WindTurbineControllerOutboundPort(String uri, ComponentI owner) throws Exception {
	        super(uri, WindTurbineControllerI.class, owner);

	        assert uri != null && owner instanceof Controller;
	    }

		@Override
		public void switchOn() throws Exception {
			((WindTurbineControllerI)this.connector).switchOn();
			
		}

		@Override
		public int getEnergyProduced() throws Exception {
			 return ((WindTurbineControllerI)this.connector).getEnergyProduced();
		}

		@Override
		public int getWindSpeed() throws Exception {
			 return ((WindTurbineControllerI)this.connector).getWindSpeed();
		}
}
