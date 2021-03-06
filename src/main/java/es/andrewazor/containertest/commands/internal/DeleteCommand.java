package es.andrewazor.containertest.commands.internal;

import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.openjdk.jmc.rjmx.services.jfr.IRecordingDescriptor;

import es.andrewazor.containertest.commands.SerializableCommand;
import es.andrewazor.containertest.net.RecordingExporter;
import es.andrewazor.containertest.tui.ClientWriter;

@Singleton
class DeleteCommand extends AbstractConnectedCommand implements SerializableCommand {

    private final ClientWriter cw;
    private final RecordingExporter exporter;

    @Inject DeleteCommand(ClientWriter cw, RecordingExporter exporter) {
        this.cw = cw;
        this.exporter = exporter;
    }

    @Override
    public String getName() {
        return "delete";
    }

    /**
     * One arg expected. Deletes recordings in target JVM by recording name.
     */
    @Override
    public void execute(String[] args) throws Exception {
        final String recordingName = args[0];
        Optional<IRecordingDescriptor> descriptor = getDescriptorByName(recordingName);
        if (descriptor.isPresent()) {
            getService().close(descriptor.get());
            exporter.removeRecording(descriptor.get());
        } else {
            cw.println(String.format("No recording with name \"%s\" found", recordingName));
            return;
        }
    }

    @Override
    public Output<?> serializableExecute(String[] args) {
        try {
            final String recordingName = args[0];
            Optional<IRecordingDescriptor> descriptor = getDescriptorByName(recordingName);
            if (descriptor.isPresent()) {
                getService().close(descriptor.get());
                exporter.removeRecording(descriptor.get());
                return new SuccessOutput();
            } else {
                return new FailureOutput(String.format("No recording with name \"%s\" found", recordingName));
            }
        } catch (Exception e) {
            return new ExceptionOutput(e);
        }
    }

    @Override
    public boolean validate(String[] args) {
        if (args.length != 1) {
            cw.println("Expected one argument: recording name");
            return false;
        }
        return true;
    }
}
