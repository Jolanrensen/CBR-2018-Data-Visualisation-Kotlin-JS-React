import data.Opleider
import io.data2viz.viz.Viz
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState

class RijschoolMenu(props: Props) : RComponent<RijschoolMenu.Props, RijschoolMenu.State>(props) {

    interface Props : RProps {
    }

    interface State : RState {
        var filter: String
        var opleiders: List<Opleider>
    }

    override fun State.init(props: Props) {

    }

    override fun RBuilder.render() {

    }

}

fun RBuilder.rijschoolMenu(runOnProps: (RijschoolMenu.Props) -> Unit) =
    child(RijschoolMenu::class) {
        runOnProps(attrs)
    }