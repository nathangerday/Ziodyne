package interfaces;

import components.Lamp.LampState;
import fr.sorbonne_u.components.interfaces.OfferedI;

public interface LampI extends OfferedI{
    public LampState getState() throws Exception;
}
