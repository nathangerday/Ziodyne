package connectors;

import components.Battery.BState;
import fr.sorbonne_u.components.connectors.AbstractConnector;
import interfaces.BatteryControllerI;
import interfaces.BatteryI;

public class BatteryConnector extends AbstractConnector implements BatteryControllerI {

    @Override
    public double getMaxCapacity() throws Exception {
        return ((BatteryI)this.offering).getMaxCapacity();
    }

    @Override
    public double getCurrentCapacity() throws Exception {
        return ((BatteryI)this.offering).getCurrentCapacity();
    }

    @Override
    public void setMode(BState mode) throws Exception {
        ((BatteryI)this.offering).setMode(mode);
    }
    
    @Override
    public BState getMode() throws Exception {
        return ((BatteryI)this.offering).getMode();
    }
}
