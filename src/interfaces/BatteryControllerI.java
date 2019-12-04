package interfaces;

import components.BatteryState;
import fr.sorbonne_u.components.interfaces.RequiredI;

public interface BatteryControllerI extends RequiredI {

    public void switchOn() throws Exception;

    public int getEnergyProduced() throws Exception;

    public int getMaxCapacity() throws Exception;

    public int getCurrentCapacity() throws Exception;

    public void setMode(BatteryState mode) throws Exception;
    
}
