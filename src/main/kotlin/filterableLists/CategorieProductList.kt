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
import delegateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
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
import styled.css
import styled.styledDiv
import toInt

class CategorieProductList(props: Props) :
    FilterableList<CategorieProductList.Props, CategorieProductList.State>(props) {

    interface Props : FilterableListProps<Product, Product>
    // Available in props
    // var filter: String
    // var setReloadRef: (ReloadItems) -> Unit
    // var selectedItemKeys: HashSet<Product>
    // var onSelectionChanged: () -> Unit
    // var selectedOtherItemKeys: HashSet<Product>
    // var filteredItemsDelegate: ReadWriteProperty<Any?, List<Product>>

    private var filteredItems by props.filteredItemsDelegate

    private var selectedItemKeys by props.selectedItemKeysDelegate

    interface State : FilterableListState {
        var expandedCategories: Set<Categorie>
    }

    private val expandedCategoriesDelegate = delegateOf(state::expandedCategories)
    private var expandedCategories by expandedCategoriesDelegate

    override fun State.init(props: Props) {
        props.setReloadRef(::refreshProducts)
        props.setKeyToTypeRef { it }
        props.setTypeToKeyRef { it }
        expandedCategories = hashSetOf()
    }

    private val jobs = hashSetOf<Job>()
    override fun componentWillUnmount() {
        jobs.forEach { it.cancel() }
    }

    private fun refreshProducts() {
        CoroutineScope(Dispatchers.Main).launch {
            val filterTerms = props.filter.split(" ", ", ", ",")
            val score = hashMapOf<Product, Int>()
            Product.values().forEach { product ->
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
            filteredItems = score.asSequence()
                .filter { it.value != 0 }
                .sortedByDescending { it.value }
                .sortedByDescending { it.key in selectedItemKeys }
                .apply { filteredProducts = map { it.key }.toList() }
                .map { it.key }
                .toList()

            // deselect all previously selected opleiders that are no longer in filteredOpleiders
            selectedItemKeys.filter { product ->
                product !in filteredProducts
            }.apply {
                forEach { key ->
                    selectedItemKeys -= key
                }
                if (size > 0) props.onSelectionChanged()
            }

        }.let {
            jobs.add(it)
        }
    }

    private fun toggleExpandedCategorie(categorie: Categorie, newState: Boolean? = null) {
        if (newState ?: categorie !in expandedCategories)
            expandedCategories += categorie
        else
            expandedCategories -= categorie
    }

    private fun toggleSelectedCategorie(categorie: Categorie, newState: Boolean? = null) {
        if (newState ?: !categorie.producten.all { it in selectedItemKeys })
            selectedItemKeys += categorie.producten
        else
            selectedItemKeys -= categorie.producten

        props.onSelectionChanged()
    }

    private fun toggleSelectedProduct(product: Product, newState: Boolean? = null) {
        if (newState ?: product !in selectedItemKeys)
            selectedItemKeys += product
        else
            selectedItemKeys -= product

        props.onSelectionChanged()
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

                    val categories = filteredItems.asSequence().map { it.categorie }
                        .toHashSet()
                        .toList()
                        .sortedBy { it.name }
                    for (categorie in categories) {
                        mListItem(
                            dense = true,
                            button = true,
                            selected = categorie.producten.any { it in selectedItemKeys },
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
                                            checked = categorie.producten.any { it in selectedItemKeys },
                                            indeterminate = !categorie.producten.all { it in selectedItemKeys } && categorie.producten.any { it in selectedItemKeys }
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
                                        selected = product in selectedItemKeys,
                                        key = product.toString(),
                                        divider = false,
                                        onClick = { toggleSelectedProduct(product) }
                                    ) {

                                        mListItemText(product.omschrijving) {
                                            css {
                                                marginLeft = 8.spacingUnits
                                            }

                                        }
                                        mCheckbox(checked = product in selectedItemKeys)
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