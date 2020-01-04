package libs.reactList

/** These helper functions were 'borrowed' from the awesome cfnz of muirwik: https://github.com/cfnz/muirwik */

import kotlinext.js.jsObject
import react.RBuilder
import react.RComponent
import react.RHandler
import react.RProps
import react.RState
import react.ReactElement
import react.children
import styled.StyledElementBuilder
import styled.StyledHandler
import styled.StyledProps
import kotlin.reflect.KClass

/**
 * Just a one liner to replace a repetitive two liner :-)
 */
fun <P : StyledProps> StyledElementBuilder<P>.setStyledPropsAndRunHandler(
    className: String?,
    handler: StyledHandler<P>?
) {
    className?.let { attrs.className = it }
    if (handler != null) handler()
}

/**
 * Create a child with empty props
 */
fun <P : RProps, S : RState> RBuilder.child(component: RComponent<P, S>, handler: RHandler<P>): ReactElement {
    val props: P = jsObject {}
    return child(component, props, handler)
}

/**
 * This is just a little helper to make the creation of our components shorter, for example
 *
 * ...handler: StyledHandler<MCheckboxProps>? = null) = child(with(StyledElementBuilder<MCheckboxProps>(checkboxComponent)) {
 *      etc
 *      create()
 * })
 *
 * becomes
 *
 * ... handler: StyledHandler<MCheckboxProps>? = null) = createStyled(checkboxComponent) {
 *      etc
 * }
 */
fun <P : StyledProps> RBuilder.createStyled(
    component: RComponent<P, RState>,
    addAsChild: Boolean = true,
    handler: StyledHandler<P>
): ReactElement {
    val builder = StyledElementBuilder<P>(component)
    handler(builder)
    return if (addAsChild) child(builder.create()) else builder.create()
}

/**
 * Helper for creating a styled component from a component class (e.g. MyComponent::class)
 */
fun <P : StyledProps> RBuilder.createStyled(
    componentClass: KClass<out RComponent<out P, out RState>>,
    addAsChild: Boolean = true,
    handler: StyledHandler<P>
): ReactElement {
    val builder = StyledElementBuilder<P>(componentClass.js)
    handler(builder)

    val el = if (addAsChild) child(builder.create()) else builder.create()

    // For some reason, we seem to need to add the children here whereas in the method above we don't...
    el.props.children

    return el
}