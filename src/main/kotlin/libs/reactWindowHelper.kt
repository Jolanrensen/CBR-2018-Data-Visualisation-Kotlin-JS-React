package libs

import react.RClass

inline fun <T : Any> RClass<ListProps<T>>.scrollTo(scrollOffset: Int) = asDynamic().scrollTo(scrollOffset) as Unit
inline fun <T : Any> RClass<ListProps<T>>.scrollToItem(index: Int, allign: Alignment) =
    asDynamic().scrollToItem(index, allign.name) as Unit
