package es.andrewazor.containertest.commands.internal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openjdk.jmc.rjmx.services.jfr.FlightRecorderException;
import org.openjdk.jmc.rjmx.services.jfr.IFlightRecorderService;
import org.openjdk.jmc.rjmx.services.jfr.IRecordingDescriptor;

import es.andrewazor.containertest.commands.SerializableCommand.ExceptionOutput;
import es.andrewazor.containertest.commands.SerializableCommand.FailureOutput;
import es.andrewazor.containertest.commands.SerializableCommand.Output;
import es.andrewazor.containertest.commands.SerializableCommand.SuccessOutput;
import es.andrewazor.containertest.net.JMCConnection;
import es.andrewazor.containertest.tui.ClientWriter;

@ExtendWith(MockitoExtension.class)
class StopRecordingCommandTest {

    StopRecordingCommand command;
    @Mock ClientWriter cw;
    @Mock JMCConnection connection;
    @Mock IFlightRecorderService service;

    @BeforeEach
    void setup() {
        command = new StopRecordingCommand(cw);
        command.connectionChanged(connection);
    }

    @Test
    void shouldBeNamedStop() {
        MatcherAssert.assertThat(command.getName(), Matchers.equalTo("stop"));
    }

    @Test
    void shouldNotExpectNoArg() {
        assertFalse(command.validate(new String[0]));
        verify(cw).println("Expected one argument: recording name");
    }

    @Test
    void shouldNotExpectMalformedArg() {
        assertFalse(command.validate(new String[]{ "." }));
        verify(cw).println(". is an invalid recording name");
    }

    @Test
    void shouldExpectRecordingNameArg() {
        assertTrue(command.validate(new String[]{ "foo" }));
        verifyZeroInteractions(cw);
    }

    @Test
    void shouldHandleNoRecordingFound() throws Exception {
        verifyZeroInteractions(service);
        verifyZeroInteractions(connection);

        when(connection.getService()).thenReturn(service);
        when(service.getAvailableRecordings()).thenReturn(Collections.emptyList());

        command.execute(new String[]{ "foo" });

        verifyNoMoreInteractions(service);
        verifyNoMoreInteractions(connection);
        verify(cw).println("Recording with name \"foo\" not found");
    }

    @Test
    void shouldHandleRecordingFound() throws Exception {
        verifyZeroInteractions(service);
        verifyZeroInteractions(connection);

        IRecordingDescriptor fooDescriptor = mock(IRecordingDescriptor.class);
        when(fooDescriptor.getName()).thenReturn("foo");
        IRecordingDescriptor barDescriptor = mock(IRecordingDescriptor.class);
        when(barDescriptor.getName()).thenReturn("bar");

        when(connection.getService()).thenReturn(service);
        when(service.getAvailableRecordings()).thenReturn(Arrays.asList(barDescriptor, fooDescriptor));

        command.execute(new String[]{ "foo" });

        ArgumentCaptor<IRecordingDescriptor> descriptorCaptor = ArgumentCaptor.forClass(IRecordingDescriptor.class);
        verify(service).stop(descriptorCaptor.capture());
        IRecordingDescriptor captured = descriptorCaptor.getValue();
        MatcherAssert.assertThat(captured, Matchers.sameInstance(fooDescriptor));

        verifyNoMoreInteractions(service);
        verifyNoMoreInteractions(connection);
        verifyZeroInteractions(cw);
    }

    @Test
    void shouldReturnSuccessOutput() throws Exception {
        verifyZeroInteractions(service);
        verifyZeroInteractions(connection);

        IRecordingDescriptor fooDescriptor = mock(IRecordingDescriptor.class);
        when(fooDescriptor.getName()).thenReturn("foo");
        IRecordingDescriptor barDescriptor = mock(IRecordingDescriptor.class);
        when(barDescriptor.getName()).thenReturn("bar");

        when(connection.getService()).thenReturn(service);
        when(service.getAvailableRecordings()).thenReturn(Arrays.asList(barDescriptor, fooDescriptor));

        Output<?> out = command.serializableExecute(new String[]{ "foo" });
        MatcherAssert.assertThat(out, Matchers.instanceOf(SuccessOutput.class));

        ArgumentCaptor<IRecordingDescriptor> descriptorCaptor = ArgumentCaptor.forClass(IRecordingDescriptor.class);
        verify(service).stop(descriptorCaptor.capture());
        IRecordingDescriptor captured = descriptorCaptor.getValue();
        MatcherAssert.assertThat(captured, Matchers.sameInstance(fooDescriptor));

        verifyNoMoreInteractions(service);
        verifyNoMoreInteractions(connection);
    }

    @Test
    void shouldReturnFailureOutput() throws Exception {
        verifyZeroInteractions(service);
        verifyZeroInteractions(connection);

        IRecordingDescriptor fooDescriptor = mock(IRecordingDescriptor.class);
        when(fooDescriptor.getName()).thenReturn("foo");

        when(connection.getService()).thenReturn(service);
        when(service.getAvailableRecordings()).thenReturn(Collections.singletonList(fooDescriptor));

        Output<?> out = command.serializableExecute(new String[]{ "bar" });
        MatcherAssert.assertThat(out, Matchers.instanceOf(FailureOutput.class));
        MatcherAssert.assertThat(out.getPayload(), Matchers.equalTo("Recording with name \"bar\" not found"));

        verifyNoMoreInteractions(service);
        verifyNoMoreInteractions(connection);
    }

    @Test
    void shouldReturnExceptionOutput() throws Exception {
        verifyZeroInteractions(service);
        verifyZeroInteractions(connection);

        IRecordingDescriptor fooDescriptor = mock(IRecordingDescriptor.class);
        when(fooDescriptor.getName()).thenReturn("foo");

        when(connection.getService()).thenReturn(service);
        when(service.getAvailableRecordings()).thenReturn(Collections.singletonList(fooDescriptor));
        doThrow(FlightRecorderException.class).when(service).stop(Mockito.any());

        Output<?> out = command.serializableExecute(new String[]{ "foo" });
        MatcherAssert.assertThat(out, Matchers.instanceOf(ExceptionOutput.class));
        MatcherAssert.assertThat(out.getPayload(), Matchers.equalTo("FlightRecorderException: "));
    }

}