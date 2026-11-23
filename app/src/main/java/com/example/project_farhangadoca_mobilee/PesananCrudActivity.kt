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
import com.example.project_farhangadoca_mobilee.data.model.PesananModel
import com.example.project_farhangadoca_mobilee.databinding.ActivityPesananCrudBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PesananCrudActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPesananCrudBinding
    private lateinit var pesananAdapter: PesananCrudAdapter
    private val pesananList = mutableListOf<PesananModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityPesananCrudBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbarPesananCrud) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        setSupportActionBar(binding.toolbarPesananCrud)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarPesananCrud.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        pesananAdapter = PesananCrudAdapter(pesananList, onEdit = { pesanan ->
            showPesananDialog(pesanan)
        }, onDelete = { pesanan ->
            deletePesanan(pesanan)
        })

        binding.rvPesanan.layoutManager = LinearLayoutManager(this)
        binding.rvPesanan.adapter = pesananAdapter

        binding.fabAddPesanan.setOnClickListener {
            showPesananDialog(null)
        }

        fetchPesanan()
    }

    private fun fetchPesanan() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val list = SupabaseClient.apiService.getPesanan()
                withContext(Dispatchers.Main) {
                    pesananList.clear()
                    pesananList.addAll(list)
                    pesananAdapter.updateData(pesananList)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PesananCrudActivity, "Gagal memuat pesanan: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showPesananDialog(existing: PesananModel?) {
        val context = this
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 20, 40, 20)
        }

        val etCustomer = EditText(context).apply { hint = "Nama Pemesan"; text = existing?.nama_pemesan?.let { android.text.Editable.Factory.getInstance().newEditable(it) } }
        val etMenu = EditText(context).apply { hint = "Menu Pesanan"; text = existing?.menu_pesanan?.let { android.text.Editable.Factory.getInstance().newEditable(it) } }
        val etQty = EditText(context).apply { hint = "Jumlah (qty)"; inputType = android.text.InputType.TYPE_CLASS_NUMBER; text = existing?.jumlah?.toString()?.let { android.text.Editable.Factory.getInstance().newEditable(it) } }
        val etTotal = EditText(context).apply { hint = "Total Harga (angka)"; inputType = android.text.InputType.TYPE_CLASS_NUMBER; text = existing?.total_harga?.toString()?.let { android.text.Editable.Factory.getInstance().newEditable(it) } }
        val etStatus = EditText(context).apply { hint = "Status (e.g. Pending, Selesai)"; text = existing?.status?.let { android.text.Editable.Factory.getInstance().newEditable(it) } ?: android.text.Editable.Factory.getInstance().newEditable("Pending") }

        layout.addView(etCustomer)
        layout.addView(etMenu)
        layout.addView(etQty)
        layout.addView(etTotal)
        layout.addView(etStatus)

        val isEdit = existing != null
        val title = if (isEdit) "Edit Pesanan" else "Tambah Pesanan"

        AlertDialog.Builder(context)
            .setTitle(title)
            .setView(layout)
            .setPositiveButton("Simpan") { _, _ ->
                val customer = etCustomer.text.toString().trim()
                val menu = etMenu.text.toString().trim()
                val qtyStr = etQty.text.toString().trim()
                val totalStr = etTotal.text.toString().trim()
                val status = etStatus.text.toString().trim()

                if (customer.isEmpty() || menu.isEmpty() || qtyStr.isEmpty() || totalStr.isEmpty()) {
                    Toast.makeText(context, "Semua field wajib diisi!", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val jumlah = qtyStr.toIntOrNull() ?: 1
                val totalHarga = totalStr.toIntOrNull() ?: 0

                val pesananModel = PesananModel(
                    id = existing?.id,
                    nama_pemesan = customer,
                    menu_pesanan = menu,
                    jumlah = jumlah,
                    total_harga = totalHarga,
                    status = if (status.isEmpty()) "Pending" else status
                )

                if (isEdit && existing?.id != null) {
                    updatePesananInSupabase(existing.id!!, pesananModel)
                } else {
                    insertPesananInSupabase(pesananModel)
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun insertPesananInSupabase(pesanan: PesananModel) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                SupabaseClient.apiService.insertPesanan(pesanan)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PesananCrudActivity, "Pesanan berhasil ditambahkan!", Toast.LENGTH_SHORT).show()
                    fetchPesanan()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PesananCrudActivity, "Gagal tambah pesanan: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updatePesananInSupabase(id: Int, pesanan: PesananModel) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                SupabaseClient.apiService.updatePesanan("eq.$id", pesanan)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PesananCrudActivity, "Pesanan berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                    fetchPesanan()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PesananCrudActivity, "Gagal update pesanan: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun deletePesanan(pesanan: PesananModel) {
        if (pesanan.id == null) return
        AlertDialog.Builder(this)
            .setTitle("Hapus Pesanan")
            .setMessage("Yakin ingin menghapus pesanan atas nama ${pesanan.nama_pemesan}?")
            .setPositiveButton("Hapus") { _, _ ->
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        SupabaseClient.apiService.deletePesanan("eq.${pesanan.id}")
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@PesananCrudActivity, "Pesanan berhasil dihapus!", Toast.LENGTH_SHORT).show()
                            fetchPesanan()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@PesananCrudActivity, "Gagal hapus pesanan: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}
