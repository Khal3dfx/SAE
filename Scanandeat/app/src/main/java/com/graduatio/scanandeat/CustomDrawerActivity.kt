package com.graduatio.scanandeat

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.graduatio.scanandeat.Admin.Admins.AdminListActivity
import com.graduatio.scanandeat.Admin.Diseases.DiseasesListActivity
import com.graduatio.scanandeat.Admin.Foods.FoodListActivity
import com.graduatio.scanandeat.Admin.Ingredients.IngredientListActivity
import com.graduatio.scanandeat.Admin.Orders.OrdersActivity
import com.graduatio.scanandeat.User.Cart.CartActivity
import com.graduatio.scanandeat.User.Deseas.MyDeseasActivity
import com.graduatio.scanandeat.User.Orders.MyOrderActivityActivity
import com.graduatio.scanandeat.User.UserActivity
import com.graduatio.scanandeat.User.UserMainActivity
import java.util.Locale

abstract class CustomDrawerActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener {
    protected var navigationView: NavigationView? = null
    open var session: Session? = null
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.navigration_activity)
        val conf: Configuration = getResources().getConfiguration()
        conf.setLayoutDirection(Locale("en"))
        getResources().updateConfiguration(conf, getResources().getDisplayMetrics())

        session = Session(this)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        getSupportActionBar()?.setTitle(customTitle)
        val frameLayout: FrameLayout = findViewById<View>(R.id.main_content) as FrameLayout
        getLayoutInflater().inflate(layoutId, frameLayout)
        val drawer: DrawerLayout = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.app_name, R.string.app_name
        )
        drawer.setDrawerListener(toggle)
        toggle.syncState()
        navigationView = findViewById<View>(R.id.nav_view) as NavigationView?
        navigationView?.setNavigationItemSelectedListener(this)
        if (session?.getString("usertype").equals("admin")) {
            navigationView?.inflateMenu(R.menu.admin)
        }else if (session?.getString("usertype").equals("user")) {
            navigationView?.inflateMenu(R.menu.user)
        }
    }

    abstract val layoutId: Int
    abstract val customTitle: String?

    override fun onBackPressed() {
        val drawer: DrawerLayout = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (session?.getString("usertype").equals("admin")) {
            val id = item.itemId
            if (id == R.id.profile) {
                launchActivity(UpdateProfileActivity::class.java)
            } else if (id == R.id.admins) {
                launchActivity(AdminListActivity::class.java)
            } else if (id == R.id.diseases) {
                launchActivity(DiseasesListActivity::class.java)
            }  else if (id == R.id.foods) {
                launchActivity(FoodListActivity::class.java)
            } else if (id == R.id.ingredients) {
                launchActivity(IngredientListActivity::class.java)
            } else if (id == R.id.orders) {
                launchActivity(OrdersActivity::class.java)
            }  else if (id == R.id.password) {

                launchActivity(UpdatePasswordActivity::class.java)

            } else if (id == R.id.logout) {
                session?.put("loggedin",false)
                session?.put("id","")
                session?.put("phone","")
                session?.put("usertype","")

                startActivity(
                    Intent(
                        getBaseContext(),
                        MainActivity::class.java
                    ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
                finish()
            }
            val drawer: DrawerLayout = findViewById<View>(R.id.drawer_layout) as DrawerLayout
            drawer.closeDrawer(GravityCompat.START)
            return true
        }
        if (session?.getString("usertype").equals("user")) {
            val id = item.itemId
            if (id == R.id.profile) {
                launchActivity(UpdateProfileActivity::class.java)
            } else if (id == R.id.mydeseas) {
                launchActivity(MyDeseasActivity::class.java)
            }  else if (id == R.id.foods) {
                launchActivity(UserMainActivity::class.java)
            }   else if (id == R.id.home) {
                launchActivity(UserActivity::class.java)
            }   else if (id == R.id.myCart) {
                launchActivity(CartActivity::class.java)
            }   else if (id == R.id.myOrder) {
                launchActivity(MyOrderActivityActivity::class.java)
            }  else if (id == R.id.password) {

                launchActivity(UpdatePasswordActivity::class.java)

            } else if (id == R.id.logout) {
                session?.put("loggedin",false)
                session?.put("id","")
                session?.put("phone","")
                session?.put("usertype","")
                session?.put("deseas","")

                startActivity(
                    Intent(
                        getBaseContext(),
                        MainActivity::class.java
                    ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
                finish()
            }
            val drawer: DrawerLayout = findViewById<View>(R.id.drawer_layout) as DrawerLayout
            drawer.closeDrawer(GravityCompat.START)
            return true
        }
        return false
    }

    private fun launchActivity(_class: Class<*>) {
        //Avoid launch the same activity
        if (this.javaClass != _class) {
            startActivity(makePageIntent(_class))
            overridePendingTransition(0, 0)
            //  finish();
        }
    }

    private fun launchActivity(_class: Class<*>, extra: String) {
        //Avoid launch the same activity
//        if (this.getClass() != _class) {
        startActivity(makePageIntent(_class).putExtra("type", extra))
        overridePendingTransition(0, 0)
        // finish();
//        }
    }

    fun makePageIntent(_class: Class<*>?): Intent {
        val intent = Intent(this, _class)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        return intent
    }
}