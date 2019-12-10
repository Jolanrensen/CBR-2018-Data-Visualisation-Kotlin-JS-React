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

external open class Parallax : Component<ParallaxOptions> {
    companion object {
        fun getInstance(elem: Element): Parallax
        fun init(els: Element, options: ParallaxOptionsPartial? = definedExternally): Parallax
        fun init(els: NodeList, options: ParallaxOptionsPartial? = definedExternally): Array<Parallax>
        fun init(els: JQuery, options: ParallaxOptionsPartial? = definedExternally): Array<Parallax>
        fun init(els: Cash, options: ParallaxOptionsPartial? = definedExternally): Array<Parallax>
    }
}

external interface ParallaxOptions {
    var responsiveThreshold: Number
}

external interface ParallaxOptionsPartial {
    var responsiveThreshold: Number?
        get() = definedExternally
        set(value) = definedExternally
}