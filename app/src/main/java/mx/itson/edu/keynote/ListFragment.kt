package mx.itson.edu.keynote

import android.content.Context
import android.content.res.Resources
import android.media.Image
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Calendar

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ListFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val tareaRef= FirebaseDatabase.getInstance().getReference("Tareas")
    lateinit var recyclerTareasDemas: RecyclerView
    lateinit var recyclerTareasProximas: RecyclerView
    lateinit var recyclerTareasPasadas: RecyclerView

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
        val myFragmentView: View? = inflater.inflate(R.layout.fragment_list, container, false)
        val btn_add: ImageView = requireActivity().findViewById(R.id.addIcon)
        val btn_lupa: ImageView = requireActivity().findViewById(R.id.search_icon)
        recyclerTareasDemas = myFragmentView!!.findViewById(R.id.recyclerTareasDemas)
        recyclerTareasPasadas = myFragmentView!!.findViewById(R.id.recyclerTareasPasadas)
        recyclerTareasProximas = myFragmentView!!.findViewById(R.id.recyclerTareasProximas)

        var fragmentManager: FragmentManager = this.parentFragmentManager

        if (this.isAdded()) {
            // El fragmento está asociado con un FragmentManager, es seguro acceder a él
            fragmentManager = this.parentFragmentManager
        }

        CoroutineScope(Dispatchers.Main).launch {
            getTareas(fragmentManager)

        }


        btn_add.setOnClickListener{
            val fragmentManager=requireActivity().supportFragmentManager
            val segundoFragmento=AgregarTarea()
            val fragmentTransaction=fragmentManager.beginTransaction()
            btn_add.visibility = View.GONE
            btn_lupa.visibility = View.GONE
            fragmentTransaction.replace(R.id.fragment_container, segundoFragmento)
            fragmentTransaction.commit()
        }



        return myFragmentView


    }

    private suspend fun getTareas(fragmentManager: FragmentManager){
        var tareasPasadas: ArrayList<Tarea> = ArrayList<Tarea>()
        var tareasDemas: ArrayList<Tarea> = ArrayList<Tarea>()
        var tareasProximas: ArrayList<Tarea> = ArrayList<Tarea>()
        val it = tareaRef.get().await()
        var mapNotes :Map<String, Object> = it.getValue() as Map<String, Object>

        for ((key,value) in mapNotes){
            var mapValue: Map<String,Object> = value as Map<String, Object>
            var titulo: String= mapValue.get("titulo").toString()
            var horaMap: HashMap<String, Object> = mapValue.get("fecha") as HashMap<String, Object>
            val seconds: Long = horaMap.get("seconds") as Long
            val nanoseconds: Long = horaMap.get("nanoseconds") as Long
            var fecha = Timestamp(seconds, nanoseconds.toInt())
            var clases: ArrayList<String> = mapValue.get("clases") as ArrayList<String>
            var info: String= mapValue.get("info").toString()
            val tarea = Tarea(titulo, fecha, clases, info)
            tarea.id = key
            if(isDatePast(fecha.toDate().time)){
                tarea.tipo = R.color.blueSelected
                tareasPasadas.add(tarea)
                continue
            }

            Log.d("EOO", "${tarea.titulo}")

            if(isDateInCurrentWeek(fecha.toDate().time)){
                tarea.tipo = R.color.green
                tareasDemas.add(tarea)
                continue
            }
            tarea.tipo = R.color.recordRed
            tareasProximas.add(tarea)
        }



        val adapterTareasPasadas = AdapterTareas(tareasPasadas, this.requireContext(), fragmentManager, this.resources)
        val adapterTareasDemas = AdapterTareas(tareasDemas, this.requireContext(), fragmentManager, this.resources)
        val adapterTareasProximas = AdapterTareas(tareasProximas, this.requireContext(), fragmentManager, this.resources)

        recyclerTareasPasadas.adapter = adapterTareasPasadas
        recyclerTareasDemas.adapter = adapterTareasDemas
        recyclerTareasProximas.adapter = adapterTareasProximas
    }

    fun isDatePast(date: Long): Boolean {
        // Obtiene una instancia del Calendario
        val calendar = Calendar.getInstance()

        // Obtiene la fecha actual en milisegundos
        val currentDate = calendar.timeInMillis

        // Compara la fecha proporcionada con la fecha actual
        return date < currentDate
    }

    fun isDateInCurrentWeek(date: Long): Boolean {
        // Obtiene una instancia del Calendario
        val calendar = Calendar.getInstance()

        // Obtiene la fecha actual en milisegundos
        val currentDate = calendar.timeInMillis

        // Establece la fecha proporcionada en el objeto Calendar
        calendar.timeInMillis = date

        // Obtiene el número de semana de la fecha actual y de la fecha proporcionada
        val currentWeek = calendar.get(Calendar.WEEK_OF_YEAR)
        calendar.timeInMillis = currentDate
        val currentWeekToday = calendar.get(Calendar.WEEK_OF_YEAR)

        // Compara los números de semana
        return currentWeek == currentWeekToday
    }


    class AdapterTareas(var tareas: ArrayList<Tarea>, var contexto: Context, var fragmentos: FragmentManager, var recusos: Resources): RecyclerView.Adapter<ListFragment.AdapterTareas.ViewHolder>(){

        class ViewHolder(view:View): RecyclerView.ViewHolder(view){
            val titulo: TextView = view.findViewById(R.id.titulo)
            val contenido: TextView = view.findViewById(R.id.contenido)
            var layoutNota: LinearLayout = view.findViewById(R.id.layoutNota)

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
            if(tarea.info?.length!! > 40){
                holder.contenido.setText(tarea.info?.substring(0, 20))
            } else{
                holder.contenido.setText(tarea.info)
            }

            recusos.getColor(tarea.tipo)

            /*holder.layoutNota.setOnClickListener{
                val bundle = Bundle()
                bundle.putString("id", tarea.id)
                bundle.putString("titulo", tarea.titulo)
                bundle.putString("contenido", tarea.contenido)
                bundle.putString("imagen", tarea.imagen)

                val fragment = Notas()

                fragment.arguments = bundle
                holder.layoutNota.setBackgroundResource(R.color.blueSelected)
                val fragmentTransaction: FragmentTransaction = fragmentos.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fragment);
                fragmentTransaction.commit();
            }*/

        }

    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}