import io.data2viz.color.Colors
import io.data2viz.color.Colors.Web.black
import io.data2viz.color.Colors.Web.white
import io.data2viz.geom.point
import io.data2viz.geom.size
import io.data2viz.math.deg
import io.data2viz.math.pct
import io.data2viz.viz.*
import react.dom.render
import kotlin.browser.*

const val vizSize = 500.0

external fun alert(message: Any?)

fun main() {
    println("Hello Kotlin/JS")
    window.onload = {
        val root = document.getElementById("root")
        render(root) {
            app()
        }
    }

}