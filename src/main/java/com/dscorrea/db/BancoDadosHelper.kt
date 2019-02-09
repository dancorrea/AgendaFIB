package com.dscorrea.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.dscorrea.agenda.Constants.CONTATOS_DB_NAME
import com.dscorrea.agenda.Constants.CONTATOS_TABLE_NAME
import org.jetbrains.anko.db.*

class BancoDadosHelper(context: Context) :
        ManagedSQLiteOpenHelper(ctx = context, name = CONTATOS_DB_NAME,  version = 1) {

    private val scriptSQLCreate = arrayOf(
            "INSERT INTO $CONTATOS_TABLE_NAME VALUES(1,'fernando.collor@gmail.com','Fernando Collor','R. Assungui, 27, Cursino, São Paulo, 04131-000, Brasil',800200300,NULL,'www.google.com.br',NULL);",
            "INSERT INTO $CONTATOS_TABLE_NAME VALUES(2,'dilma@gmail.com','Dilma','R. José Cocciuffo, 90 - Cursino, São Paulo, 04121-120, Brasil',800235468,NULL,'www.uol.com.br',NULL);",
            "INSERT INTO $CONTATOS_TABLE_NAME VALUES(3,'lula@gmail.com','Lula','R. José Cocciuffo, 56 - Cursino, São Paulo, 04121-120, Brasil',80023587,NULL,'www.google.com',NULL);",
            "INSERT INTO $CONTATOS_TABLE_NAME VALUES(4,'maluf@gmail.com','Maluf','R. Camilo José, 48 - Cursino, São Paulo, 04125-140, Brasil',800025774,NULL,'www.uol.com.br',NULL);")

    //singleton da classe
    companion object {
        private var instance: BancoDadosHelper? = null

        @Synchronized
        fun getInstance(ctx: Context): BancoDadosHelper {
            if (instance == null) {
                instance = BancoDadosHelper(ctx.getApplicationContext())
            }
            return instance!!
        }
    }


    override fun onCreate(db: SQLiteDatabase) {
        db.createTable("agenda", true,
                "id" to INTEGER + PRIMARY_KEY + UNIQUE,
                "email" to TEXT,
                "nome" to TEXT,
                "endereco" to TEXT,
                "telefone" to INTEGER,
                "datanascimento" to INTEGER,
                "site" to TEXT,
                "foto" to TEXT
        )

        // insere dados iniciais na tabela
        scriptSQLCreate.forEach {sql -> db.execSQL(sql)}

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.dropTable(CONTATOS_TABLE_NAME, true)
        onCreate(db)
    }

}

val Context.database: BancoDadosHelper get() = BancoDadosHelper.getInstance(getApplicationContext())
