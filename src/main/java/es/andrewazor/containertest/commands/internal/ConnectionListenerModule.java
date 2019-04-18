package es.andrewazor.containertest.commands.internal;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoSet;
import es.andrewazor.containertest.net.ConnectionListener;

@Module
public abstract class ConnectionListenerModule {
    @Binds @IntoSet abstract ConnectionListener bindDeleteCommand(DeleteCommand command);
    @Binds @IntoSet abstract ConnectionListener bindDisconnectCommand(DisconnectCommand command);
    @Binds @IntoSet abstract ConnectionListener bindDumpCommand(DumpCommand command);
    @Binds @IntoSet abstract ConnectionListener bindFooCommand(FooCommand command);
    @Binds @IntoSet abstract ConnectionListener bindListCommand(ListCommand command);
    @Binds @IntoSet abstract ConnectionListener bindListEventTypesCommand(ListEventTypesCommand command);
    @Binds @IntoSet abstract ConnectionListener bindListRecordingOptionsCommand(ListRecordingOptionsCommand command);
    @Binds @IntoSet abstract ConnectionListener bindPrintUrlCommand(PrintUrlCommand command);
    @Binds @IntoSet abstract ConnectionListener bindRecordingOptionsCustomizerCommand(RecordingOptionsCustomizerCommand command);
    @Binds @IntoSet abstract ConnectionListener bindSearchEventsCommand(SearchEventsCommand command);
    @Binds @IntoSet abstract ConnectionListener bindSnapshotCommand(SnapshotCommand command);
    @Binds @IntoSet abstract ConnectionListener bindStartRecordingCommand(StartRecordingCommand command);
    @Binds @IntoSet abstract ConnectionListener bindStopRecordingCommand(StopRecordingCommand command);
    @Binds @IntoSet abstract ConnectionListener bindWaitForCommand(WaitForCommand command);
    @Binds @IntoSet abstract ConnectionListener bindWaitForDownloadCommand(WaitForDownloadCommand command);
}