import ExamenlocatieOrOpleider.EXAMENLOCATIE
import ExamenlocatieOrOpleider.OPLEIDER
import com.ccfraser.muirwik.components.*
import com.ccfraser.muirwik.components.button.MButtonSize
import com.ccfraser.muirwik.components.button.mButton
import com.ccfraser.muirwik.components.button.mFab
import com.ccfraser.muirwik.components.card.mCardActions
import com.ccfraser.muirwik.components.card.mCardContent
import com.ccfraser.muirwik.components.card.mCardHeader
import data.Data
import io.data2viz.color.Colors
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.css.*
import org.w3c.dom.events.Event
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import styled.StyleSheet
import styled.css
import styled.styledDiv

interface AppProps : RProps

interface AppState : RState {
    var dataLoaded: Boolean

    var welcomeText: String
    var circleColor: io.data2viz.color.Color

    var selectedGemeente: NederlandVizMap.Gemeente?
    var examenlocatieOrOpleider: ExamenlocatieOrOpleider

    var numberOfResultaatFilterCards: Int
}

enum class ExamenlocatieOrOpleider {
    EXAMENLOCATIE, OPLEIDER
}

class App(prps: AppProps) : RComponent<AppProps, AppState>(prps) {

    override fun AppState.init(props: AppProps) {
        dataLoaded = false
        welcomeText = "Hello world!"
        circleColor = Colors.rgb(255, 0, 0)

        selectedGemeente = null
        examenlocatieOrOpleider = OPLEIDER
        numberOfResultaatFilterCards = 1
    }

    private var selectedGemeente by stateDelegateOf(AppState::selectedGemeente)
    private var dataLoaded by stateDelegateOf(AppState::dataLoaded)
    private var examenlocatieOrOpleider by stateDelegateOf(AppState::examenlocatieOrOpleider)
    private var numberOfResultaatFilterCards by stateDelegateOf(AppState::numberOfResultaatFilterCards)

    private fun loadData() {
        if (Data.hasStartedLoading) return

        GlobalScope.launch {
            if (dataLoaded) return@launch
            delay(1000)
            Data.buildAllData()
            println("data loaded!")
            dataLoaded = true
        }
    }

    private var opleiderApplyFilterFunctions = arrayListOf<ApplyFilter>()
    private var examenlocatieApplyFilterFunctions = arrayListOf<ApplyFilter>()


    private val setApplyOpleidersFilterFunction = { it: ApplyFilter ->
        opleiderApplyFilterFunctions.add(it)
        Unit
    }

    private val setApplyExamenlocatieFilterFunction = { it: ApplyFilter ->
        examenlocatieApplyFilterFunctions.add(it)
        Unit
    }

    private val setExamenlocatieFilters = { filter: String ->
        for (it in examenlocatieApplyFilterFunctions) {
            try {
                it.invoke(filter)
            } catch (e: dynamic) {
                console.error(e)
            }
        }
    }

    private val setOpleiderFilters = { filter: String ->
        for (it in opleiderApplyFilterFunctions) {
            try {
                it.invoke(filter)
            } catch (e: dynamic) {
                console.error(e)
            }
        }
    }

    private val toggleExamenlocatieOrOpleider = { it: Event? ->
        examenlocatieOrOpleider = when (examenlocatieOrOpleider) {
            OPLEIDER -> EXAMENLOCATIE
            EXAMENLOCATIE -> OPLEIDER
        }
    }

