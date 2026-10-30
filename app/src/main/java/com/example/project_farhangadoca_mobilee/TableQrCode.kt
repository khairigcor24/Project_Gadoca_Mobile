package com.example.project_farhangadoca_mobilee

object TableQrCode {
    const val PREFIX = "GADOCA:TABLE:"

    private val format = Regex("^GADOCA:TABLE:([A-Za-z0-9][A-Za-z0-9_-]{0,19})$")

    fun parse(rawValue: String?): String? {
        val match = rawValue?.trim()?.let(format::matchEntire) ?: return null
        return match.groupValues[1]
    }
}
