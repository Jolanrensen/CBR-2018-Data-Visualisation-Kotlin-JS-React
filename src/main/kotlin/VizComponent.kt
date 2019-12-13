import io.data2viz.viz.*
import org.w3c.dom.HTMLCanvasElement
import react.*
import react.dom.canvas


class VizComponent : RComponent<VizComponent.Props, VizComponent.State>() {


    interface State : RState

    interface Props : RProps {
        var runOnViz: Viz.() -> Unit
    }

    override fun RBuilder.render() {
        canvas {
            ref {
                viz(props.runOnViz).bindRendererOn(it as HTMLCanvasElement)
            }
        }

    }

}

fun RBuilder.vizComponent(handler: VizComponent.Props.() -> Unit) =
    child(VizComponent::class) { attrs(handler) }