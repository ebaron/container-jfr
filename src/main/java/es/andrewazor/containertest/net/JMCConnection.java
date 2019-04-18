package es.andrewazor.containertest.net;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXServiceURL;

import org.openjdk.jmc.rjmx.ConnectionException;
import org.openjdk.jmc.rjmx.IConnectionHandle;
import org.openjdk.jmc.rjmx.IConnectionListener;
import org.openjdk.jmc.rjmx.ServiceNotAvailableException;
import org.openjdk.jmc.rjmx.internal.DefaultConnectionHandle;
import org.openjdk.jmc.rjmx.internal.JMXConnectionDescriptor;
import org.openjdk.jmc.rjmx.internal.RJMXConnection;
import org.openjdk.jmc.rjmx.internal.ServerDescriptor;
import org.openjdk.jmc.rjmx.services.jfr.IFlightRecorderService;
import org.openjdk.jmc.rjmx.services.jfr.internal.FlightRecorderServiceFactory;
import org.openjdk.jmc.ui.common.security.InMemoryCredentials;

import es.andrewazor.containertest.sys.Clock;
import es.andrewazor.containertest.tui.ClientWriter;

public class JMCConnection {

    static final String URL_FORMAT = "service:jmx:rmi:///jndi/rmi://%s:%d/jmxrmi";
    public static final int DEFAULT_PORT = 9091;

    private final ClientWriter cw;
    private final Clock clock;
    private final RJMXConnection rjmxConnection;
    private final IConnectionHandle handle;
    private final IFlightRecorderService service;

    JMCConnection(ClientWriter cw, Clock clock, String host, int port) throws Exception {
        this.cw = cw;
        this.clock = clock;
        this.rjmxConnection = attemptConnect(host, port, 0);
        this.handle = new DefaultConnectionHandle(rjmxConnection, "RJMX Connection", new IConnectionListener[0]);
        this.service = new FlightRecorderServiceFactory().getServiceInstance(handle);
    }

    public IConnectionHandle getHandle() {
        return this.handle;
    }

    public IFlightRecorderService getService() {
        return this.service;
    }

    public long getApproximateServerTime(Clock clock) {
        return rjmxConnection.getApproximateServerTime(clock.getWallTime());
    }

    public void disconnect() {
        this.rjmxConnection.close();
    }

    public <T> T getMBean(ObjectName objectName, Class<T> klazz) throws ConnectionException, ServiceNotAvailableException {
        return JMX.newMXBeanProxy(handle.getServiceOrThrow(MBeanServerConnection.class), objectName, klazz);
    }

    private RJMXConnection attemptConnect(String host, int port, int maxRetry) throws Exception {
        JMXConnectionDescriptor cd = new JMXConnectionDescriptor(
                new JMXServiceURL(String.format(URL_FORMAT, host, port)),
                new InMemoryCredentials(null, null));
        ServerDescriptor sd = new ServerDescriptor(null, "Container", null);

        int attempts = 0;
        while (true) {
            try {
                RJMXConnection conn = new RJMXConnection(cd, sd, JMCConnection::failConnection);
                if (!conn.connect()) {
                    failConnection();
                }
                return conn;
            } catch (Exception e) {
                attempts++;
                cw.println(String.format("Connection attempt %d failed.", attempts));
                if (attempts >= maxRetry) {
                    cw.println("Too many failed connections. Aborting.");
                    throw e;
                } else {
                    cw.println(e);
                }
                clock.sleep(500);
            }
        }
    }

    private static void failConnection() {
        throw new RuntimeException("Connection Failed");
    }
}
