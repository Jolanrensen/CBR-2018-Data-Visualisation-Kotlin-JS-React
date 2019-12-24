import com.ccfraser.muirwik.components.*
import com.ccfraser.muirwik.components.list.*
import data.Data
import data.Examenlocatie
import data.Opleider
import kotlinx.css.*
import libs.*
import react.*
import styled.*
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KMutableProperty0

class ExamenlocatiesList(props: Props) : RComponent<ExamenlocatiesList.Props, ExamenlocatiesList.State>(props) {

    interface Props : RProps {
        var filterDelegate: ReadOnlyProperty<Any?, String>
        var setRefreshExamenlocatiesRef: (RefreshExamenlocaties) -> Unit
        var filteredExamenlocatiesDelegate: ReadWriteProperty<Any?, List<Examenlocatie>>
        var isExamenlocatieSelectedDelegate: ReadOnlyProperty<Any, HashMap<String, Boolean>>
        var isOpleiderSelectedDelegate: ReadOnlyProperty<Any, HashMap<String, Boolean>>
        var onSelectedExamenlocatiesChanged: () -> Unit
    }

    private val filter by props.filterDelegate
    private var filteredExamenlocaties by props.filteredExamenlocatiesDelegate
    private val isExamenlocatieSelected by props.isExamenlocatieSelectedDelegate
    private val isOpleiderSelected by props.isOpleiderSelectedDelegate


    interface State : RState

    override fun State.init(props: Props) {
        props.setRefreshExamenlocatiesRef { refreshExamenlocaties() }
    }

    private var list: ReactListRef? = null

    private fun refreshExamenlocaties() {
        val filterTerms = filter.split(" ", ", ", ",")
        val score = hashMapOf<String, Int>()
        (if (isOpleiderSelected.values.any { it })
            isOpleiderSelected.asSequence()
                .filter { it.value }
                .map { Data.opleiderToExamenlocaties[it.key]!! }
                .flatten()
                .map { it to Data.alleExamenlocaties[it]!! }
                .toMap()
        else Data.alleExamenlocaties).forEach { (examNaam, examenlocatie) ->
            filterTerms.forEach {
                val naam = examNaam.contains(it, true)
                val plaatsnaam = examenlocatie.plaatsnaam.contains(it, true)
                val postcode = examenlocatie.postcode.contains(it, true)
                val straatnaam = examenlocatie.straatnaam.contains(it, true)
                score[examNaam] = (score[examNaam] ?: 0) +
                        naam.toInt() * 3 + plaatsnaam.toInt() * 2 + postcode.toInt() + straatnaam.toInt()
            }
        }
        setState {
            val filteredExamenlocatieCodes: List<String>
            filteredExamenlocaties = score.asSequence()
                .filter { it.value != 0 }
                .sortedByDescending { it.value }
                .sortedByDescending { isExamenlocatieSelected[it.key] ?: false }
                .apply { filteredExamenlocatieCodes = map { it.key }.toList() }
                .map { Data.alleExamenlocaties[it.key]!! }
                .toList()

            // deselect all previously selected examenlocaties that are no longer in filteredExamenlocaties
            isExamenlocatieSelected.filter { (naam, selected) ->
                selected && naam !in filteredExamenlocatieCodes
            }.apply {
                forEach { (key, _) ->
                    isExamenlocatieSelected[key] = false
                }
                if (size > 0) props.onSelectedExamenlocatiesChanged()
            }
        }

        list?.scrollTo(0)
    }

    private fun toggleSelected(examenlocatie: String?, newState: Boolean? = null) {
        examenlocatie ?: return
        setState {
            isExamenlocatieSelected[examenlocatie] =
                newState ?: !(isExamenlocatieSelected[examenlocatie] ?: false)
            props.onSelectedExamenlocatiesChanged()
        }
    }

    private fun renderRow(index: Int, key: String) = buildElement {
        val examenlocatie = filteredExamenlocaties[index]
        mListItem(
            button = true,
            selected = isExamenlocatieSelected[examenlocatie.naam] ?: false,
            key = key,
            divider = false,
            onClick = { toggleSelected(examenlocatie.naam) }
        ) {
            mListItemAvatar {
                mAvatar {
                    +examenlocatie.naam.first().toString()
                }
            }
            mListItemText("${examenlocatie.naam}, ${examenlocatie.plaatsnaam} (${examenlocatie.naam})")
            mCheckbox(
                checked = isExamenlocatieSelected[examenlocatie.naam] ?: false,
                onChange = { _, newState -> toggleSelected(examenlocatie.naam, newState) })
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
//                    display = Display.inlineFlex
                    overflow = Overflow.auto
                    maxHeight = 400.px
                }
                styledReactList {
                    css(themeStyles.list)
                    attrs {
                        length = filteredExamenlocaties.size
                        itemRenderer = ::renderRow
                        type = "variable"
                        ref {
                            list = it
                        }
                    }
                }
            }
            Unit
        }
    }

}

fun RBuilder.examenlocatiesList(handler: ExamenlocatiesList.Props.() -> Unit) =
    child(ExamenlocatiesList::class) {
        attrs(handler)
    }

typealias RefreshExamenlocaties = () -> Unit