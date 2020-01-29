package interfaces;

import components.Lamp.State;
import fr.sorbonne_u.components.interfaces.RequiredI;

public interface LampControllerI extends RequiredI {
    
    public State getState() throws Exception;
  
}
