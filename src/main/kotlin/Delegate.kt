
import react.Component
import react.RState
import react.setState
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty

open class VarDelegate<T>(
    open val get: () -> T,
    val set: (T) -> Unit
) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>) = get()
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) = set(value)
}

class ValDelegate<T>(
    override val get: () -> T
) : VarDelegate<T>(get = get, set = {})

fun <T> VarDelegate<T>.toValDelegate() = delegateOf(get)
fun <T> ValDelegate<T>.toVarDelegate() = this as VarDelegate<T>

fun <T> delegateOf(
    get: () -> T
) = ValDelegate(get)

fun <T> delegateOf(
    get: () -> T,
    set: (T) -> Unit
) = VarDelegate(get, set)

// can be called like "val stateDelegate = delegateOf(state::stateItem)"
fun <S : RState, T> Component<*, S>.delegateOf(stateItem: KMutableProperty0<T>) = delegateOf(
    get = stateItem::get,
    set = { setState { stateItem.set(it) } }
)

