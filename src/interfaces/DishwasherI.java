package interfaces;

import components.Dishwasher.DWMode;
import fr.sorbonne_u.components.interfaces.OfferedI;

public interface DishwasherI extends OfferedI{
    public boolean isOn() throws Exception;
    public double getTimeLeft() throws Exception;
    public DWMode getMode() throws Exception;
    public void setMode(DWMode mode) throws Exception;
    public void switchBreak() throws Exception;
    public boolean isOnBreak() throws Exception;
}