/**
 *  Ghost Lights
 *
 *  Copyright 2015 Jeremy Huckeba
 *
 */
definition(
name: "Door Alarm",
namespace: "LunkwillAndFook",
author: "Jeremy Huckeba",
description: "Trigger an alarm unless a button is pressed.",
category: "My Apps",
iconUrl: "http://cdn.device-icons.smartthings.com/Health%20&%20Wellness/health2-icn.png",
iconX2Url: "http://cdn.device-icons.smartthings.com/Health%20&%20Wellness/health2-icn@2x.png",
iconX3Url: "http://cdn.device-icons.smartthings.com/Health%20&%20Wellness/health2-icn@3x.png")

preferences {
    page(name: "page1", title: "Welcome", nextPage: "page2", install: true, uninstall: true) {
    	section("Warning") {
            paragraph "No warranty for any purpose expressed or implied. Use at your own risk!" 
            label title: "Assign a name", required: false
        }
        section("When this sensor is opened...") {
            input(name: "targetSensor", type: "capability.contactSensor", title: "Open/Closed Sensor", multiple: false, required: true)
        }
        section("Sound these sirens...") {
            input(name: "targetAlarms", type: "capability.alarm", title: "Alarms", multiple: true, required: true)
        }
        section("Unless one of these buttons is pressed or held...") {
            input(name: "targetButtons", type: "capability.button", title: "Buttons", multiple: true, required: true)
        }
        section("Then delay sounding the alarm for...") {
        	input(name: "buttonPressedDelaySeconds", type: "number", title: "Pressed Delay (seconds)", required: true)
            input(name: "buttonHeldDelaySeconds", type: "number", title: "Held Delay (seconds)", required: true)
        }
        section("And also...") {
        	input(name: "isFlashStrobeWhenDelayed", type: "bool", title: "Flash the siren strobes while delaying?", required: true)
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
    
}