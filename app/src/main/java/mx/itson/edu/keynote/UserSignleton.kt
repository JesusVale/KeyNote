package mx.itson.edu.keynote

object UserSingleton {
    private lateinit var user:User
    fun getUsuario(): User= user
    fun setUsuario(user: User){
        this.user=user
    }

}