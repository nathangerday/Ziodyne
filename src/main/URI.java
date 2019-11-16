package main;

/**
 * Contains the urls for ports
 * @author kelly
 *
 */

public class URI {
    //components uri
    public static final String COMPONENT_CONTROLLER = "controller-uri";
    public static final String COMPONENT_LAMP = "lamp-uri";
    public static final String COMPONENT_FRIDGE = "fridge-uri";
    public static final String COMPONENT_DISHWASHER = "dishwasher-uri";
    public static final String COMPONENT_WINDTURBINE = "windturbine-uri";
    public static final String COMPONENT_ELECTRICMETER = "electricmeter-uri";
    public static final String COMPONENT_BATTERY = "battery-uri" ;

    //jvm
    public static final String JVM_CONTROLLER = "controller";
    public static final String JVM_ELECTRICMETER = "electricmeter";
    public static final String JVM_COMPONENTS = "components";

    //ports uri

    public static final String LAMP_CONTROLLER_OUTBOUND_PORT = "port-lamp-controller-out-uri";
    public static final String LAMP_INBOUND_PORT = "port-lamp-in-uri";

    public static final String FRIDGE_CONTROLLER_OUTBOUND_PORT = "port-fridge-controller-out-uri";
    public static final String FRIDGE_INBOUND_PORT = "port-fridge-in-uri";

    public static final String DISHWASHER_CONTROLLER_OUTBOUND_PORT = "port-dishwasher-controller-out-uri";
    public static final String DISHWASHER_INBOUND_PORT = "port-dishwasher-in-uri";

    public static final String WINDTURBINE_CONTROLLER_OUTBOUND_PORT = "port-windturbine-controller-out-uri";
    public static final String WINDTURBINE_INBOUND_PORT = "port-windturbine-in-uri";

    public static final String ELECTRICMETER_CONTROLLER_OUTBOUND_PORT = "port-electricmeter-controller-out-uri";
    public static final String ELECTRICMETER_INBOUND_PORT = "port-electricmeter-in-uri";

    public static final Object BATTERY_CONTROLLER_OUTBOUND_PORT = "port-battery-controller-out-uri" ;
    public static final String BATTERY_INBOUND_PORT = "port-battery-in-uri" ;

}
