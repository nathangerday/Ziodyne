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


    //ports uri

    public static final String LAMP_CONTROLLER_OUTBOUND_PORT = "port-lamp-controller-out-uri";
    public static final String LAMP_INBOUND_PORT = "port-lamp-in-uri";

    public static final String FRIDGE_CONTROLLER_OUTBOUND_PORT = "port-fridge-controller-out-uri";
    public static final String FRIDGE_INBOUND_PORT = "port-fridge-in-uri";

    public static final String DISHWASHER_CONTROLLER_OUTBOUND_PORT = "port-dishwasher-controller-out-uri";
    public static final String DISHWASHER_INBOUND_PORT = "port-dishwasher-in-uri";

    public static final String WINDTURBINE_CONTROLLER_OUTBOUND_PORT = "port-windturbine-controller-out-uri";
    public static final String WINDTURBINE_INBOUND_PORT = "port-windturbine-in-uri";
}
