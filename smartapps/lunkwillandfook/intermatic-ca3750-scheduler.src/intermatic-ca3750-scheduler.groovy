/**
 *  Intermatic CA3750 Scheduler
 *
 *  Copyright 2015 Jeremy Huckeba
 *
 */
definition(
    name: "Intermatic CA3750 Scheduler",
    namespace: "LunkwillAndFook",
    author: "Jeremy Huckeba",
    description: "Enables advanced scheduling and 24-hour schedule bypass for the Intermatic CA3750.",
    category: "My Apps",
    iconUrl: "http://cdn.device-icons.smartthings.com/Health%20&%20Wellness/health2-icn.png",
    iconX2Url: "http://cdn.device-icons.smartthings.com/Health%20&%20Wellness/health2-icn@2x.png",
    iconX3Url: "http://cdn.device-icons.smartthings.com/Health%20&%20Wellness/health2-icn@3x.png")

preferences {
	page(name: "page1", title: "Welcome", nextPage: "page2", install: false, uninstall: true) {
    	section() {
        	paragraph "Welcome. This app will let you schedule the first relay of an Intermatic CA3750 with options for a 24-hour mode which can be used to bypass the schedule." 
    		label title: "Assign a name", required: false
			input(name: "targetSwitch", type: "capability.switch", title: "The Intermatic CA3750 switch to target.", multiple: false, required: true)
		}
	}
	page(name: "page2", title: "Schedule", nextPage: "page3", install: false, uninstall: true) {
    	section() {
        	input(name: "startTime", type: "time", title: "The daily start tme", multiple: false, required: true)
			input(name: "endTime", type: "time", title: "The daily end time", multiple: false, required: true)
		}
	}
	page(name: "page3", title: "Schedule", install: true, uninstall: true) {
    	section() {
        	input(name: "bypassSwitch", type: "capability.switch", title: "The switch which, when on, bypasses the schedule and operates the target switch for 24 hours.", multiple: false, required: false)
			input(name: "bypassModes", type: "mode", title: "Modes which will bypass the schedule.", multiple: true, required: false)
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
	subscribe(bypassSwitch, "switch.on", bypassSwitchHandler)
	log.debug "scheduling for start time $startTime"
	runDaily(startTime, scheduleStartHandler)
	log.debug "scheduling for end time $endTime"
	runDaily(endTime, scheduleEndHandler)
}

def scheduleStartHandler() {
	if(!bypassModes.contains(location.mode)) {
		if(bypassSwitch == null || bypassSwitch.currentState("switch").value == "off") {
			log.debug "target switch turned on per schedule"
			targetSwitch.on1();
		} else {
			log.debug "schedule start bypassed due to bypass switch"
		}
	} else {
		log.debug "start schedule bypassed because current mode is ${location.mode}"
	}
}

def scheduleEndHandler() {
	if(!bypassModes.contains(location.mode)) {
		if(bypassSwitch == null || bypassSwitch.currentState("switch").value == "off") {
			log.debug "bypass switch turned off per schedule"
			targetSwitch.off1();
		} else {
			log.debug "schedule end bypassed due to bypass switch"
		}
	} else {
		log.debug "end schedule bypassed because current mode is ${location.mode}"
	}
}

def bypassSwitchHandler(evt) {
	if (evt.value == "on") {
		log.debug "bypass switch turned on"
		def today = new Date()
		def tomorrow = today + 1
		schedule(tomorrow, twentyFourHourHandler)
		targetSwitch.on1()
        log.debug "bypass switch scheduled to turn off at ${tomorrow}"
	} else if (evt.value == "off") {
		log.debug "bypass switch turned off"
		unschedule("twentyFourHourHandler")
	}
}

def twentyFourHourHandler() {
	log.debug "24 hour handler run"
	bypassSwitch.off()
	targetSwitch.off1()
}