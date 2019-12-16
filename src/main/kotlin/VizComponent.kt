import io.data2viz.viz.*
import org.w3c.dom.HTMLCanvasElement
import react.*
import react.dom.canvas
import styled.styledCanvas


class VizComponent : RComponent<VizComponent.Props, VizComponent.State>() {

    interface State : RState {
        var currentViz: Viz?
    }

    init {
        setState {
            currentViz = null
        }
    }

    interface Props : RProps {
        var runOnViz: Viz.() -> Unit
    }

    override fun RBuilder.render() {
        canvas {
            ref {
                //                try {
//                throw IllegalArgumentException("Test")
                if (state.currentViz == null) {
                    viz(props.runOnViz).apply {
                        bindRendererOn(it as HTMLCanvasElement)
                        this@VizComponent.setState {
                            currentViz = this@apply
                        }
                    }
                } else {
                    state.currentViz?.render()
                }
//                } catch (e: Exception) {
//                    // TODO try how to fix reloading, maybe not rebind every time
//                    console.error("Couldn't load viz", e)
//                }
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