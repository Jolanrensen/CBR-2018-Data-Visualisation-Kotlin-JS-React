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

external open class Slider : Component<SliderOptions> {
    override var el: Element
    override var options: SliderOptions
    open var activeIndex: Number
    open fun pause()
    open fun start()
    open fun next()
    open fun prev()

    companion object {
        fun getInstance(elem: Element): Slider
        fun init(els: Element, options: SliderOptionsPartial? = definedExternally): Slider
        fun init(els: NodeList, options: SliderOptionsPartial? = definedExternally): Array<Slider>
        fun init(els: JQuery, options: SliderOptionsPartial? = definedExternally): Array<Slider>
        fun init(els: Cash, options: SliderOptionsPartial? = definedExternally): Array<Slider>
    }
}

external interface SliderOptions {
    var indicators: Boolean
    var height: Number
    var duration: Number
    var interval: Number
}

external interface SliderOptionsPartial {
    var indicators: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var height: Number?
        get() = definedExternally
        set(value) = definedExternally
    var duration: Number?
        get() = definedExternally
        set(value) = definedExternally
    var interval: Number?
        get() = definedExternally
        set(value) = definedExternally
}