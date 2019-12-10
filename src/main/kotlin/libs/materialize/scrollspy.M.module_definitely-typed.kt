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

external open class ScrollSpy : Component<ScrollSpyOptions> {
    companion object {
        fun getInstance(elem: Element): ScrollSpy
        fun init(els: Element, options: ScrollSpyOptionsPartial? = definedExternally): ScrollSpy
        fun init(els: NodeList, options: ScrollSpyOptionsPartial? = definedExternally): Array<ScrollSpy>
        fun init(els: JQuery, options: ScrollSpyOptionsPartial? = definedExternally): Array<ScrollSpy>
        fun init(els: Cash, options: ScrollSpyOptionsPartial? = definedExternally): Array<ScrollSpy>
    }
}

external interface ScrollSpyOptions {
    var throttle: Number
    var scrollOffset: Number
    var activeClass: String
    var getActiveElement: (id: String) -> String
}

external interface ScrollSpyOptionsPartial {
    var throttle: Number?
        get() = definedExternally
        set(value) = definedExternally
    var scrollOffset: Number?
        get() = definedExternally
        set(value) = definedExternally
    var activeClass: String?
        get() = definedExternally
        set(value) = definedExternally
    var getActiveElement: ((id: String) -> String)?
        get() = definedExternally
        set(value) = definedExternally
}