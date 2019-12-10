@file:JsQualifier("M")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION")
package M

import Cash
import JQuery
import kotlin.js.*
import kotlin.js.Json
import org.khronos.webgl.*
import org.w3c.dom.*
import org.w3c.dom.events.*
import org.w3c.dom.parsing.*
import org.w3c.dom.svg.*
import org.w3c.dom.url.*
import org.w3c.fetch.*
import org.w3c.files.*
import org.w3c.notifications.*
import org.w3c.performance.*
import org.w3c.workers.*
import org.w3c.xhr.*

external open class Carousel : Component<CarouselOptions> {
    open var pressed: Boolean
    open var dragged: Number
    open var center: Number
    open fun next(n: Number? = definedExternally)
    open fun prev(n: Number? = definedExternally)
    open fun set(n: Number? = definedExternally)

    companion object {
        fun getInstance(elem: Element): Carousel
        fun init(els: Element, options: CarouselOptionsPartial? = definedExternally): Carousel
        fun init(els: NodeList, options: CarouselOptionsPartial? = definedExternally): Array<Carousel>
        fun init(els: JQuery, options: CarouselOptionsPartial? = definedExternally): Array<Carousel>
        fun init(els: Cash, options: CarouselOptionsPartial? = definedExternally): Array<Carousel>
    }
}

external interface CarouselOptions {
    var duration: Number
    var dist: Number
    var shift: Number
    var padding: Number
    var numVisible: Number
    var fullWidth: Boolean
    var indicators: Boolean
    var noWrap: Boolean
    var onCycleTo: (`this`: Carousel, current: Element, dragged: Boolean) -> Unit
}

external interface CarouselOptionsPartial {
    var duration: Number?
        get() = definedExternally
        set(value) = definedExternally
    var dist: Number?
        get() = definedExternally
        set(value) = definedExternally
    var shift: Number?
        get() = definedExternally
        set(value) = definedExternally
    var padding: Number?
        get() = definedExternally
        set(value) = definedExternally
    var numVisible: Number?
        get() = definedExternally
        set(value) = definedExternally
    var fullWidth: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var indicators: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var noWrap: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var onCycleTo: ((`this`: Carousel, current: Element, dragged: Boolean) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
}