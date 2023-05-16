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
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.snapshots
import com.google.firebase.ktx.Firebase

class registrar_usuario : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val database = Firebase.database
    private val myRef = database.getReference("Users")
    private val userRef= FirebaseDatabase.getInstance().getReference("Users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_usuario)
        auth = Firebase.auth
        val btnRegistrar: Button = findViewById(R.id.btn_registrar)

        val txtLogin: TextView = findViewById(R.id.iniciar_sesion)

        btnRegistrar.setOnClickListener{
            validarDatos()
        }

        txtLogin.setOnClickListener {
            val intent: Intent = Intent(this, InicioSesion::class.java)
            startActivity(intent)
        }


    }

    private fun validarDatos(){
        val nombreView: EditText= findViewById(R.id.nombre_usuario)
        val correoView: EditText = findViewById(R.id.correoTxt)
        val passwordView: EditText = findViewById(R.id.passwordTxt)
        val passwordView2: EditText = findViewById(R.id.passwordTxt2)
        var nombre=nombreView.text.toString().trim()
        var correo=correoView.text.toString().trim()
        var password =passwordView.text.toString()
        var password2 =passwordView2.text.toString()
        if(correo.isEmpty() || password.isEmpty() || password2.isEmpty()){
            Toast.makeText(applicationContext,"Los campos no pueden estar vacios", Toast.LENGTH_SHORT).show()
        }else{
            if(password.length<7){
                Toast.makeText(applicationContext,"La contraseña debe ser mayor a 7 caracteres",
                    Toast.LENGTH_SHORT).show()
            }else{
                if(password == password2){

                    val usuario=User(
                        null,
                        correo,
                        nombre,
                        password
                    )
                    registrarFirebase(usuario)
                }else{
                    Toast.makeText(applicationContext,"Las contraseñas no son iguales",
                        Toast.LENGTH_SHORT).show()
                }

            }
        }
    }

    private fun registrarFirebase(usuario :User){
        auth.createUserWithEmailAndPassword(usuario.correo.toString(),usuario.password.toString())
            .addOnCompleteListener(this){task->
                if(task.isSuccessful){
                    //userRef.push().setValue(usuario)

                    val userId= userRef.push().key!!
                    usuario.id=task.result.user?.uid
                    userRef.child(userId).setValue(usuario)
                    //myRef.push().setValue(usuario)
                    val intent: Intent = Intent(this, InicioSesion::class.java)
                    startActivity(intent)
                    Toast.makeText(baseContext,"Autenticacion exitosa",Toast.LENGTH_LONG)

                }else{
                    Toast.makeText(baseContext,"Autenticacion fallida",Toast.LENGTH_SHORT)
                }

            }
    }
}