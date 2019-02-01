package es.andrewazor.containertest.commands.internal;

import java.util.Collection;

import org.openjdk.jmc.rjmx.services.jfr.IEventTypeInfo;

import es.andrewazor.containertest.JMCConnection;

class ListEventTypesCommand extends AbstractCommand {
    ListEventTypesCommand(JMCConnection connection) {
        super(connection);
    }

    @Override
    public String getName() {
        return "list-event-types";
    }

    /**
     * No args expected. Prints a list of available event types in the target JVM.
     */
    @Override
    public void execute(String[] args) throws Exception {
        System.out.println("Available event types");
        Collection<? extends IEventTypeInfo> events = service.getAvailableEventTypes();
        for (IEventTypeInfo event : events) {
            System.out.println(String.format("\t%s", event));
        }
    }

    @Override
    public boolean validate(String[] args) {
        return true;
    }
}