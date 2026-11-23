package com.example.project_farhangadoca_mobilee

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.project_farhangadoca_mobilee.data.model.MenuModel
import com.example.project_farhangadoca_mobilee.databinding.ItemMenuCrudBinding
import java.text.NumberFormat
import java.util.Locale

class MenuCrudAdapter(
    private var menus: List<MenuModel>,
    private val onEdit: (MenuModel) -> Unit,
    private val onDelete: (MenuModel) -> Unit
) : RecyclerView.Adapter<MenuCrudAdapter.MenuViewHolder>() {

    inner class MenuViewHolder(val binding: ItemMenuCrudBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = ItemMenuCrudBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MenuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val menu = menus[position]
        holder.binding.tvMenuName.text = menu.nama_menu
        val rupiah = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID")).format(menu.harga)
        holder.binding.tvMenuCategory.text = "${menu.kategori} · $rupiah"
        holder.binding.tvMenuDesc.text = menu.deskripsi

        holder.binding.btnEditMenu.setOnClickListener { onEdit(menu) }
        holder.binding.btnDeleteMenu.setOnClickListener { onDelete(menu) }
    }

    override fun getItemCount(): Int = menus.size

    fun updateData(newMenus: List<MenuModel>) {
        menus = newMenus
        notifyDataSetChanged()
    }
}
