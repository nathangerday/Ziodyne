package interfaces;

import components.Battery.BState;
import fr.sorbonne_u.components.interfaces.OfferedI;

public interface BatteryI extends OfferedI {
    public double getMaxCapacity() throws Exception;
    public double getCurrentCapacity() throws Exception;
	public void setMode(BState mode) throws Exception;
}
