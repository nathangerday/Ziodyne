package interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;

public interface WindTurbineI extends OfferedI{
	
	public void switchOn() throws Exception;
	
	public int getEnergyProduced() throws Exception;
	
	public int getWindSpeed() throws Exception;
	
	/*
	public void distributeEnergy() throws Exception;
	*/
}
