package com.williams.acairegister.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.williams.acairegister.databinding.ItemVendaHojeBinding

class ItemVendaHojeAdapter(
    private val lista: List<ItemVendaHoje>
) : RecyclerView.Adapter<ItemVendaHojeAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemVendaHojeBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemVendaHojeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = lista[position]

        holder.binding.txtProduto.text =
            if (item.complemento == "Nenhum")
                item.produto
            else
                "${item.produto} + ${item.complemento}"

        holder.binding.txtValor.text =
            "R$ %.2f".format(item.valorTotal)

        holder.binding.txtHora.text = item.hora
    }

    override fun getItemCount() = lista.size
}
