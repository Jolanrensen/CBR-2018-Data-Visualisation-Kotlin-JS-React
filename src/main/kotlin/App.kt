
import com.ccfraser.muirwik.components.MColor
import com.ccfraser.muirwik.components.MGridSize
import com.ccfraser.muirwik.components.MGridSpacing
import com.ccfraser.muirwik.components.button.MButtonSize
import com.ccfraser.muirwik.components.button.mButton
import com.ccfraser.muirwik.components.card.mCardActions
import com.ccfraser.muirwik.components.card.mCardContent
import com.ccfraser.muirwik.components.card.mCardHeader
import com.ccfraser.muirwik.components.mAvatar
import com.ccfraser.muirwik.components.mGridContainer
import com.ccfraser.muirwik.components.mGridItem
import com.ccfraser.muirwik.components.mTypography
import com.ccfraser.muirwik.components.spacingUnits
import com.ccfraser.muirwik.components.table.MTableCellAlign
import com.ccfraser.muirwik.components.table.mTable
import com.ccfraser.muirwik.components.table.mTableBody
import com.ccfraser.muirwik.components.table.mTableCell
import com.ccfraser.muirwik.components.table.mTableHead
import com.ccfraser.muirwik.components.table.mTableRow
import data.Data
import data.ExamenResultaat
import data.ExamenResultaatCategorie
import data.ExamenResultaatVersie.EERSTE_EXAMEN_OF_TOETS
import data.ExamenResultaatVersie.HEREXAMEN_OF_TOETS
import data.Product
import data2viz.GeoPathNode
import data2viz.vizComponentCard
import filterableLists.categorieProductList
import filterableLists.examenlocatiesList
import filterableLists.opleidersList
import io.data2viz.color.Colors
import io.data2viz.geo.projection.albersProjection
import io.data2viz.geo.projection.albersUSAProjection
import io.data2viz.geo.projection.azimuthalEqualAreaProjection
import io.data2viz.geo.projection.azimuthalEquidistant
import io.data2viz.geo.projection.conicConformalProjection
import io.data2viz.geo.projection.conicEqualAreaProjection
import io.data2viz.geo.projection.conicEquidistantProjection
import io.data2viz.geo.projection.equalEarthProjection
import io.data2viz.geo.projection.equirectangularProjection
import io.data2viz.geo.projection.gnomonicProjection
import io.data2viz.geo.projection.identityProjection
import io.data2viz.geo.projection.mercatorProjection
import io.data2viz.geo.projection.naturalEarthProjection
import io.data2viz.geo.projection.orthographicProjection
import io.data2viz.geo.projection.stereographicProjection
import io.data2viz.geo.projection.transverseMercatorProjection
import io.data2viz.geom.point
import io.data2viz.geom.size
import io.data2viz.math.deg
import io.data2viz.viz.TextHAlign
import io.data2viz.viz.TextVAlign
import io.data2viz.viz.textAlign
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.css.Color
import kotlinx.css.Overflow
import kotlinx.css.backgroundColor
import kotlinx.css.color
import kotlinx.css.margin
import kotlinx.css.marginTop
import kotlinx.css.mm
import kotlinx.css.overflowX
import kotlinx.css.padding
import kotlinx.css.pct
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
import io.data2viz.math.pct as percent

class App(props: Props) : RComponent<App.Props, App.State>(props) {

    interface Props : RProps

    interface State : RState {
        var welcomeText: String
        var circleColor: io.data2viz.color.Color

        var refreshOpleiders: ReloadItems
        var selectedOpleiderKeys: Set<String>

        var refreshExamenlocaties: ReloadItems
        var selectedExamenlocatieKeys: Set<String>

        var selectedProducts: Set<Product>
        var refreshProducts: ReloadItems
    }

    override fun State.init(props: Props) {
        welcomeText = "Hello world!"
        circleColor = Colors.rgb(255, 0, 0)

        refreshOpleiders = {}
        selectedOpleiderKeys = setOf()
        refreshExamenlocaties = {}
        selectedExamenlocatieKeys = setOf()
        selectedProducts = setOf()
        refreshProducts = {}
    }

    private val selectedOpleiderKeysDelegate = delegateOf(state::selectedOpleiderKeys)
    private var selectedOpleiderKeys by selectedOpleiderKeysDelegate

    private val selectedExamenlocatieKeysDelegate = delegateOf(state::selectedExamenlocatieKeys)
    private var selectedExamenlocatieKeys by selectedExamenlocatieKeysDelegate

    private var selectedProductsDelegate = delegateOf(state::selectedProducts)
    private var selectedProducts by selectedProductsDelegate

