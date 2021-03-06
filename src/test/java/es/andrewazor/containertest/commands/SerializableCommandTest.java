package es.andrewazor.containertest.commands;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import es.andrewazor.containertest.commands.SerializableCommand.ExceptionOutput;
import es.andrewazor.containertest.commands.SerializableCommand.FailureOutput;
import es.andrewazor.containertest.commands.SerializableCommand.ListOutput;
import es.andrewazor.containertest.commands.SerializableCommand.MapOutput;
import es.andrewazor.containertest.commands.SerializableCommand.StringOutput;
import es.andrewazor.containertest.commands.SerializableCommand.SuccessOutput;

@ExtendWith(MockitoExtension.class)
class SerializableCommandTest {

    @Nested
    class SuccessOutputTest {
        SuccessOutput out;

        @BeforeEach
        void setup() {
            out = new SuccessOutput();
        }

        @Test
        void shouldContainNullPayload() {
            MatcherAssert.assertThat(out.getPayload(), Matchers.nullValue());
        }
    }

    @Nested
    class StringOutputTest {
        StringOutput out;

        @BeforeEach
        void setup() {
            out = new StringOutput("foo");
        }

        @Test
        void shouldContainExpectedMessage() {
            MatcherAssert.assertThat(out.getPayload(), Matchers.equalTo("foo"));
        }
    }

    @Nested
    class ListOutputTest {
        ListOutput<String> out;

        @BeforeEach
        void setup() {
            out = new ListOutput<>(Collections.singletonList("foo"));
        }

        @Test
        void shouldContainExpectedData() {
            MatcherAssert.assertThat(out.getPayload(), Matchers.equalTo(Collections.singletonList("foo")));
        }
    }

    @Nested
    class MapOutputTest {
        MapOutput<String, Integer> out;

        @BeforeEach
        void setup() {
            out = new MapOutput<>(Map.of("foo", 5));
        }

        @Test
        void shouldContainExpectedData() {
            MatcherAssert.assertThat(out.getPayload(), Matchers.equalTo(Map.of("foo", 5)));
        }
    }

    @Nested
    class ExceptionOutputTest {
        ExceptionOutput out;

        @BeforeEach
        void setup() {
            out = new ExceptionOutput(new IOException("for testing reasons"));
        }

        @Test
        void shouldContainExpectedMessage() {
            MatcherAssert.assertThat(out.getPayload(), Matchers.equalTo("IOException: for testing reasons"));
        }
    }

    @Nested
    class FailureOutputTest {
        FailureOutput out;

        @BeforeEach
        void setup() {
            out = new FailureOutput("for testing reasons");
        }

        @Test
        void shouldContainExpectedMessage() {
            MatcherAssert.assertThat(out.getPayload(), Matchers.equalTo("for testing reasons"));
        }
    }

}