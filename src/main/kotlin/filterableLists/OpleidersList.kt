package filterableLists

import FilterableList
import FilterableListProps
import FilterableListState
import com.ccfraser.muirwik.components.*
import com.ccfraser.muirwik.components.MPopoverHorizontalPosition.left
import com.ccfraser.muirwik.components.MPopoverHorizontalPosition.right
import com.ccfraser.muirwik.components.MPopoverVerticalPosition.top
import com.ccfraser.muirwik.components.dialog.ModalOnCloseReason
import com.ccfraser.muirwik.components.list.mListItem
import com.ccfraser.muirwik.components.list.mListItemAvatar
import com.ccfraser.muirwik.components.list.mListItemText
import com.ccfraser.muirwik.components.table.mTable
import com.ccfraser.muirwik.components.table.mTableBody
import com.ccfraser.muirwik.components.table.mTableCell
import com.ccfraser.muirwik.components.table.mTableRow
import data.Categorie
import data.Data
import data.Opleider
import data2viz.vizComponent
import delegates.ReactPropAndStateDelegates.propDelegateOf
import delegates.ReactPropAndStateDelegates.stateDelegateOf
import io.data2viz.color.Colors.Web
import io.data2viz.color.RgbColor
import io.data2viz.geom.Arc
import io.data2viz.geom.size
import io.data2viz.scale.Scale
import io.data2viz.scale.Scales
import io.data2viz.shape.*
import kotlinx.css.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import libs.reactList.ReactListRef
import libs.reactList.ref
import libs.reactList.styledReactList
import org.w3c.dom.Node
import org.w3c.dom.events.Event
import react.RBuilder
import react.ReactElement
import react.buildElement
import react.dom.findDOMNode
import react.dom.h1
import react.ref
import styled.StyleSheet
import styled.css
import styled.styledDiv
import styled.styledH4
import toInt

interface OpleidersListProps : FilterableListProps<String, Opleider>

interface OpleidersListState : FilterableListState

