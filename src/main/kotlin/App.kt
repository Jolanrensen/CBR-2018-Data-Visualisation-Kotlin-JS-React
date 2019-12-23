import com.ccfraser.muirwik.components.*
import com.ccfraser.muirwik.components.button.*
import com.ccfraser.muirwik.components.card.mCard
import com.ccfraser.muirwik.components.card.mCardActions
import com.ccfraser.muirwik.components.card.mCardContent
import com.ccfraser.muirwik.components.card.mCardHeader
import com.ccfraser.muirwik.components.form.MFormControlVariant
import com.ccfraser.muirwik.components.form.mFormControl
import com.ccfraser.muirwik.components.gridlist.mGridList
import com.ccfraser.muirwik.components.input.MInputAdornmentPosition
import com.ccfraser.muirwik.components.input.mFilledInput
import com.ccfraser.muirwik.components.input.mInputAdornment
import com.ccfraser.muirwik.components.input.mInputLabel
import data.Data
import data.Opleider
import io.data2viz.color.Colors
import io.data2viz.geom.point
import io.data2viz.geom.size
import io.data2viz.math.deg
import io.data2viz.math.pct
import io.data2viz.viz.TextHAlign
import io.data2viz.viz.TextVAlign
import io.data2viz.viz.textAlign
import kotlinx.coroutines.*
import kotlinx.css.*
import kotlinx.html.InputType
import kotlinx.html.js.onClickFunction
import react.*
import react.dom.*
import styled.css
import styled.styledCol
import styled.styledDiv
import styled.styledP
import kotlin.browser.window


class App(props: Props) : RComponent<App.Props, App.State>(props) {

    val vizSize = 300.0

    interface State : RState {
        var welcomeText: String
        var circleColor: io.data2viz.color.Color
        var setRijschoolFilter: (String) -> Unit
        var refreshOpleiders: () -> Unit // OpleidersList
    }

    override fun State.init(props: Props) {
        welcomeText = "Hello world!"
        circleColor = Colors.rgb(255, 0, 0)
        setRijschoolFilter = {}
        refreshOpleiders = {}
    }


    interface Props : RProps {

    }

    override fun RBuilder.render() {
        styledDiv {
            css {
                padding(vertical = 16.px)
                backgroundColor = Color.green
            }

            +(state.welcomeText)

            attrs.onClickFunction = {
                setState {
                    welcomeText = "Something else"
                }
            }
        }

        styledP {
            css {
                color = Color.blue
            }
            +"Hello from React!"
            attrs {
                onClickFunction = {
                    alert("Clickedie clackedie")
                }
            }
        }

        mButton("Change color",
            color = MColor.primary,
            size = MButtonSize.medium,
            onClick = {
                setState {
                    circleColor = if (state.circleColor == Colors.rgb(255, 0, 0))
                        Colors.rgb(0, 255, 0)
                    else Colors.rgb(255, 0, 0)
                }
            })

        vizComponentCard(
            runOnCard = {
                css {
                    width = LinearDimension(if (window.screen.availWidth > 720) "60%" else "90%")
                }
                mCardHeader(
                    title = "Mooie grafiek",
                    subHeader = "Nou kweenie hoor",
                    avatar = mAvatar(addAsChild = false) {
                        +"gg"
                    }
                )
            }
        ) {
            clear()
            size = size(800, 250)

            (0 until 360 step 30).forEach {
                val angle = it.deg
                val position = point(250 + angle.cos * 100, 125 + angle.sin * 100)
                val color = state.circleColor

                circle {
                    // draw a circle with "pure-color"
                    fill = color
                    radius = 25.0
                    x = position.x
                    y = position.y
                }
                circle {
                    // draw a circle with the desaturated color
                    fill = color.desaturate(10.0)
                    radius = 25.0
                    x = position.x + 270
                    y = position.y
                }
                text {
                    // indicate the perceived lightness of the color
                    x = position.x
                    y = position.y
                    textColor = if (color.luminance() > 50.pct) Colors.Web.black else Colors.Web.white
                    textContent = "${(color.luminance().value * 100).toInt()}%"
                    textAlign = textAlign(TextHAlign.MIDDLE, TextVAlign.MIDDLE)
                }
            }
        }

        div {
            attrs {
                onClickFunction = {
                    println("Card clicked!")
                }
            }
            mCard {
                mCardHeader(
                    title = "Test",
                    subHeader = "OtherTest",
                    avatar = mAvatar(addAsChild = false) {
                        +"R"
                    }
                )

                mCardContent {
                    mTypography {
                        +"This impressive paella is a perfect party dish and a fun meal to cook together with your guests. Add 1 cup of frozen peas along with the mussels, if you like."
                    }
                    attrs {

                    }
                }

                mCardActions {
                    mButton("Load data",
                        color = MColor.primary,
                        size = MButtonSize.medium,
                        onClick = {
                            Data.buildData()
                            println("data loaded!")
                            state.refreshOpleiders()
                        })
                }
            }
        }

        mGridContainer {
            mGridItem(
                xs = MGridSize.cells6
            ) {
//                mGridItem(xs = MGridSize.cells12) {
//                    mFormControl(
//                        variant = MFormControlVariant.filled,
//                        fullWidth = true
//                    ) {
//                        mInputLabel(htmlFor = "filled-adornment-filter", caption = "Filter")
//                        mFilledInput(
//                            id = "filled-adornment-filter",
//                            type = InputType.text,
//                            onChange = {
//                                state.setRijschoolFilter(it.targetInputValue)
//                            }
//                        ) {
//                            attrs {
//                                onKeyPress = {
//                                    when (it.key) {
//                                        "Enter" -> {
//                                            it.preventDefault()
//                                            state.refreshOpleiders()
//                                        }
//                                    }
//                                }
//                                endAdornment = buildElement {
//                                    mInputAdornment(position = MInputAdornmentPosition.end) {
//                                        mIconButton(
//                                            iconName = "find",
//                                            onClick = { state.refreshOpleiders() },
//                                            edge = MIconEdge.end
//                                        ) {
//                                            css {
//                                                //margin = 1.px
//                                            }
//                                        }
//                                    }
//                                }!!
//                            }
//                        }
//                    }
//                }

                mGridItem(xs = MGridSize.cells11) {
                    mTextField(
                        label = "Filter",
                        fullWidth = true,
                        variant = MFormControlVariant.filled,
                        onChange = {
                            state.setRijschoolFilter(it.targetInputValue)
                        }
                    ) {
                        css {
                            marginLeft = 1.spacingUnits
                            marginRight = 1.spacingUnits
                        }
                        attrs {
                            onKeyPress = {
                                when (it.key) {
                                    "Enter" -> {
                                        it.preventDefault()
                                        state.refreshOpleiders()
                                    }
                                }
                            }
                        }
                    }
                }
                mGridItem(xs = MGridSize.cells1) {
                    mIconButton("find", onClick = { state.refreshOpleiders() }) {
                        css {
                            //margin = 1.px
                        }
                    }
                }
                mGridItem(xs = MGridSize.cells12) {
                    opleidersList {
                        setRefreshOpleidersRef = {
                            setState {
                                refreshOpleiders = it
                            }
                        }
                        setFilterRef = {
                            setState {
                                setRijschoolFilter = it
                            }
                        }
                    }
                }
            }

        }

        video {
            attrs {
                src = "https://thumbs.gfycat.com/ThankfulWeakChick-mobile.mp4"
                autoPlay = true
                loop = true
            }
        }


    }
}

fun RBuilder.app() = child(App::class) {}