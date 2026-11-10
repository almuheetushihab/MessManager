package com.messmanager.app.data.local

import org.junit.Assert.assertEquals
import org.junit.Test

class Migration2To3Test {

    @Test
    fun `migration 2 to 3 version numbers are correct`() {
        assertEquals(2, MIGRATION_2_3.startVersion)
        assertEquals(3, MIGRATION_2_3.endVersion)
    }
}
