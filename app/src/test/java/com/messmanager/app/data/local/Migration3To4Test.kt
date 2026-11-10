package com.messmanager.app.data.local

import org.junit.Assert.assertEquals
import org.junit.Test

class Migration3To4Test {

    @Test
    fun `migration 3 to 4 version numbers are correct`() {
        assertEquals(3, MIGRATION_3_4.startVersion)
        assertEquals(4, MIGRATION_3_4.endVersion)
    }
}
