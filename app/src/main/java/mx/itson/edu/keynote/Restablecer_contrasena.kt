package mx.itson.edu.keynote

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class Restablecer_contrasena : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restablecer_contrasena)
        auth = Firebase.auth
        val btnRecuperar: Button = findViewById(R.id.btnRecuperar)
        val txtCorreo: EditText =findViewById(R.id.correoTxt)
        btnRecuperar.setOnClickListener{
            if(txtCorreo.text.isEmpty()){
                Toast.makeText(applicationContext,"El correo no puede ser vacio", Toast.LENGTH_LONG).show()
            }else{
                auth.sendPasswordResetEmail(txtCorreo.text.toString()).addOnSuccessListener {
                    Toast.makeText(applicationContext,"Se mando un correo a la cuenta ${txtCorreo.text.toString()}", Toast.LENGTH_LONG).show()
                    val intent: Intent = Intent(this, InicioSesion::class.java)
                    startActivity(intent)
                }.addOnFailureListener{
                    Toast.makeText(applicationContext,"Correo no pertenece a ninguna cuenta", Toast.LENGTH_LONG).show()
                }
            }


        }
    }
}