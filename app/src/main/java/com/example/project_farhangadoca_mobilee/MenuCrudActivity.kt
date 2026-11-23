package com.example.project_farhangadoca_mobilee

import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project_farhangadoca_mobilee.data.api.SupabaseClient
import com.example.project_farhangadoca_mobilee.data.model.MenuModel
import com.example.project_farhangadoca_mobilee.databinding.ActivityMenuCrudBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MenuCrudActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuCrudBinding
    private lateinit var menuAdapter: MenuCrudAdapter
    private val menuList = mutableListOf<MenuModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMenuCrudBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbarMenuCrud) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        setSupportActionBar(binding.toolbarMenuCrud)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarMenuCrud.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        menuAdapter = MenuCrudAdapter(menuList, onEdit = { menu ->
            showMenuDialog(menu)
        }, onDelete = { menu ->
            deleteMenu(menu)
        })

        binding.rvMenus.layoutManager = LinearLayoutManager(this)
        binding.rvMenus.adapter = menuAdapter

        binding.fabAddMenu.setOnClickListener {
            showMenuDialog(null)
        }

        fetchMenus()
    }

    private fun fetchMenus() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val menus = SupabaseClient.apiService.getMenus()
                withContext(Dispatchers.Main) {
                    menuList.clear()
                    menuList.addAll(menus)
                    menuAdapter.updateData(menuList)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MenuCrudActivity, "Gagal memuat menu: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showMenuDialog(existingMenu: MenuModel?) {
        val context = this
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 20, 40, 20)
        }

        val etName = EditText(context).apply { hint = "Nama Menu"; text = existingMenu?.nama_menu?.let { android.text.Editable.Factory.getInstance().newEditable(it) } }
        val etCategory = EditText(context).apply { hint = "Kategori (e.g. Kopi, Pastry)"; text = existingMenu?.kategori?.let { android.text.Editable.Factory.getInstance().newEditable(it) } }
        val etPrice = EditText(context).apply { hint = "Harga (angka)"; inputType = android.text.InputType.TYPE_CLASS_NUMBER; text = existingMenu?.harga?.toString()?.let { android.text.Editable.Factory.getInstance().newEditable(it) } }
        val etDesc = EditText(context).apply { hint = "Deskripsi"; text = existingMenu?.deskripsi?.let { android.text.Editable.Factory.getInstance().newEditable(it) } }

        layout.addView(etName)
        layout.addView(etCategory)
        layout.addView(etPrice)
        layout.addView(etDesc)

        val isEdit = existingMenu != null
        val title = if (isEdit) "Edit Menu" else "Tambah Menu"

        AlertDialog.Builder(context)
            .setTitle(title)
            .setView(layout)
            .setPositiveButton("Simpan") { _, _ ->
                val nama = etName.text.toString().trim()
                val kategori = etCategory.text.toString().trim()
                val hargaStr = etPrice.text.toString().trim()
                val deskripsi = etDesc.text.toString().trim()

                if (nama.isEmpty() || kategori.isEmpty() || hargaStr.isEmpty()) {
                    Toast.makeText(context, "Nama, Kategori, dan Harga wajib diisi!", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val harga = hargaStr.toIntOrNull() ?: 0
                val menuModel = MenuModel(
                    id = existingMenu?.id,
                    nama_menu = nama,
                    kategori = kategori,
                    harga = harga,
                    deskripsi = deskripsi
                )

                if (isEdit && existingMenu?.id != null) {
                    updateMenuInSupabase(existingMenu.id!!, menuModel)
                } else {
                    insertMenuInSupabase(menuModel)
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun insertMenuInSupabase(menu: MenuModel) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                SupabaseClient.apiService.insertMenu(menu)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MenuCrudActivity, "Menu berhasil ditambahkan!", Toast.LENGTH_SHORT).show()
                    fetchMenus()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MenuCrudActivity, "Gagal tambah menu: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateMenuInSupabase(id: Int, menu: MenuModel) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                SupabaseClient.apiService.updateMenu("eq.$id", menu)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MenuCrudActivity, "Menu berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                    fetchMenus()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MenuCrudActivity, "Gagal update menu: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun deleteMenu(menu: MenuModel) {
        if (menu.id == null) return
        AlertDialog.Builder(this)
            .setTitle("Hapus Menu")
            .setMessage("Yakin ingin menghapus ${menu.nama_menu}?")
            .setPositiveButton("Hapus") { _, _ ->
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        SupabaseClient.apiService.deleteMenu("eq.${menu.id}")
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@MenuCrudActivity, "Menu berhasil dihapus!", Toast.LENGTH_SHORT).show()
                            fetchMenus()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@MenuCrudActivity, "Gagal hapus menu: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}
