package mx.itson.edu.keynote

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ConfiguracionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ConfiguracionFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val file = 1
    private var downloadUri: Uri? = null
    lateinit var perfilImg: com.google.android.material.imageview.ShapeableImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val myFragmentView: View? = inflater.inflate(R.layout.fragment_configuracion, container, false)
        val btnLogout: Button = myFragmentView!!.findViewById(R.id.btnLogout)
        val btnCambiarFoto: Button = myFragmentView!!.findViewById(R.id.btnCambiarFoto)
        perfilImg =  myFragmentView!!.findViewById(R.id.perfilImg)

        if(UserSingleton.getUsuario().imagenPerfil != "" && UserSingleton.getUsuario().imagenPerfil != null){
            Log.d("Entro", "${UserSingleton.getUsuario().imagenPerfil}")
            Log.d("Entro", "${UserSingleton.getUsuario().imagenPerfil == null}")
            if(UserSingleton.getUsuario().imagenPerfil != null){
                Glide.with(this)
                    .load(UserSingleton.getUsuario().imagenPerfil)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(perfilImg)
            }

        }

        btnLogout.setOnClickListener {
            val intent = Intent (activity, InicioSesion::class.java)
            activity?.startActivity(intent)
//            activity?.let{
//                val intent = Intent (it, InicioSesion::class.java)
//                it.startActivity(intent)
//            }
        }

        btnCambiarFoto.setOnClickListener{
            abrirGaleria()
        }

        return myFragmentView
    }

    private fun abrirGaleria(){
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(intent, file)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == file) {
            if (resultCode == Activity.RESULT_OK) {
                val FileUri = data!!.data

                val storageRef = FirebaseStorage.getInstance().getReference().child("images")
                if(FileUri != null){
                    val uploadTask = storageRef.putFile(FileUri)
                    uploadTask.continueWithTask { task ->
                        if (!task.isSuccessful) {
                            task.exception?.let {
                                throw it
                            }
                        }
                        storageRef.downloadUrl
                    }.addOnCompleteListener { task ->
                        if (task.isSuccessful) {

                            downloadUri = task.result
                            Log.d("EA", "EA")
                            actualizarUsuario()
                            Glide.with(this)
                                .load(downloadUri)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(perfilImg)

                            // Aquí puedes usar la URI de descarga de la imagen
                        } else {
                            // Manejar el error
                        }
                    }
                }


                /*val Folder: StorageReference =
                    FirebaseStorage.getInstance().getReference().child("images")

                val file_name: StorageReference = Folder.child("file" + FileUri!!.lastPathSegment)
                file_name.putFile(FileUri).addOnSuccessListener { taskSnapshot ->
                    file_name.getDownloadUrl().addOnSuccessListener { uri ->
                     /**   val hashMap =
                            HashMap<String, String>()
                        hashMap["link"] = java.lang.String.valueOf(uri)
                        noteRef.setValue(hashMap) **/
                        imagen = java.lang.String.valueOf(uri)
                        Log.d("Mensaje", "Se subió correctamente")
                    }
                }*/
            }
        }
        // Convertir el Bitmap a ByteArray
        // val baos = ByteArrayOutputStream()
        //imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        //imagen = baos.toByteArray()

    }

    private fun actualizarUsuario(){
        Log.d("UserConf", "ea")
        val nuevosValores = mapOf(
            "imagenPerfil" to downloadUri.toString()
        )

        var usuarioId:String? = null

        FirebaseDatabase.getInstance().getReference("Users").get().addOnSuccessListener {
            var mapUsers :Map<String, Object> = it.getValue() as Map<String, Object>
            for ((key,value) in mapUsers){
                var mapValue: Map<String,Object> = value as Map<String, Object>
                if(mapValue["id"].toString()== UserSingleton.getUsuario().id){
                    val user=User(
                        mapValue["id"].toString(),
                        mapValue["correo"].toString(),
                        mapValue["nombre"].toString(),
                        mapValue["password"].toString()
                    )

                    Log.d("USUARIO", user.toString())
                    usuarioId = key
                    user.imagenPerfil = downloadUri.toString()
                    UserSingleton.setUsuario(user)
                    if(usuarioId != null){
                        FirebaseDatabase.getInstance().getReference("Users").child(usuarioId!!).setValue(user)
                    }
                }
            }

        }

    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ConfiguracionFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ConfiguracionFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}