package interfaces;

import components.Lamp.LampState;
import fr.sorbonne_u.components.interfaces.RequiredI;

public interface LampControllerI extends RequiredI {
    
    public LampState getState() throws Exception;
  
}
