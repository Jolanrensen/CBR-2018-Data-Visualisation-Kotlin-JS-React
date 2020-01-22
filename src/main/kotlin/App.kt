import com.ccfraser.muirwik.components.*
import com.ccfraser.muirwik.components.button.MButtonSize
import com.ccfraser.muirwik.components.button.mButton
import com.ccfraser.muirwik.components.card.mCardContent
import com.ccfraser.muirwik.components.card.mCardHeader
import data.Data
import io.data2viz.color.Colors
import kotlinx.css.*
import kotlinx.html.js.onClickFunction
import react.*
import react.dom.div
import styled.StyleSheet
import styled.css
import styled.styledDiv
import styled.styledP

interface AppProps : RProps

interface AppState : RState {
    var dataLoaded: Boolean

    var welcomeText: String
    var circleColor: io.data2viz.color.Color

    var selectedGemeente: NederlandVizMap.Gemeente?
}

class App(prps: AppProps) : RComponent<AppProps, AppState>(prps) {

    override fun AppState.init(props: AppProps) {
        dataLoaded = false
        welcomeText = "Hello world!"
        circleColor = Colors.rgb(255, 0, 0)

        selectedGemeente = null
    }

    private var selectedGemeente by stateDelegateOf(AppState::selectedGemeente)
    private var dataLoaded by stateDelegateOf(AppState::dataLoaded)

    private fun loadData() {
        if (Data.hasStartedLoading) return
        runAsync {
            Data.buildAllData()

            println("data loaded!")
            dataLoaded = true
        }
    }

    override fun RBuilder.render() {
        mCssBaseline()

        themeContext.Consumer { theme ->
            styledDiv {
                css {
                    flexGrow = 1.0
                    width = 100.pct
                    zIndex = 1
                    overflow = Overflow.hidden
                    position = Position.relative
                    display = Display.flex
                }

                styledDiv {
                    // App Frame
                    css {
                        overflow = Overflow.hidden; position = Position.relative; display = Display.flex; width =
                        100.pct
                    }

                    mAppBar(position = MAppBarPosition.absolute) {
                        css {
                            zIndex = theme.zIndex.drawer + 1
                        }
                        mToolbar {
//                        mHidden(mdUp = true, implementation = MHiddenImplementation.css) {
//                            mIconButton("menu", color = MColor.inherit, onClick = { setState { responsiveDrawerOpen = true }})
//                        }
                            mToolbarTitle("CBR 2017 - 2018 ")
//                        mIconButton("lightbulb_outline", onClick = {
//                            themeColor = if (themeColor == "light") "dark" else "light"
//                            props.onThemeTypeChange(themeColor)
//                        })
                        }
                    }

                    styledDiv {
                        css {
                            height = 100.pct
                            flexGrow = 1.0; minWidth = 0.px
                            backgroundColor = Color(theme.palette.background.default)
                        }
                        spacer()

//                        styledDiv {
//                            css {
//                                padding(vertical = 16.px)
//                                backgroundColor = Color.green
//                            }
//
//                            +state.welcomeText
//
//                            attrs.onClickFunction = {
//                                setState {
//                                    welcomeText = "Something else"
//                                }
//                            }
//                        }

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

                        // vizComponentCard(
                        //     width = 800.0,
                        //     height = 250.0,
                        //     runOnCard = {
                        //         mCardHeader(
                        //             title = "Mooie grafiek",
                        //             subHeader = "Nou kweenie hoor",
                        //             avatar = mAvatar(addAsChild = false) {
                        //                 +"gg"
                        //             }
                        //         )
                        //     }
                        // ) {
                        //     println("reloading bolletjes")
                        //     (0 until 360 step 30).forEach {
                        //         val angle = it.deg
                        //         val position = point(250 + angle.cos * 100, 125 + angle.sin * 100)
                        //         val color = state.circleColor
                        //
                        //         circle {
                        //             // draw a circle with "pure-color"
                        //             fill = color
                        //             radius = 25.0
                        //             x = position.x
                        //             y = position.y
                        //         }
                        //         circle {
                        //             // draw a circle with the desaturated color
                        //             fill = color.desaturate(10.0)
                        //             radius = 25.0
                        //             x = position.x + 270
                        //             y = position.y
                        //         }
                        //         text {
                        //             // indicate the perceived lightness of the color
                        //             x = position.x
                        //             y = position.y
                        //             textColor = if (color.luminance() > 50.percent) Colors.Web.black else Colors.Web.white
                        //             textContent = "${(color.luminance().value * 100).toInt()}%"
                        //             textAlign = textAlign(TextHAlign.MIDDLE, TextVAlign.MIDDLE)
                        //         }
                        //     }
                        // }

                        div {
                            attrs {
                                onClickFunction = {
                                    println("Card clicked!")
                                }
                            }
                            hoveringCard {
                                css {
                                    margin(5.mm)
                                }
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
                            }
                        }

                        (0..1).forEach { _ ->
                            resultFilterAndShow {
                                dataLoaded = this@App.dataLoaded
                                // TODO
                            }
                        }

                        mGridContainer(
                            spacing = MGridSpacing.spacing3,
                            alignContent = MGridAlignContent.center
                        ) {
                            mGridItem(xs = MGridSize.cells12) {
                                hoveringCard {
                                    mCardContent {
                                        +(selectedGemeente?.let { "${it.name}, Slagingspercentage eerste keer: ${(it.slagingspercentage * 100.0).toInt()}%" }
                                            ?: "-")
                                        nederlandMap {
                                            attrs {
                                                dataLoaded = this@App.dataLoaded

                                                // this combi works
                                                // sele = state.selectedGemeentenaam
                                                // setSele = { setState { selectedGemeentenaam = it } }

                                                // does not work at all
                                                // sele = stateDelegateOf(state::selectedGemeentenaam)

                                                // this works
                                                // sele = StateDelegate(state.selectedGemeentenaam) {
                                                //     setState { selectedGemeentenaam = it }
                                                // }

                                                selectedGemeente = stateAsProp(AppState::selectedGemeente)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // video {
                        //     attrs {
                        //         src = "https://thumbs.gfycat.com/ThankfulWeakChick-mobile.mp4"
                        //         autoPlay = true
                        //         loop = true
                        //     }
                        // }
                    }
                }
            }
        }
        loadData()
    }
}


fun RBuilder.spacer() {
    themeContext.Consumer { theme ->
        val themeStyles = object : StyleSheet("ComponentStyles", isStatic = true) {
            val toolbar by css {
                toolbarJsCssToPartialCss(theme.mixins.toolbar)
            }
        }

        // This puts in a spacer to get below the AppBar.
        styledDiv {
            css(themeStyles.toolbar)
        }
        mDivider {  }
    }
}

fun RBuilder.app() = child(App::class) {}