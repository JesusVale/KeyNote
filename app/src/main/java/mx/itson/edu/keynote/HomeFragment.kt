package mx.itson.edu.keynote

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val noteRef= FirebaseDatabase.getInstance().getReference("Notes")
    lateinit var recyclerHorario:RecyclerView
    var myFragmentView: View? = null

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

        myFragmentView =inflater.inflate(R.layout.fragment_home, container, false)
        val layoutManager = LinearLayoutManager(this.activity, LinearLayoutManager.HORIZONTAL, false)
        val layoutManagerUltimas = LinearLayoutManager(this.activity, LinearLayoutManager.HORIZONTAL, false)
        val layoutManagerFijadas = LinearLayoutManager(this.activity, LinearLayoutManager.HORIZONTAL, false)
        var fragmentManager: FragmentManager = this.parentFragmentManager

        if (this.isAdded()) {
            // El fragmento está asociado con un FragmentManager, es seguro acceder a él
            fragmentManager = this.parentFragmentManager
        } else {
        }

        recyclerHorario = myFragmentView!!.findViewById(R.id.recyclerHorario)
        val recyclerUltimas:RecyclerView = myFragmentView!!.findViewById(R.id.recyclerUltimas)
        val recyclerFijadas:RecyclerView = myFragmentView!!.findViewById(R.id.recyclerFijadas)

        recyclerHorario.setLayoutManager(layoutManager)
        recyclerUltimas.setLayoutManager(layoutManagerUltimas)
        recyclerFijadas.setLayoutManager(layoutManagerFijadas)

        CoroutineScope(Dispatchers.Main).launch {
            val notas = getNotas()
            val adapterNotas:AdapterNotas = AdapterNotas(notas, myFragmentView!!.context, fragmentManager)
            recyclerHorario.adapter = adapterNotas
        }

        return myFragmentView
    }

    private suspend fun getNotas(): ArrayList<Note> = withContext(Dispatchers.Main) {


        var notas: ArrayList<Note> = ArrayList()

        val it = noteRef.get().await()
            if(it.exists()){
                var mapNotes :Map<String, Object> = it.getValue() as Map<String, Object>
                for ((key,value) in mapNotes){
                    var mapValue: Map<String,Object> = value as Map<String, Object>
                    if(mapValue["idUser"].toString()==UserSingleton.getUsuario().id){

                        var mapNote: Map<String,Object> = value as Map<String, Object>
                        var titulo: String= mapNote.get("titulo").toString()
                        var contenido: String= mapNote.get("contenido").toString()
                        var tipo: String= mapNote.get("tipo").toString()
                        var imagen: String= mapNote.get("image").toString()
                        val nuevaTarea = Note(null,titulo, contenido, tipo, imagen)
                        nuevaTarea.id = key
                        notas.add(nuevaTarea)

                    }
                    // agregar la variable temporal a la lista tareas fuera del bloque addOnSuccessListener

                }
            }




        /*val db = Firebase.firestore
        val collection = db.collection("Notes")
        val querySnapshot = collection.get().await()
        var notas: ArrayList<Note> = ArrayList()
        for (document in querySnapshot) {
            Log.d("TAG", "${document.id} => ${document.data}")
            val mapValue: Map<String, Any> = document.data
            var titulo: String= mapValue.get("titulo").toString()
            var contenido: String= mapValue.get("contenido").toString()
            var tipo: String= mapValue.get("tipo").toString()
            var imagen: String= mapValue.get("image").toString()
            val note = Note(titulo, contenido, tipo, imagen)
            note.id = document.id;
            notas.add(note)
        }


        notas*/
        notas
    }



    class AdapterNotas(var tareas: ArrayList<Note>, var contexto:Context, var fragmentos: FragmentManager ): RecyclerView.Adapter<HomeFragment.AdapterNotas.ViewHolder>(){

        class ViewHolder(view:View): RecyclerView.ViewHolder(view){
            val titulo:TextView = view.findViewById(R.id.titulo)
            val contenido:TextView = view.findViewById(R.id.contenido)
            var layoutNota:LinearLayout = view.findViewById(R.id.layoutNota)

            init {
                titulo
                contenido
                layoutNota
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            var inflador = LayoutInflater.from(parent.context)
            var vista = inflador.inflate(R.layout.nota_view, null)

            return ViewHolder(vista)

        }

        override fun getItemCount(): Int {
            return tareas.size
        }

        override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
            val tarea = tareas[pos]
            holder.titulo.setText(tarea.titulo)
            if(tarea.contenido?.length!! > 40){
                holder.contenido.setText(tarea.contenido?.substring(0, 20))
            } else{
                holder.contenido.setText(tarea.contenido)
            }

            holder.layoutNota.setBackgroundResource(R.drawable.navyblue_note_background)

            holder.layoutNota.setOnClickListener{
                val bundle = Bundle()
                bundle.putString("id", tarea.id)
                bundle.putString("titulo", tarea.titulo)
                bundle.putString("contenido", tarea.contenido)
                bundle.putString("imagen", tarea.imagen)

                val fragment = Notas()

                fragment.arguments = bundle
                holder.layoutNota.setBackgroundResource(R.color.blueSelected)
                val fragmentTransaction: FragmentTransaction = fragmentos.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fragment).addToBackStack(null)
                fragmentTransaction.commit();
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
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}