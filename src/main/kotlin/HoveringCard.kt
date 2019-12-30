
import com.ccfraser.muirwik.components.card.MCardProps
import com.ccfraser.muirwik.components.card.mCard
import kotlinx.html.js.onMouseOutFunction
import kotlinx.html.js.onMouseOverFunction
import react.RBuilder
import react.RComponent
import react.RState
import react.dom.div
import react.setState
import styled.StyledHandler

class HoveringCard(props: Props) : RComponent<HoveringCard.Props, HoveringCard.State>(props) {

    interface Props : MCardProps {
        var runOnCard: StyledHandler<MCardProps>?
    }

    interface State : RState {
        var raised: Boolean
    }

    override fun State.init(props: Props) {
        raised = false
    }

    override fun RBuilder.render() {
        div {
            attrs {
                onMouseOverFunction = {
                    setState { raised = true }
                }
                onMouseOutFunction = {
                    setState { raised = false }
                }
            }
            mCard(raised = state.raised, handler = props.runOnCard)
        }
    }
}

fun RBuilder.hoveringCard(handler: StyledHandler<MCardProps>? = null) =
    child(HoveringCard::class) {
        attrs.runOnCard = handler
    }