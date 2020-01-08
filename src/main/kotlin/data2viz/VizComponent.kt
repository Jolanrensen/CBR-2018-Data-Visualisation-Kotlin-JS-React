package data2viz

import io.data2viz.geom.size
import io.data2viz.viz.Viz
import io.data2viz.viz.bindRendererOn
import io.data2viz.viz.viz
import kotlinx.css.Align
import kotlinx.css.Display
import kotlinx.css.JustifyContent
import kotlinx.css.alignItems
import kotlinx.css.display
import kotlinx.css.height
import kotlinx.css.justifyContent
import kotlinx.css.px
import kotlinx.css.width
import org.w3c.dom.HTMLCanvasElement
import react.RBuilder
import react.dom.findDOMNode
import styled.css
import styled.styledCanvas
import styled.styledDiv

fun RBuilder.vizComponent(width: Double, height: Double, runOnViz: Viz.() -> Unit) {
    styledDiv {
        css {
            display = Display.flex
            justifyContent = JustifyContent.center
            alignItems = Align.center
        }
        styledCanvas {
            css {
                this.width = width.px
                this.height = height.px
            }
            ref {
                try {
                    val canvas = findDOMNode(it) as HTMLCanvasElement
                    viz {
                        size = size(width, height)
                        runOnViz(this)
                        bindRendererOn(canvas)
                    }
                } catch (e: Exception) {}
            }
        }
    }
}