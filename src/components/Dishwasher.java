package components;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import interfaces.DishwasherI;
import ports.DishwasherInboundPort;

public class Dishwasher extends AbstractComponent{
    protected boolean isOn;
    protected boolean isModeEco;
    protected int timeLeft;

    protected String uri;
    protected DishwasherInboundPort p;

    protected Dishwasher(String uri) throws Exception{
        super(uri, 1, 0);

        this.uri = uri;
        this.isOn = false;
        this.isModeEco = false;
        this.timeLeft = 0;
        
        this.addOfferedInterface(DishwasherI.class);
        p = new DishwasherInboundPort(uri, this);
        p.publishPort();

    }

    @Override
    public void shutdown() throws ComponentShutdownException {
        try {
            p.unpublishPort();
        }catch(Exception e) {
            throw new ComponentShutdownException(e);
        }
        super.shutdown();
    }

    
    @Override
    public void shutdownNow() throws ComponentShutdownException {
        try {
            p.unpublishPort();
        }catch(Exception e) {
            throw new ComponentShutdownException(e);
        }
        super.shutdownNow();
    }


    public boolean isOn(){
        return this.isOn;
    }

    public boolean isModeEco(){
        return this.isModeEco;
    }

    public void setModeEco(boolean on){
        this.isModeEco = on;
    }

    public int getTimeLeft(){
        return this.timeLeft;
    }

    public void startProgram(){
        this.isOn = true;
        if(isModeEco){
            this.timeLeft = 2000;
        }else{
            this.timeLeft = 1400;
        }
        //TODO Start another thread to decrease "timeLeft" over time
    }

}