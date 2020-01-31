package connectors;

import components.Dishwasher.DWMode;
import fr.sorbonne_u.components.connectors.AbstractConnector;
import interfaces.DishwasherControllerI;
import interfaces.DishwasherI;

public class DishwasherConnector extends AbstractConnector implements DishwasherControllerI{

    @Override
    public boolean isOn() throws Exception {
        return ((DishwasherI)this.offering).isOn();
    }

    @Override
    public double getTimeLeft() throws Exception {
        return ((DishwasherI)this.offering).getTimeLeft();
    }

    @Override
    public DWMode getMode() throws Exception {
        return ((DishwasherI)this.offering).getMode();
    }

    @Override
    public void setMode(DWMode mode) throws Exception {
        ((DishwasherI)this.offering).setMode(mode);        
    }

    @Override
    public void switchBreak() throws Exception {
        ((DishwasherI)this.offering).switchBreak();      
    }

    @Override
    public boolean isOnBreak() throws Exception {
        return ((DishwasherI)this.offering).isOnBreak();
    }
}
