package mx.itson.edu.keynote

data class User(var id:String?=null, var correo:String?= null, var nombre: String?=null, var password : String?=null,  var imagenPerfil: String? = ""){
    override fun toString()=correo+"\t"+nombre+ "\t"+password
}