class OpleidersList(prps: OpleidersListProps) :
    FilterableList<String, Opleider, OpleidersListProps, OpleidersListState>(prps) {

    private var isOpleiderSelected by propDelegateOf(OpleidersListProps::selectedItemKeys)
    private var isExamenlocatieSelected by propDelegateOf(OpleidersListProps::selectedOtherItemKeys)

    private val filter by propDelegateOf(OpleidersListProps::filter)
    private val itemsData by propDelegateOf(OpleidersListProps::itemsData)

    private val filteredItems
        get() = props.filteredItems
            ?: getFilteredItems(
                filter,
                itemsData,
                isOpleiderSelected,
                isExamenlocatieSelected
            ) // for if ref is not yet set in FilterList

    override fun OpleidersListState.init(props: OpleidersListProps) {
        popoverOpen = false
    }

    private var popoverOpen by stateDelegateOf(OpleidersListState::popoverOpen)

    private var list: ReactListRef? = null

    override fun sortType(type: Opleider) = type.run {
        when {
            slagingspercentageEersteKeer == null && slagingspercentageHerkansing == null -> 0.0
            slagingspercentageEersteKeer == null -> slagingspercentageHerkansing!!
            slagingspercentageHerkansing == null -> slagingspercentageEersteKeer!!
            else -> (type.slagingspercentageEersteKeer!! + type.slagingspercentageHerkansing!!) / 2.0
        }
    }

    override fun keyToType(key: String, itemsData: Map<String, Opleider>) =
        itemsData[key] ?: error("opleider $key does not exist")

    override fun typeToKey(type: Opleider, itemsData: Map<String, Opleider>) = type.code
    override fun getFilteredItems(
        filter: String,
        itemsData: Map<String, Opleider>,
        selectedItemKeys: Set<String>,
        selectedOtherItemKeys: Set<String>
    ): List<Opleider> {
        val filterTerms = filter.split(" ", ", ", ",")
        val score = hashMapOf<String, Int>()
        (if (selectedOtherItemKeys.isNotEmpty())
            selectedOtherItemKeys.asSequence()
                .map { Data.examenlocatieToOpleiders[it]!! }
                .flatten()
                .map { it to (itemsData[it] ?: error("opleider $it does not exist")) }
                .toMap()
        else itemsData).forEach { (oplCode, opleider) ->
            filterTerms.forEach {
                val naam = opleider.naam.contains(it, true)
                val code = oplCode.contains(it, true)
                val plaatsnaam = opleider.plaatsnaam.contains(it, true)
                val postcode = opleider.postcode.contains(it, true)
                val straatnaam = opleider.straatnaam.contains(it, true)
                score[oplCode] = (score[oplCode] ?: 0) +
                        naam.toInt() * 3 + code.toInt() + plaatsnaam.toInt() * 2 + postcode.toInt() + straatnaam.toInt()
            }
            if (filterTerms.isEmpty()) score[oplCode] = 1
        }

        val result = score.asSequence()
            .filter { it.value != 0 }
            .sortedByDescending {
                itemsData[it.key]?.let { sortType(it) } ?: error("opleider $it does not exist")
            }
            .sortedByDescending { it.value }
            .sortedByDescending { it.key in selectedItemKeys }
            .map { itemsData[it.key] ?: error("opleider $it does not exist") }
            .toList()

        list?.scrollTo(0)

        return result
    }

    private val toggleSelected = { opleider: String?, newState: Boolean? ->
        { _: Event ->
            if (opleider != null) {
                if (newState ?: opleider !in isOpleiderSelected)
                    isOpleiderSelected += opleider
                else
                    isOpleiderSelected -= opleider
            }
        }
    }

    private val renderRow = { index: Int, key: String ->
        buildElement {
            val opleider = filteredItems[index]
            mListItem(
                button = true,
                selected = opleider.code in isOpleiderSelected,
                key = key,
                divider = false
            ) {
                mListItemAvatar {
                    mAvatar {
                        attrs {
                            onClick = openPopOver(opleider, this)
                        }
                        +opleider.naam.first().toString()
                    }
                }
                mListItemText("${opleider.naam}, ${opleider.plaatsnaam} (${opleider.code})") {
                    attrs {
                        onClick = toggleSelected(opleider.code, null)
                    }
                }
                mCheckbox(checked = opleider.code in isOpleiderSelected) {
                    attrs {
                        onClick = toggleSelected(opleider.code, null)
                    }
                }
            }
        }
    }

    val openPopOver = { opleider: Opleider, mAvatarProps: MAvatarProps ->
        var avatarRef: Node? = null
        mAvatarProps.ref<dynamic> {
            avatarRef = findDOMNode(it)
        }

        ({ e: Event ->
            e.preventDefault()
            popoverOpleider = opleider
            popoverAvatar = avatarRef
            popoverOpen = true
        })
    }

    val onPopoverClose: (Event, ModalOnCloseReason) -> Unit = { _, _ ->
        popoverOpen = false
    }

    private var popoverOpleider: Opleider? = null
    private var popoverAvatar: Node? = null

    override fun RBuilder.render() {
        themeContext.Consumer { theme ->
            val themeStyles = object : StyleSheet("ComponentStyles", isStatic = true) {
                val list by css {
                    width = 320.px
                    backgroundColor = Color(theme.palette.background.paper)
                }
            }

            styledDiv {
                css {
                    padding(1.spacingUnits)
                    overflow = Overflow.auto
                    height = 400.px
                    minHeight = 400.px
                    maxHeight = 400.px
                }
                styledReactList {
                    css(themeStyles.list)
                    attrs {
                        length = filteredItems.size
                        itemRenderer = renderRow
                        type = "variable"
                        ref {
                            list = it
                        }
                    }
                }
            }
            mPopover(
                open = popoverOpen,
                onClose = onPopoverClose,
                anchorOriginVertical = top,
                anchorOriginHorizontal = right
            ) {
                attrs {
                    anchorEl = popoverAvatar

                    transformOriginVertical = top
                    transformOriginHorizontal = left
                }

                mTable {
                    mTableBody {
                        if (popoverOpleider == null) return@mTableBody
                        popoverOpleider!!.content.forEach { (key, element) ->
                            mTableRow(key = key) {
                                mTableCell { +key }
                                mTableCell { +element }
                            }
                        }
                    }
                }

                styledH4 {
                    css {
                        marginLeft = 10.px
                        textAlign = TextAlign.center
                    }
                    +"Verdeling categoriën"
                }

                if (popoverOpleider == null) return@mPopover

                val width = 500.0
                val height = 200.0
                vizComponent(
                    width = width,
                    height = height
                ) {
                    val margin = 20.0
                    val radius = height / 2.0 - margin

                    val categorieCount = Categorie.values().map { it to 0 }.toMap().toMutableMap()
                    for (it in Data.opleiderToResultaten[popoverOpleider!!.code]!!) {
                        categorieCount[it.categorie] = categorieCount[it.categorie]!! +
                                it.examenResultaatAantallen.sumBy { it.aantal }
                    }

                    val totalCount = categorieCount.values.sum().toDouble()

                    val topX = categorieCount
                        .asSequence()
                        .sortedByDescending { it.value }
                        .take(6)

                    val overig = "Overig" to categorieCount.asSequence().sumBy {
                        if (it in topX) 0 else it.value
                    }

                    val categorieList = (
                            topX.map { (key, value) ->
                                "${key.name}: ${key.omschrijving}"
                                    .let { // trim if too long
                                        if (it.length > 35) it.take(35) + "..."
                                        else it
                                    } to value
                            } + overig
                            )
                        .toList()
                        .toTypedArray()

                    // follow https://www.d3-graph-gallery.com/graph/pie_basic.html
                    val arcParams = pie<Pair<String, Int>> {
                        value = { (it.second.toDouble() / totalCount) * tau }
                    }.render(categorieList)


                    val colorOf = { categorie: Pair<String, Int> ->
                        listOf(
                            Web.purple,
                            Web.navy,
                            Web.teal,
                            Web.lime,
                            Web.yellow,
                            Web.red,
                            Web.gray
                        )[categorieList.indexOf(categorie)]
                    }

                    val arcBuilder: ArcBuilder<Pair<String, Int>> = arcBuilder {
                        startAngle = { data ->
                            arcParams.find { it.data == data }!!.startAngle
                        }
                        endAngle = { data ->
                            arcParams.find { it.data == data }!!.endAngle
                        }
                        padAngle = { data ->
                            arcParams.find { it.data == data }!!.padAngle ?: 0.0
                        }
                        outerRadius = { radius }
                    }

                    group {
                        transform {
                            translate(x = width / 6.0 + margin, y = height / 2.0)
                        }
                        arcParams.forEach {
                            arcBuilder.buildArcForDatum(it.data!!, path {
                                fill = colorOf(it.data!!)
                                stroke = Web.black
                                strokeWidth = 2.0
                            })
                        }
                    }

                    // legenda
                    group {
                        transform {
                            translate(x = width / 3.0 + 2.0 * margin, y = margin)
                        }

                        categorieList.filter { it.second > 0 }
                            .forEachIndexed { index, data ->
                                group {
                                    transform {
                                        translate(y = index * 15.0)
                                    }
                                    rect {
                                        size = size(10.0, 10.0)
                                        fill = colorOf(data)
                                    }
                                    group {
                                        transform {
                                            translate(x = 15.0, y = 9.0)
                                        }
                                        text {
                                            textContent = data.first
                                        }
                                    }
                                }
                            }
                    }
                }
            }

            styledDiv {
                css {
                    padding(
                        left = 5.spacingUnits,
                        right = 5.spacingUnits,
                        top = 2.spacingUnits
                    )
                }

                mTypography {
                    +"  ⬑   Klik op een avatar voor meer info!"
                }
                mTypography {
                    +"Resultaten worden o.a. gesorteerd op gemiddeld totaal slagingspercentage"
                }
            }
            Unit
        }
    }
}

// as for a fun RBuilder.opleiderslist, its ::filterableLists.getOpleidersList wouldn't be an extension function anymore
val opleidersList: RBuilder.(OpleidersListProps.() -> Unit) -> ReactElement = { handler ->
    child(OpleidersList::class) {
        attrs(handler)
    }
}
