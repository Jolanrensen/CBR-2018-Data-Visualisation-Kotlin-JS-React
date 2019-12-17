import io.data2viz.viz.*
import kotlinx.html.CANVAS
import org.w3c.dom.HTMLCanvasElement
import react.*
import react.dom.canvas
import react.dom.findDOMNode
import styled.styledCanvas


class VizComponent(props: Props) : RComponent<VizComponent.Props, VizComponent.State>(props) {

    interface State : RState

    interface Props : RProps {
        var runOnViz: Viz.() -> Unit
    }

    override fun RBuilder.render() {
        canvas {
            ref {
                viz(props.runOnViz).apply {
                    try {
                        bindRendererOn(findDOMNode(it) as HTMLCanvasElement)
                    } catch (e: Exception) {}
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