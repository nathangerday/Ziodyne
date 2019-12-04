package interfaces;

import components.BatteryState;
import fr.sorbonne_u.components.interfaces.OfferedI;

public interface BatteryI extends OfferedI {

    public void switchOn() throws Exception;

    public int getEnergyProduced() throws Exception;

    public int getMaxCapacity() throws Exception;

    public int getCurrentCapacity() throws Exception;

	void setMode(BatteryState mode) throws Exception;


}
