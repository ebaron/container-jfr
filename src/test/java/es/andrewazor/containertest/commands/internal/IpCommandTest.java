package es.andrewazor.containertest.commands.internal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.net.UnknownHostException;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.andrewazor.containertest.commands.SerializableCommand.ExceptionOutput;
import es.andrewazor.containertest.commands.SerializableCommand.Output;
import es.andrewazor.containertest.commands.SerializableCommand.StringOutput;
import es.andrewazor.containertest.net.NetworkResolver;
import es.andrewazor.containertest.tui.ClientWriter;

@ExtendWith(MockitoExtension.class)
class IpCommandTest {

    IpCommand command;
    @Mock ClientWriter cw;
    @Mock NetworkResolver resolver;

    @BeforeEach
    void setup() {
        command = new IpCommand(cw, resolver);
    }

    @Test
    void shouldBeNamedIp() {
        MatcherAssert.assertThat(command.getName(), Matchers.equalTo("ip"));
    }

    @Test
    void shouldExpectNoArgs() {
        assertTrue(command.validate(new String[0]));
        verifyZeroInteractions(cw);
    }

    @Test
    void shouldNotExpectArgs() {
        assertFalse(command.validate(new String[1]));
        verify(cw).println("No arguments expected");
    }

    @Test
    void shouldBeAvailable() {
        assertTrue(command.isAvailable());
    }

    @Test
    void shouldPrintResolverIp() throws Exception {
        when(resolver.getHostAddress()).thenReturn("192.168.2.1");
        command.execute(new String[0]);
        verify(cw).println("\t192.168.2.1");
    }

    @Test
    void shouldReturnStringOutput() throws Exception {
        when(resolver.getHostAddress()).thenReturn("192.168.2.1");
        Output<?> out = command.serializableExecute(new String[0]);
        MatcherAssert.assertThat(out, Matchers.instanceOf(StringOutput.class));
        MatcherAssert.assertThat(out.getPayload(), Matchers.equalTo("192.168.2.1"));
    };

    @Test
    void shouldReturnExceptionOutput() throws Exception {
        when(resolver.getHostAddress()).thenThrow(UnknownHostException.class);
        Output<?> out = command.serializableExecute(new String[0]);
        MatcherAssert.assertThat(out, Matchers.instanceOf(ExceptionOutput.class));
        MatcherAssert.assertThat(out.getPayload(), Matchers.equalTo("UnknownHostException: "));
    };

}