package mx.itson.edu.keynote

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView

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
        btn_plus.setOnClickListener {
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
        return myFragmentView
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