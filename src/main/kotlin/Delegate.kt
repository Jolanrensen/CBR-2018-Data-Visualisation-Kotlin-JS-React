import react.Component
import react.RProps
import react.RState
import react.setState
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty

/**
 * Kotlin/JS React
 *
 * Jolan Rensen - 2020
 */

/**
 *  Can be used as a way to shorthand setState { stateItem = * } and state.stateItem to just a variable stateItem
 *  As this is purely functional it's fine to put it inside a class body
 *  Use like:
 *
 *  var item by stateDelegateOf(YourState::item)
 *
 *  Do not send these as a prop to a childComponent. Use StateAsProp for that!
 * */
class StateDelegate<T>(
    val get: () -> T,
    val set: (T) -> Unit = {}
) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>) = get()
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) = set(value)

    override fun equals(other: Any?) =
        if (other !is StateDelegate<*>) false
        else (other as? StateDelegate<*>)?.get?.invoke() == get()
}

fun <S : RState, T> Component<*, S>.stateDelegateOf(stateItem: KMutableProperty1<S, T>): StateDelegate<T> {
    return StateDelegate({ stateItem.get(state) }) {
        setState {
            stateItem.set(this, it)
        }
    }
}

/**
 * Can be used to send both a getter and setter for a state to a child component as a prop.
 * Inside child components, use ReadWritePropDelegates to "unpack" a StateAsProp
 * Can even be used to send to PureComponents
 *
 * childComponent {
 *     attrs {
 *         item = state.item
 *         setItem = {
 *             setState {
 *                 item = it
 *             }
 *         }
 *     }
 * }
 *
 * can now be written like:
 *
 * childComponent {
 *     attrs {
 *         item = stateAsProp(YourState::item)
 *     }
 * }
 *
 */
class StateAsProp<T>(
    val value: T,
    val set: (T) -> Unit = {}
) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>) = value
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) = set(value)

    override fun equals(other: Any?) =
        if (other !is StateAsProp<*>) false
        else (other as? StateAsProp<*>)?.value == value
}

fun <S : RState, T> Component<*, S>.stateAsProp(stateItem: KMutableProperty1<S, T>): StateAsProp<T> =
    StateAsProp(stateItem.get(state)) {
        setState {
            stateItem.set(this, it)
        }
    }

/** same as stateAsProp, but without setter, just a getter */
fun <S : RState, T> Component<*, S>.stateAsProp(default: T) =
    StateAsProp(default)

/**
 * Can be used to "unpack" a StateAsProp to easily use its getter and setter in a single variable.
 * Safe to use in PureComponents
 * Use like:
 *
 * var item by propDelegateOf(YourProps::item)
 *
 */
class ReadWritePropDelegate<T>(
    val get: () -> T,
    val set: (T) -> Unit = {}
) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>) = get()
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) = set(value)

    override fun equals(other: Any?) =
        if (other !is ReadWritePropDelegate<*>) false
        else (other as? ReadWritePropDelegate<*>)?.get?.invoke() == get()
}

// not sure what this was for again... todo remove
//fun <Props : RProps, State : RState, VarType : Any?> Component<Props, State>.propDelegateOf(propItem: KMutableProperty1<Props, StateDelegate<VarType>>) =
//    PropDelegate({ propItem.get(props).get() }
//    ) { propItem.get(props).set(it) }

fun <Props : RProps, State : RState, VarType : Any?> Component<Props, State>.propDelegateOf(propItem: KMutableProperty1<Props, StateAsProp<VarType>>) =
    ReadWritePropDelegate({ propItem.get(props).value }
    ) { propItem.get(props).set(it) }


/**
 * Can be used to access (getter-only) prop values without having to type prop.* every time.
 * Safe to use in PureComponents
 * Use like:
 *
 * val item by readOnlyPropDelegateOf(YourProps::item)
 */
class ReadOnlyPropDelegate<T>(
    val get: () -> T
) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>) = get()

    override fun equals(other: Any?) =
        if (other !is ReadWritePropDelegate<*>) false
        else (other as? ReadWritePropDelegate<*>)?.get?.invoke() == get()
}

fun <Props : RProps, State : RState, ValType : Any?> Component<Props, State>.propDelegateOf(propItem: KMutableProperty1<Props, ValType>) =
    ReadOnlyPropDelegate { propItem.get(props) }