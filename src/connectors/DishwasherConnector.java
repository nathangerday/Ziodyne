package connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import interfaces.DishwasherControllerI;
import interfaces.DishwasherI;

public class DishwasherConnector extends AbstractConnector implements DishwasherControllerI{


    @Override
    public boolean isDishwasherOn() throws Exception {
        return ((DishwasherI)this.offering).isOn();
    }

    @Override
    public boolean isDishwasherModeEco() throws Exception {
        return ((DishwasherI)this.offering).isModeEco();
    }

    @Override
    public void setDishwasherModeEco(boolean on) throws Exception {
        ((DishwasherI)this.offering).setModeEco(on);
    }

    @Override
    public int getDishwasherTimeLeft() throws Exception {
        return ((DishwasherI)this.offering).getTimeLeft();
    }

    @Override
    public void startDishwasherProgram() throws Exception {
        ((DishwasherI)this.offering).startProgram();
    }
}
