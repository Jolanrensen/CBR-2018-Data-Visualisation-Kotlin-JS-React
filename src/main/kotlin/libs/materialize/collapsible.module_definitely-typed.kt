@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION")

import M.CollapsibleOptionsPartial
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

/* extending interface from autocomplete.d.ts */
inline fun JQuery.collapsible(method: Any): JQuery = this.asDynamic().collapsible(method)

/* extending interface from autocomplete.d.ts */
inline fun JQuery.collapsible(method: Any, n: Number): JQuery = this.asDynamic().collapsible(method, n)

/* extending interface from autocomplete.d.ts */
inline fun JQuery.collapsible(options: CollapsibleOptionsPartial?): JQuery = this.asDynamic().collapsible(options)
inline fun JQuery.collapsible(): JQuery = this.asDynamic().collapsible()