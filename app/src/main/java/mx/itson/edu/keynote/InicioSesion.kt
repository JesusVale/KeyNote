package mx.itson.edu.keynote

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class InicioSesion : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val userRef= FirebaseDatabase.getInstance().getReference("Users")
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
            finish()
        }

        registrar.setOnClickListener {
            val intent:Intent = Intent(this, registrar_usuario::class.java)
            startActivity(intent)
            finish()
        }


    }

    private fun validarDatos(){
        val correoView: EditText= findViewById(R.id.correoTxt)
        val passwordView: EditText= findViewById(R.id.passwordTxt)
        var correo=correoView.text.toString().trim()
        var password =passwordView.text.toString()
        val isValidEmail = android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()

        if(correo.isEmpty() || password.isEmpty()){
            Toast.makeText(applicationContext,"Los campos no pueden estar vacios",Toast.LENGTH_SHORT).show()
        }else {
            if (!isValidEmail) {
                Toast.makeText(applicationContext, "Ingrese un correo electrónico válido", Toast.LENGTH_SHORT).show()
            } else if (password.length < 7) {
                Toast.makeText(applicationContext, "La contraseña debe tener al menos 7 caracteres", Toast.LENGTH_SHORT).show()
            } else {
                val usuario = User(
                    null,
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

                    userRef.get().addOnSuccessListener {
                        var mapUsers :Map<String, Object> = it.getValue() as Map<String, Object>
                        for ((key,value) in mapUsers){
                            var mapValue: Map<String,Object> = value as Map<String, Object>
                            if(mapValue["id"].toString()== task.result.user?.uid){
                                val user=User(
                                    mapValue["id"].toString(),
                                    mapValue["correo"].toString(),
                                    mapValue["nombre"].toString(),
                                    mapValue["password"].toString(),
                                    mapValue["imagenPerfil"].toString()
                                )
                                Log.d("USUARIO", user.toString())
                                UserSingleton.setUsuario(user)
                            }
                        }
                    }
                    val intent:Intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)

                }else{
                    Toast.makeText(baseContext,"Tu correo o contraseña no son correctas",Toast.LENGTH_SHORT)
                }

            }
    }
}