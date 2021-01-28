package com.taeim.latexmathsample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import com.taeim.mathdisplay.AndroidMathView
import com.taeim.mathdisplay.AndroidMathView.MTMathViewMode
import com.taeim.mathdisplay.MTFontManager
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.widget.TextView
import com.taeim.latexmathsample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    var menufont: MenuItem? = null
    var menumode: MenuItem? = null
    var menusize: MenuItem? = null

    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        createEquations()

        setAndroidMathView()
    }

    private fun setAndroidMathView() {

        binding.mathView2.latex = "\\frak Q(\\lambda,\\hat{\\lambda}) =  -\\frac{1}{2} \\mathbb P(O \\mid \\lambda ) \\sum_s \\sum_m \\sum_t \\gamma_m^{(s)} (t) +  \\quad \\left( \\log(2 \\pi ) + \\log \\left| \\cal C_m^{(s)} \\right| +  \\left( o_t - \\hat{\\mu}_m^{(s)} \\right) ^T \\cal C_m^{(s)-1} \\right)"
        binding.mathView2.setColorResource(R.color.colorPrimary)
        binding.mathView2.setFontSize(33.5f)

        binding.mathView3.latex = "\\int_{-\\infty}^\\infty \\! e^{-x^2} dx = \\sqrt{\\pi}"
        binding.mathView3.setColorString("#5312f3")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        menufont = menu.findItem(R.id.menufont)
        menumode = menu.findItem(R.id.menumode)
        menusize = menu.findItem(R.id.menusize)
        return true
    }

    var sampleEquations: MutableList<AndroidMathView> = mutableListOf()
    var defaultFontSize = 20.0f  // Starting fontsize dp pixels

    fun createEquations() {
        val layoutPadHoriz = 24
        val layoutPadVert = 42

        // Some padding around the equations. Cosmetic
        val layoutParams = LinearLayout.LayoutParams(0, 0)
        layoutParams.setMargins(layoutPadHoriz, layoutPadVert, layoutPadHoriz, layoutPadVert)
        /*
             We read a plain text file with LaTeX strings and comments
             Each LaTeX string is placed in a separate MTMathViews.
             Comments are placed in TextViews.
             All are placed into a view to scroll vertically and horizontally.
         */
        val inputStream = getResources().openRawResource(R.raw.samples)
        val lineList = mutableListOf<String>()

        inputStream.bufferedReader().useLines { lines -> lines.forEach { lineList.add(it) } }

        lineList.forEach {
            if (it.isNotBlank()) {
                if (it[0] == '#') {
                    val tv = TextView(this)
                    tv.text = it.trim()
                    tv.setTextColor(Color.DKGRAY)
                    println("textSize ${tv.textSize}")
                    binding.mainLayout.addView(tv)
                } else {
                    val mathView = AndroidMathView(this)
                    mathView.textSize = AndroidMathView.convertDpToPixel(defaultFontSize)
                    mathView.latex = it
                    sampleEquations.add(mathView)
                    binding.mainLayout.addView(mathView, layoutParams)
                }
            }
        }
    }

    fun applyfont(fontname: String) {
        for (eq in sampleEquations) {
            eq.font = MTFontManager.fontWithName(fontname, eq.textSize)
        }
    }

    fun applyfontsize(fontsize: Float) {
        val pixelfontsize = AndroidMathView.convertDpToPixel(fontsize)
        for (eq in sampleEquations) {
            eq.font = MTFontManager.fontWithName(eq.font!!.name, pixelfontsize)
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
        // Font menu
            R.id.fontdefault -> {
                applyfont("latinmodern-math")
                fontmenucheck(R.id.fontdefault)
                return true
            }
            R.id.fonttermes -> {
                applyfont("texgyretermes-math")
                fontmenucheck(R.id.fonttermes)
                return true
            }
            R.id.fontxits -> {
                applyfont("xits-math")
                fontmenucheck(R.id.fontxits)
                return true
            }
        // Size Menu
            R.id.f12 -> {
                applyfontsize(12.0f)
                sizemenucheck(R.id.f12)
                return true
            }
            R.id.f16 -> {
                applyfontsize(16.0f)
                sizemenucheck(R.id.f16)
                return true
            }
            R.id.f20 -> {
                applyfontsize(20.0f)
                sizemenucheck(R.id.f20)
                return true
            }
            R.id.f40 -> {
                applyfontsize(40.0f)
                sizemenucheck(R.id.f40)
                return true
            }
        // Mode Menu
            R.id.modedisplay -> {
                for (eq in sampleEquations) {
                    eq.labelMode = MTMathViewMode.KMTMathViewModeDisplay
                }
                modemenucheck(R.id.modedisplay)
                return true
            }
            R.id.modetext -> {
                for (eq in sampleEquations) {
                    eq.labelMode = MTMathViewMode.KMTMathViewModeText
                }
                modemenucheck(R.id.modetext)
                return true
            }
        // Good for profiling
            R.id.modereload -> {
                binding.mainLayout.removeAllViewsInLayout()
                sampleEquations.clear()
                binding.mainLayout.invalidate()
                // Nice to see the screen show blank before adding back equations
                binding.mainLayout.postDelayed({ createEquations() }, 10)
                sizemenucheck(R.id.f20)
                modemenucheck(R.id.modedisplay)
                fontmenucheck(R.id.fontdefault)
                return true
            }


        // Color Menu
            R.id.colorblack -> {
                for (eq in sampleEquations) {
                    eq.fontColor = Color.BLACK
                }
                return true
            }
            R.id.colorpurple -> {
                for (eq in sampleEquations) {
                    eq.fontColor = Color.MAGENTA
                }
                return true
            }


            else -> super.onOptionsItemSelected(item)
        }
    }

    // Utility functions to make sure menu checked state is correct
    fun menucheck(menu: MenuItem, thisone: Int) {
        val m = menu.subMenu
        for (i in 0 until m.size()) {
            val mi = m.getItem(i)
            if (mi.itemId == thisone) {
                mi.setChecked(true)
            } else {
                mi.setChecked(false)
            }
        }
    }

    fun fontmenucheck(thisone: Int) {
        if (menufont != null) {
            menucheck(menufont!!, thisone)
        }
    }

    fun modemenucheck(thisone: Int) {
        if (menumode != null) {
            menucheck(menumode!!, thisone)
        }
    }

    fun sizemenucheck(thisone: Int) {
        if (menusize != null) {
            menucheck(menusize!!, thisone)
        }
    }


}
