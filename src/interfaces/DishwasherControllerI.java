package interfaces;

import components.Dishwasher.DWMode;
import fr.sorbonne_u.components.interfaces.RequiredI;

public interface DishwasherControllerI extends RequiredI{
    public boolean isOn() throws Exception;
    public double getTimeLeft() throws Exception;
    public DWMode getMode() throws Exception;
    public void setMode(DWMode mode) throws Exception;
    public void switchBreak() throws Exception;
    public boolean isOnBreak() throws Exception;
}