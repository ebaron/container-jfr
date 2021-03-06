package es.andrewazor.containertest.tui.ws;

import java.io.IOException;
import java.util.Collections;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import dagger.Lazy;
import es.andrewazor.containertest.commands.SerializableCommand;
import es.andrewazor.containertest.commands.SerializableCommandRegistry;
import es.andrewazor.containertest.tui.ClientReader;
import es.andrewazor.containertest.tui.ClientWriter;
import es.andrewazor.containertest.tui.CommandExecutor;

class WsCommandExecutor implements CommandExecutor {

    private final Gson gson;
    private final MessagingServer server;
    private final ClientReader cr;
    private final ClientWriter cw;
    private final Lazy<SerializableCommandRegistry> registry;
    private volatile Thread readingThread;
    private volatile boolean running = true;

    WsCommandExecutor(MessagingServer server, ClientReader cr, ClientWriter cw, Lazy<SerializableCommandRegistry> commandRegistry, Gson gson) {
        this.gson = gson;
        this.server = server;
        this.cr = cr;
        this.cw = cw;
        this.registry = commandRegistry;
    }

    @Override
    public synchronized void run(String unused) {
        readingThread = Thread.currentThread();
        try (cr) {
            while (running) {
                try {
                    String rawMsg = cr.readLine();
                    if (rawMsg == null) {
                        continue;
                    }
                    CommandMessage commandMessage = gson.fromJson(rawMsg, CommandMessage.class);
                    if (commandMessage.args == null) {
                        commandMessage.args = Collections.emptyList();
                    }
                    String commandName = commandMessage.command;
                    String[] args = commandMessage.args.toArray(new String[0]);
                    if (commandName == null || !registry.get().getRegisteredCommandNames().contains(commandName)) {
                        flush(new InvalidCommandResponseMessage(commandName));
                        continue;
                    }
                    if (!registry.get().isCommandAvailable(commandName)) {
                        flush(new CommandUnavailableMessage(commandName));
                        continue;
                    }
                    if (!registry.get().validate(commandName, args)) {
                        flush(new InvalidCommandArgumentsResponseMessage(commandName, args));
                        continue;
                    }
                    SerializableCommand.Output<?> out = registry.get().execute(commandName, args);
                    if (out instanceof SerializableCommand.SuccessOutput) {
                        flush(new SuccessResponseMessage<Void>(commandName, null));
                    } else if (out instanceof SerializableCommand.FailureOutput) {
                        flush(new FailureResponseMessage(commandName, ((SerializableCommand.FailureOutput) out).getPayload()));
                    } else if (out instanceof SerializableCommand.StringOutput) {
                        flush(new SuccessResponseMessage<>(commandName, out.getPayload()));
                    } else if (out instanceof SerializableCommand.ListOutput) {
                        flush(new SuccessResponseMessage<>(commandName, out.getPayload()));
                    } else if (out instanceof SerializableCommand.MapOutput) {
                        flush(new SuccessResponseMessage<>(commandName, out.getPayload()));
                    } else if (out instanceof SerializableCommand.ExceptionOutput) {
                        flush(new CommandExceptionResponseMessage(commandName, ((SerializableCommand.ExceptionOutput) out).getPayload()));
                    } else {
                        flush(new CommandExceptionResponseMessage(commandName, "internal error"));
                    }
                } catch (JsonSyntaxException jse) {
                    reportException(null, jse);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void shutdown() {
        this.running = false;
        if (readingThread != Thread.currentThread()) {
            readingThread.interrupt();
            readingThread = null;
        }
    }

    private void reportException(String commandName, Exception e) {
        e.printStackTrace();
        cw.println(e.getMessage());
        flush(new CommandExceptionResponseMessage(commandName, e));
    }

    private void flush(ResponseMessage<?> message) {
        server.flush(message);
    }

}