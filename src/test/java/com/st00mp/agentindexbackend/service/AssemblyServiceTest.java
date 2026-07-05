package com.st00mp.agentindexbackend.service;

import com.st00mp.agentindexbackend.exception.UnresolvedPlaceholderException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AssemblyServiceTest {

    @Nested
    class SuccessfulAssembly {

        @Test
        void assemble_singlePlaceholder_replacesValue() {
            // Given
            var service = new AssemblyService(null, null);

            // When
            var result = service.assemble("You are the assistant for {{company_name}}.", Map.of("company_name", "Fiduciaire Horizon"));

            // Then
            assertEquals("You are the assistant for Fiduciaire Horizon.", result);
        }

        @Test
        void assemble_repeatedPlaceholder_replacesAllOccurrences() {
            // Given
            var service = new AssemblyService(null, null);

            // When
            var result = service.assemble(
                    "You are the assistant for {{company_name}}. Always sign your messages as {{company_name}}.",
                    Map.of("company_name", "Fiduciaire Horizon"));

            // Then
            assertEquals(
                    "You are the assistant for Fiduciaire Horizon. Always sign your messages as Fiduciaire Horizon.",
                    result);
        }

        @Test
        void assemble_valueWithoutPlaceholder_isIgnored() {
            // Given
            var service = new AssemblyService(null, null);

            // When
            var result = service.assemble(
                    "You are the assistant for {{company_name}}.",
                    Map.of("company_name", "Fiduciaire Horizon",
                            "engine", "sonnet-5"));

            // Then
            assertEquals("You are the assistant for Fiduciaire Horizon.", result);
        }

        @Test
        void assemble_noPlaceholder_returnsUnchanged() {
            // Given
            var service = new AssemblyService(null, null);

            // When
            var result = service.assemble("You are a helpful assistant.", Map.of());

            // Then
            assertEquals("You are a helpful assistant.", result);
        }
    }

    @Nested
    class ValidationFailures {

        @Test
        void assemble_missingValue_throws() {
            // Given
            var service = new AssemblyService(null, null);

            // When / Then
            assertThrows(UnresolvedPlaceholderException.class, () ->
                    service.assemble("Tone is {{tone}}.", Map.of())
            );
        }

        @Test
        void assemble_blankValue_throws() {
            // Given
            var service = new AssemblyService(null, null);

            // When / Then
            assertThrows(UnresolvedPlaceholderException.class, () ->
                    service.assemble("Tone is {{tone}}.", Map.of("tone", "   "))
            );
        }

        @Test
        void assemble_multipleMissingValues_throwsWithAllKeys() {
            // Given
            var service = new AssemblyService(null, null);

            // When
            var exception = assertThrows(UnresolvedPlaceholderException.class, () ->
                    service.assemble("Tone is {{tone}} for {{company_name}}.", Map.of()));

            // Then
            assertTrue(exception.getMessage().contains("tone"));
            assertTrue(exception.getMessage().contains("company_name"));
        }
    }
}