package interfaces;

import fr.sorbonne_u.components.interfaces.RequiredI;

public interface DishwasherControllerI extends RequiredI{

    public boolean isDishwasherOn() throws Exception;

    public boolean isDishwasherModeEco() throws Exception;

    public void setDishwasherModeEco(boolean on) throws Exception;

    public int getDishwasherTimeLeft() throws Exception;

    public void startDishwasherProgram() throws Exception;
    
    
}