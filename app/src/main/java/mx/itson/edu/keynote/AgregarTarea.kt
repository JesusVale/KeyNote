package mx.itson.edu.keynote

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AgregarTarea.newInstance] factory method to
 * create an instance of this fragment.
 */

class AgregarTarea : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val claseRef= FirebaseDatabase.getInstance().getReference("Clases")
    private val tareaRef= FirebaseDatabase.getInstance().getReference("Tareas")
    private var checkboxes: ArrayList<CheckBox> = ArrayList<CheckBox>()
    private var clasesSeleccionados: ArrayList<String> = ArrayList<String>()
    private var clasesSelecTitulo: ArrayList<String> = ArrayList<String>()
    private lateinit var recyclerClasesTarea: RecyclerView
    private lateinit var btn_mostrarFecha: TextView
    private lateinit var fechaSeleccionada:Timestamp
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
        val myFragmentView: View? = inflater.inflate(R.layout.fragment_agregar_tarea, container, false)
        val btn_plus: ImageButton = myFragmentView!!.findViewById(R.id.btnPlus)
        val btn_delete: ImageButton = myFragmentView!!.findViewById(R.id.btnX)
        val btn_add: ImageView = requireActivity().findViewById(R.id.addIcon)
        val btn_lupa: ImageView = requireActivity().findViewById(R.id.search_icon)
        val seleccionarClase: TextView = myFragmentView!!.findViewById(R.id.seleccionarClase)
        val tituloTxt: EditText = myFragmentView!!.findViewById(R.id.tituloTxt)
        val infoTxt: EditText = myFragmentView!!.findViewById(R.id.infoTxt)

        btn_mostrarFecha = myFragmentView!!.findViewById(R.id.abrirFecha_btn)

        btn_mostrarFecha.setOnClickListener{
            mostrarDateTimePicker()
        }

        recyclerClasesTarea = myFragmentView!!.findViewById(R.id.recyclerClasesTarea)
        val layoutManager = LinearLayoutManager(this.activity, LinearLayoutManager.HORIZONTAL, false)
        recyclerClasesTarea.layoutManager = layoutManager
        btn_plus.setOnClickListener {

            val tarea: Tarea = Tarea(tituloTxt.text.toString(), fechaSeleccionada, clasesSeleccionados, infoTxt.text.toString())
            agregarTarea(tarea)

            val fragmentManager=requireActivity().supportFragmentManager
            val segundoFragmento=ListFragment()
            val fragmentTransaction=fragmentManager.beginTransaction()
            btn_add.visibility = View.VISIBLE
            btn_lupa.visibility = View.VISIBLE
            fragmentTransaction.replace(R.id.fragment_container, segundoFragmento);
            fragmentTransaction.commit();
        }
        btn_delete.setOnClickListener {
            val fragmentManager=requireActivity().supportFragmentManager
            val segundoFragmento=ListFragment()
            val fragmentTransaction=fragmentManager.beginTransaction()
            btn_add.visibility = View.VISIBLE
            btn_lupa.visibility = View.VISIBLE
            fragmentTransaction.replace(R.id.fragment_container, segundoFragmento);
            fragmentTransaction.commit();
        }

        val layout = LinearLayout(activity)
        layout.orientation = LinearLayout.VERTICAL

        var fragmentManager: FragmentManager = this.parentFragmentManager

        if (this.isAdded()) {
            // El fragmento está asociado con un FragmentManager, es seguro acceder a él
            fragmentManager = this.parentFragmentManager
        }

        var clases: ArrayList<Clase> = ArrayList<Clase>()

        CoroutineScope(Dispatchers.Main).launch {
            val obtenerClases = async { getClases(fragmentManager) }
            clases.addAll(obtenerClases.await())
        }

        val clasesBlock = runBlocking {
            var clasesList = getClases(fragmentManager)
            clases.addAll(clasesList)
        }

        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(20, 40, 20, 20)

        for(clase in clases){
            val checkbox = CheckBox(activity)
            checkbox.setText(clase.titulo)
            checkbox.layoutParams = layoutParams
            checkbox.tag = clase.id
            layout.addView(checkbox)
            checkboxes.add(checkbox)
        }

        val dialog = AlertDialog.Builder(this.activity)
            .setTitle("Selecciona una o varias opciones")
            .setView(layout)
            .setPositiveButton("OK", null)
            .setNegativeButton("Cancelar", null)

            .create()


        seleccionarClase.setOnClickListener{
            dialog.show()
        }

        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setOnClickListener {
                clasesSeleccionados.clear()
                for(checkbox in checkboxes){
                    if(checkbox.isChecked){
                        clasesSeleccionados.add(checkbox.tag.toString())
                        clasesSelecTitulo.add(checkbox.text.toString())
                    }
                }
                CoroutineScope(Dispatchers.Main).launch {
                    mostrarClasesTarea(clasesSeleccionados)
                }

                dialog.dismiss()
            }
        }


        return myFragmentView
    }

    private suspend fun getClases(manager: FragmentManager): ArrayList<Clase>{
        val clases: ArrayList<Clase> = ArrayList<Clase>()

        val it = claseRef.get().await()
        var mapNotes :Map<String, Object> = it.getValue() as Map<String, Object>
        for ((key,value) in mapNotes) {
            var mapValue: Map<String, Object> = value as Map<String, Object>
            var titulo: String = mapValue.get("titulo").toString()
            var info: String = mapValue.get("info").toString()
            var dias: ArrayList<String> = mapValue.get("dias") as ArrayList<String>
            var horaMap: HashMap<String, Object> = mapValue.get("hora") as HashMap<String, Object>
            val seconds: Long = horaMap.get("seconds") as Long
            val nanoseconds: Long = horaMap.get("nanoseconds") as Long
            var hora = Timestamp(seconds, nanoseconds.toInt())
            var color: Long = mapValue.get("color") as Long
            val clase = Clase(titulo, info, dias, hora, color.toInt())
            clase.id = key
            clases.add(clase)
        }

        return clases
    }

    private fun mostrarDateTimePicker() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this.requireContext(),
            { _, year, monthOfYear, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, monthOfYear)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                fechaSeleccionada = Timestamp(calendar.timeInMillis / 1000, 0)
                val fechaHoraSeleccionada = SimpleDateFormat("dd/MM", Locale.getDefault()).format(calendar.time)
                btn_mostrarFecha.text = fechaHoraSeleccionada
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private suspend fun mostrarClasesTarea(clasesId: ArrayList<String>){
        var clasesTareaL: ArrayList<Clase> = ArrayList<Clase>()


        for(id in clasesId){
            val database = FirebaseDatabase.getInstance()
            val ref = database.getReference("Clases/${id}")
            val it = ref.get().await()
            var mapValue :Map<String, Object> = it.getValue() as Map<String, Object>
            var titulo: String = mapValue.get("titulo").toString()
            var info: String = mapValue.get("info").toString()
            var dias: ArrayList<String> = mapValue.get("dias") as ArrayList<String>
            var horaMap: HashMap<String, Object> =
                mapValue.get("hora") as HashMap<String, Object>
            val seconds: Long = horaMap.get("seconds") as Long
            val nanoseconds: Long = horaMap.get("nanoseconds") as Long
            var hora = Timestamp(seconds, nanoseconds.toInt())
            var color: Long = mapValue.get("color") as Long
            val clase = Clase(titulo, info, dias, hora, color.toInt())
            clasesTareaL.add(clase)
        }

        val adapter = AdapterClasesTarea(clasesTareaL, this.resources)
        recyclerClasesTarea.adapter = adapter

    }

    class AdapterClasesTarea(var clases: ArrayList<Clase>, var recusos: Resources): RecyclerView.Adapter<AgregarTarea.AdapterClasesTarea.ViewHolder>(){

        class ViewHolder(view:View): RecyclerView.ViewHolder(view){
            val textClaseTarea:TextView = view.findViewById(R.id.textClaseTarea)

            init {
                textClaseTarea
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            var inflador = LayoutInflater.from(parent.context)
            var vista = inflador.inflate(R.layout.clase_tarea_view, null)

            return ViewHolder(vista)

        }

        override fun getItemCount(): Int {
            return clases.size
        }

        override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
            val clase = clases[pos]
            holder.textClaseTarea.setText(clase.titulo)
            holder.textClaseTarea.setBackgroundColor(recusos.getColor(clase.color))
        }

    }

    fun agregarTarea(tarea: Tarea){
        val userId= tareaRef.push().key!!
        tareaRef.child(userId).setValue(tarea)
        Toast.makeText(this.context, "Se guardó la tarea correctamente", Toast.LENGTH_SHORT)
            .show()
    }




    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment agregarTarea.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AgregarTarea().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}