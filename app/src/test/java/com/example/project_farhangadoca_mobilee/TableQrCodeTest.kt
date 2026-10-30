package com.example.project_farhangadoca_mobilee

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TableQrCodeTest {

    @Test
    fun parsesValidTableQr() {
        assertEquals("A1", TableQrCode.parse("GADOCA:TABLE:A1"))
    }

    @Test
    fun rejectsUnexpectedOrMalformedQr() {
        assertNull(TableQrCode.parse("A1"))
        assertNull(TableQrCode.parse("GADOCA:ORDER:A1"))
        assertNull(TableQrCode.parse("GADOCA:TABLE:"))
        assertNull(TableQrCode.parse("GADOCA:TABLE:A1/ADMIN"))
        assertNull(TableQrCode.parse(null))
    }
}
