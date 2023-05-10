package mx.itson.edu.keynote

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


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
    var tareas: ArrayList<Note> = ArrayList<Note>()
    private val noteRef= FirebaseDatabase.getInstance().getReference("Notes")
    private lateinit var infiniteViewPager: ViewPager2

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

        val myFragmentView: View? = inflater.inflate(R.layout.fragment_home, container, false)

        val layoutManager = LinearLayoutManager(this.activity, LinearLayoutManager.HORIZONTAL, false)
        val layoutManagerUltimas = LinearLayoutManager(this.activity, LinearLayoutManager.HORIZONTAL, false)
        val layoutManagerFijadas = LinearLayoutManager(this.activity, LinearLayoutManager.HORIZONTAL, false)


        val recyclerHorario:RecyclerView = myFragmentView!!.findViewById(R.id.recyclerHorario)
        val recyclerUltimas:RecyclerView = myFragmentView!!.findViewById(R.id.recyclerUltimas)
        val recyclerFijadas:RecyclerView = myFragmentView!!.findViewById(R.id.recyclerFijadas)

        recyclerHorario.setLayoutManager(layoutManager)
        recyclerUltimas.setLayoutManager(layoutManagerUltimas)
        recyclerFijadas.setLayoutManager(layoutManagerFijadas)

        getNotas()

        val adapterNotas:AdapterNotas = AdapterNotas(tareas)
        recyclerHorario.adapter = adapterNotas
        recyclerUltimas.adapter = adapterNotas
        recyclerFijadas.adapter = adapterNotas

        // Inflate the layout for this fragment
        return myFragmentView
    }

    /*fun agregarNotas(){
        tareas.add(Nota("1:00 - 2:00 Ejercicio", "Tu me dices que no tienes ritmo, pues mira lo que acabas de hacer, yo ya tengo un trabajo muy bueno", R.drawable.navyblue_note_background))
        tareas.add(Nota("10:00 - 12:00 Tarea", "Matematicas Realizar los siguientes ejercicios...", R.drawable.blue_note_background))
        tareas.add(Nota("11:00 - 12:00 Dibujo", "EOOOOO E000 EEO EEO EEEEO EEEEE0 EO EO EO EO ALL RIGHT ALL RIGHT!!", R.drawable.orange_note_background))
        tareas.add(Nota("3:00 - 4:00 Canto", "Que sepa el mundo que en marcha estoy, que voy a cumplir mi misión", R.drawable.red_note_background))
    }*/


    private fun getNotas(){
        Firebase.firestore.collection("Notes")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {

                    tareas.add(Note(document.getString("titulo"), document.getString("contenido"), document.getString("tipo"), document.getString("imagen")))
                }
            }
            .addOnFailureListener { exception ->

            }

    }

    class AdapterNotas(var tareas: ArrayList<Note> ): RecyclerView.Adapter<HomeFragment.AdapterNotas.ViewHolder>(){

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
            holder.contenido.setText(tarea.contenido?.substring(0, 20))
            holder.layoutNota.setBackgroundResource(R.drawable.navyblue_note_background)

            holder.layoutNota.setOnClickListener{

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