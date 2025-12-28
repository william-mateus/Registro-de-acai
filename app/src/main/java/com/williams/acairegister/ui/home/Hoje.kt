package com.williams.acairegister.ui.home

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.williams.acairegister.DbRegistro
import com.williams.acairegister.R
import com.williams.acairegister.databinding.FragmentHomeBinding
import java.time.LocalDate

class Hoje : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        carregarVendasHoje()

        binding.floatingActionButton.setOnClickListener {
            findNavController().navigate(R.id.registerFragment)
        }

        return binding.root
    }

    private fun carregarVendasHoje() {
        val hoje = LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))

        val db = DbRegistro(requireContext())
        val vendasHoje = db.buscarVendasDoDia(hoje)

        binding.recyclerView.adapter = ItemVendaHojeAdapter(vendasHoje)

        atualizarPieChart(vendasHoje)
    }

    private fun atualizarPieChart(vendas: List<ItemVendaHoje>) {

        // ----------------------------
        // Cálculos
        // ----------------------------
        val total = vendas.sumOf { it.valorTotal }

        val lucro = total * 0.6
        val loja = total * 0.4

        // ----------------------------
        // Gráfico por produto (mantém igual)
        // ----------------------------
        val mapa = vendas.groupBy { it.produto }
            .mapValues { it.value.sumOf { venda -> venda.valorTotal }.toFloat() }

        val entries = mapa.map {
            PieEntry(it.value, it.key)
        }

        val dataSet = PieDataSet(entries, "").apply {
            colors = listOf(
                Color.parseColor("#6A1B9A"),
                Color.parseColor("#8E24AA"),
                Color.parseColor("#AB47BC"),
                Color.parseColor("#CE93D8")
            )
            valueTextColor = Color.WHITE
            valueTextSize = 12f
        }

        binding.pieChart.data = PieData(dataSet)

        // ----------------------------
        // CONFIGURAÇÃO DO DONUT
        // ----------------------------
        binding.pieChart.apply {

            isDrawHoleEnabled = true
            holeRadius = 65f
            transparentCircleRadius = 70f
            setHoleColor(Color.TRANSPARENT)

            description.isEnabled = false
            legend.isEnabled = true

            // Texto central
            setDrawCenterText(true)
            centerText =
                "Total\nR$ %.2f\n\nLucro (60%%)\nR$ %.2f\n\nLoja (40%%)\nR$ %.2f"
                    .format(total, lucro, loja)

            setCenterTextSize(13f)
            setCenterTextColor(Color.BLACK)

            invalidate()
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
