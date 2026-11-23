package com.example.project_farhangadoca_mobilee.data.model

data class PesananModel(
    val id: Int? = null,
    val nama_pemesan: String,
    val menu_pesanan: String,
    val jumlah: Int,
    val total_harga: Int,
    val status: String,
    val created_at: String? = null
)
