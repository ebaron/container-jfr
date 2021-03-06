package es.andrewazor.containertest.net;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;
import es.andrewazor.containertest.sys.Clock;
import es.andrewazor.containertest.sys.Environment;
import es.andrewazor.containertest.tui.ClientWriter;

@Module
public abstract class NetworkModule {
    @Binds
    @IntoSet
    abstract ConnectionListener bindRecordingExporter(RecordingExporter exporter);

    @Provides
    @Singleton
    static RecordingExporter provideRecordingExporter(Environment env, ClientWriter cw, NetworkResolver resolver) {
        return new RecordingExporter(env, cw, resolver);
    }

    @Provides
    @Singleton
    static NetworkResolver provideNetworkResolver() {
        return new NetworkResolver();
    }

    @Provides
    @Singleton
    static JMCConnectionToolkit provideJMCConnectionToolkit(ClientWriter cw, Clock clock) {
        return new JMCConnectionToolkit(cw, clock);
    }
}