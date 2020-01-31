    package delegates

    import delegates.ReactPropAndStateDelegates.StateAsProp
    import delegates.ReactPropAndStateDelegates.propDelegateOf
    import delegates.ReactPropAndStateDelegates.stateAsProp
    import delegates.ReactPropAndStateDelegates.stateDelegateOf
    import kotlinx.html.js.onClickFunction
    import libs.RPureComponent
    import org.w3c.dom.events.Event
    import react.*
    import react.dom.br
    import react.dom.button
    import react.dom.div
    import styled.styledDiv

    /**
     * ExampleComponent
     *
     * Using a normal React Component: RComponent
     * (RPureComponent would also be fine)
     **/

    // Props (attrs) for ExampleComponent
    interface ExampleComponentProps : RProps

    // State for ExampleComponent
    interface ExampleComponentState : RState {
        var counter: Int
        var someList: List<String>
        var secondCounter: Int
    }

    class ExampleComponent(prps: ExampleComponentProps) : /* name is not "props" on purpose to avoid shadowing */
        RComponent<ExampleComponentProps, ExampleComponentState>(prps) {

        /* ExampleComponent has no props */

        // Initialize state of ExampleComponent (can include props)
        override fun ExampleComponentState.init(props: ExampleComponentProps) {
            counter = 0
            someList = listOf()
            secondCounter = 0
        }

        // Create state delegates for easy operation and access to the state items
        private var counter by stateDelegateOf(ExampleComponentState::counter)
        private var someList by stateDelegateOf(ExampleComponentState::someList)
        private var secondCounter by stateDelegateOf(ExampleComponentState::secondCounter)


        private val increaseCounter: (Event?) -> Unit = {
            counter++ // Thanks to the state delegate this calls setState for you!
        }

        private val increaseSecondCounter: (Event?) -> Unit = {
            secondCounter++
        }

        private val someString // getter to make sure the state updates
            get() = "Hi this is a string from the parent containing counter: $counter"

        override fun RBuilder.render() {
            div {
                +"ExampleComponent:"
                br {}
                +"Counter is currently at $counter, second counter is at $secondCounter"
                br {}
                +"List is currently: $someList"
                br {}
                button {
                    attrs.onClickFunction = increaseCounter
                    +"Increase counter"
                }
                button {
                    attrs.onClickFunction = increaseSecondCounter
                    +"Increase second counter"
                }
                br {}
                br {}
                exampleChild {
                    attrs {
                        counter = stateAsProp(ExampleComponentState::counter) // Provides a child with R/W access to counter
                        someString = this@ExampleComponent.someString // someString is a getter, so it works like a function call
                        someList = stateAsProp(ExampleComponentState::someList)
                        secondCounter = this@ExampleComponent.secondCounter // Would be the same as typing "state.secondCounter"
                    }
                }
            }
        }
    }

    fun RBuilder.exampleComponent(handler: (RElementBuilder<ExampleComponentProps>.() -> Unit)? = null) =
        child(ExampleComponent::class) {
            handler?.invoke(this)
        }


    /**
     * ExampleChild
     *
     * Using a React Pure Component: RPureComponent
     * (RComponent would also be fine)
     */

    // Props (attrs) for ExampleChild
    interface ExampleChildProps : RProps {
        var counter: StateAsProp<Int>
        var someString: String
        var someList: StateAsProp<List<String>>
        var secondCounter: Int
    }

    // State for ExampleChild
    interface ExampleChildState : RState

    class ExampleChild(prps: ExampleChildProps) :
            RPureComponent<ExampleChildProps, ExampleChildState>(prps) {

        /* ExampleChild has props, so create delegates */

        // counter is a StateAsProp, a getter and setter, so use "var" (or "val" if you want it to be final)
        private var counter by propDelegateOf(ExampleChildProps::counter)

        // someString is a normal (read-only) property. Using a prop delegate, using "val" is enforced
        private val someString by propDelegateOf(ExampleChildProps::someString)

        // again a StateAsProp
        private var someList by propDelegateOf(ExampleChildProps::someList)

        // secondCounter is again a read-only prop
        private val secondCounter by propDelegateOf(ExampleChildProps::secondCounter)

        // Initialize state of ExampleChild (can be omitted if not used, like now)
        override fun ExampleChildState.init(props: ExampleChildProps) {}


        private val increaseCounter: (Event?) -> Unit = {
            counter++ // This works fine now! It updates counter in the parent component's state
        }

        // More advanced example
        private val addItemToList: (String) -> (Event?) -> Unit = { item ->
            {
                someList += item     // yes, that did just compile, amazing right?
            }
        }

        override fun RBuilder.render() {
            styledDiv {
                +"ExampleChild:"
                br {}
                +"Counter is currently $counter, secondCounter is $secondCounter in child component"
                br {}
                +someString
                br {}
                button {
                    attrs.onClickFunction = increaseCounter
                    +"Increase counter from child"
                }
                button {
                    attrs.onClickFunction = addItemToList("ITEM ")
                    +"Add \"ITEM \" to the list"
                }
            }
        }
    }

    fun RBuilder.exampleChild(handler: RElementBuilder<ExampleChildProps>.() -> Unit) =
        child(ExampleChild::class) {
            handler()
        }