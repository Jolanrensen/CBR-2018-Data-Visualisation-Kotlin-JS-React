package data2viz
import io.data2viz.viz.Viz
import io.data2viz.viz.bindRendererOn
import io.data2viz.viz.viz
import org.w3c.dom.HTMLCanvasElement
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.canvas
import react.dom.findDOMNode

class VizComponent(props: Props) : RComponent<VizComponent.Props, VizComponent.State>(props) {

    interface State : RState

    interface Props : RProps {
        var runOnViz: Viz.() -> Unit
    }

    override fun RBuilder.render() {
        canvas {
            ref {
                viz {
                    clear()
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

fun RBuilder.vizComponent(runOnViz: Viz.() -> Unit) =
    child(VizComponent::class) {
        attrs {
            this.runOnViz = runOnViz
        }
    }