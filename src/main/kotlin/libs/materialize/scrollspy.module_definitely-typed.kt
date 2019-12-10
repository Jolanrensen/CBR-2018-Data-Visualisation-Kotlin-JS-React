@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION")

import M.ScrollSpyOptionsPartial
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
inline fun JQuery.scrollSpy(options: ScrollSpyOptionsPartial?): JQuery = this.asDynamic().scrollSpy(options)
inline fun JQuery.scrollSpy(): JQuery = this.asDynamic().scrollSpy()

/* extending interface from autocomplete.d.ts */
inline fun JQuery.scrollSpy(method: Any): JQuery = this.asDynamic().scrollSpy(method)