import io.data2viz.viz.*
import org.w3c.dom.HTMLCanvasElement
import react.*
import react.dom.canvas
import styled.styledCanvas


class VizComponent : RComponent<VizComponent.Props, VizComponent.State>() {

    interface State : RState

    interface Props : RProps {
        var runOnViz: Viz.() -> Unit
    }

    override fun RBuilder.render() {
        canvas {
            ref {
                try {
                    viz(props.runOnViz).bindRendererOn(it as HTMLCanvasElement)
                } catch (e: Exception) {
                    // TODO try how to fix reloading, maybe not rebind every time
                    console.error("Couldn't load viz", e)
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