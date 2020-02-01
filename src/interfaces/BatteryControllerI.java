package interfaces;

import components.Battery.BState;
import fr.sorbonne_u.components.interfaces.RequiredI;

public interface BatteryControllerI extends RequiredI {
    public double getMaxCapacity() throws Exception;
    public double getCurrentCapacity() throws Exception;
    public void setMode(BState mode) throws Exception;
    public BState getMode() throws Exception;
}
