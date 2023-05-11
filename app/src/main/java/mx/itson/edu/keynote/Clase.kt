package mx.itson.edu.keynote

import com.google.firebase.Timestamp

data class Clase(var titulo: String?, var info: String?, var dias: ArrayList<String>, var hora:Timestamp, var color: Int){
    var id: String? = ""
}
