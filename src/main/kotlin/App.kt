import io.data2viz.color.Colors
import io.data2viz.geom.point
import io.data2viz.geom.size
import io.data2viz.math.deg
import io.data2viz.math.pct
import io.data2viz.viz.TextHAlign
import io.data2viz.viz.TextVAlign
import io.data2viz.viz.textAlign
import kotlinx.css.*
import kotlinx.html.js.onClickFunction
import kotlinx.html.style
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.*
import styled.css
import styled.styledDiv
import styled.styledP


class App : RComponent<App.Props, App.State>() {

    val vizSize = 500.0

    interface State : RState {

    }

    interface Props : RProps {

    }

    override fun RBuilder.render() {
        styledDiv {
            css {
                padding(vertical = 16.px)
                backgroundColor = Color.green
            }
            +"Hello world!"
        }
        styledP {
            css {
                color = Color.blue
            }
            +"Hello from React!!!!!!!!"
            attrs {
                onClickFunction = {
                    alert("Clickedie clackedie")
                }
            }
        }

        vizComponent {
            runOnViz = {
                size = size(vizSize * 2, vizSize)

                (0 until 360 step 30).forEach {
                    val angle = it.deg
                    val position = point(250 + angle.cos * 100, 125 + angle.sin * 100)
                    val color = Colors.hsl(angle, 100.pct, 50.pct)

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
                        textColor = if (color.luminance() > 50.pct) Colors.Web.black else Colors.Web.white
                        textContent = "${(color.luminance().value * 100).toInt()}%"
                        textAlign = textAlign(TextHAlign.MIDDLE, TextVAlign.MIDDLE)
                    }
                }
            }
        }
    }

}

fun RBuilder.app() = child(App::class) {}