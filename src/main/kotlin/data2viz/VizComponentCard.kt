package data2viz

import com.ccfraser.muirwik.components.card.MCardProps
import com.ccfraser.muirwik.components.card.mCardContent
import hoveringCard
import io.data2viz.viz.Viz
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
                    //  todo kunnen we nog stylen of centeren ofzo
                    vizComponent(props.runOnViz)
                }
            }
        }
    }
}

fun RBuilder.vizComponentCard(runOnCard: StyledHandler<MCardProps> = {}, runOnViz: Viz.() -> Unit) =
    child(VizComponentCard::class) {
        attrs {
            this.runOnCard = runOnCard
            this.runOnViz = runOnViz
        }
    }