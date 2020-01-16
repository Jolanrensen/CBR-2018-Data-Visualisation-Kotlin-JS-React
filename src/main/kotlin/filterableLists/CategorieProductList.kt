package filterableLists

import FilterableList
import FilterableListProps
import FilterableListState
import com.ccfraser.muirwik.components.MGridAlignItems
import com.ccfraser.muirwik.components.MGridDirection
import com.ccfraser.muirwik.components.MGridJustify
import com.ccfraser.muirwik.components.MGridSize
import com.ccfraser.muirwik.components.alignItems
import com.ccfraser.muirwik.components.button.mIconButton
import com.ccfraser.muirwik.components.direction
import com.ccfraser.muirwik.components.justify
import com.ccfraser.muirwik.components.list.mList
import com.ccfraser.muirwik.components.list.mListItem
import com.ccfraser.muirwik.components.list.mListItemAvatar
import com.ccfraser.muirwik.components.list.mListItemText
import com.ccfraser.muirwik.components.mAvatar
import com.ccfraser.muirwik.components.mCheckbox
import com.ccfraser.muirwik.components.mGridContainer
import com.ccfraser.muirwik.components.mGridItem
import com.ccfraser.muirwik.components.spacingUnits
import com.ccfraser.muirwik.components.themeContext
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
import propDelegateOf
import react.RBuilder
import react.ReactElement
import readOnlyPropDelegateOf
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

    private var isProductSelected by propDelegateOf(CategorieProductListProps::selectedItemKeysDelegate)
    private val filter by readOnlyPropDelegateOf(CategorieProductListProps::filter)
    private val onSelectionChanged by readOnlyPropDelegateOf(CategorieProductListProps::onSelectionChanged)

    private var expandedCategories by stateDelegateOf(CategorieProductListState::expandedCategories)

    override fun CategorieProductListState.init(props: CategorieProductListProps) {
        expandedCategories = hashSetOf()
    }

    override fun getFilteredItems(): List<Product> {
        val filterTerms = filter.split(" ", ", ", ",")
        val score = hashMapOf<Product, Int>()
        Product.values().forEach { product ->
            // Todo maybe replace with itemsDataDelegate
            filterTerms.forEach {
                val naam = product.omschrijving.contains(it, true)
                val name = product.name.contains(it, true)
                val categorienaam = product.categorie.omschrijving.contains(it, true)
                val categoriename = product.categorie.name.contains(it, true)

                score[product] = (score[product] ?: 0) +
                    naam.toInt() * 3 + name.toInt() * 3 + categorienaam.toInt() + categoriename.toInt()
            }
        }

        val filteredProducts: List<Product>
        val result = score.asSequence()
            .filter { it.value != 0 }
            .sortedByDescending { it.value }
            .sortedByDescending { it.key in isProductSelected }
            .apply { filteredProducts = map { it.key }.toList() }
            .map { it.key }
            .toList()

        // deselect all previously selected opleiders that are no longer in filteredOpleiders
        isProductSelected.filter { product ->
            product !in filteredProducts
        }.apply {
            forEach { key ->
                isProductSelected -= key
            }
            if (size > 0) onSelectionChanged()
        }
        return result
    }

    override fun keyToType(key: Product) = key
    override fun typeToKey(type: Product) = type


    private fun toggleExpandedCategorie(categorie: Categorie, newState: Boolean? = null) {
        if (newState ?: categorie !in expandedCategories)
            expandedCategories += categorie
        else
            expandedCategories -= categorie
    }

    private fun toggleSelectedCategorie(categorie: Categorie, newState: Boolean? = null) {
        if (newState ?: !categorie.producten.all { it in isProductSelected })
            isProductSelected += categorie.producten
        else
            isProductSelected -= categorie.producten

        onSelectionChanged()
    }

    private fun toggleSelectedProduct(product: Product, newState: Boolean? = null) {
        if (newState ?: product !in isProductSelected)
            isProductSelected += product
        else
            isProductSelected -= product

        onSelectionChanged()
    }

    override fun RBuilder.render() {
        val filteredItems = getFilteredItems()
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

                    val categories = filteredItems.asSequence().map { it.categorie }
                        .toHashSet()
                        .toList()
                        .sortedBy { it.name }
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
                                    mListItem(onClick = { toggleSelectedCategorie(categorie) }) {
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
                                    mListItem(onClick = { toggleExpandedCategorie(categorie) }) {
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
                                        onClick = { toggleSelectedProduct(product) }
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