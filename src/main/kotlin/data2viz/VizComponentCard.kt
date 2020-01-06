package data2viz

import com.ccfraser.muirwik.components.card.MCardProps
import com.ccfraser.muirwik.components.card.mCardContent
import hoveringCard
import io.data2viz.viz.Viz
import kotlinx.css.Align
import kotlinx.css.Display
import kotlinx.css.JustifyContent
import kotlinx.css.alignItems
import kotlinx.css.display
import kotlinx.css.justifyContent
import kotlinx.css.margin
import kotlinx.css.mm
import kotlinx.css.padding
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import styled.StyledHandler
import styled.css
import styled.styledDiv

interface VizComponentCardProps : RProps {
    var runOnCard: StyledHandler<MCardProps>
    var runOnViz: Viz.() -> Unit
    var width: Double
    var height: Double
}

interface VizComponentCardState : RState

class VizComponentCard(props: VizComponentCardProps) : RComponent<VizComponentCardProps, VizComponentCardState>(props) {

    override fun RBuilder.render() {
        styledDiv {
            css {
                margin(5.mm)
            }
            hoveringCard {
                css {
                    padding(5.mm)
                }
                props.runOnCard(this)
                mCardContent {
                    css {
                        display = Display.flex
                        justifyContent = JustifyContent.center
                        alignItems = Align.center
                    }
                    vizComponent({
                        width = props.width
                        height = props.height
                    }, props.runOnViz)
                }
            }
        }
    }
}

fun RBuilder.vizComponentCard(width: Double, height: Double, runOnCard: StyledHandler<MCardProps> = {}, runOnViz: Viz.() -> Unit) =
    child(VizComponentCard::class) {
        attrs {
            this.width = width
            this.height = height
            this.runOnCard = runOnCard
            this.runOnViz = runOnViz
        }
    }