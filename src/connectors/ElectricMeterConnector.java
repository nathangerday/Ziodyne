package connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import interfaces.ElectricMeterControllerI;
import interfaces.ElectricMeterI;

public class ElectricMeterConnector extends AbstractConnector implements ElectricMeterControllerI {
    @Override
    public int getConsommation() throws Exception{
        return ((ElectricMeterI)this.offering).getConsommation();
    }
}
