package mx.itson.edu.keynote

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var bottomNavigationView: BottomNavigationView
    lateinit var drawerLayout: DrawerLayout
    lateinit var navigationView: NavigationView
    lateinit var toolbar: Toolbar
    lateinit var toolbarTitle: TextView
    lateinit var searchIcon: ImageView
    lateinit var gradientCalendarToolbar: Drawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicialización de las vistas
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.nav_view)
        toolbar = findViewById(R.id.toolbar)
        toolbarTitle = findViewById(R.id.toolbar_title)
        searchIcon = findViewById(R.id.search_icon)
        gradientCalendarToolbar = resources.getDrawable(R.drawable.gradient_toolbar)

        setSupportActionBar(toolbar);

        val toggle:ActionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null){
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, HomeFragment()).commit();
            //navigationView.setCheckedItem(R.id.nav_home)
        }
        toolbarTitle.setText("Inicio")
        replaceFragment(HomeFragment());

        searchIcon.setOnClickListener {
            var intent: Intent = Intent(this, Buscador::class.java)
            startActivity(intent)
        }

        bottomNavigationView.setBackground(null);
        bottomNavigationView.setOnItemSelectedListener {item ->
            when (item.itemId) {
                R.id.bottom_home ->{
                    toolbarTitle.setText("Inicio")
                    originalToolbarState()
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.bottom_calendar_plus ->{
                    toolbarTitle.setText("Horario")
                    originalToolbarState()
                    replaceFragment(CalendarPlus())
                    true
                }
                R.id.bottom_calendar ->{
                    toolbarTitle.setText("Calendario")
                    calendarToolbarState()
                    replaceFragment(Calendar())
                    true
                }
                R.id.bottom_list ->{
                    toolbarTitle.setText("Tareas")
                    originalToolbarState()
                    replaceFragment(ListFragment())
                    true
                }
                else -> false
            }
        }

        navigationView.setNavigationItemSelectedListener(this)

}

    fun replaceFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = supportFragmentManager;
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.nav_notas ->{
                toolbarTitle.setText("Notas")
                originalToolbarState()
                replaceFragment(Notas())
                drawerLayout.closeDrawer(GravityCompat.START)
                true
            }
            R.id.nav_ocultas ->{
                toolbarTitle.setText("Ocultas")
                originalToolbarState()
                replaceFragment(OcultasFragment())
                drawerLayout.closeDrawer(GravityCompat.START)
                true
            }
            R.id.nav_eliminadas->{
                toolbarTitle.setText("Eliminadas")
                originalToolbarState()
                replaceFragment(EliminadasFragment())
                drawerLayout.closeDrawer(GravityCompat.START)
                true
            }
            else -> false
        }
    }

    fun originalToolbarState(){
        toolbar.setBackgroundColor(Color.WHITE)
        searchIcon.visibility = View.VISIBLE
        toolbarTitle.setTextColor(Color.DKGRAY)
        toolbar.setOutlineProvider(null);
    }

    fun calendarToolbarState(){
        toolbar.setBackground(gradientCalendarToolbar)
        searchIcon.visibility = View.GONE
        toolbarTitle.setTextColor(Color.WHITE)
        toolbar.setOutlineProvider(null);
    }

}