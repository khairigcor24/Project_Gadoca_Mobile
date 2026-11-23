package com.example.project_farhangadoca_mobilee.data.model

data class MenuModel(
    val id: Int? = null,
    val nama_menu: String,
    val kategori: String,
    val harga: Int,
    val deskripsi: String,
    val gambar_url: String? = null,
    val created_at: String? = null
)
