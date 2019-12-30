package filterableLists

import FilterableList
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
import react.RBuilder
import react.ReactElement
import react.setState
import styled.css
import styled.styledDiv
import toInt

class CategorieProductList(props: Props) :
    FilterableList<CategorieProductList.Props, CategorieProductList.State>(props) {

    interface Props : FilterableListProps<Product>
    // Available in props
    // var filter: String
    // var setReloadRef: (ReloadItems) -> Unit
    // var selectedItemKeys: HashSet<Product>
    // var onSelectionChanged: () -> Unit
    // var selectedOtherItemKeys: HashSet<Product>

    interface State : FilterableListState<Product> {
        // var filteredItems: List<Product>
        var expandedCategories: HashSet<Categorie>
    }

    override fun State.init(props: Props) {
        props.setReloadRef(::refreshProducts)
        filteredItems = listOf()
        expandedCategories = hashSetOf()
    }

    private fun refreshProducts() {
        val filterTerms = props.filter.split(" ", ", ", ",")
        val score = hashMapOf<Product, Int>()
        Product.values().forEach { product ->
            filterTerms.forEach {
                val naam = product.naam.contains(it, true)
                val name = product.name.contains(it, true)
                val categorienaam = product.categorie.naam.contains(it, true)
                val categoriename = product.categorie.name.contains(it, true)

                score[product] = (score[product] ?: 0) +
                    naam.toInt() * 3 + name.toInt() * 3 + categorienaam.toInt() + categoriename.toInt()
            }
        }
        setState {
            val filteredProducts: List<Product>
            filteredItems = score.asSequence()
                .filter { it.value != 0 }
                .sortedByDescending { it.value }
                .sortedByDescending { it.key in props.selectedItemKeys }
                .apply { filteredProducts = map { it.key }.toList() }
                .map { it.key }
                .toList()

            // deselect all previously selected opleiders that are no longer in filteredOpleiders
            props.selectedItemKeys.filter { product ->
                product !in filteredProducts
            }.apply {
                forEach { key ->
                    props.selectedItemKeys.remove(key)
                }
                if (size > 0) props.onSelectionChanged()
            }
        }
    }

    private fun toggleExpandedCategorie(categorie: Categorie, newState: Boolean? = null) {
        setState {
            if (newState ?: categorie !in expandedCategories)
                expandedCategories.add(categorie)
            else
                expandedCategories.remove(categorie)
        }
    }

    private fun toggleSelectedCategorie(categorie: Categorie, newState: Boolean? = null) {
        setState {
            if (newState ?: !categorie.producten.all { it in props.selectedItemKeys }) {
                props.selectedItemKeys.addAll(categorie.producten)
            } else {
                props.selectedItemKeys.removeAll(categorie.producten)
            }

            props.onSelectionChanged()
        }
    }

    private fun toggleSelectedProduct(product: Product, newState: Boolean? = null) {
        setState {
            if (newState ?: product !in props.selectedItemKeys)
                props.selectedItemKeys.add(product)
            else
                props.selectedItemKeys.remove(product)

            props.onSelectionChanged()
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

                    val categories = state.filteredItems.map { it.categorie }.toHashSet()
                    for (categorie in categories) {
                        mListItem(
                            dense = true,
                            button = true,
                            selected = categorie.producten.any { it in props.selectedItemKeys },
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
                                        mListItemText(categorie.naam)
                                        mCheckbox(
                                            checked = categorie.producten.any { it in props.selectedItemKeys },
                                            indeterminate = !categorie.producten.all { it in props.selectedItemKeys } && categorie.producten.any { it in props.selectedItemKeys }
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
                                        mIconButton(if (categorie in state.expandedCategories) "expand_less" else "expand_more")
                                    }
                                }
                            }
                        }
                        mCollapse(categorie in state.expandedCategories) {
                            mList(disablePadding = true) {
                                css {
                                    backgroundColor = Color(theme.palette.background.paper)
                                }
                                for (product in state.filteredItems.filter { it.categorie == categorie }) {
                                    mListItem(
                                        button = true,
                                        selected = product in props.selectedItemKeys,
                                        key = product.toString(),
                                        divider = false,
                                        onClick = { toggleSelectedProduct(product) }
                                    ) {

                                        mListItemText(product.naam) {
                                            css {
                                                marginLeft = 8.spacingUnits
                                            }

                                        }
                                        mCheckbox(checked = product in props.selectedItemKeys)
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

val categorieProductList: RBuilder.(CategorieProductList.Props.() -> Unit) -> ReactElement = { handler ->
    child(CategorieProductList::class) {
        attrs(handler)
    }
}