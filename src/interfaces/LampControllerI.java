package interfaces;

import fr.sorbonne_u.components.interfaces.RequiredI;

public interface LampControllerI extends RequiredI {
    public boolean isLampOn() throws Exception;
    public void switchLamp() throws Exception;
}
