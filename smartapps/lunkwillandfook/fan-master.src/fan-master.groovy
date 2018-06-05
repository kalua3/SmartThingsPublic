/**
 *	Fan Master
 *
 *	Author: Jeremy Huckeba
 *	Date Created: 2018-06-05
 *  Last Updated: 2018-06-05
 *
 */
definition(
    name: "Fan Master",
    namespace: "LunkwillAndFook",
    author: "Jeremy Huckeba",
    description: "Trigger fan speed when a routine is run",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/MyApps/Cat-MyApps.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/MyApps/Cat-MyApps@2x.png"
)

preferences {
	page(name: "page1", title: "Welcome", nextPage: "page2", uninstall: true) {
    	section() {
        	paragraph "Welcome. This app will let you configure fan speeds that will be set when a routine is run. Just name this configuration, select a routine, select your fans, and select the speeds you want to set." 
    		label title: "Assign a name", required: false, defaultValue: "Fan Master"
        }
    }
    page(name: "page2", title: "Routine and Fans", uninstall: true, nextPage: "page3")
    page(name: "page3", title: "Fan Speed Configuration", uninstall: true, install: true)    
    
    preferences {
        page(name: "selectActions")
    }
}

def page2() {
    dynamicPage(name: "page2") {
        // get the available actions
        def actions = location.helloHome?.getPhrases()*.label
        if (actions) {
            // sort them alphabetically
            actions.sort()
            section("Hello Home Actions") {
                log.trace actions
                // use the actions as the options for an enum input
                input "triggerRoutine", "enum", title: "Set for specific routine", multiple: false, required: true, options: actions
                input "selectedFans", "capability.fanSpeed", title: "Set these fans", multiple: true, required: false
            }
        }
    }
}

def page3() {
	dynamicPage(name: "page3") {
    	section("fan speeds") {
        	if(selectedFans != null) {
                def i = 0
                selectedFans.each { selectedFan ->
                	def inputName = "fanSpeed$i"
                    input inputName, "number", title: selectedFan.label, range: "0..100", multiple: false, required: true, defaultValue: "100"
                    i++
                }
            } else {
             	paragraph "There are no fans selected."
            }
        }
    }
}

def installed() {
	log.debug "Installed with settings: ${settings}"

	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unsubscribe()
	initialize()
}

def initialize() {
	log.debug "Initializing mode changed handler."
    subscribe(location, "routineExecuted", routineChangedHandler)
}

def routineChangedHandler(evt){
	log.debug "Updated with routine: ${evt.displayName}"
    if(evt.displayName == triggerRoutine) {            
		def i = 0
    	selectedFans.each { selectedFan -> 
        	def desiredSpeed = settings["fanSpeed$i"]
        	selectedFan.setFanSpeed(desiredSpeed)
            i++
        }
    }
}