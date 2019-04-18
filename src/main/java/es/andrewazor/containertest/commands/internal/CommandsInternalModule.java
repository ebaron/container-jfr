package es.andrewazor.containertest.commands.internal;

import javax.inject.Singleton;

import org.openjdk.jmc.flightrecorder.configuration.recording.RecordingOptionsBuilder;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;
import es.andrewazor.containertest.commands.Command;
import es.andrewazor.containertest.tui.ClientWriter;

@Module
public abstract class CommandsInternalModule {
    @Binds @IntoSet abstract Command bindConnectCommand(ConnectCommand command);
    @Binds @IntoSet abstract Command bindDeleteCommand(DeleteCommand command);
    @Binds @IntoSet abstract Command bindDisconnectCommand(DisconnectCommand command);
    @Binds @IntoSet abstract Command bindDumpCommand(DumpCommand command);
    @Binds @IntoSet abstract Command bindExitCommand(ExitCommand command);
    @Binds @IntoSet abstract Command bindFooCommand(FooCommand command);
    @Binds @IntoSet abstract Command bindHelpCommand(HelpCommand command);
    @Binds @IntoSet abstract Command bindHostnameCommand(HostnameCommand command);
    @Binds @IntoSet abstract Command bindIpCommand(IpCommand command);
    @Binds @IntoSet abstract Command bindListCommand(ListCommand command);
    @Binds @IntoSet abstract Command bindListEventTypesCommand(ListEventTypesCommand command);
    @Binds @IntoSet abstract Command bindListRecordingOptionsCommand(ListRecordingOptionsCommand command);
    @Binds @IntoSet abstract Command bindPrintUrlCommand(PrintUrlCommand command);
    @Binds @IntoSet abstract Command bindRecordingOptionsCustomizerCommand(RecordingOptionsCustomizerCommand command);
    @Binds @IntoSet abstract Command bindSearchEventsCommand(SearchEventsCommand command);
    @Binds @IntoSet abstract Command bindSnapshotCommand(SnapshotCommand command);
    @Binds @IntoSet abstract Command bindStartRecordingCommand(StartRecordingCommand command);
    @Binds @IntoSet abstract Command bindStopRecordingCommand(StopRecordingCommand command);
    @Binds @IntoSet abstract Command bindWaitCommand(WaitCommand command);
    @Binds @IntoSet abstract Command bindWaitForCommand(WaitForCommand command);
    @Binds @IntoSet abstract Command bindWaitForDownloadCommand(WaitForDownloadCommand command);
    @Provides static EventOptionsBuilder.Factory provideEventOptionsBuilderFactory(ClientWriter cw) {
        return new EventOptionsBuilder.Factory(cw);
    }
    @Provides static RecordingOptionsBuilderFactory provideRecordingOptionsBuilderFactory(RecordingOptionsCustomizer customizer) {
        return service -> customizer.apply(new RecordingOptionsBuilder(service));
    }
    @Provides @Singleton static RecordingOptionsCustomizer provideRecordingOptionsCustomizer(ClientWriter cw) {
        return new RecordingOptionsCustomizer(cw);
    }
}