
import com.ccfraser.muirwik.components.MColor
import com.ccfraser.muirwik.components.MGridSize
import com.ccfraser.muirwik.components.MGridSpacing
import com.ccfraser.muirwik.components.button.MButtonSize
import com.ccfraser.muirwik.components.button.mButton
import com.ccfraser.muirwik.components.card.mCard
import com.ccfraser.muirwik.components.card.mCardActions
import com.ccfraser.muirwik.components.card.mCardContent
import com.ccfraser.muirwik.components.card.mCardHeader
import com.ccfraser.muirwik.components.mAvatar
import com.ccfraser.muirwik.components.mGridContainer
import com.ccfraser.muirwik.components.mGridItem
import com.ccfraser.muirwik.components.mTypography
import data.Data
import io.data2viz.color.Colors
import io.data2viz.geom.point
import io.data2viz.geom.size
import io.data2viz.math.deg
import io.data2viz.math.pct
import io.data2viz.viz.TextHAlign
import io.data2viz.viz.TextVAlign
import io.data2viz.viz.textAlign
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.css.Color
import kotlinx.css.LinearDimension
import kotlinx.css.backgroundColor
import kotlinx.css.color
import kotlinx.css.padding
import kotlinx.css.px
import kotlinx.css.width
import kotlinx.html.js.onClickFunction
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.div
import react.setState
import styled.css
import styled.styledDiv
import styled.styledP
import kotlin.browser.window

class App(props: Props) : RComponent<App.Props, App.State>(props) {

    val vizSize = 300.0

    interface State : RState {
        var welcomeText: String
        var circleColor: io.data2viz.color.Color

        // OpleidersList
        var opleiderFilter: String
        var refreshOpleiders: ReloadItems
        var selectedOpleiderKeys: HashSet<String>

        // ExamenlocatiesList
        var examenlocatieFilter: String
        var refreshExamenlocaties: ReloadItems
        var selectedExamenlocatieKeys: HashSet<String>
    }

    override fun State.init(props: Props) {
        welcomeText = "Hello world!"
        circleColor = Colors.rgb(255, 0, 0)

        opleiderFilter = ""
        refreshOpleiders = {}
        selectedOpleiderKeys = hashSetOf()

        examenlocatieFilter = ""
        refreshExamenlocaties = {}
        selectedExamenlocatieKeys = hashSetOf()
    }

    private fun loadData() {
        if (Data.alleOpleiders.isNotEmpty()) return
        GlobalScope.launch {
            Data.buildData()
            println("data loaded!")
            state.refreshOpleiders()
            state.refreshExamenlocaties()
        }
    }

    interface Props : RProps

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

