import ExamenlocatieOrOpleider.EXAMENLOCATIE
import ExamenlocatieOrOpleider.OPLEIDER
import SlagingspercentageSoort.*
import com.ccfraser.muirwik.components.*
import com.ccfraser.muirwik.components.button.MButtonSize
import com.ccfraser.muirwik.components.button.mButton
import com.ccfraser.muirwik.components.button.mFab
import com.ccfraser.muirwik.components.card.mCardActions
import com.ccfraser.muirwik.components.card.mCardContent
import com.ccfraser.muirwik.components.card.mCardHeader
import data.Data
import data.ExamenResultaatVersie.EERSTE_EXAMEN_OF_TOETS
import data.ExamenResultaatVersie.HEREXAMEN_OF_TOETS
import io.data2viz.color.Colors
import kotlinext.js.jsObject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.css.*
import org.w3c.dom.events.Event
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.a
import react.dom.br
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
    var slagingspercentageSoort: SlagingspercentageSoort

    var numberOfResultaatFilterCards: Int
}

enum class ExamenlocatieOrOpleider(val naamMeervoud: String) {
    EXAMENLOCATIE("examenlocaties"),
    OPLEIDER("opleiders")
}

enum class SlagingspercentageSoort(val naam: String) {
    EERSTE_KEER(EERSTE_EXAMEN_OF_TOETS.title),
    HERKANSING(HEREXAMEN_OF_TOETS.title),
    GECOMBINEERD("Gecombineerd")
}

class App(prps: AppProps) : RComponent<AppProps, AppState>(prps) {

    override fun AppState.init(props: AppProps) {
        dataLoaded = false
        welcomeText = "Hello world!"
        circleColor = Colors.rgb(255, 0, 0)

        selectedGemeente = null
        examenlocatieOrOpleider = OPLEIDER
        slagingspercentageSoort = EERSTE_KEER
        numberOfResultaatFilterCards = 2
    }

    private var selectedGemeente by stateDelegateOf(AppState::selectedGemeente)
    private var dataLoaded by stateDelegateOf(AppState::dataLoaded)
    private var examenlocatieOrOpleider by stateDelegateOf(AppState::examenlocatieOrOpleider)
    private var slagingspercentageSoort by stateDelegateOf(AppState::slagingspercentageSoort)
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

    private val toggleExamenlocatieOrOpleider = { _: Event? ->
        examenlocatieOrOpleider = when (examenlocatieOrOpleider) {
            OPLEIDER -> EXAMENLOCATIE
            EXAMENLOCATIE -> OPLEIDER
        }
    }

