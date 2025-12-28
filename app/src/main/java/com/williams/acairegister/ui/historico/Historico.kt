package com.williams.acairegister.ui.historico

import ItemVendaHojeAdapter
import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.williams.acairegister.DbRegistro
import com.williams.acairegister.databinding.FragmentGalleryBinding
import com.williams.acairegister.ui.home.ItemVendaHoje
import java.util.Calendar

class Historico : Fragment() {

    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!

    private var dataSelecionada: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentGalleryBinding.inflate(inflater, container, false)

        configurarBotaoData()

        return binding.root
    }

    // -----------------------------
    // Botão para escolher data
    // -----------------------------
    private fun configurarBotaoData() {

        binding.getBeginDateButton.setOnClickListener {

            val calendar = Calendar.getInstance()

            DatePickerDialog(
                requireContext(),
                { _, ano, mes, dia ->

                    val data =
                        "%02d/%02d/%04d".format(dia, mes + 1, ano)

                    dataSelecionada = data
                    binding.getBeginDateButton.text = data

                    carregarVendasDaData(data)

                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    // -----------------------------
    // Carrega lista + gráfico
    // -----------------------------
    private fun carregarVendasDaData(data: String) {

        val db = DbRegistro(requireContext())
        val vendas = db.buscarVendasDoDia(data)

        binding.recyclerView.adapter =
            ItemVendaHojeAdapter(vendas, mostrarData = true)


        atualizarPieChart(vendas)
    }

    // -----------------------------
    // Gráfico igual ao "Hoje"
    // -----------------------------
    private fun atualizarPieChart(vendas: List<ItemVendaHoje>) {

        // -----------------------------
        // Dados do gráfico (igual já estava)
        // -----------------------------
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

        val pieData = PieData(dataSet)

        // -----------------------------
        // Cálculos do centro
        // -----------------------------
        val total = vendas.sumOf { it.valorTotal }
        val lucro = total * 0.60
        val loja = total * 0.40

        val textoCentro = """
        Lucro
        R$ %.2f

        Loja
        R$ %.2f

        Total
        R$ %.2f
    """.trimIndent().format(lucro, loja, total)

        // -----------------------------
        // Estilo do texto central
        // -----------------------------
        val centerText = android.text.SpannableString(textoCentro)

        // Lucro
        centerText.setSpan(
            android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
            0, 5,
            android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // -----------------------------
        // Configuração final do gráfico
        // -----------------------------
        binding.pieChart.apply {
            data = pieData

            isDrawHoleEnabled = true
            holeRadius = 55f
            transparentCircleRadius = 60f
            setHoleColor(Color.TRANSPARENT)

            setCenterText(centerText)
            setCenterTextSize(12f)
            setCenterTextColor(Color.DKGRAY)

            description.isEnabled = false
            legend.isEnabled = true

            invalidate()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
