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

external open class Tabs : Component<TabsOptions> {
    open fun select(tabId: String)
    open var index: Number
    open fun updateTabIndicator()

    companion object {
        fun getInstance(elem: Element): Tabs
        fun init(els: Element, options: TabsOptionsPartial? = definedExternally): Tabs
        fun init(els: NodeList, options: TabsOptionsPartial? = definedExternally): Array<Tabs>
        fun init(els: JQuery, options: TabsOptionsPartial? = definedExternally): Array<Tabs>
        fun init(els: Cash, options: TabsOptionsPartial? = definedExternally): Array<Tabs>
    }
}

external interface TabsOptions {
    var duration: Number
    var onShow: (`this`: Tabs, newContent: Element) -> Unit
    var swipeable: Boolean
    var responsiveThreshold: Number
}

external interface TabsOptionsPartial {
    var duration: Number?
        get() = definedExternally
        set(value) = definedExternally
    var onShow: ((`this`: Tabs, newContent: Element) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var swipeable: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var responsiveThreshold: Number?
        get() = definedExternally
        set(value) = definedExternally
}