    private val onFabClick = { _: Event? ->
        numberOfResultaatFilterCards++
        Unit
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
                        mGridContainer(
                            spacing = MGridSpacing.spacing2,
                            alignContent = MGridAlignContent.center,
                            alignItems = MGridAlignItems.flexEnd,
                            justify = MGridJustify.flexEnd
                        ) {
                            css {
                                padding(5.mm)
                            }
                            mGridItem(xs = MGridSize.cells12, lg = MGridSize.cells6) {
                                hoveringCard {
                                    css {
                                        margin(1.mm)
                                    }
                                    mCardHeader(
                                        title = "Slagingspercentage ${when (examenlocatieOrOpleider) {
                                            OPLEIDER -> "rijscholen"
                                            EXAMENLOCATIE -> "examenlocaties"
                                        }} per gemeente",
                                        subHeader = selectedGemeente?.let { it.name } ?: "",
                                        avatar = mAvatar(addAsChild = false) {
                                            css {
                                                color = Color.black
                                                backgroundColor = selectedGemeente?.let {
                                                    getGemeenteColor(false, it, examenlocatieOrOpleider).toRgb()
                                                        .let { rgb(it.r, it.g, it.b) }
                                                } ?: rgb(189, 189, 189)
                                            }
                                            +(selectedGemeente?.let {
                                                "${(when (examenlocatieOrOpleider) {
                                                    OPLEIDER -> it.slagingspercentageOpleiders
                                                    EXAMENLOCATIE -> it.slagingspercentageExamenlocaties
                                                } * 100.0).toInt()
                                                }%"
                                            } ?: "--%")
                                        }
                                    )

                                    mCardContent {
                                        nederlandMap {
                                            attrs {
                                                dataLoaded = this@App.dataLoaded
                                                examenlocatieOrOpleider = this@App.examenlocatieOrOpleider
                                                selectedGemeente = stateAsProp(AppState::selectedGemeente)

                                                setExamenlocatieFilters = this@App.setExamenlocatieFilters
                                                setOpleiderFilters = this@App.setOpleiderFilters
                                            }
                                        }
                                    }
                                    mCardContent {
                                        mTypography { +"Houd je cursor boven een gemeente om het slagingspercentage te zien." }
                                        mTypography { +"Klik op een gemeente om daarop te zoeken in de opleiders/examenlocaties hieronder." }
                                    }

                                    mCardActions {
                                        mButton(
                                            caption = when (examenlocatieOrOpleider) {
                                                OPLEIDER -> "examenlocaties"
                                                EXAMENLOCATIE -> "rijscholen"
                                            },
                                            color = MColor.primary,
                                            size = MButtonSize.small,
                                            onClick = toggleExamenlocatieOrOpleider
                                        )
                                    }
                                }
                            }

                            mGridItem(xs = MGridSize.cells12, lg = MGridSize.cells6) {
                                hoveringCard {
                                    css {
                                        margin(1.mm)
                                    }
                                    mCardHeader(
                                        title = "Resultaten Vergelijken",
                                        avatar = mAvatar(addAsChild = false) { +"R" }
                                    )

                                    mCardContent {
                                        mTypography {
                                            +"Hieronder kun je sets resultaten met elkaar vergelijken. Dit werkt het best op de desktop aangezien dan de tabellen onder elkaar terecht komen. Het selecteren van opleiders filtert automatisch de beschikbare examenlocaties en vice versa. Geselecteerde items die verdwijnen dankzij een filteropdracht worden automatisch gedeselecteerd."
                                        }
                                        mTypography {
                                            +"Druk op Enter of op het vergrootglas om een filter toe te passen (Dit ivm performance)."
                                        }
                                    }
                                }
                            }

//                        opleiderApplyFilterFunctions = arrayListOf()
//                        examenlocatieApplyFilterFunctions = arrayListOf()
                            mGridItem(xs = MGridSize.cells12) {
                                (0 until numberOfResultaatFilterCards).forEach {
                                    hoveringCard {
                                        css {
                                            margin(1.mm)
                                            marginBottom = 5.mm
                                            padding(5.mm)
                                        }
                                        resultFilterAndShow {
                                            dataLoaded = this@App.dataLoaded
                                            setApplyOpleidersFilterFunction =
                                                this@App.setApplyOpleidersFilterFunction
                                            setApplyExamenlocatieFilterFunction =
                                                this@App.setApplyExamenlocatieFilterFunction
                                        }
                                    }
                                }
                            }
                        }
                        styledDiv {
                            css {
                                float = Float.right
                            }
                            mFab(
                                iconName = "add",
                                size = MButtonSize.large,
                                color = MColor.secondary,
                                onClick = onFabClick
                            ) {
                                css {
                                    margin(1.spacingUnits)
                                }
                            }
                        }

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
        mDivider { }
    }
}

fun RBuilder.app() = child(App::class) {}