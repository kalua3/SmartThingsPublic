/**
 *  Door Alarm
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
iconUrl: "http://cdn.device-icons.smartthings.com/Health%20&%20Wellness/health7-icn.png",
iconX2Url: "http://cdn.device-icons.smartthings.com/Health%20&%20Wellness/health7-icn@2x.png",
iconX3Url: "http://cdn.device-icons.smartthings.com/Health%20&%20Wellness/health7-icn@3x.png")

preferences {
    page(name: "page1", title: "Welcome", install: true, uninstall: true) {
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
        	input(name: "buttonPushedDelay", type: "number", title: "Pushed Delay (seconds)", required: true)
            input(name: "buttonHeldDelay", type: "number", title: "Held Delay (seconds)", required: true)
        }
        section("And also...") {
        	input(name: "isFlashStrobeWhenDelayed", type: "bool", title: "Flash the siren strobes while delaying?", required: true)
            input(name: "doesButtonPressStopAlarm", type: "bool", title: "Does pressing a button stop the siren if it is sounding?", required: true)
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
	unschedule()
    atomicState.isDelayed = false
    subscribe(targetSensor, "contact", contactHandler)
    subscribe(targetButtons, "button", buttonHandler)
}

def contactHandler(evt) {
  log.debug "Contact is in ${evt.value} state"
  if("open" == evt.value) {
  	atomicState.sensorState = "open"
  
	if(atomicState.isDelayed == false) {
        // contact was opened, turn on a light maybe?
        log.debug "Delay is ${atomicState.isDelayed}"
        // trigger alarm
        targetAlarms.both()
        //targetAlarms.on()
    }
  } else if("closed" == evt.value) {
  	atomicState.sensorState = "closed"
    
    if(atomicState.isDelayed == true) {
        log.debug "door closed. turning off delay and strobe."
    	atomicState.isDelayed = false
        targetAlarms.off()
    }
  }
}

def buttonHandler(evt) {
  if(doesButtonPressStopAlarm) {
  	targetAlarms.off()
  }
  
  atomicState.isDelayed = true
  if (evt.value == "held") {
    log.debug "button was held"
    schedule(now() + (buttonHeldDelay * 1000), scheduleFinishedHandler)
  } else if (evt.value == "pushed") {
  	log.debug "button was pushed"
    schedule(now() + (buttonPushedDelay * 1000), scheduleFinishedHandler)
  }
  
  log.debug "is flash strobe when delayed: ${isFlashStrobeWhenDelayed}"
  if(isFlashStrobeWhenDelayed) {
  	targetAlarms.strobe()
  }
}

def scheduleFinishedHandler(evt) {
    if(atomicState.isDelayed == true) {
        log.debug "schedule was triggered and isDelayed is ${atomicState.isDelayed}"
        atomicState.isDelayed = false
        targetAlarms.off()

        if(atomicState.sensorState == "open") {
            log.debug "contact was open during schedule handler"
            targetAlarms.both()
            //targetAlarms.on()
        }
    }
}