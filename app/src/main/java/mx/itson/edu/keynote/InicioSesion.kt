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

class InicioSesion : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio_sesion)
        auth = Firebase.auth
        val btnLogin: Button = findViewById(R.id.btn_login)
        val registrar: TextView = findViewById(R.id.registrarse)
        val txtOlvidado: TextView = findViewById(R.id.contra_olvidada)


        btnLogin.setOnClickListener{
            validarDatos()
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

    private fun validarDatos(){
        val correoView: EditText= findViewById(R.id.correoTxt)
        val passwordView: EditText= findViewById(R.id.passwordTxt)
        var correo=correoView.text.toString().trim()
        var password =passwordView.text.toString()

        if(correo.isEmpty() || password.isEmpty()){
            Toast.makeText(applicationContext,"Los campos no pueden estar vacios",Toast.LENGTH_SHORT).show()
        }else{
            if(password.length<7){
                Toast.makeText(applicationContext,"La contraseña debe ser mayor a 7 caracteres",Toast.LENGTH_SHORT).show()
            }else{
                val usuario=User(
                    correo,
                    null,
                    password
                )
                loginFirebase(usuario)
            }
        }
    }
    private fun loginFirebase(usuario :User){
        auth.signInWithEmailAndPassword(usuario.correo.toString(),usuario.password.toString())
            .addOnCompleteListener(this){task->
                if(task.isSuccessful){
                    val intent:Intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }else{
                    Toast.makeText(baseContext,"Tu correo o contraseña no son correctas",Toast.LENGTH_SHORT)
                }

            }
    }
}