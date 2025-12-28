package com.williams.acairegister

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.williams.acairegister.databinding.FragmentRegisterBinding
import java.time.LocalTime

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        // ----------------------------
        // Spinner Tipo de Venda
        // ----------------------------
        val tiposDeVenda = listOf(
            TipoVenda("1 Litro", 35.0),
            TipoVenda("700 ML", 28.0),
            TipoVenda("500 ML", 22.0),
            TipoVenda("300 ML", 18.0)
        )

        val vendaAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            tiposDeVenda
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        binding.tipoVendaSpinner.adapter = vendaAdapter

        // ----------------------------
        // Spinner Complemento
        // ----------------------------
        val tiposDeComplemento = listOf(
            TipoVenda("Nenhum", 0.0),
            TipoVenda("Nutella", 5.0),
            TipoVenda("Leite Ninho", 5.0),
            TipoVenda("Creme de Avelã", 4.0),
            TipoVenda("Stikadinho", 1.5)
        )

        val complementoAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            tiposDeComplemento
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        binding.tipoComplementoSpinner.adapter = complementoAdapter

        // ----------------------------
        // Botão Salvar
        // ----------------------------
        binding.saveButton.setOnClickListener {

            // PEGANDO SELEÇÃO DOS SPINNERS
            val vendaSelecionada =
                binding.tipoVendaSpinner.selectedItem as TipoVenda

            val complementoSelecionado =
                binding.tipoComplementoSpinner.selectedItem as TipoVenda

            val produto = vendaSelecionada.nome
            val complemento = complementoSelecionado.nome

            val valorTotal =
                vendaSelecionada.preco + complementoSelecionado.preco

            // Data DD/MM/YYYY
            val dia = binding.dataPicker.dayOfMonth
            val mes = binding.dataPicker.month + 1
            val ano = binding.dataPicker.year
            val data = "%02d/%02d/%04d".format(dia, mes, ano)

            // Hora HH:mm
            val hora = LocalTime.now().toString().substring(0, 5)

            val db = DbRegistro(requireContext())
            db.inserirVenda(
                produto = produto,
                complemento = complemento,
                valor = valorTotal,
                data = data,
                hora = hora
            )

            Toast.makeText(
                requireContext(),
                "Venda registrada!",
                Toast.LENGTH_SHORT
            ).show()

            findNavController().navigateUp()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
