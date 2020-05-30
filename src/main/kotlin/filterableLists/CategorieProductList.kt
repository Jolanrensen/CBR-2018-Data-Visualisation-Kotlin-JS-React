package filterableLists

import FilterableList
import FilterableListProps
import FilterableListState
import com.ccfraser.muirwik.components.*
import com.ccfraser.muirwik.components.button.mIconButton
import com.ccfraser.muirwik.components.list.mList
import com.ccfraser.muirwik.components.list.mListItem
import com.ccfraser.muirwik.components.list.mListItemAvatar
import com.ccfraser.muirwik.components.list.mListItemText
import com.ccfraser.muirwik.components.transitions.mCollapse
import data.Categorie
import data.Product
import data.producten
import delegates.ReactPropAndStateDelegates.propDelegateOf
import delegates.ReactPropAndStateDelegates.stateDelegateOf
import kotlinx.css.*
import libs.reactList.ReactListRef
import libs.reactList.styledReactList
import org.w3c.dom.events.Event
import react.RBuilder
import react.ReactElement
import react.buildElement
import react.dom.div
import react.dom.key
import styled.StyleSheet
import styled.css
import styled.styledDiv
import toInt

interface CategorieProductListProps : FilterableListProps<Product, Product, Nothing>

interface CategorieProductListState : FilterableListState {
    var expandedCategories: Set<Categorie>
}

