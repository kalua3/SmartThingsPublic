/**
 *  Pump Scheduler
 *
 *  Copyright 2015 Jeremy Huckeba
 *
 */
definition(
name: "Pump Scheduler with Optional Bypass Switch",
namespace: "LunkwillAndFook",
author: "Jeremy Huckeba",
description: "Enables advanced scheduling and 24-hour schedule bypass for pump switches.",
category: "My Apps",
iconUrl: "http://cdn.device-icons.smartthings.com/Health%20&%20Wellness/health2-icn.png",
iconX2Url: "http://cdn.device-icons.smartthings.com/Health%20&%20Wellness/health2-icn@2x.png",
iconX3Url: "http://cdn.device-icons.smartthings.com/Health%20&%20Wellness/health2-icn@3x.png")

preferences {
    page(name: "page1", title: "Welcome", nextPage: "page2", install: false, uninstall: true) {
        section() {
            paragraph "Welcome. This app will let you schedule a pump switch with options for a 24-hour mode switch which can be used to bypass the schedule for 24 hours before automatically resetting." 
            label title: "Assign a name", required: false
            input(name: "targetSwitch", type: "capability.switch", title: "The pump switch to target.", multiple: false, required: true)
        }
    }
    page(name: "page2", title: "Schedule", nextPage: "page3", install: false, uninstall: true) {
        section("Primary start time") {
            input(name: "startTime1", type: "time", title: "The daily start time", multiple: false, required: true)
            input(name: "endTime1", type: "time", title: "The daily end time", multiple: false, required: true)
        }
        section("Secondary start time") {
            input(name: "startTime2", type: "time", title: "The second daily start time (optional)", multiple: false, required: false)
            input(name: "endTime2", type: "time", title: "The second daily end time (optional)", multiple: false, required: false)
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
    log.debug "scheduling for start time $startTime1"
    runDaily(startTime1, scheduleStartHandler)
    log.debug "scheduling for end time $endTime1"
    runDaily(endTime1, scheduleEndHandler)

    if(startTime2 != null) {
        log.debug "scheduling for optional secondary start time $startTime2"
        runDaily(startTime2, scheduleStartHandler)
    }

    if(endTime2 != null) {
        log.debug "scheduling for optional secondary end time $endTime2"
        runDaily(endTime2, scheduleEndHandler)
    }
}

def scheduleStartHandler() {
    if(!bypassModes.contains(location.mode)) {
        if(bypassSwitch == null || bypassSwitch.currentState("switch").value == "off") {
            log.debug "target switch turned on per schedule"
            targetSwitch.on();
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
            targetSwitch.off();
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
        targetSwitch.on()
        log.debug "bypass switch scheduled to turn off at ${tomorrow}"
    } else if (evt.value == "off") {
        log.debug "bypass switch turned off"
        unschedule("twentyFourHourHandler")
    }
}

def twentyFourHourHandler() {
    log.debug "24 hour handler run"
    bypassSwitch.off()
    targetSwitch.off()
}