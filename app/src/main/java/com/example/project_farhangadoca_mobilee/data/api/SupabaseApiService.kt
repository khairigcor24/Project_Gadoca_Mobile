package com.example.project_farhangadoca_mobilee.data.api

import com.example.project_farhangadoca_mobilee.data.model.MenuModel
import com.example.project_farhangadoca_mobilee.data.model.PesananModel
import com.example.project_farhangadoca_mobilee.data.model.UserModel
import com.example.project_farhangadoca_mobilee.data.model.UserRequestModel
import retrofit2.Response
import retrofit2.http.*

interface SupabaseApiService {

    // 1. Users (Register)
    @POST("users")
    suspend fun registerUser(@Body user: UserRequestModel): Response<List<UserModel>>

    // 2. Menu (Table Custom 1) - CRUD
    @GET("menu")
    suspend fun getMenus(): List<MenuModel>

    @POST("menu")
    suspend fun insertMenu(@Body menu: MenuModel): List<MenuModel>

    @PATCH("menu")
    suspend fun updateMenu(
        @Query("id") idQuery: String, // format "eq.$id"
        @Body menu: MenuModel
    ): List<MenuModel>

    @DELETE("menu")
    suspend fun deleteMenu(
        @Query("id") idQuery: String // format "eq.$id"
    ): Response<Void>

    // 3. Pesanan (Table Custom 2) - CRUD
    @GET("pesanan")
    suspend fun getPesanan(): List<PesananModel>

    @POST("pesanan")
    suspend fun insertPesanan(@Body pesanan: PesananModel): List<PesananModel>

    @PATCH("pesanan")
    suspend fun updatePesanan(
        @Query("id") idQuery: String, // format "eq.$id"
        @Body pesanan: PesananModel
    ): List<PesananModel>

    @DELETE("pesanan")
    suspend fun deletePesanan(
        @Query("id") idQuery: String // format "eq.$id"
    ): Response<Void>
}
