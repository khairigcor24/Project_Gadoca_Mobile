package com.example.project_farhangadoca_mobilee

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.project_farhangadoca_mobilee.data.model.PesananModel
import com.example.project_farhangadoca_mobilee.databinding.ItemPesananCrudBinding
import java.text.NumberFormat
import java.util.Locale

class PesananCrudAdapter(
    private var pesananList: List<PesananModel>,
    private val onEdit: (PesananModel) -> Unit,
    private val onDelete: (PesananModel) -> Unit
) : RecyclerView.Adapter<PesananCrudAdapter.PesananViewHolder>() {

    inner class PesananViewHolder(val binding: ItemPesananCrudBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PesananViewHolder {
        val binding = ItemPesananCrudBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PesananViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PesananViewHolder, position: Int) {
        val pesanan = pesananList[position]
        holder.binding.tvOrderCustomer.text = pesanan.nama_pemesan
        val rupiah = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID")).format(pesanan.total_harga)
        holder.binding.tvOrderMenu.text = "${pesanan.menu_pesanan} (x${pesanan.jumlah}) · $rupiah"
        holder.binding.tvOrderStatus.text = "Status: ${pesanan.status}"

        holder.binding.btnEditOrder.setOnClickListener { onEdit(pesanan) }
        holder.binding.btnDeleteOrder.setOnClickListener { onDelete(pesanan) }
    }

    override fun getItemCount(): Int = pesananList.size

    fun updateData(newPesanan: List<PesananModel>) {
        pesananList = newPesanan
        notifyDataSetChanged()
    }
}
