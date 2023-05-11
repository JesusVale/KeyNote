package mx.itson.edu.keynote

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CalendarPlus.newInstance] factory method to
 * create an instance of this fragment.
 */
class CalendarPlus : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val claseRef= FirebaseDatabase.getInstance().getReference("Clases")
    lateinit var recyclerHorarioL:RecyclerView
    var clases: ArrayList<Clase> = ArrayList<Clase>()

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
        val myFragmentView: View? = inflater.inflate(R.layout.fragment_calendar_plus, container, false)
        val btn_add: ImageView = requireActivity().findViewById(R.id.addIcon)
        val btn_lupa: ImageView = requireActivity().findViewById(R.id.search_icon)
        btn_add.setOnClickListener{
            val fragmentManager=requireActivity().supportFragmentManager
            val segundoFragmento=AgregarClaseFragment()
            val fragmentTransaction=fragmentManager.beginTransaction()
            btn_add.visibility = View.GONE
            btn_lupa.visibility = View.GONE
            fragmentTransaction.replace(R.id.fragment_container, segundoFragmento)
            fragmentTransaction.commit()
        }

        var fragmentManager: FragmentManager = this.parentFragmentManager

        if (this.isAdded()) {
            // El fragmento está asociado con un FragmentManager, es seguro acceder a él
            fragmentManager = this.parentFragmentManager
        } else {

        }




        return myFragmentView
    }


    private suspend fun getClases(manager: FragmentManager){
        var task=claseRef.get().addOnSuccessListener {

            var mapNotes :Map<String, Object> = it.getValue() as Map<String, Object>
            var mapKeys: Set<String> = mapNotes.keys
            for ((key,value) in mapNotes) {
                var mapValue: Map<String,Object> = value as Map<String, Object>
                var titulo: String= mapValue.get("titulo").toString()
                var info: String= mapValue.get("info").toString()
                var dias = mapValue.get("dias")
                var imagen: String= mapValue.get("image").toString()
                //val nuevaTarea = Note(titulo, contenido, tipo, imagen)
                //nuevaTarea.id = key;
                // agregar la variable temporal a la lista tareas fuera del bloque addOnSuccessListener
                //tareas.add(nuevaTarea)
            }

            /*val adapterNotas: HomeFragment.AdapterNotas =
                HomeFragment.AdapterNotas(tareas, myFragmentView!!.context, manager)
            recyclerHorario.adapter = adapterNotas*/
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }
        task.await()
    }


    class AdapterClases(var clases: ArrayList<Clase>, var fragmentos: FragmentManager): RecyclerView.Adapter<CalendarPlus.AdapterClases.ViewHolder>(){

        class ViewHolder(view:View): RecyclerView.ViewHolder(view){
            val textoClase: TextView = view.findViewById(R.id.textoClase)

            init {
                textoClase
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            var inflador = LayoutInflater.from(parent.context)
            var vista = inflador.inflate(R.layout.clase_view, null)

            return ViewHolder(vista)

        }

        override fun getItemCount(): Int {
            return clases.size
        }

        override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
            val clase = clases[pos]

            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())

            val texto: String = "${sdf.format(clase.hora.toDate())} ${clase.titulo}"

            holder.textoClase.setBackgroundColor(clase.color)

            holder.textoClase.setOnClickListener{
                val bundle = Bundle()
                bundle.putString("id", clase.id)

                val fragment = AgregarClaseFragment()

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
         * @return A new instance of fragment CalendarPlus.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CalendarPlus().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}