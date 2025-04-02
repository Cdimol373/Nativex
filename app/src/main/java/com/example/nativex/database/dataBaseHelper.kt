/*Esta clase es crucial para interactuar con la base de datos SQLite, ya que
se encarga de crear, actualizar y gestionar las tablas de la base de datos.*/
/*Esta clase es crucial para interactuar con la base de datos SQLite, ya que
se encarga de crear, actualizar y gestionar las tablas de la base de datos.*/
package com.example.nativex.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.nativex.model.Historial

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "ruleta_nativex.db"
        private const val DATABASE_VERSION = 1

        // Tabla Usuarios
        private const val TABLE_USUARIOS = "usuario"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NOMBRE = "nombre"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_CONTRASEÑA = "contraseña"
        private const val COLUMN_MONEDAS = "monedas"
        private const val COLUMN_PARTIDAS = "partidas"

        // Tabla Juegos
        private const val TABLE_JUEGOS = "juegos"
        private const val COLUMN_JUEGO_ID = "id_juego"
        private const val COLUMN_RESULTADO = "resultado"
        private const val COLUMN_USUARIO_ID_JUEGO = "usuario_id"

        // Tabla Historial
        private const val TABLE_HISTORIAL = "historial"
        private const val COLUMN_HISTORIAL_ID = "id_historial"
        private const val COLUMN_PARTIDAS_LIST = "partidas_list"
        private const val COLUMN_MONEDAS_HISTORIAL = "monedas"

        // Tabla Ruleta
        private const val TABLE_RULETA = "ruleta"
        private const val COLUMN_RULETA_ID = "id_ruleta"
        private const val COLUMN_NUMERO_GANADOR = "numero_ganador"
        private const val COLUMN_VALOR_SEGMENTO = "valor_segmento"
        private const val COLUMN_TIPO_SEGMENTO = "tipo_segmento"
        private const val COLUMN_USUARIO_ID_RULETA = "usuario_id"
        private const val COLUMN_FECHA = "fecha"

        // Tabla Administrador
        private const val TABLE_ADMIN = "administrador"
        private const val COLUMN_ADMIN_ID = "id_admin"
        private const val COLUMN_ADMIN_CREDENCIALES = "credenciales"
    }

    init {
        val db = this.writableDatabase // Fuerzo la creación de la base de datos al inicializar la clase
        db.close()
    }

    override fun onCreate(db: SQLiteDatabase) {
        Log.d("DatabaseHelper", "onCreate: Creando la base de datos...")

        val createUsuariosTable = """
            CREATE TABLE IF NOT EXISTS $TABLE_USUARIOS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NOMBRE TEXT NOT NULL,
                $COLUMN_EMAIL TEXT UNIQUE NOT NULL,
                $COLUMN_CONTRASEÑA TEXT NOT NULL,
                $COLUMN_MONEDAS INTEGER DEFAULT 1000,
                $COLUMN_PARTIDAS INTEGER DEFAULT 0
            )
        """.trimIndent()

        val createJuegosTable = """
            CREATE TABLE IF NOT EXISTS $TABLE_JUEGOS (
                $COLUMN_JUEGO_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_RESULTADO BOOLEAN NOT NULL,
                $COLUMN_USUARIO_ID_JUEGO INTEGER NOT NULL,
                FOREIGN KEY($COLUMN_USUARIO_ID_JUEGO) REFERENCES $TABLE_USUARIOS($COLUMN_ID)
            )
        """.trimIndent()

        val createHistorialTable = """
            CREATE TABLE IF NOT EXISTS $TABLE_HISTORIAL (
                $COLUMN_HISTORIAL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_PARTIDAS_LIST TEXT NOT NULL,
                $COLUMN_MONEDAS_HISTORIAL INTEGER NOT NULL
            )
        """.trimIndent()

        val createRuletaTable = """
            CREATE TABLE IF NOT EXISTS $TABLE_RULETA (
                $COLUMN_RULETA_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NUMERO_GANADOR INTEGER NOT NULL,
                $COLUMN_VALOR_SEGMENTO INTEGER NOT NULL,
                $COLUMN_TIPO_SEGMENTO TEXT NOT NULL CHECK($COLUMN_TIPO_SEGMENTO IN ('positivo', 'negativo')),
                $COLUMN_USUARIO_ID_RULETA INTEGER NOT NULL,
                $COLUMN_FECHA TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY($COLUMN_USUARIO_ID_RULETA) REFERENCES $TABLE_USUARIOS($COLUMN_ID)
            )
        """.trimIndent()

        val createAdminTable = """
            CREATE TABLE IF NOT EXISTS $TABLE_ADMIN (
                $COLUMN_ADMIN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_ADMIN_CREDENCIALES TEXT NOT NULL
            )
        """.trimIndent()

        db.execSQL(createUsuariosTable)
        db.execSQL(createJuegosTable)
        db.execSQL(createHistorialTable)
        db.execSQL(createRuletaTable)
        db.execSQL(createAdminTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USUARIOS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_JUEGOS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_HISTORIAL")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_RULETA")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ADMIN")
        onCreate(db)
    }

    override fun onOpen(db: SQLiteDatabase) {
        super.onOpen(db)
        Log.d("DatabaseHelper", "Base de datos abierta correctamente")
    }
    fun guardarHistorial(partidasList: String, monedas: Int) {
        val db = writableDatabase
        val insertQuery = "INSERT INTO $TABLE_HISTORIAL ($COLUMN_PARTIDAS_LIST, $COLUMN_MONEDAS_HISTORIAL) VALUES (?, ?)"
        val statement = db.compileStatement(insertQuery)
        statement.bindString(1, partidasList)
        statement.bindLong(2, monedas.toLong())
        statement.executeInsert()
        db.close()
    }


    fun obtenerHistorial(): List<Historial> {
        val historialList = mutableListOf<Historial>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_HISTORIAL", null)

        while (cursor.moveToNext()) {
            val idHistorial = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_HISTORIAL_ID))
            val partidasList = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PARTIDAS_LIST))
            val monedasHistorial = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MONEDAS_HISTORIAL))

            historialList.add(Historial(idHistorial, partidasList, monedasHistorial))
        }
        cursor.close()
        db.close()
        return historialList
    }

    /**
     * Guarda la puntuación en la base de datos
     */
    fun guardarPuntuacion(monedas: Int) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_MONEDAS_HISTORIAL, monedas)
            put(COLUMN_PARTIDAS_LIST, "Última partida") // Puedes cambiar esto para almacenar datos reales
        }
        db.insert(TABLE_HISTORIAL, null, values)
        db.close()
    }

    /**
     * Obtiene la última puntuación registrada en la base de datos.
     */
    fun obtenerUltimaPuntuacion(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT $COLUMN_MONEDAS_HISTORIAL FROM $TABLE_HISTORIAL ORDER BY $COLUMN_HISTORIAL_ID DESC LIMIT 1", null)
        val puntuacion = if (cursor.moveToFirst()) cursor.getInt(0) else 0
        cursor.close()
        db.close()
        return puntuacion
    }
}
