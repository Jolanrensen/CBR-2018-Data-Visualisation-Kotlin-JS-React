package filterableLists

import FilterableList
import FilterableListProps
import FilterableListState
import com.ccfraser.muirwik.components.*
import com.ccfraser.muirwik.components.button.mIconButton
import com.ccfraser.muirwik.components.dialog.ModalOnCloseReason
import com.ccfraser.muirwik.components.list.mList
import com.ccfraser.muirwik.components.list.mListItem
import com.ccfraser.muirwik.components.list.mListItemAvatar
import com.ccfraser.muirwik.components.list.mListItemText
import com.ccfraser.muirwik.components.transitions.mCollapse
import data.Categorie
import data.Product
import data.producten
import kotlinx.css.Color
import kotlinx.css.Overflow
import kotlinx.css.backgroundColor
import kotlinx.css.margin
import kotlinx.css.marginLeft
import kotlinx.css.maxHeight
import kotlinx.css.overflow
import kotlinx.css.padding
import kotlinx.css.px
import org.w3c.dom.events.Event
import propDelegateOf
import react.RBuilder
import react.ReactElement
import stateDelegateOf
import styled.css
import styled.styledDiv
import toInt

interface CategorieProductListProps : FilterableListProps<Product, Product>
// Available in props
// var filter: String
// var setReloadRef: (ReloadItems) -> Unit
// var selectedItemKeys: HashSet<Product>
// var onSelectionChanged: () -> Unit
// var selectedOtherItemKeys: HashSet<Product>
// var filteredItemsDelegate: ReadWriteProperty<Any?, List<Product>>

interface CategorieProductListState : FilterableListState {
    var expandedCategories: Set<Categorie>
}

class CategorieProductList(prps: CategorieProductListProps) :
    FilterableList<Product, Product, CategorieProductListProps, CategorieProductListState>(prps) {

    private var isProductSelected by propDelegateOf(CategorieProductListProps::selectedItemKeys)

    private val filter by propDelegateOf(CategorieProductListProps::filter)
    private val itemsData by propDelegateOf(CategorieProductListProps::itemsData)

    private val filteredItems
        get() = props.filteredItems
            ?: getFilteredItems(
                filter,
                itemsData,
                isProductSelected,
                setOf()
            ) // for if ref is not yet set in FilterList

    private var expandedCategories by stateDelegateOf(CategorieProductListState::expandedCategories)

    override fun CategorieProductListState.init(props: CategorieProductListProps) {
        expandedCategories = hashSetOf()
        popoverOpen = false
    }

    override fun sortType(type: Product) = type.categorie.frequency.toDouble()

    override fun keyToType(key: Product, itemsData: Map<Product, Product>) = key
    override fun typeToKey(type: Product, itemsData: Map<Product, Product>) = type
    override fun getFilteredItems(
        filter: String,
        itemsData: Map<Product, Product>,
        selectedItemKeys: Set<Product>,
        selectedOtherItemKeys: Set<Product>
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

    override fun RBuilder.render() {
        themeContext.Consumer { theme ->
            styledDiv {
                css {
                    padding(1.spacingUnits)
                    overflow = Overflow.auto
                    maxHeight = 400.px
                }
                mList {
                    css {
                        backgroundColor = Color(theme.palette.background.paper)
                    }

                    val categories = filteredItems
                        .asSequence()
                        .map { it.categorie }
                        .distinct()

                    for (categorie in categories) {
                        mListItem(
                            dense = true,
                            button = true,
                            selected = categorie.producten.any { it in isProductSelected },
                            key = categorie.toString(),
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
                                mGridItem(xs = MGridSize.cells10) {
                                    css {
                                        padding(0.spacingUnits)
                                    }
                                    mListItem(onClick = toggleSelectedCategorie(categorie, null)) {
                                        css {
                                            padding(0.spacingUnits)
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
                                mGridItem(xs = MGridSize.cells2) {
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
                        mCollapse(categorie in expandedCategories) {
                            mList(disablePadding = true) {
                                css {
                                    backgroundColor = Color(theme.palette.background.paper)
                                }
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