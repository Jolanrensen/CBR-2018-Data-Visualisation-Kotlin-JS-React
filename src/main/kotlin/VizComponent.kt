import io.data2viz.color.Colors
import io.data2viz.color.Colors.Web.black
import io.data2viz.color.Colors.Web.white
import io.data2viz.geom.*
import io.data2viz.math.*
import io.data2viz.viz.*
import kotlinx.html.HtmlBlockInlineTag
import kotlinx.html.HtmlContent
import org.w3c.dom.Element
import org.w3c.dom.HTMLCanvasElement
import react.*
import react.dom.canvas
import react.dom.div
import react.dom.findDOMNode
import react.dom.p

class VizComponent : RComponent<VizComponent.Props, VizComponent.State>() {


    interface State : RState {
    }

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