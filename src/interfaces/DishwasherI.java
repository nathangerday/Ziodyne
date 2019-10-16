package interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;

public interface DishwasherI extends OfferedI{

    public boolean isOn() throws Exception;

    public boolean isModeEco() throws Exception;

    public void setModeEco(boolean on) throws Exception;

    public int getTimeLeft() throws Exception;

    public void startProgram() throws Exception;
    
    
}