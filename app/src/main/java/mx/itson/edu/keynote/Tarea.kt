package mx.itson.edu.keynote

import com.google.firebase.Timestamp

data class Tarea(var titulo:String?, var fecha: Timestamp, var clases: ArrayList<String>, var info: String?){
    var id: String? = ""
    var tipo:Int = R.color.purpleButton
}

