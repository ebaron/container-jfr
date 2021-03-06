package es.andrewazor.containertest.net;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openjdk.jmc.rjmx.internal.WrappedConnectionException;

import es.andrewazor.containertest.sys.Clock;
import es.andrewazor.containertest.tui.ClientWriter;

@ExtendWith(MockitoExtension.class)
class JMCConnectionToolkitTest {

    JMCConnectionToolkit toolkit;
    @Mock ClientWriter cw;
    @Mock Clock clock;

    @BeforeEach
    void setup() {
        toolkit = new JMCConnectionToolkit(cw, clock);
    }

    @Test
    void shouldThrowInTestEnvironment() {
        assertThrows(WrappedConnectionException.class, () -> toolkit.connect("foo", 9091));
    }

    @Test
    void shouldThrowInTestEnvironment2() {
        assertThrows(WrappedConnectionException.class, () -> toolkit.connect("foo"));
    }

}