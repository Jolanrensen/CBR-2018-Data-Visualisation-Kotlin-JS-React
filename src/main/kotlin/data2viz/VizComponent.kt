package data2viz

import io.data2viz.geom.size
import io.data2viz.viz.Viz
import io.data2viz.viz.bindRendererOn
import io.data2viz.viz.viz
import kotlinx.css.height
import kotlinx.css.px
import kotlinx.css.width
import org.w3c.dom.HTMLCanvasElement
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.findDOMNode
import styled.css
import styled.styledCanvas

interface VizComponentState : RState

interface VizComponentProps : RProps {
    var runOnViz: Viz.() -> Unit
    var width: Double
    var height: Double
}

class VizComponent(props: VizComponentProps) :
    RComponent<VizComponentProps, VizComponentState>(props) {

    override fun RBuilder.render() {
        styledCanvas {
            css {
                width = props.width.px
                height = props.height.px
            }
            ref {
                viz {
                    clear()
                    size = size(props.width, props.height)
                    props.runOnViz(this)
                    try {
                        bindRendererOn(findDOMNode(it) as HTMLCanvasElement)
                    } catch (e: Exception) {
                    }
                }
            }
        }
    }
}

fun RBuilder.vizComponent(handler: VizComponentProps.() -> Unit, runOnViz: Viz.() -> Unit) =
    child(VizComponent::class) {
        attrs {
            handler(this)
            this.runOnViz = runOnViz
        }
    }