    private val toggleSlagingspercentageSoort = { _: Event? ->
        slagingspercentageSoort = when (slagingspercentageSoort) {
            EERSTE_KEER -> HERKANSING
            HERKANSING -> GECOMBINEERD
            GECOMBINEERD -> EERSTE_KEER
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
                            mToolbarTitle("CBR Opleiderresultaten juli 2017 t/m juni 2018  -  2IMV20 Visualization  -  2020")
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
                            mGridItem(xs = MGridSize.cells12) {
                                hoveringCard {
                                    css {
                                        margin(1.mm)
                                    }
                                    mCardHeader(
                                        title = "Over deze website",
                                        avatar = mAvatar(addAsChild = false) { +"O" }
                                    ) {
                                        attrs.titleTypographyProps = jsObject {
                                            variant = MTypographyVariant.h5
                                            component = "h2"
                                        }
                                    }
                                    mCardContent {
                                        mTypography {
                                            +"Deze website is gemaakt voor het TU/e vak 2IMV20 Visualization door Jolan Rensen en Daan Waalboer."
                                            br {}
                                            +"De data van deze website is verkregen van "
                                            a("https://data.overheid.nl/dataset/cbr-opleiderresultaten") { +"overheid.nl" }
                                            +" en verrijkt met gemeentedata verkregen van "
                                            a("https://www.cbs.nl/nl-nl/maatwerk/2018/36/buurt-wijk-en-gemeente-2018-voor-postcode-huisnummer") { +"cbs.nl" }
                                            +"."
                                            br {}
                                            +"De kaart van Nederland komt van de gemeentedata van 2018 van "
                                            a("https://cartomap.github.io/nl/") { +"Cartomap" }
                                            +"."
                                            br {}
                                            br {}
                                            +"Deze website is gemaakt met "
                                            a("https://kotlinlang.org/docs/reference/js-overview.html") { +"Kotlin voor JavaScript" }
                                            +" en "
                                            a("https://reactjs.org/") { +"React" }
                                            +" m.b.v. de officiële "
                                            a("https://github.com/JetBrains/kotlin-wrappers/tree/master/kotlin-react") { +"Kotlin React wrapper library" }
                                            +"."
                                            br {}
                                            +"Voor de UI is gekozen voor "
                                            a("https://material-ui.com/") { +"Material-UI" }
                                            +" m.b.v de Kotlin wrapper "
                                            a("https://github.com/cfnz/muirwik") { +"Muirwik" }
                                            +"."
                                            br {}
                                            +"De kaart en legenda zijn gemaakt met de Kotlin Multiplatform library "
                                            a("https://github.com/data2viz/data2viz") { +"Data2viz" }
                                            +", die gebaseerd is op "
                                            a("https://github.com/d3/d3") { +"D3.js" }
                                            +"."
                                        }
                                    }
                                }
                            }

                            mGridItem(xs = MGridSize.cells12, lg = MGridSize.cells6) {
                                hoveringCard {
                                    css {
                                        margin(1.mm)
                                    }
                                    mCardHeader(
                                        title = "Slagingspercentage (${slagingspercentageSoort.naam}) voor ${examenlocatieOrOpleider.naamMeervoud} per gemeente",
                                        subHeader = selectedGemeente?.name ?: "-",
                                        avatar = mAvatar(addAsChild = false) {
                                            css {
                                                color = Color.black
                                                backgroundColor = selectedGemeente?.let {
                                                    getGemeenteColor(
                                                        false,
                                                        it,
                                                        examenlocatieOrOpleider,
                                                        slagingspercentageSoort
                                                    )
                                                        .toRgb()
                                                        .let { rgb(it.r, it.g, it.b) }
                                                } ?: rgb(189, 189, 189)
                                            }
                                            +(selectedGemeente?.let {
                                                "${(when (examenlocatieOrOpleider) {
                                                    OPLEIDER -> when (slagingspercentageSoort) {
                                                        EERSTE_KEER -> it.slagingspercentageEersteKeerOpleiders
                                                        HERKANSING -> it.slagingspercentageHerexamenOpleiders
                                                        GECOMBINEERD -> it.slagingspercentageGecombineerdOpleiders
                                                    }
                                                    EXAMENLOCATIE -> when (slagingspercentageSoort) {
                                                        EERSTE_KEER -> it.slagingspercentageEersteKeerExamenlocaties
                                                        HERKANSING -> it.slagingspercentageHerexamenExamenlocaties
                                                        GECOMBINEERD -> it.slagingspercentageGecombineerdExamenlocaties
                                                    }
                                                } * 100.0).toInt()
                                                }%"
                                            } ?: "--%")
                                        }
                                    ) {
                                        attrs {
                                            titleTypographyProps = jsObject {
                                                variant = MTypographyVariant.h5
                                                component = "h2"
                                            }
                                            subheaderTypographyProps = jsObject {
                                                variant = MTypographyVariant.h6
                                                component = "h5"
                                            }
                                        }
                                    }

                                    mCardContent {
                                        nederlandMap {
                                            attrs {
                                                dataLoaded = this@App.dataLoaded
                                                examenlocatieOrOpleider = this@App.examenlocatieOrOpleider
                                                slagingspercentageSoort = this@App.slagingspercentageSoort
                                                selectedGemeente = stateAsProp(AppState::selectedGemeente)

                                                setExamenlocatieFilters = this@App.setExamenlocatieFilters
                                                setOpleiderFilters = this@App.setOpleiderFilters
                                            }
                                        }
                                    }
                                    mCardContent {
                                        mTypography {
                                            css {
                                                display = Display.flex
                                                justifyContent = JustifyContent.center
                                                alignItems = Align.center
                                            }
                                            +"Houd je cursor boven een gemeente om het slagingspercentage te zien."
                                            br {}
                                            +"Klik op een gemeente om daarop te zoeken in de opleiders/examenlocaties hieronder."
                                        }
                                    }

                                    mCardActions {
                                        mButton(
                                            caption = when (examenlocatieOrOpleider) {
                                                EXAMENLOCATIE -> OPLEIDER.naamMeervoud
                                                OPLEIDER -> EXAMENLOCATIE.naamMeervoud
                                            },
                                            color = MColor.primary,
                                            size = MButtonSize.small,
                                            onClick = toggleExamenlocatieOrOpleider
                                        )
                                        mButton(
                                            caption = when (slagingspercentageSoort) {
                                                EERSTE_KEER -> HERKANSING.naam
                                                HERKANSING -> GECOMBINEERD.naam
                                                GECOMBINEERD -> EERSTE_KEER.naam
                                            },
                                            color = MColor.primary,
                                            size = MButtonSize.small,
                                            onClick = toggleSlagingspercentageSoort
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
                                    ) {
                                        attrs.titleTypographyProps = jsObject {
                                            variant = MTypographyVariant.h5
                                            component = "h2"
                                        }
                                    }

                                    mCardContent {
                                        mTypography {
                                            +"Hieronder kun je sets resultaten met elkaar vergelijken. Dit werkt het best op de desktop aangezien dan de tabellen onder elkaar terecht komen. Het selecteren van opleiders filtert automatisch de beschikbare examenlocaties en vice versa. Geselecteerde items die verdwijnen dankzij een filteropdracht worden automatisch gedeselecteerd."
                                            br {}
                                            +"Druk op Enter of op het vergrootglas om een filter toe te passen (Dit ivm performance)."
                                        }
                                    }
                                }
                            }

//                        opleiderApplyFilterFunctions = arrayListOf()
//                        examenlocatieApplyFilterFunctions = arrayListOf()
                            mGridItem(xs = MGridSize.cells12) {
                                (0 until numberOfResultaatFilterCards).forEachApply {
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