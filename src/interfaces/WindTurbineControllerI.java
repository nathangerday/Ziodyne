package interfaces;

import fr.sorbonne_u.components.interfaces.RequiredI;

public interface WindTurbineControllerI extends RequiredI{
    public boolean isOn() throws Exception;
    public boolean isOnBreak() throws Exception;
    public void switchBreak() throws Exception;
    public double getWindSpeed() throws Exception;
}
