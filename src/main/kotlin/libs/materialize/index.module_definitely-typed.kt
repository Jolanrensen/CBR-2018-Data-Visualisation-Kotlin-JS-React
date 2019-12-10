@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION")

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

external interface OffsetType {
    var top: Number
    var left: Number
}

@JsModule("cash")
external interface CashStatic {
    fun isArray(n: Any): Boolean
    fun isFunction(n: Any): Boolean
    fun isNumeric(n: Any): Boolean
    fun isString(str: Any): Boolean
    fun extend(target: Any, source: Any): Any
    fun matches(element: Cash, selector: String): Boolean
    fun parseHTML(htmlString: String): Cash
    fun each(collection: Array<Any>, callback: Function<*>): Array<Any>
    var fn: Any
    @nativeInvoke
    operator fun invoke(selector: String, context: Element? = definedExternally): Cash
    @nativeInvoke
    operator fun invoke(selector: String, context: Cash? = definedExternally): Cash
    @nativeInvoke
    operator fun invoke(element: Element): Cash
    @nativeInvoke
    operator fun invoke(elementArray: Array<Element>): Cash
    @nativeInvoke
    operator fun invoke(selector: String): Cash
}

external interface Cash {
    fun add(selector: String, context: Element? = definedExternally): Cash
    fun add(selector: Cash, context: Element? = definedExternally): Cash
    fun add(selector: Element, context: Element? = definedExternally): Cash
    fun addClass(c: String): Cash
    fun after(selector: Element): Cash
    fun after(selector: String): Cash
    fun append(content: String): Cash
    fun append(content: Element): Cash
    fun append(content: Cash): Cash
    fun appendTo(parent: String): Cash
    fun appendTo(parent: Element): Cash
    fun appendTo(parent: Cash): Cash
    fun attr(name: String): Any
    fun attr(name: String, value: String): Cash
    fun before(selector: String): Cash
    fun before(selector: Element): Cash
    fun children(selector: String? = definedExternally): Cash
    fun closest(selector: String? = definedExternally): Cash
    fun clone(): Cash
    fun css(prop: Any): Any
    fun css(prop: String, value: Any): Cash
    fun data(name: Any): Any
    fun data(name: String, value: Any): Cash
    fun each(callback: Function<*>): Cash
    fun empty(): Cash
    fun eq(index: Number): Cash
    fun extend(target: Any): Any
    fun filter(selector: Function<*>): Cash
    fun find(selector: String): Cash
    fun first(): Cash
    fun get(index: Number): HTMLElement
    fun has(selector: String): Boolean
    fun hasClass(c: String): Boolean
    fun height(): Number
    fun html(): String
    fun html(content: String): Cash
    fun index(elem: Element? = definedExternally): Number
    fun innerHeight(): Number
    fun innerWidth(): Number
    fun insertAfter(selector: String): Cash
    fun insertAfter(selector: Element): Cash
    fun insertAfter(selector: Cash): Cash
    fun insertBefore(selector: String): Cash
    fun insertBefore(selector: Element): Cash
    fun insertBefore(selector: Cash): Cash
    fun `is`(selector: String): Boolean
    fun `is`(selector: Element): Boolean
    fun `is`(selector: Cash): Boolean
    fun last(): Cash
    fun next(): Cash
    fun not(selector: String): Cash
    fun not(selector: Element): Cash
    fun not(selector: Cash): Cash
    fun off(eventName: String, callback: Function<*>): Cash
    fun offset(): OffsetType
    fun offsetParent(): OffsetType
    fun on(eventName: String, delegate: Any, callback: Function<*>? = definedExternally, runOnce: Boolean? = definedExternally): Cash
    fun on(eventName: Array<String>, delegate: Any, callback: Function<*>? = definedExternally, runOnce: Boolean? = definedExternally): Cash
    fun one(eventName: String, delegate: Any, callback: Function<*>? = definedExternally, runOnce: Boolean? = definedExternally): Cash
    fun one(eventName: Array<String>, delegate: Any, callback: Function<*>? = definedExternally, runOnce: Boolean? = definedExternally): Cash
    fun outerHeight(flag: Boolean? = definedExternally): Number
    fun outerWidth(flag: Boolean? = definedExternally): Number
    fun parent(): Cash
    fun parents(selector: String? = definedExternally): Cash
    fun position(): OffsetType
    fun prepend(content: String): Cash
    fun prependTo(parent: String): Cash
    fun prependTo(parent: Element): Cash
    fun prependTo(parent: Cash): Cash
    fun prev(): Cash
    fun prop(name: String): Any
    fun prop(name: String, value: String): Cash
    fun ready(fn: Function<*>)
    fun remove(): Cash
    fun removeAttr(name: String): Cash
    fun removeClass(c: String? = definedExternally): Cash
    fun removeData(key: String): Cash
    fun removeProp(name: String): Cash
    fun serialize(): String
    fun siblings(): Cash
    fun text(): String
    fun text(content: String? = definedExternally): Cash
    fun toggleClass(c: String, state: Boolean? = definedExternally): Cash
    fun trigger(eventName: String, data: Any? = definedExternally): Cash
    fun `val`(): Any
    fun `val`(value: String? = definedExternally): Cash
    fun width(): Number
}

external var cash: CashStatic