        mGridContainer(
            spacing = MGridSpacing.spacing2
        ) {
            mGridItem(
                xs = MGridSize.cells4
            ) {
                filterList(opleidersList) {
                    setReloadRef = {
                        setState {
                            refreshOpleiders = it
                        }
                    }
                    selectedItemKeys = state.selectedOpleiderKeys
                    selectedOtherItemKeys = state.selectedExamenlocatieKeys
                    onSelectionChanged = {
                        state.refreshExamenlocaties()
                    }

                }
            }

            mGridItem(
                xs = MGridSize.cells4
            ) {
                filterList(examenlocatiesList) {
                    setReloadRef = {
                        setState {
                            refreshExamenlocaties = it
                        }
                    }
                    selectedItemKeys = state.selectedExamenlocatieKeys
                    selectedOtherItemKeys = state.selectedOpleiderKeys
                    onSelectionChanged = {
                        state.refreshOpleiders()
                    }
                }
            }


            // mGridItem(
            //     xs = MGridSize.cells4
            // ) {
            //     mGridItem(xs = MGridSize.cells12) {
            //         mFormControl(
            //             variant = MFormControlVariant.filled,
            //             fullWidth = true,
            //             margin = MFormControlMargin.normal
            //         ) {
            //             css {
            //                 padding(LinearDimension.contentBox)
            //             }
            //             mInputLabel(
            //                 htmlFor = "filled-adornment-filter",
            //                 caption = "Filter Opleider",
            //                 margin = MLabelMargin.dense
            //             )
            //             mFilledInput(
            //                 id = "filled-adornment-filter",
            //                 type = InputType.text,
            //                 onChange = {
            //                     it.persist()
            //                     setState {
            //                         opleiderFilter = it.targetInputValue
            //                         if (LIVE_RELOAD) refreshOpleiders()
            //                     }
            //                 }
            //             ) {
            //                 attrs {
            //                     margin = MInputMargin.dense
            //                     onKeyPress = {
            //                         when (it.key) {
            //                             "Enter" -> {
            //                                 it.preventDefault()
            //                                 state.refreshOpleiders()
            //                             }
            //                         }
            //                     }
            //                     endAdornment = mInputAdornment(position = MInputAdornmentPosition.end) {
            //                         mIconButton(
            //                             iconName = "search",
            //                             onClick = { state.refreshOpleiders() },
            //                             edge = MIconEdge.end
            //                         )
            //                     }
            //                 }
            //             }
            //         }
            //     }
            //     mGridItem(xs = MGridSize.cells12) {
            //         opleidersList {
            //             // filterDelegate = readOnlyPropertyOf { state.opleiderFilter }
            //             setReloadRef = {
            //                 setState {
            //                     refreshOpleiders = it
            //                 }
            //             }
            //             filteredItemsDelegate = readWritePropertyOf(
            //                 get = { state.filteredOpleiders },
            //                 set = { setState { filteredOpleiders = it } }
            //             )
            //
            //             isOpleiderSelectedDelegate = readOnlyPropertyOf { state.isOpleiderSelected }
            //             isExamenlocatieSelectedDelegate = readOnlyPropertyOf { state.isExamenlocatieSelected }
            //             onSelectionChanged = {
            //                 state.refreshExamenlocaties()
            //             }
            //         }
            //     }
            // }
            //
            //
            // mGridItem(
            //     xs = MGridSize.cells4
            // ) {
            //     mGridItem(xs = MGridSize.cells12) {
            //         mFormControl(
            //             variant = MFormControlVariant.filled,
            //             fullWidth = true,
            //             margin = MFormControlMargin.normal
            //         ) {
            //             css {
            //                 padding(LinearDimension.contentBox)
            //             }
            //             mInputLabel(
            //                 htmlFor = "filled-adornment-filter",
            //                 caption = "Filter Examenlocaties",
            //                 margin = MLabelMargin.dense
            //             )
            //             mFilledInput(
            //                 id = "filled-adornment-filter",
            //                 type = InputType.text,
            //                 onChange = {
            //                     it.persist()
            //                     setState {
            //                         examenlocatieFilter = it.targetInputValue
            //                         if (LIVE_RELOAD) refreshExamenlocaties()
            //                     }
            //                 }
            //             ) {
            //                 attrs {
            //                     margin = MInputMargin.dense
            //                     onKeyPress = {
            //                         when (it.key) {
            //                             "Enter" -> {
            //                                 it.preventDefault()
            //                                 state.refreshExamenlocaties()
            //                             }
            //                         }
            //                     }
            //                     endAdornment = mInputAdornment(position = MInputAdornmentPosition.end) {
            //                         mIconButton(
            //                             iconName = "search",
            //                             onClick = { state.refreshExamenlocaties() },
            //                             edge = MIconEdge.end
            //                         )
            //                     }
            //                 }
            //             }
            //         }
            //     }
            //     mGridItem(xs = MGridSize.cells12) {
            //         examenlocatiesList {
            //             filter = readOnlyPropertyOf { state.examenlocatieFilter }
            //             setRefreshExamenlocatiesRef = {
            //                 setState {
            //                     refreshExamenlocaties = it
            //                 }
            //             }
            //             filteredExamenlocatiesDelegate = readWritePropertyOf(
            //                 get = { state.filteredExamenlocaties },
            //                 set = { setState { filteredExamenlocaties = it } }
            //             )
            //             isExamenlocatieSelectedDelegate = readOnlyPropertyOf { state.isExamenlocatieSelected }
            //             isOpleiderSelectedDelegate = readOnlyPropertyOf { state.isOpleiderSelected }
            //             onSelectedExamenlocatiesChanged = {
            //                 state.refreshOpleiders()
            //             }
            //         }
            //     }
            // }
        }

        // video {
        //     attrs {
        //         src = "https://thumbs.gfycat.com/ThankfulWeakChick-mobile.mp4"
        //         autoPlay = true
        //         loop = true
        //     }
        // }
        loadData()
    }
}

fun RBuilder.app() = child(App::class) {}