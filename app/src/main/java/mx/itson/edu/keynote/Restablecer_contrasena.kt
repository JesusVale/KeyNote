package mx.itson.edu.keynote

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class Restablecer_contrasena : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restablecer_contrasena)

        val btnRecuperar: Button = findViewById(R.id.btnRecuperar)

        btnRecuperar.setOnClickListener{
            val intent: Intent = Intent(this, InicioSesion::class.java)
            startActivity(intent)
        }
    }
}