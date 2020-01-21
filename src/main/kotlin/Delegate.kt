import react.Component
import react.RProps
import react.RState
import react.setState
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty

/**
 *  Can be used as a way to shorthand setState { stateItem = * } and state.stateItem to just a variable stateItem
 *  As this is purely functional it's fine to put it inside a class body
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

/**
 *  Can be used as a way to shorthand setState { stateItem = * } and state.stateItem to just a variable stateItem
 *  As this is purely functional it's fine to put it inside a class body, in contrast to StateDelegate
 * */
fun <S : RState, T> Component<*, S>.stateDelegateOf(stateItem: KMutableProperty1<S, T>): StateDelegate<T> {
    return StateDelegate({ stateItem.get(state) }) {
        setState {
            stateItem.set(this, it)
        }
    }
}

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

fun <S : RState, T> Component<*, S>.stateAsProp(default: T) =
    StateAsProp(default)

// class ValPropDelegate<T>(
//     val get: () -> T
// ) {
//     operator fun getValue(thisRef: Any?, property: KProperty<*>) = get()
//
//     override fun equals(other: Any?) =
//         if (other !is ValPropDelegate<*>) false
//         else (other as? ValPropDelegate<*>)?.get() == get()
// }
//
// inline fun <reified P : RProps, reified T> Component<P, *>.valPropDelegateOf(propItem: KMutableProperty1<P, T>) =
//     ValPropDelegate { propItem.get(props) }

class PropDelegate<T>(
    val get: () -> T,
    val set: (T) -> Unit = {}
) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>) = get()
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) = set(value)

    override fun equals(other: Any?) =
        if (other !is PropDelegate<*>) false
        else (other as? PropDelegate<*>)?.get?.invoke() == get()
}

fun <Props : RProps, State : RState, VarType : Any?> Component<Props, State>.propDelegateOf(propItem: KMutableProperty1<Props, StateDelegate<VarType>>) =
    PropDelegate({ propItem.get(props).get() }
    ) { propItem.get(props).set(it) }

fun <Props : RProps, State : RState, VarType : Any?> Component<Props, State>.propDelegateOf(propItem: KMutableProperty1<Props, StateAsProp<VarType>>) =
    PropDelegate({ propItem.get(props).value }
    ) { propItem.get(props).set(it) }

class ReadOnlyPropDelegate<T>(
    val get: () -> T
) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>) = get()

    override fun equals(other: Any?) =
        if (other !is PropDelegate<*>) false
        else (other as? PropDelegate<*>)?.get?.invoke() == get()
}

/**
 * val x get() = props.x
 * is equivalent to
 * val x by propDelegateOf(Props::x)
 */
fun <Props : RProps, State : RState, ValType : Any?> Component<Props, State>.readOnlyPropDelegateOf(propItem: KMutableProperty1<Props, ValType>) =
    ReadOnlyPropDelegate { propItem.get(props) }