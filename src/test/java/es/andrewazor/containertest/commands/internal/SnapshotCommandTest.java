package es.andrewazor.containertest.commands.internal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openjdk.jmc.common.unit.IConstrainedMap;
import org.openjdk.jmc.flightrecorder.configuration.recording.RecordingOptionsBuilder;
import org.openjdk.jmc.rjmx.services.jfr.FlightRecorderException;
import org.openjdk.jmc.rjmx.services.jfr.IFlightRecorderService;
import org.openjdk.jmc.rjmx.services.jfr.IRecordingDescriptor;

import es.andrewazor.containertest.commands.SerializableCommand.ExceptionOutput;
import es.andrewazor.containertest.commands.SerializableCommand.Output;
import es.andrewazor.containertest.commands.SerializableCommand.StringOutput;
import es.andrewazor.containertest.net.JMCConnection;
import es.andrewazor.containertest.net.RecordingExporter;
import es.andrewazor.containertest.tui.ClientWriter;

@ExtendWith(MockitoExtension.class)
class SnapshotCommandTest {

    SnapshotCommand command;
    @Mock ClientWriter cw;
    @Mock JMCConnection connection;
    @Mock IFlightRecorderService service;
    @Mock RecordingExporter exporter;
    @Mock EventOptionsBuilder.Factory eventOptionsBuilderFactory;
    @Mock RecordingOptionsBuilderFactory recordingOptionsBuilderFactory;

    @BeforeEach
    void setup() {
        command = new SnapshotCommand(cw, exporter, eventOptionsBuilderFactory,
                recordingOptionsBuilderFactory);
        command.connectionChanged(connection);
    }

    @Test
    void shouldBeNamedSnapshot() {
        MatcherAssert.assertThat(command.getName(), Matchers.equalTo("snapshot"));
    }

    @Test
    void shouldValidateCorrectArgc() {
        assertTrue(command.validate(new String[0]));
        verifyZeroInteractions(cw);
    }

    @ParameterizedTest
    @ValueSource(ints={
        1,
        2,
    })
    void shouldInvalidateIncorrectArgc(int c) {
        assertFalse(command.validate(new String[c]));
        verify(cw).println("No arguments expected");
    }

    @Test
    void shouldRenameAndExportSnapshot() throws Exception {
        IRecordingDescriptor snapshot = mock(IRecordingDescriptor.class);
        when(connection.getService()).thenReturn(service);
        when(service.getSnapshotRecording()).thenReturn(snapshot);
        RecordingOptionsBuilder recordingOptionsBuilder = mock(RecordingOptionsBuilder.class);
        when(recordingOptionsBuilderFactory.create(Mockito.any())).thenReturn(recordingOptionsBuilder);
        IConstrainedMap<String> builtMap = mock(IConstrainedMap.class);
        when(recordingOptionsBuilder.build()).thenReturn(builtMap);

        when(snapshot.getName()).thenReturn("Snapshot");
        when(snapshot.getId()).thenReturn(1L);

        verifyZeroInteractions(connection);
        verifyZeroInteractions(service);
        verifyZeroInteractions(exporter);
        verifyZeroInteractions(cw);

        command.execute(new String[0]);

        verify(cw).println("Latest snapshot: \"snapshot-1\"");
        verify(service).getSnapshotRecording();
        verify(service).updateRecordingOptions(Mockito.same(snapshot), Mockito.same(builtMap));

        ArgumentCaptor<IRecordingDescriptor> captor = ArgumentCaptor.forClass(IRecordingDescriptor.class);
        verify(exporter).addRecording(captor.capture());
        IRecordingDescriptor renamed = captor.getValue();
        MatcherAssert.assertThat(renamed.getName(), Matchers.equalTo("snapshot-1"));
    }

    @Test
    void shouldReturnSerializedSuccessOutput() throws Exception {
        IRecordingDescriptor snapshot = mock(IRecordingDescriptor.class);
        when(connection.getService()).thenReturn(service);
        when(service.getSnapshotRecording()).thenReturn(snapshot);
        RecordingOptionsBuilder recordingOptionsBuilder = mock(RecordingOptionsBuilder.class);
        when(recordingOptionsBuilderFactory.create(Mockito.any())).thenReturn(recordingOptionsBuilder);
        IConstrainedMap<String> builtMap = mock(IConstrainedMap.class);
        when(recordingOptionsBuilder.build()).thenReturn(builtMap);

        when(snapshot.getName()).thenReturn("Snapshot");
        when(snapshot.getId()).thenReturn(1L);

        verifyZeroInteractions(connection);
        verifyZeroInteractions(service);
        verifyZeroInteractions(exporter);
        verifyZeroInteractions(cw);

        Output<?> out = command.serializableExecute(new String[0]);
        MatcherAssert.assertThat(out, Matchers.instanceOf(StringOutput.class));
        MatcherAssert.assertThat(out.getPayload(), Matchers.equalTo("snapshot-1"));

        verify(service).getSnapshotRecording();
        verify(service).updateRecordingOptions(Mockito.same(snapshot), Mockito.same(builtMap));

        ArgumentCaptor<IRecordingDescriptor> captor = ArgumentCaptor.forClass(IRecordingDescriptor.class);
        verify(exporter).addRecording(captor.capture());
        IRecordingDescriptor renamed = captor.getValue();
        MatcherAssert.assertThat(renamed.getName(), Matchers.equalTo("snapshot-1"));
    }

    @Test
    void shouldReturnSerializedExceptionOutput() throws Exception {
        when(connection.getService()).thenReturn(service);
        doThrow(FlightRecorderException.class).when(service).getSnapshotRecording();

        Output<?> out = command.serializableExecute(new String[0]);
        MatcherAssert.assertThat(out, Matchers.instanceOf(ExceptionOutput.class));
        MatcherAssert.assertThat(out.getPayload(), Matchers.equalTo("FlightRecorderException: "));
    }

}