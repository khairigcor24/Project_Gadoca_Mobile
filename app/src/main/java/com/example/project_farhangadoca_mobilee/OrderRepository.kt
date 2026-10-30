package com.example.project_farhangadoca_mobilee

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

data class OrderRecord(
    val id: String,
    val productName: String,
    val description: String,
    val price: Int,
    val imageResId: Int,
    val paymentMethod: String,
    val createdAt: Long,
    val status: String = "Menunggu diproses",
    val tableId: String? = null
)

class OrderRepository(context: Context) {

    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun getOrders(): List<OrderRecord> {
        val savedOrders = preferences.getString(ORDERS_KEY, null) ?: return emptyList()
        val jsonOrders = JSONArray(savedOrders)
        return List(jsonOrders.length()) { index ->
            val json = jsonOrders.getJSONObject(index)
            OrderRecord(
                id = json.getString("id"),
                productName = json.getString("productName"),
                description = json.getString("description"),
                price = json.getInt("price"),
                imageResId = json.getInt("imageResId"),
                paymentMethod = json.getString("paymentMethod"),
                createdAt = json.getLong("createdAt"),
                status = json.optString("status", STATUS_PENDING),
                tableId = json.optString("tableId").takeIf { it.isNotBlank() }
            )
        }
    }

    fun getSelectedTableId(): String? =
        preferences.getString(SELECTED_TABLE_KEY, null)

    fun saveSelectedTableId(tableId: String) {
        require(TableQrCode.parse("${TableQrCode.PREFIX}$tableId") == tableId) {
            "Identitas meja tidak valid."
        }
        check(preferences.edit().putString(SELECTED_TABLE_KEY, tableId).commit()) {
            "Pilihan meja tidak berhasil disimpan."
        }
    }

    fun addOrder(
        productName: String,
        description: String,
        price: Int,
        imageResId: Int,
        paymentMethod: String,
        tableId: String? = getSelectedTableId()
    ): OrderRecord {
        val order = OrderRecord(
            id = UUID.randomUUID().toString(),
            productName = productName,
            description = description,
            price = price,
            imageResId = imageResId,
            paymentMethod = paymentMethod,
            createdAt = System.currentTimeMillis(),
            tableId = tableId
        )
        val orders = getOrders().toMutableList()
        orders.add(0, order)

        val jsonOrders = JSONArray()
        orders.forEach { item ->
            jsonOrders.put(
                JSONObject()
                    .put("id", item.id)
                    .put("productName", item.productName)
                    .put("description", item.description)
                    .put("price", item.price)
                    .put("imageResId", item.imageResId)
                    .put("paymentMethod", item.paymentMethod)
                    .put("createdAt", item.createdAt)
                    .put("status", item.status)
                    .put("tableId", item.tableId)
            )
        }

        saveOrders(jsonOrders.toString())
        return order
    }

    fun deleteOrders(orderIds: Set<String>) {
        val remainingOrders = getOrders().filterNot { it.id in orderIds }
        saveOrders(remainingOrders.toJson())
    }

    fun confirmOrders(orderIds: Set<String>): List<OrderRecord> {
        val orders = getOrders()
        val changedOrders = orders.filter {
            it.id in orderIds && it.status != STATUS_CONFIRMED
        }.map { it.copy(status = STATUS_CONFIRMED) }
        if (changedOrders.isEmpty()) return emptyList()

        val changedById = changedOrders.associateBy { it.id }
        val updatedOrders = orders.map { order -> changedById[order.id] ?: order }
        saveOrders(updatedOrders.toJson())
        return changedOrders
    }

    private fun saveOrders(serializedOrders: String) {
        check(preferences.edit().putString(ORDERS_KEY, serializedOrders).commit()) {
            "Perubahan pesanan tidak berhasil disimpan."
        }
    }

    private fun List<OrderRecord>.toJson(): String {
        val jsonOrders = JSONArray()
        forEach { item ->
            jsonOrders.put(
                JSONObject()
                    .put("id", item.id)
                    .put("productName", item.productName)
                    .put("description", item.description)
                    .put("price", item.price)
                    .put("imageResId", item.imageResId)
                    .put("paymentMethod", item.paymentMethod)
                    .put("createdAt", item.createdAt)
                    .put("status", item.status)
                    .put("tableId", item.tableId)
            )
        }
        return jsonOrders.toString()
    }

    companion object {
        private const val PREFERENCES_NAME = "gadoca_orders"
        private const val ORDERS_KEY = "confirmed_orders"
        private const val SELECTED_TABLE_KEY = "selected_table_id"
        const val STATUS_PENDING = "Menunggu diproses"
        const val STATUS_CONFIRMED = "Pesanan dikonfirmasi"
    }
}
