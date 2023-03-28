package mx.itson.edu.keynote

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView);
        val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout);
        val navigationView: NavigationView = findViewById(R.id.nav_view);
        val toolbar:Toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        val toggle:ActionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null){
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, HomeFragment()).commit();
            //navigationView.setCheckedItem(R.id.nav_home)
        }
        toolbar.setTitle("Inicio")
        replaceFragment(HomeFragment());

        bottomNavigationView.setBackground(null);
        bottomNavigationView.setOnItemSelectedListener {item ->
            when (item.itemId) {
                R.id.bottom_home ->{
                    toolbar.setTitle("Inicio")
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.bottom_calendar_plus ->{
                    toolbar.setTitle("Horario")
                    replaceFragment(CalendarPlus())
                    print("lol")
                    true
                }
                R.id.bottom_calendar ->{
                    toolbar.setTitle("Calendario")
                    replaceFragment(Calendar())
                    true
                }
                R.id.bottom_list ->{
                    toolbar.setTitle("Tareas")
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

        val toolbar:Toolbar = findViewById(R.id.toolbar);
        val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout);
        return when (item.itemId) {
            R.id.nav_notas ->{
                toolbar.setTitle("Notas")
                replaceFragment(Notas())
                drawerLayout.closeDrawer(GravityCompat.START)
                print("lol")
                true
            }
            R.id.nav_ocultas ->{
                toolbar.setTitle("Ocultas")
                replaceFragment(OcultasFragment())
                drawerLayout.closeDrawer(GravityCompat.START)
                true
            }
            R.id.nav_eliminadas->{
                toolbar.setTitle("Eliminadas")
                replaceFragment(EliminadasFragment())
                drawerLayout.closeDrawer(GravityCompat.START)
                true
            }
            else -> false
        }
    }

}