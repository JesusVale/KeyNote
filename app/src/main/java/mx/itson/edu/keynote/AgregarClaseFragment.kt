package mx.itson.edu.keynote

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import com.google.firebase.Timestamp
import com.google.firebase.database.FirebaseDatabase
import java.time.LocalDateTime

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AgregarClaseFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AgregarClaseFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val claseRef= FirebaseDatabase.getInstance().getReference("Clases")
    private var diasSeleccionados: ArrayList<String> = ArrayList()
    private var colorSeleccionado: Int = 0
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
        val myFragmentView: View? = inflater.inflate(R.layout.fragment_agregar_clase, container, false)
        val btn_plus: ImageButton = myFragmentView!!.findViewById(R.id.btnPlus)
        val btn_delete: ImageButton = myFragmentView!!.findViewById(R.id.btnX)
        val btn_add: ImageView = requireActivity().findViewById(R.id.addIcon)
        val btn_lupa: ImageView = requireActivity().findViewById(R.id.search_icon)
        val tituloClase: EditText =myFragmentView!!.findViewById(R.id.tituloTxt)
        val checkBoxL: CheckBox = myFragmentView!!.findViewById(R.id.lunes)
        val checkBoxM: CheckBox = myFragmentView!!.findViewById(R.id.martes)
        val checkBoxMi: CheckBox = myFragmentView!!.findViewById(R.id.miercoles)
        val checkBoxJ: CheckBox = myFragmentView!!.findViewById(R.id.jueves)
        val checkBoxV: CheckBox = myFragmentView!!.findViewById(R.id.viernes)
        val fechaTxt: TimePicker = myFragmentView!!.findViewById(R.id.fechaTxt)
        val colores: RadioGroup = myFragmentView!!.findViewById(R.id.colores)
        val infoClase: EditText = myFragmentView!!.findViewById(R.id.infoTxt)
        btn_plus.setOnClickListener {
            val fragmentManager=requireActivity().supportFragmentManager
            val segundoFragmento=CalendarPlus()
            val fragmentTransaction=fragmentManager.beginTransaction()
            val now = Timestamp.now()
            val nowDate = now.toDate()
            nowDate.hours = fechaTxt.hour
            val timestamp = Timestamp(nowDate)

            val clase: Clase = Clase(tituloClase.text.toString(), infoClase.text.toString(), diasSeleccionados, timestamp, colorSeleccionado)
            guardarClaseFirebase(clase)

            btn_add.visibility = View.VISIBLE
            btn_lupa.visibility = View.VISIBLE
            fragmentTransaction.replace(R.id.fragment_container, segundoFragmento);
            fragmentTransaction.commit();
        }
        btn_delete.setOnClickListener {
            val fragmentManager=requireActivity().supportFragmentManager
            val segundoFragmento=CalendarPlus()
            val fragmentTransaction=fragmentManager.beginTransaction()
            btn_add.visibility = View.VISIBLE
            btn_lupa.visibility = View.VISIBLE
            fragmentTransaction.replace(R.id.fragment_container, segundoFragmento);
            fragmentTransaction.commit();
        }

        checkBoxL.setOnCheckedChangeListener{ _, isChecked ->
            if (isChecked) {
                diasSeleccionados.add("L")
                checkBoxL.setBackgroundResource(R.drawable.navyblue_note_background)
            } else {
                diasSeleccionados.remove("L")
                checkBoxL.setBackgroundResource(R.drawable.rounded_textfield)
            }
        }
        checkBoxM.setOnCheckedChangeListener{ _, isChecked ->
            if (isChecked) {
                diasSeleccionados.add("M")
                checkBoxM.setBackgroundResource(R.drawable.navyblue_note_background)
            } else {
                diasSeleccionados.remove("M")
                checkBoxM.setBackgroundResource(R.drawable.rounded_textfield)
            }
        }
        checkBoxMi.setOnCheckedChangeListener{ _, isChecked ->
            if (isChecked) {
                diasSeleccionados.add("Mi")
                checkBoxMi.setBackgroundResource(R.drawable.navyblue_note_background)
            } else {
                diasSeleccionados.remove("Mi")
                checkBoxMi.setBackgroundResource(R.drawable.rounded_textfield)
            }
        }
        checkBoxJ.setOnCheckedChangeListener{ _, isChecked ->
            if (isChecked) {
                diasSeleccionados.add("J")
                checkBoxJ.setBackgroundResource(R.drawable.navyblue_note_background)
            } else {
                diasSeleccionados.remove("J")
                checkBoxJ.setBackgroundResource(R.drawable.rounded_textfield)
            }
        }
        checkBoxV.setOnCheckedChangeListener{ _, isChecked ->
            if (isChecked) {
                diasSeleccionados.add("V")
                checkBoxV.setBackgroundResource(R.drawable.navyblue_note_background)
            } else {
                diasSeleccionados.remove("V")
                checkBoxV.setBackgroundResource(R.drawable.rounded_textfield)
            }
        }
        colores.setOnCheckedChangeListener { group, checkedId ->
            // Obtener la opción seleccionada
            val radioButton: RadioButton = myFragmentView.findViewById(checkedId)
            when(radioButton.tag){
                "r"->{
                    colorSeleccionado = R.color.recordRed
                }
                "g"->{
                    colorSeleccionado = R.color.green
                }
                "b"->{
                    colorSeleccionado = R.color.blue
                }
                "p"->{
                    colorSeleccionado = R.color.purpleButton
                }
                "y"->{
                    colorSeleccionado = R.color.yellowButton
                }
            }

        }
        return myFragmentView
    }


    private fun guardarClaseFirebase(clase: Clase){
        val userId= claseRef.push().key!!
        claseRef.child(userId).setValue(clase)
        Toast.makeText(this.context, "Se guardó la clase correctamente", Toast.LENGTH_SHORT)
            .show()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AgregarClaseFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AgregarClaseFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}