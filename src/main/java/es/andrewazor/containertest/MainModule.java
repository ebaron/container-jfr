package es.andrewazor.containertest;

import javax.inject.Singleton;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dagger.Module;
import dagger.Provides;
import es.andrewazor.containertest.commands.CommandsModule;
import es.andrewazor.containertest.net.NetworkModule;
import es.andrewazor.containertest.sys.SystemModule;
import es.andrewazor.containertest.tui.TuiModule;

@Module(includes = {
    NetworkModule.class,
    SystemModule.class,
    CommandsModule.class,
    TuiModule.class
})
abstract class MainModule {
    @Provides
    @Singleton
    static Gson provideGson() {
        return new GsonBuilder()
            .serializeNulls()
            .create();
    }
}