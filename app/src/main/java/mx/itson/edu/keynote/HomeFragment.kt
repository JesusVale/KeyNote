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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
    var tareas: ArrayList<Note> = ArrayList<Note>()
    private val noteRef= FirebaseDatabase.getInstance().getReference("Notes")
    private lateinit var infiniteViewPager: ViewPager2
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

        CoroutineScope(Dispatchers.IO).launch {
            // Small delay so the user can actually see the splash screen
            // for a moment as feedback of an attempt to retrieve data.
            delay(250)
            getNotas(fragmentManager)
        }

        Log.d("Total de tareas ea", "${tareas.size}")
        val adapterNotas:AdapterNotas = AdapterNotas(tareas, myFragmentView!!.context, fragmentManager)
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


    private suspend fun getNotas(manager: FragmentManager){
        var task=noteRef.get().addOnSuccessListener {

            var mapNotes :Map<String, Object> = it.getValue() as Map<String, Object>
            var mapKeys: Set<String> = mapNotes.keys
            for ((key,value) in mapNotes) {
                var mapValue: Map<String,Object> = value as Map<String, Object>
                var titulo: String= mapValue.get("titulo").toString()
                var contenido: String= mapValue.get("contenido").toString()
                var tipo: String= mapValue.get("tipo").toString()
                var imagen: String= mapValue.get("image").toString()
                val nuevaTarea = Note(titulo, contenido, tipo, imagen)
                nuevaTarea.id = key;
                // agregar la variable temporal a la lista tareas fuera del bloque addOnSuccessListener
                tareas.add(nuevaTarea)
            }

            val adapterNotas:AdapterNotas = AdapterNotas(tareas, myFragmentView!!.context, manager)
            recyclerHorario.adapter = adapterNotas
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }
        task.await()
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
                val fragmentTransaction: FragmentTransaction = fragmentos.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fragment);
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