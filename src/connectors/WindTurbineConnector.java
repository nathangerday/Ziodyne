package connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;

import interfaces.WindTurbineControllerI;
import interfaces.WindTurbineI;

public class WindTurbineConnector extends AbstractConnector implements WindTurbineControllerI {

	@Override
    public boolean isOn() throws Exception {
        return ((WindTurbineI)this.offering).isOn();
    }
	
	@Override
    public boolean isOnBreak() throws Exception {
        return ((WindTurbineI)this.offering).isOnBreak();
    }
	
	@Override
    public void switchBreak() throws Exception {
        ((WindTurbineI)this.offering).switchBreak();
    }

	@Override
	public double getWindSpeed() throws Exception {
		return ((WindTurbineI)this.offering).getWindSpeed();
	}
}