class CategorieProductList(prps: CategorieProductListProps) :
    FilterableList<Product, Product, Nothing, CategorieProductListProps, CategorieProductListState>(prps) {

    private var isProductSelected by propDelegateOf(CategorieProductListProps::selectedItemKeys)

    private val filter by propDelegateOf(CategorieProductListProps::filter)
    private val itemsData by propDelegateOf(CategorieProductListProps::itemsData)

    private val filteredItems
        get() = props.filteredItems
            ?: getFilteredItems(
                filter,
                itemsData,
                isProductSelected,
                emptySet(),
                emptySet()
            ) // for if ref is not yet set in FilterList

    private var expandedCategories by stateDelegateOf(CategorieProductListState::expandedCategories)

    override fun CategorieProductListState.init(props: CategorieProductListProps) {
        expandedCategories = hashSetOf()
        popoverOpen = false
    }

    private var list: ReactListRef? = null

    override fun sortType(type: Product) = type.categorie.frequency.toDouble()

    override fun keyToType(key: Product, itemsData: Map<Product, Product>) = key
    override fun typeToKey(type: Product, itemsData: Map<Product, Product>) = type
    override fun getFilteredItems(
        filter: String,
        itemsData: Map<Product, Product>,
        selectedItemKeys: Set<Product>,
        selectedOtherItemKeys: Set<Product>,
        thirdSelectedItemKeys: Set<Nothing>
    ): List<Product> {
        val filterTerms = filter.split(" ", ", ", ",")
        val score = hashMapOf<Product, Int>()
        itemsData.forEach { (product, _) ->
            filterTerms.forEach {
                val naam = product.omschrijving.contains(it, true)
                val name = product.name.contains(it, true)
                val categorienaam = product.categorie.omschrijving.contains(it, true)
                val categoriename = product.categorie.name.contains(it, true)

                score[product] = (score[product] ?: 0) +
                        naam.toInt() * 3 + name.toInt() * 3 + categorienaam.toInt() + categoriename.toInt()
            }
            if (filterTerms.isEmpty()) score[product] = 1
        }

        list?.scrollTo(0)

        return score.asSequence()
            .filter { it.value != 0 }
            .sortedByDescending {
                itemsData[it.key]?.let { sortType(it) } ?: error("product $it does not exist")
            }
            .sortedByDescending { it.value }
            .sortedByDescending { it.key in selectedItemKeys }
            .map { it.key }
            .toList()
    }


    private val toggleExpandedCategorie = { categorie: Categorie, newState: Boolean? ->
        { _: Event ->
            if (newState ?: categorie !in expandedCategories)
                expandedCategories += categorie
            else
                expandedCategories -= categorie
        }
    }

    private val toggleSelectedCategorie = { categorie: Categorie, newState: Boolean? ->
        { _: Event ->
            if (newState ?: !categorie.producten.all { it in isProductSelected })
                isProductSelected += categorie.producten
            else
                isProductSelected -= categorie.producten
        }
    }

    private val toggleSelectedProduct = { product: Product, newState: Boolean? ->
        { _: Event ->
            if (newState ?: product !in isProductSelected)
                isProductSelected += product
            else
                isProductSelected -= product
        }
    }

    private val renderCategorieRow = { categories: List<Categorie>, filteredItems: List<Product> ->
        { index: Int, key: String ->
            buildElement {
                val categorie = categories[index]
                div {
                    attrs.key = key
                    mListItem(
                        dense = false,
                        button = true,
                        selected = categorie.producten.any { it in isProductSelected },
                        key = key,
                        divider = false
                    ) {
                        css {
                            padding(0.spacingUnits)
                            margin(0.spacingUnits)
                        }
                        mGridContainer {
                            css {
                                padding(0.spacingUnits)
                                margin(0.spacingUnits)
                            }
                            attrs {
                                direction = MGridDirection.row
                                justify = MGridJustify.spaceBetween
                                alignItems = MGridAlignItems.center
                            }
                            mGridItem(xs = MGridSize.cells11) {
                                css {
                                    padding(0.spacingUnits)
                                }
                                mListItem(onClick = toggleSelectedCategorie(categorie, null)) {
                                    css {
                                        padding(0.spacingUnits)
                                        paddingLeft = 2.spacingUnits
                                        margin(0.spacingUnits)
                                    }
                                    mListItemAvatar {
                                        mAvatar {
                                            +categorie.name
                                        }
                                    }
                                    mListItemText(categorie.omschrijving)
                                    mCheckbox(
                                        checked = categorie.producten.any { it in isProductSelected },
                                        indeterminate = !categorie.producten
                                            .all { it in isProductSelected } && categorie.producten
                                            .any { it in isProductSelected }
                                    )
                                }
                            }
                            mGridItem(xs = MGridSize.cells1) {
                                css {
                                    padding(0.spacingUnits)
                                }
                                mListItem(onClick = toggleExpandedCategorie(categorie, null)) {
                                    css {
                                        padding(0.spacingUnits)
                                    }
                                    mIconButton(if (categorie in expandedCategories) "expand_less" else "expand_more")
                                }
                            }
                        }
                    }
                    if (categorie in expandedCategories) mCollapse(true) {
                        mList(disablePadding = true) {
                            for (product in filteredItems
                                .asSequence()
                                .filter { it.categorie == categorie }
                                .sortedBy { it.name }
                            ) {
                                mListItem(
                                    button = true,
                                    selected = product in isProductSelected,
                                    key = product.toString(),
                                    divider = false,
                                    onClick = toggleSelectedProduct(product, null)
                                ) {

                                    mListItemText(product.omschrijving) {
                                        css {
                                            marginLeft = 8.spacingUnits
                                        }

                                    }
                                    mCheckbox(checked = product in isProductSelected)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

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
                val categories = filteredItems
                    .asSequence()
                    .map { it.categorie }
                    .distinct()
                    .toList()
                styledReactList {
                    css(themeStyles.list)
                    attrs {
                        length = categories.size
                        itemRenderer = renderCategorieRow(categories, filteredItems)
                        type = "variable"
                        ref {
                            list = it
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
                    +"Resultaten worden o.a. gesorteerd op meest voorkomend product"
                }
            }
            Unit
        }
    }
}

val categorieProductList: RBuilder.(CategorieProductListProps.() -> Unit) -> ReactElement =
    { handler ->
        child(CategorieProductList::class) {
            attrs(handler)
        }
    }