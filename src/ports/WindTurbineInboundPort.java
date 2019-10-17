package ports;

import components.WindTurbine;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import interfaces.WindTurbineI;

public class WindTurbineInboundPort extends AbstractInboundPort implements WindTurbineI{
	
	private static final long serialVersionUID = 1L;

	public WindTurbineInboundPort(String uri, ComponentI windTurbine) throws Exception {
	        super(uri, WindTurbineI.class, windTurbine);

	        assert uri != null && windTurbine instanceof WindTurbine;
	    }
	

	public WindTurbineInboundPort(ComponentI windTurbine) throws Exception {
	        super(WindTurbineI.class, windTurbine);

	        assert windTurbine instanceof WindTurbine;
	    }
	

	@Override
	public void switchOn() throws Exception {
		 this.getOwner().handleRequestSync(
	                new AbstractComponent.AbstractService<Void>() {
	                    @Override
	                    public Void call() throws Exception {
	                        ((WindTurbine)this.getServiceOwner()).switchOn();
	                        return null;
	                    }
	                }) ;
	}

	@Override
	public int getEnergyProduced() throws Exception {
		 return this.getOwner().handleRequestSync(
	                owner -> ((WindTurbine)owner).getEnergyProduced());
	}

	@Override
	public int getWindSpeed() throws Exception {
		 return this.getOwner().handleRequestSync(
	                owner -> ((WindTurbine)owner).getWindSpeed());
	}
}
