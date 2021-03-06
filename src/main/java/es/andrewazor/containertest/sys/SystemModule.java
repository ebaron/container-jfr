package es.andrewazor.containertest.sys;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public abstract class SystemModule {
    @Provides
    @Singleton
    static Clock provideClock() {
        return new Clock();
    }

    @Provides
    @Singleton
    static Environment provideEnvironment() {
        return new Environment();
    }
}