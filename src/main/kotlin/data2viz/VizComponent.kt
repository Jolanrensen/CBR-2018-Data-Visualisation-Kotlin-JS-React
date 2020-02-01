package data2viz

import io.data2viz.geom.size
import io.data2viz.viz.Viz
import io.data2viz.viz.bindRendererOn
import io.data2viz.viz.viz
import kotlinx.css.*
import kotlinx.html.id
import org.w3c.dom.HTMLCanvasElement
import react.RBuilder
import react.dom.findDOMNode
import styled.css
import styled.styledCanvas
import styled.styledDiv

fun RBuilder.vizComponent(
    width: Double,
    height: Double,
    runOnHiddenViz: (Viz.(hiddenCanvas: HTMLCanvasElement) -> Unit)? = null,
    runOnViz: Viz.() -> Unit
) {
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
            attrs {
                id = "viz canvas"
            }
            ref {
                try {
                    val canvas = findDOMNode(it) as HTMLCanvasElement
                    viz {
                        size = size(width, height)
                        runOnViz()
                        bindRendererOn(canvas)
                    }
                } catch (e: Exception) {
                }
            }
        }

        if (runOnHiddenViz != null) {
            styledCanvas {
                css {
                    this.width = width.px
                    this.height = height.px
                    display = Display.none
                }
                attrs {
                    id = "hidden viz canvas"
                }
                ref {
                    try {
                        val canvas = findDOMNode(it) as HTMLCanvasElement
                        viz {
                            size = size(width, height)
                            runOnHiddenViz(canvas)
                            bindRendererOn(canvas)
                        }
                    } catch (e: Exception) {
                    }
                }
            }
        }
    }
}