    private fun loadData() {
        if (Data.alleOpleiders.isNotEmpty()) return
        GlobalScope.launch {
            Data.buildData()
            println("data loaded!")
            state.apply {
                refreshOpleiders()
                refreshExamenlocaties()
                refreshProducts()
            }
        }
    }

    private fun selectionFinished() =
        selectedOpleiderKeys.isNotEmpty() &&
            selectedExamenlocatieKeys.isNotEmpty() &&
            selectedProducts.isNotEmpty()

    override fun RBuilder.render() {
        val currentResults = if (!selectionFinished())
            sequenceOf()
        else
            Data.getResults(
                selectedOpleiderKeys,
                selectedExamenlocatieKeys
            ).asSequence()

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
                mCardHeader(
                    title = "Mooie grafiek",
                    subHeader = "Nou kweenie hoor",
                    avatar = mAvatar(addAsChild = false) {
                        +"gg"
                    }
                )
            }
        ) {
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
                    textColor = if (color.luminance() > 50.percent) Colors.Web.black else Colors.Web.white
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
            spacing = MGridSpacing.spacing1
        ) {
            mGridItem(
                xs = MGridSize.cells4
            ) {
                filterList(opleidersList, "opleiders") {
                    setReloadRef = {
                        setState {
                            refreshOpleiders = it
                        }
                    }
                    // liveReload = false
                    selectedItemKeysDelegate = selectedOpleiderKeysDelegate
                    selectedOtherItemKeysDelegate = selectedExamenlocatieKeysDelegate
                    onSelectionChanged = {
                        state.refreshExamenlocaties()
                    }
                }
            }

            mGridItem(
                xs = MGridSize.cells4
            ) {
                filterList(examenlocatiesList, "examenlocaties") {
                    setReloadRef = {
                        setState {
                            refreshExamenlocaties = it
                        }
                    }
                    // liveReload = false
                    selectedItemKeysDelegate = selectedExamenlocatieKeysDelegate
                    selectedOtherItemKeysDelegate = selectedOpleiderKeysDelegate
                    onSelectionChanged = {
                        state.refreshOpleiders()
                    }
                }
            }

            mGridItem(
                xs = MGridSize.cells4
            ) {
                filterList(categorieProductList, "categorieën/producten") {
                    setReloadRef = {
                        setState {
                            refreshProducts = it
                        }
                    }
                    alwaysAllowSelectAll = true
                    // liveReload = false
                    selectedItemKeysDelegate = selectedProductsDelegate
                    selectedOtherItemKeysDelegate = delegateOf { setOf<Product>() } // not used
                    onSelectionChanged = {
                        // TODO?
                    }
                }
            }

            mGridItem(
                xs = MGridSize.cells4
            ) {
                hoveringCard {
                    css {
                        width = 100.pct
                        marginTop = 3.spacingUnits
                        overflowX = Overflow.auto
                    }
                    mTable {
                        //css { minWidth = 700.px }
                        mTableHead {
                            mTableRow {
                                mTableCell { +EERSTE_EXAMEN_OF_TOETS.title }
                                ExamenResultaat.values().forEach {
                                    mTableCell(align = MTableCellAlign.right) { +it.title }
                                }
                            }
                        }
                        mTableBody {
                            ExamenResultaatCategorie.values().forEach { categorie ->
                                mTableRow(key = categorie.title) {
                                    mTableCell { +categorie.title }
                                    ExamenResultaat.values().forEach { resultaat ->
                                        mTableCell(align = MTableCellAlign.right) {
                                            +if (selectionFinished()) {
                                                currentResults
                                                    .filter { it.product in selectedProducts }
                                                    .sumBy {
                                                        it.examenResultaatAantallen
                                                            .asSequence()
                                                            .filter {
                                                                it.examenResultaatVersie == EERSTE_EXAMEN_OF_TOETS
                                                                    && it.examenResultaatCategorie == categorie
                                                                    && it.examenResultaat == resultaat
                                                            }.sumBy { it.aantal }
                                                    }.toString()
                                            } else "-"
                                        }
                                    }
                                }
                            }
                            mTableRow {
                                mTableCell { +"Totaal" }
                                ExamenResultaat.values().forEach { resultaat ->
                                    mTableCell(align = MTableCellAlign.right) {
                                        +if (selectionFinished()) {
                                            currentResults
                                                .filter { it.product in selectedProducts }
                                                .sumBy {
                                                    it.examenResultaatAantallen
                                                        .asSequence()
                                                        .filter {
                                                            it.examenResultaatVersie == EERSTE_EXAMEN_OF_TOETS
                                                                && it.examenResultaat == resultaat
                                                        }.sumBy { it.aantal }
                                                }.toString()
                                        } else "-"
                                    }
                                }
                            }
                        }
                    }
                }
            }

            mGridItem(
                xs = MGridSize.cells4
            ) {
                hoveringCard {
                    css {
                        width = 100.pct
                        marginTop = 3.spacingUnits
                        overflowX = Overflow.auto
                    }
                    mTable {
                        //css { minWidth = 700.px }
                        mTableHead {
                            mTableRow {
                                mTableCell { +HEREXAMEN_OF_TOETS.title }
                                ExamenResultaat.values().forEach {
                                    mTableCell(align = MTableCellAlign.right) { +it.title }
                                }
                            }
                        }
                        mTableBody {
                            ExamenResultaatCategorie.values().forEach { categorie ->
                                mTableRow(key = categorie.title) {
                                    mTableCell { +categorie.title }
                                    ExamenResultaat.values().forEach { resultaat ->
                                        mTableCell(align = MTableCellAlign.right) {
                                            +if (selectionFinished()) {
                                                currentResults
                                                    .filter { it.product in selectedProducts }
                                                    .sumBy {
                                                        it.examenResultaatAantallen
                                                            .asSequence()
                                                            .filter {
                                                                it.examenResultaatVersie == HEREXAMEN_OF_TOETS
                                                                    && it.examenResultaatCategorie == categorie
                                                                    && it.examenResultaat == resultaat
                                                            }.sumBy { it.aantal }
                                                    }.toString()
                                            } else "-"
                                        }
                                    }
                                }
                            }
                            mTableRow {
                                mTableCell { +"Totaal" }
                                ExamenResultaat.values().forEach { resultaat ->
                                    mTableCell(align = MTableCellAlign.right) {
                                        +if (selectionFinished()) {
                                            currentResults
                                                .filter { it.product in selectedProducts }
                                                .sumBy {
                                                    it.examenResultaatAantallen
                                                        .asSequence()
                                                        .filter {
                                                            it.examenResultaatVersie == HEREXAMEN_OF_TOETS
                                                                && it.examenResultaat == resultaat
                                                        }.sumBy { it.aantal }
                                                }.toString()
                                        } else "-"
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


        vizComponentCard(
            runOnCard = {
                mCardHeader(
                    title = "Kaartje",
                    subHeader = "Nou kweenie hoor",
                    avatar = mAvatar(addAsChild = false) {
                        +"K"
                    }
                )
            }
        ) {
            // js https://github.com/data2viz/data2viz/blob/72426841ba601aebfe351b12b38e4938571152cd/examples/ex-geo/ex-geo-js/src/main/kotlin/EarthJs.kt
            // common https://github.com/data2viz/data2viz/tree/72426841ba601aebfe351b12b38e4938571152cd/examples/ex-geo/ex-geo-common/src/main/kotlin

            size = size(800, 900)

            // example projections
            val allProjections = hashMapOf(
                "albers" to albersProjection(),
                "albersUSA" to albersUSAProjection {
                    scale = 500.0
                },
                "azimuthalEqualArea" to azimuthalEqualAreaProjection(),
                "azimuthalEquidistant" to azimuthalEquidistant(),
                "conicConformal" to conicConformalProjection(),
                "conicEqual" to conicEqualAreaProjection(),
                "conicEquidistant" to conicEquidistantProjection(),
                "equalEarth" to equalEarthProjection(),
                "equirectangular" to equirectangularProjection(),
                "gnomonic" to gnomonicProjection(),
                "identity" to identityProjection(),
                "mercator" to mercatorProjection(),
                "naturalEarth" to naturalEarthProjection(),
                "orthographic" to orthographicProjection(),
                "stereographic" to stereographicProjection(),
                "transverseMercator" to transverseMercatorProjection()
            )
            val allProjectionsNames = allProjections.keys.toList()

            val world = Data.geoJson

            // val projection = allProjections[projectionName]!!

            val geoPathNode = GeoPathNode().apply {
                stroke = Colors.Web.black
                strokeWidth = 1.0
                fill = Colors.Web.whitesmoke
                geoProjection = conicEqualAreaProjection {
                    // Todo this is now focused on the us, focus on nl
                    scale = 15000.0
                    center(5.0.deg, 52.72.deg)
                    // parallels(29.5.deg, 45.5.deg)
                    // translate(480.0, 250.0)
                    // rotate(96.0.deg, 0.0.deg)
                }
                geoData = world
                redrawPath()
            }

            add(geoPathNode)

            geoPathNode.redrawPath()

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