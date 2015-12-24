/**
 *  Ghost Lights
 *
 *  Copyright 2015 Jeremy Huckeba
 *
 */
definition(
name: "Ghost Lights",
namespace: "LunkwillAndFook",
author: "Jeremy Huckeba",
description: "Turns lights on and off to make it appear that you are home when you're not.",
category: "My Apps",
iconUrl: "http://cdn.device-icons.smartthings.com/Health%20&%20Wellness/health2-icn.png",
iconX2Url: "http://cdn.device-icons.smartthings.com/Health%20&%20Wellness/health2-icn@2x.png",
iconX3Url: "http://cdn.device-icons.smartthings.com/Health%20&%20Wellness/health2-icn@3x.png")

preferences {
    page(name: "page1", title: "Welcome", nextPage: "page2", install: true, uninstall: true) {
        section() {
            paragraph "Welcome. This app will randomly cycle between the selected light switches turning them on and off every 30 minutes to 60 minutes for the selected modes." 
            label title: "Assign a name", required: false
            input(name: "targetDevices", type: "capability.switch", title: "The switches to target.", multiple: true, required: true)
            input(name: "triggerMode", type: "mode", title: "Modes which will cause the app to run.", multiple: true, required: true)
        }
    }
}

def random = new Random()
def currentDevice

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
    scheduleHandler()
}

def scheduleHandler() {
    if(triggerModes.contains(location.mode)) {
        if(currentDevice != null) {
            currentDevice.off()
            debug.log "device ${currentDevice} turned off"
        }

        def newDevice = getRandomDevice()
        while(newDevice == currentDevice) {
            newDevice = getRandomDevice()
        }

        debug.log "new device ${newDevice} acquired"
        
        currentDevice = newDevice
        currentDevice.on()
        debug.log "device ${newDevice} turned on"

        def randomMinuteIndex = Math.abs(random.nextInt() % 60) + 30
        log.debug "randomMinuteIndex: ${randomMinuteIndex}"
        def chron = "0 0/${randomMinuteIndex} * * * ?"
        schedule(chron, scheduleHandler)
        debug.log "next schedule to run in ${randomMinuteIndex} minutes"
    } else {
        log.debug "scheduleHandler bypassed because of current mode"
    }
}

private getRandomDevice() {
    def randomDeviceIndex = Math.abs(random.nextInt() % targetDevices.length)
    return targetDevices[randomDeviceIndex]
}