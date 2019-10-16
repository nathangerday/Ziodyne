package interfaces;

import fr.sorbonne_u.components.interfaces.RequiredI;

public interface WindTurbineControllerI extends RequiredI{

	public void switchOn() throws Exception;
	
	public int getEnergyProduced() throws Exception;
	
	public int getWindSpeed() throws Exception;

}
