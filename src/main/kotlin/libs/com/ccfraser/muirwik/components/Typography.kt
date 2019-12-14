package com.ccfraser.muirwik.components

import react.RBuilder
import react.RComponent
import react.RState
import styled.StyledHandler


@JsModule("@material-ui/core/Typography")
private external val typographyDefault: dynamic

@Suppress("UnsafeCastFromDynamic")
private val typographyComponent: RComponent<MTypographyProps, RState> = typographyDefault.default

@Suppress("EnumEntryName")
enum class MTypographyAlign {
    inherit, left, center, right, justify
}

@Suppress("EnumEntryName")
enum class MTypographyColor {
    initial, inherit, primary, secondary, textPrimary, textSecondary, error
}

@Suppress("EnumEntryName")
enum class MTypographyVariant {
    h1, h2, h3, h4, h5, h6, subtitle1, subtitle2, body1, body2, caption, button, overline, srOnly, inherit
}

interface MTypographyProps : StyledPropsWithCommonAttributes {
    var component: String
    var gutterBottom: Boolean
    var noWrap: Boolean
    var paragraph: Boolean
}
var MTypographyProps.align by EnumPropToString(MTypographyAlign.values())
var MTypographyProps.color by EnumPropToString(MTypographyColor.values())
var MTypographyProps.variant by EnumPropToString(MTypographyVariant.values())


fun RBuilder.mTypography(
        text: String? = null,
        variant: MTypographyVariant = MTypographyVariant.body1,
        color: MTypographyColor = MTypographyColor.initial,
        align: MTypographyAlign = MTypographyAlign.left,
        gutterBottom: Boolean = false,
        noWrap: Boolean = false,
        paragraph: Boolean = false,
        component: String? = null,

        addAsChild: Boolean = true,
        className: String? = null,
        handler: StyledHandler<MTypographyProps>? = null) = createStyled(typographyComponent, addAsChild) {
    attrs.align = align
    attrs.color = color
    component?.let { attrs.component = it }
    attrs.gutterBottom = gutterBottom
    attrs.noWrap = noWrap
    attrs.paragraph = paragraph
    attrs.variant = variant

    text?.let {childList.add(it)}

    setStyledPropsAndRunHandler(className, handler)
}

