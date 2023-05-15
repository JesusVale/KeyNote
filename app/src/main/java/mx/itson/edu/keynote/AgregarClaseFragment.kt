package mx.itson.edu.keynote

import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.view.forEach
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
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

        if(arguments != null){
            editMode = true

            val titulo = requireArguments().getString("titulo")
            val info = requireArguments().getString("info")
            val color = requireArguments().getInt("color")
            val dias = requireArguments().getStringArrayList("dias")
            val hora = requireArguments().getSerializable("hora") as java.util.Date
            tituloClase.setText(titulo)
            infoClase.setText(info)
            //Manejar Checkbox
            if (dias != null) {
                for (dia in dias){
                    when(dia){
                        "L"->{
                            checkBoxL.isChecked = true
                            checkBoxL.setBackgroundResource(R.drawable.navyblue_note_background)
                        }
                        "M"->{
                            checkBoxM.isChecked = true
                            checkBoxM.setBackgroundResource(R.drawable.navyblue_note_background)
                        }
                        "Mi"->{
                            checkBoxMi.isChecked = true
                            checkBoxMi.setBackgroundResource(R.drawable.navyblue_note_background)
                        }
                        "J"->{
                            checkBoxJ.isChecked = true
                            checkBoxJ.setBackgroundResource(R.drawable.navyblue_note_background)
                        }
                        "V"->{
                            checkBoxV.isChecked = true
                            checkBoxV.setBackgroundResource(R.drawable.navyblue_note_background)
                        }
                    }
                }
            }
            //
            when(color){
                R.color.recordRed->{
                    val radio: RadioButton = myFragmentView!!.findViewById(R.id.rRadio)
                    radio.backgroundTintList= ColorStateList.valueOf(resources.getColor(R.color.recordRedSelected))
                    radio.isChecked = true
                }
                R.color.green->{
                    val radio: RadioButton = myFragmentView!!.findViewById(R.id.gRadio)
                    radio.isChecked = true
                    radio.backgroundTintList= ColorStateList.valueOf(resources.getColor(R.color.greenSelected))
                }
                R.color.blue->{
                    val radio: RadioButton = myFragmentView!!.findViewById(R.id.bRadio)
                    radio.isChecked = true
                    radio.backgroundTintList= ColorStateList.valueOf(resources.getColor(R.color.blueSelected))
                }
                R.color.purpleButton->{
                    val radio: RadioButton = myFragmentView!!.findViewById(R.id.pRadio)
                    radio.isChecked = true
                    radio.backgroundTintList= ColorStateList.valueOf(resources.getColor(R.color.purpleSelected))
                }
                R.color.yellowButton->{
                    val radio: RadioButton = myFragmentView!!.findViewById(R.id.yRadio)
                    radio.isChecked = true
                    radio.backgroundTintList= ColorStateList.valueOf(resources.getColor(R.color.yellowSelected))
                }
            }


            fechaTxt.hour = hora.hours
            fechaTxt.minute = hora.minutes

        }


        btn_plus.setOnClickListener {
            val fragmentManager=requireActivity().supportFragmentManager
            val segundoFragmento=CalendarPlus()
            val fragmentTransaction=fragmentManager.beginTransaction()
            val now = Timestamp.now()
            val nowDate = now.toDate()
            nowDate.hours = fechaTxt.hour
            nowDate.minutes = fechaTxt.minute
            val timestamp = Timestamp(nowDate)
            //validar datos
            var titulo=tituloClase.text.toString()
            var info=infoClase.text.toString()

            if(titulo.length>=1||colorSeleccionado!=0){
                val clase: Clase = Clase(tituloClase.text.toString(), infoClase.text.toString(), diasSeleccionados, timestamp, colorSeleccionado)
                if(editMode){
                    val id = requireArguments().getString("id")
                    clase.id = id
                    actualizarClaseFirebase(clase)
                } else{
                    guardarClaseFirebase(clase)
                }

                btn_add.visibility = View.VISIBLE
                btn_lupa.visibility = View.VISIBLE
                fragmentTransaction.replace(R.id.fragment_container, segundoFragmento);
                fragmentTransaction.commit();
            }else{
                Toast.makeText(this.context, "No coloco un titulo o no selecciono un color", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        btn_delete.setOnClickListener {
            /*val fragmentManager=requireActivity().supportFragmentManager
            val segundoFragmento=CalendarPlus()
            val fragmentTransaction=fragmentManager.beginTransaction()*/
            btn_add.visibility = View.VISIBLE
            btn_lupa.visibility = View.VISIBLE
            if(editMode){
                val id: String? = requireArguments().getString("id")
                eliminarFirebase(id)
            }

           /* fragmentTransaction.replace(R.id.fragment_container, segundoFragmento);
            fragmentTransaction.commit();*/
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
                    radioButton.backgroundTintList= ColorStateList.valueOf(resources.getColor(R.color.recordRedSelected))
                    colorSeleccionado = R.color.recordRed
                }
                "g"->{
                    radioButton.backgroundTintList= ColorStateList.valueOf(resources.getColor(R.color.greenSelected))
                    colorSeleccionado = R.color.green
                }
                "b"->{
                    radioButton.backgroundTintList= ColorStateList.valueOf(resources.getColor(R.color.blueSelected))
                    colorSeleccionado = R.color.blue
                }
                "p"->{
                    radioButton.backgroundTintList= ColorStateList.valueOf(resources.getColor(R.color.purpleSelected))
                    colorSeleccionado = R.color.purpleButton
                }
                "y"->{
                    radioButton.backgroundTintList= ColorStateList.valueOf(resources.getColor(R.color.yellowSelected))
                    colorSeleccionado = R.color.yellowButton
                }

            }

            group.forEach { view ->
                if (view is RadioButton) {
                    if(!view.isChecked){
                        when(view.tag){
                            "r"->{
                                view.backgroundTintList= ColorStateList.valueOf(getResources().getColor(R.color.recordRed))

                            }
                            "g"->{
                                view.backgroundTintList= ColorStateList.valueOf(getResources().getColor(R.color.green))

                            }
                            "b"->{
                                view.backgroundTintList= ColorStateList.valueOf(getResources().getColor(R.color.blue))

                            }
                            "p"->{
                                view.backgroundTintList= ColorStateList.valueOf(getResources().getColor(R.color.purpleButton))

                            }
                            "y"->{
                                view.backgroundTintList= ColorStateList.valueOf(getResources().getColor(R.color.yellowButton))

                            }

                        }
                    }

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

    private fun actualizarClaseFirebase(clase: Clase){
        val nuevosValores = mapOf(
            "titulo" to clase.titulo,
            "info" to clase.info,
            "hora" to clase.hora,
            "dias" to clase.dias,
            "color" to clase.color
        )
        claseRef.child(clase.id!!).setValue(nuevosValores)
        Toast.makeText(this.context, "Se guardaron los cambios correctamente", Toast.LENGTH_SHORT)
            .show()
    }

    private fun eliminarFirebase(id:String?){
        claseRef.child(id!!).removeValue()
        Toast.makeText(this.context, "Se eliminó la clase correctamente", Toast.LENGTH_SHORT)
            .show()
        val fragment: Fragment = CalendarPlus()
        replaceFragment(fragment)
    }

    fun replaceFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = parentFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
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