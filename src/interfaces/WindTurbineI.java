package interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;

public interface WindTurbineI extends OfferedI{
    public boolean isOn() throws Exception;
    public boolean isOnBreak() throws Exception;
    public void switchBreak() throws Exception;
	public double getWindSpeed() throws Exception;
}
