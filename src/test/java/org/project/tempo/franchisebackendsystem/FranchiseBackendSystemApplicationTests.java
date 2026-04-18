package org.project.tempo.franchisebackendsystem;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

class FranchiseBackendSystemApplicationTests {

    @Test
    void shouldCreateApplicationInstance() {
        assertDoesNotThrow(FranchiseBackendSystemApplication::new);
    }
}
