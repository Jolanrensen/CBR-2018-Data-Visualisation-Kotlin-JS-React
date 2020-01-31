
import com.ccfraser.muirwik.components.card.MCardProps
import com.ccfraser.muirwik.components.card.mCard
import delegates.ReactPropAndStateDelegates.stateDelegateOf
import kotlinx.html.js.onMouseOutFunction
import kotlinx.html.js.onMouseOverFunction
import org.w3c.dom.events.Event
import react.RBuilder
import react.RComponent
import react.RState
import react.dom.div
import react.setState
import styled.StyledHandler

interface HoveringCardProps : MCardProps {
    var runOnCard: StyledHandler<MCardProps>?
}

interface HoveringCardState : RState {
    var raised: Boolean
}

class HoveringCard(prps: HoveringCardProps) : RComponent<HoveringCardProps, HoveringCardState>(prps) {

    override fun HoveringCardState.init(prps: HoveringCardProps) {
        raised = false
    }
    var raised by stateDelegateOf(HoveringCardState::raised)

    private val onMouseOver: (Event?) -> Unit = {
        raised = true
    }

    private val onMouseOut: (Event?) -> Unit = {
        raised = false
    }

    override fun RBuilder.render() {
        div {
            attrs {
                onMouseOverFunction = onMouseOver
                onMouseOutFunction = onMouseOut
            }
            mCard(raised = raised, handler = props.runOnCard)
        }
    }
}

fun RBuilder.hoveringCard(handler: StyledHandler<MCardProps>? = null) =
    child(HoveringCard::class) {
        attrs.runOnCard = handler
    }