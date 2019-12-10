import directives.initDirectives
import external.composition_api.VueCompositionApi
import external.font_awsome.initFa
import external.vue.Vue
import external.vue.VueRouter
import io.data2viz.color.Colors
import io.data2viz.color.Colors.Web.black
import io.data2viz.color.Colors.Web.white
import io.data2viz.geom.point
import io.data2viz.geom.size
import io.data2viz.math.deg
import io.data2viz.math.pct
import io.data2viz.viz.*
import kotlinext.js.jsObject
import kotlinx.css.*
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import wrapper.vRender
import kotlin.browser.document
import kotlin.browser.window

const val vizSize = 500.0

external fun alert(message: Any?)

fun main() {
    println("Hello Kotlin/JS")

    val test = jQuery(".clickable")[0]!!
    test.onclick = {
        alert("hoi")
    }

    val viz = viz {
        size = size(vizSize * 2, vizSize)

        (0 until 360 step 30).forEach {
            val angle = it.deg
            val position = point(250 + angle.cos * 100, 125 + angle.sin * 100)
            val color = Colors.hsl(angle, 100.pct, 50.pct)

            circle {
                // draw a circle with "pure-color"
                fill = color
                radius = 25.0
                x = position.x
                y = position.y
            }
            circle {
                // draw a circle with the desaturated color
                fill = color.desaturate(10.0)
                radius = 25.0
                x = position.x + 270
                y = position.y
            }
            text {
                // indicate the perceived lightness of the color
                x = position.x
                y = position.y
                textColor = if (color.luminance() > 50.pct) black else white
                textContent = "${(color.luminance().value * 100).toInt()}%"
                textAlign = textAlign(TextHAlign.MIDDLE, TextVAlign.MIDDLE)
            }
        }
    }
    viz.bindRendererOn("viz")           //<- select a canvas with this id to install the viz

    initFa()
    initDirectives()

    Vue.use(VueCompositionApi)
    Vue.use(VueRouter)

    Vue.config.productionTip = false

    window.onload = {
        Vue(jsObject {
            router = vRouter
            render = vRender {
                +App()
            }
        }).`$mount`(document.getElementById("root")!!)
    }





}