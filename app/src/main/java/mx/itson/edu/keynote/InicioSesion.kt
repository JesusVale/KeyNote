package mx.itson.edu.keynote

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class InicioSesion : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio_sesion)

        val btnLogin: Button = findViewById(R.id.btn_login)
        val registrar: TextView = findViewById(R.id.registrarse)
        val txtOlvidado: TextView = findViewById(R.id.contra_olvidada)


        btnLogin.setOnClickListener{
            val intent:Intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        txtOlvidado.setOnClickListener {
            val intent:Intent = Intent(this, Restablecer_contrasena::class.java)
            startActivity(intent)
        }

        registrar.setOnClickListener {
            val intent:Intent = Intent(this, registrar_usuario::class.java)
            startActivity(intent)
        }

    }
}