package connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;

import interfaces.WindTurbineControllerI;
import interfaces.WindTurbineI;

public class WindTurbineConnector extends AbstractConnector implements WindTurbineControllerI {

	@Override
	public void switchOn() throws Exception {
		((WindTurbineI)this.offering).switchOn();
	}

	@Override
	public int getEnergyProduced() throws Exception {
		return ((WindTurbineI)this.offering).getEnergyProduced();
	}

	@Override
	public int getWindSpeed() throws Exception {
		return ((WindTurbineI)this.offering).getWindSpeed();
	}


}
