package mx.itson.edu.keynote

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class registrar_usuario : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_usuario)

        val btnRegistrar: Button = findViewById(R.id.btn_registrar)

        val txtLogin: TextView = findViewById(R.id.iniciar_sesion)

        btnRegistrar.setOnClickListener{
            val intent: Intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        txtLogin.setOnClickListener {
            val intent: Intent = Intent(this, InicioSesion::class.java)
            startActivity(intent)
        }
    }
}