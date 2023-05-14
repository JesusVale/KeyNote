package mx.itson.edu.keynote

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Notas.newInstance] factory method to
 * create an instance of this fragment.
 */
class Notas : Fragment() {

    private lateinit var imgView: ImageView
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val noteRef= FirebaseDatabase.getInstance().getReference("Notes")
    private var imagen = null
    private var editMode:Boolean = false
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

        val myFragmentView: View? = inflater.inflate(R.layout.fragment_notas, container, false)
        val btn_camara: ImageButton = myFragmentView!!.findViewById(R.id.btn_photo)
        val btn_audio: ImageButton = myFragmentView!!.findViewById(R.id.btn_voice)
        val btn_save: ImageButton = myFragmentView!!.findViewById(R.id.btn_save)
        val btn_delete: Button = myFragmentView!!.findViewById(R.id.btn_delete)
        val tituloNota: EditText = myFragmentView!!.findViewById(R.id.tituloNota)
        val contenidoNota: EditText = myFragmentView!!.findViewById(R.id.textoNota)
        imgView = myFragmentView!!.findViewById(R.id.iv_visor)

        if(arguments != null){
            val titulo = requireArguments().getString("titulo")
            val contenido = requireArguments().getString("contenido")
            val imagen = requireArguments().getString("imagen")
            tituloNota.setText(titulo)
            contenidoNota.setText(contenido)
            btn_delete.visibility = View.VISIBLE
            editMode = true
        }

        btn_camara.setOnClickListener {
//                    val fragmentManager=requireActivity().supportFragmentManager
//                    val segundoFragmento=CamaraFragment()
//                    val fragmentTransaction=fragmentManager.beginTransaction()
//                    fragmentTransaction.replace(R.id.fragment_container, segundoFragmento)
//                    fragmentTransaction.commit()
            abrirCamara()

        }

        btn_save.setOnClickListener{
            var nota: Note = Note(tituloNota.text.toString(), contenidoNota.text.toString(), "Normal", "")
            if(editMode){
                val id = requireArguments().getString("id")
                nota.id = id
                editarFirebase(nota)
            } else{
                guardarFirebase(nota)
            }

        }

        btn_delete.setOnClickListener{
            val id: String? = requireArguments().getString("id")
            eliminarFirebase(id)
        }

        btn_audio.setOnClickListener {
            val fragmentManager=requireActivity().supportFragmentManager
            val segundoFragmento=AudioRecordFragment()
            val fragmentTransaction=fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container, segundoFragmento);
            fragmentTransaction.commit();
        }
        return myFragmentView
    }

    private fun abrirCamara(){
        val intent: Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(intent, 1)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            imgView.setImageBitmap(imageBitmap)
        }
    }

    private fun guardarFirebase(note: Note){
        /*val userId= noteRef.push().key!!
        noteRef.child(userId).setValue(note)*/
        val db = Firebase.firestore
        val collection = db.collection("Notes")
        collection.add(note)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(this.context, "Se guardó la nota correctamente", Toast.LENGTH_SHORT)
                    .show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this.context, "No se pudo guardar la nota correctamente", Toast.LENGTH_SHORT)
                    .show()
            }

    }

    private fun eliminarFirebase(id:String?){
        noteRef.child(id!!).removeValue()
        Toast.makeText(this.context, "Se eliminó la nota correctamente", Toast.LENGTH_SHORT)
            .show()
        val fragment: Fragment = HomeFragment()
        replaceFragment(fragment)
    }

    fun replaceFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = parentFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    private fun editarFirebase(note: Note){
        val nuevosValores = mapOf(
            "titulo" to note.titulo,
            "contenido" to note.contenido
        )
        noteRef.child(note.id!!).setValue(nuevosValores)
        Toast.makeText(this.context, "Se guardaron los cambios correctamente", Toast.LENGTH_SHORT)
            .show()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Notas.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Notas().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}