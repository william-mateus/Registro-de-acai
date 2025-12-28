package com.williams.acairegister


import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.williams.acairegister.ui.home.ItemVendaHoje

class DbRegistro(context: Context) :
    SQLiteOpenHelper(context, "DataBase.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE vendas (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                produto TEXT,
                complemento TEXT,
                valor REAL,
                data TEXT,
                hora TEXT
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS vendas")
        onCreate(db)
    }

    fun inserirVenda(
        produto: String,
        complemento: String,
        valor: Double,
        data: String,
        hora: String
    ) {
        val valores = ContentValues().apply {
            put("produto", produto)
            put("complemento", complemento)
            put("valor", valor)
            put("data", data)
            put("hora", hora)
        }

        writableDatabase.insert("vendas", null, valores)
    }

    fun buscarVendasDoDia(dataSelecionada: String): List<ItemVendaHoje> {
        val lista = mutableListOf<ItemVendaHoje>()

        val cursor = readableDatabase.rawQuery(
            """
        SELECT produto, complemento, valor, data, hora
        FROM vendas
        WHERE data = ?
        ORDER BY hora DESC
        """,
            arrayOf(dataSelecionada)
        )

        cursor.use {
            while (it.moveToNext()) {
                lista.add(
                    ItemVendaHoje(
                        produto = it.getString(0),
                        complemento = it.getString(1),
                        valorTotal = it.getDouble(2),
                        data = it.getString(3),
                        hora = it.getString(4)
                    )
                )
            }
        }

        return lista
    }


    fun buscarVendasDoMes(mesAno: String): List<ItemVendaHoje> {
        val lista = mutableListOf<ItemVendaHoje>()

        val cursor = readableDatabase.rawQuery(
            """
        SELECT produto, complemento, valor, data, hora
        FROM vendas
        WHERE data LIKE ?
        ORDER BY data, hora
        """,
            arrayOf("%/$mesAno")
        )

        cursor.use {
            while (it.moveToNext()) {
                lista.add(
                    ItemVendaHoje(
                        produto = it.getString(0),
                        complemento = it.getString(1),
                        valorTotal = it.getDouble(2),
                        data = it.getString(3),
                        hora = it.getString(4)
                    )
                )
            }
        }

        return lista
    }

}
