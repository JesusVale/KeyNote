package mx.itson.edu.keynote

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase

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
    lateinit var btn_camara: ImageButton
    private val noteRef= FirebaseDatabase.getInstance().getReference("Notes")
    private var imagen = null
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
            val tituloNota: EditText = myFragmentView!!.findViewById(R.id.tituloNota)
            val contenidoNota: EditText = myFragmentView!!.findViewById(R.id.textoNota)
            imgView = myFragmentView!!.findViewById(R.id.iv_visor)
        btn_camara.setOnClickListener {
//                    val fragmentManager=requireActivity().supportFragmentManager
//                    val segundoFragmento=CamaraFragment()
//                    val fragmentTransaction=fragmentManager.beginTransaction()
//                    fragmentTransaction.replace(R.id.fragment_container, segundoFragmento)
//                    fragmentTransaction.commit()
            abrirCamara()

            }

        btn_save.setOnClickListener{
            var note: Note = Note(tituloNota.text.toString(), contenidoNota.text.toString(), "Normal", "")
            guardarFirebase(note)
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
        val userId= noteRef.push().key!!
        noteRef.child(userId).setValue(note)
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