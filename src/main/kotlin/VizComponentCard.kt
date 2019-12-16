import com.ccfraser.muirwik.components.card.MCardProps
import com.ccfraser.muirwik.components.card.mCard
import com.ccfraser.muirwik.components.card.mCardContent
import com.ccfraser.muirwik.components.card.mCardHeader
import com.ccfraser.muirwik.components.mTypography
import com.ccfraser.muirwik.components.spacingUnits
import io.data2viz.viz.Viz
import kotlinx.css.*
import kotlinx.html.js.onMouseOutFunction
import kotlinx.html.js.onMouseOverFunction
import react.*
import react.dom.div
import styled.StyledHandler
import styled.css
import styled.styledDiv

class VizComponentCard : RComponent<VizComponentCard.Props, VizComponentCard.State>() {

    interface Props : RProps {
        var runOnCard: StyledHandler<MCardProps>
        var runOnViz: Viz.() -> Unit
    }

    interface State : RState {
        var raised: Boolean
    }

    init {
        setState {
            raised = false
        }
    }

    override fun RBuilder.render() {
        styledDiv {
            css {
                margin(all = 5.mm)
            }
            attrs {
                onMouseOverFunction = {
                    setState {
                        raised = true
                    }

                }
                onMouseOutFunction = {
                    setState {
                        raised = false
                    }
                }
            } 
            mCard {
                css {
//                    display = Display.flex
                    padding(5.mm)
                }
                attrs {
                    elevation = if (state.raised) 3 else 1
                }
                props.runOnCard(this)

                mCardContent {
//                    styledDiv { todo kunnen we nog stylen of centeren ofzo
//                        css {
//                            display = Display.flex
//                            justifyContent = JustifyContent.center
//                            alignItems = Align.center
//                        }
                        vizComponent(props.runOnViz)
//                    }
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