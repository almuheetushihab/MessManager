package com.messmanager.app.data.local

import org.junit.Assert.assertEquals
import org.junit.Test

class Migration1To2Test {

    @Test
    fun `migration 1 to 2 version numbers are correct`() {
        assertEquals(1, MIGRATION_1_2.startVersion)
        assertEquals(2, MIGRATION_1_2.endVersion)
    }
}
