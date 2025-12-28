package com.williams.acairegister.ui.mes

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
import com.williams.acairegister.databinding.FragmentSlideshowBinding
import com.williams.acairegister.ui.home.ItemVendaHoje
import java.util.Calendar

class MesFragment : Fragment() {

    private var _binding: FragmentSlideshowBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSlideshowBinding.inflate(inflater, container, false)

        binding.btnSelecionarMes.setOnClickListener {
            abrirSeletorMes()
        }

        return binding.root
    }

    private fun abrirSeletorMes() {
        val cal = Calendar.getInstance()

        DatePickerDialog(
            requireContext(),
            { _, ano, mes, _ ->
                val mesAno = "%02d/%04d".format(mes + 1, ano)
                carregarVendasDoMes(mesAno)
                binding.btnSelecionarMes.text = mesAno
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun carregarVendasDoMes(mesAno: String) {
        val db = DbRegistro(requireContext())
        val vendas = db.buscarVendasDoMes(mesAno)

        binding.recyclerView.adapter =
            ItemVendaHojeAdapter(vendas, mostrarData = true)


        atualizarPieChart(vendas)
    }

    private fun atualizarPieChart(vendas: List<ItemVendaHoje>) {

        // ---------- Dados do gráfico ----------
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

        val data = PieData(dataSet)

        // ---------- Cálculos ----------
        val total = vendas.sumOf { it.valorTotal }
        val lucro = total * 0.6
        val loja = total * 0.4

        // ---------- Texto central ----------
        val centerText = """
        Lucro
        R$ ${"%.2f".format(lucro)}

        Loja
        R$ ${"%.2f".format(loja)}

        Total
        R$ ${"%.2f".format(total)}
    """.trimIndent()

        // ---------- Configuração do gráfico ----------
        binding.pieChart.apply {
            this.data = data

            isDrawHoleEnabled = true
            holeRadius = 58f
            transparentCircleRadius = 62f
            setHoleColor(Color.TRANSPARENT)

            setCenterText(centerText)
            setCenterTextSize(12f)
            setCenterTextColor(Color.DKGRAY)

            description.isEnabled = false
            legend.isEnabled = true
            setEntryLabelColor(Color.WHITE)

            invalidate()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
