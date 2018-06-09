/**
 *	Fan Thermostat
 *
 *	Author: Jeremy Huckeba
 *	Date Created: 2018-06-05
 *  Last Updated: 2018-06-05
 *
 */
definition(
    name: "Fan Thermostat",
    namespace: "LunkwillAndFook",
    author: "Jeremy Huckeba",
    description: "Trigger fan speed when selected temperature sensors exceed a set threshold",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/MyApps/Cat-MyApps.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/MyApps/Cat-MyApps@2x.png"
)

preferences {
	page(name: "page1", title: "Welcome", nextPage: "page2", uninstall: true) {
    	section() {
        	paragraph "Welcome. This app will let you configure fan speeds that will be set when one or more temperature sensors exceed a threshold. Just name this configuration, select the temperature sensors, select your fans, and select the speeds you want to set. Note that there is a 5 minute anti-short-cycle delay to prevent damage to fan motors and switches." 
    		label title: "Assign a name", required: false, defaultValue: "Fan Thermostat"
        }
    }
    page(name: "page2", title: "Temperature Sensors and Fans", uninstall: true, nextPage: "page3")
    page(name: "page3", title: "Configuration", uninstall: true, install: true)    
    
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
                input "selectedTempSensors", "capability.temperatureMeasurement", title: "Monitor these temperature sensors (the average will be taken if more than one)", multiple: true, required: false
                input "selectedFans", "capability.fanSpeed", title: "Set these fans", multiple: true, required: false
                input(name: "onlyForModes", type: "mode", title: "Only for modes:", multiple: true, required: false)
            }
        }
    }
}

def page3() {
	dynamicPage(name: "page3") {
    	section("temperature threshold") {
        	input "temperatureDegrees", "number", title: "temperature threshold", range: "0..100", multiple: false, required: true, defaultValue: "74"
            input "isTriggeredWhen", "enum", title: "triggered when temperature", options: ["Rises Above", "Falls Below"]
            input "shouldTurnOffWhenMeetsThreshold", "bool", title: "turn fans off when temperature meets threshold?"
        }
    	section("fan speeds") {
        	if(selectedFans != null) {
                def i = 0
                selectedFans.each { selectedFan ->
                	def speedInputName = "fanSpeed$i"
                    
                    def supportsFanMode = false
                    selectedFan.supportedAttributes.each { attrib ->
                    	if(attrib.name == "fanMode") supportsFanMode = true
                    }
                    
                    if(supportsFanMode) {
                    	input speedInputName, "enum", title: selectedFan.label, options: ["Turn Off", "Breeze", "Low", "Medium", "Medium-High", "High"], multiple: false, required: true, defaultValue: "Turn Off"
                    } else {
                    	input speedInputName, "number", title: selectedFan.label, range: "0..100", multiple: false, required: true, defaultValue: "100"
                    }
                    i++
                }
                
                input "doNotModifyForMinutesAfterExternallyChanged", "number", title: "skip fan for x minutes if changed by something else (0 means do not skip)", defaultValue: 0
            } else {
             	paragraph "There are no fans selected."
            }
        }
    }
}

def installed() {
	log.trace "Installed with settings: ${settings}"

	initialize()
}

def updated() {
	log.trace "Updated with settings: ${settings}"

	unsubscribe()
	initialize()
}

def initialize() {
	unschedule()
    
    log.trace "Initializing state"
    def triggeredFans = []
    def externallyTriggeredFans = []
   	atomicState.triggeredFans = triggeredFans
    atomicState.externallyTriggeredFans = externallyTriggeredFans
    
	log.trace "Initializing temperature sensor handlers."
    selectedTempSensors.each { selectedTempSensor -> 
    	subscribe(selectedTempSensor, "temperature", temperatureChangedHandler)
    }
    
    log.trace "Initializing fan speed handlers"
    selectedFans.each { selectedFan ->
    	subscribe(selectedFan, "fanSpeed", fanSpeedChangedHandler)
    }
}

def setFanMode(fan, mode) {
	switch(mode) {
    	case "Turn Off":
        	fan.fanOff()
        	break;
        case "Low":
        	fan.fanOne()
        	break;
        case "Medium":
        	fan.fanTwo()
        	break;
        case "Medium-High":
        	fan.fanThree()
        	break;
        case "High":
        	fan.fanFour()
        	break;
        case "Breeze":
        	fan.fanAuto()
        	break;
    }
}

def temperatureChangedHandler(evt) {
	log.trace "Temperature changed for sensor: ${evt.displayName}"
    
    def total = 0
    def count = 0
    // get average temperature for all sensors
    selectedTempSensors.each { selectedTempSensor -> 
    	if(selectedTempSensor.hasCommand("refresh")) {
    		selectedTempSensor.refresh()
        }
        
        count++
        total += selectedTempSensor.currentValue("temperature")
    }
    
    def averageTemperature = total / count
    log.trace "Average temperature is currently $averageTemperature degrees"
    
    if(testMode() == true
    	&& (isTriggeredWhen == "Rises Above" && averageTemperature > temperatureDegrees)
    	|| (isTriggeredWhen == "Falls Below" && averageTemperature < temperatureDegrees)) {
        
        if(atomicState.lastTriggeredOn == null || new Date() >= atomicState.asdTime) {
        	log.trace "Fan thermostat triggered. Setting fan speeds"
            // set fan speed
            unschedule()
            def controlIndex = 0;
            def fanSpeed = 0
            def wasOneFanTriggered = false
            selectedFans.each { selectedFan ->
				if(!getIsLockedByExternalTrigger(selectedFan)) {
                
                	def hasFanMode = false
                    selectedFan.supportedAttributes.each { attrib ->
                    	if(attrib.name == "fanMode") hasFanMode = true
                	}
                    
                    fanSpeed = settings["fanSpeed$controlIndex"]
                    if(hasFanMode) {
                    	setFanMode(selectedFan, fanSpeed)
                    } else {
                    	selectedFan.setFanSpeed(fanSpeed)
                    }
                    
                    // update triggered fan state
                    removeTriggeredFan(selectedFan.id)
                    def triggeredFans = atomicState.triggeredFans
                    triggeredFans << [key: selectedFan.id]
                    atomicState.triggeredFans = triggeredFans
                    
                    wasOneFanTriggered = true
                    controlIndex++
                }
            }

			if(wasOneFanTriggered) {
            	atomicState.asdTime = new Date(new Date().time + (5 * 60 * 1000))
            }

        } else {
        	log.trace "Fan thermostat ASD triggered. Rescheduling"
        	runOnce(atomicState.asdTime, temperatureChangedHandler(evt), [overwrite: true])
        }
    } else if (testMode() == true && shouldTurnOffWhenMeetsThreshold 
    	&& (isTriggeredWhen == "Rises Above" && averageTemperature <= temperatureDegrees)
    	|| (isTriggeredWhen == "Falls Below" && averageTemperature >= temperatureDegrees)) {
        
        if(atomicState.lastTriggeredOn == null || new Date() >= atomicState.asdTime) {
        	log.trace "Fan thermostat triggered. Turning fans off"
            // turn off fans
            unschedule()
            def wasOneFanTriggered = false
            selectedFans.each { selectedFan ->
                selectedFan.setFanSpeed(0)
                wasOneFanTriggered = true
                
                // update triggered fan state
                removeTriggeredFan(selectedFan.id)
                def triggeredFans = atomicState.triggeredFans
                triggeredFans << [key: selectedFan.id]
                atomicState.triggeredFans = triggeredFans
            }

			if(wasOneFanTriggered) {
            	atomicState.asdTime = new Date(new Date().time + (5 * 60 * 1000))
            }
        } else {
        	log.trace "Fan thermostat ASD triggered. Rescheduling"
        	runOnce(atomicState.asdTime, temperatureChangedHandler(evt), [overwrite: true])
        }
    }
}

def removeTriggeredFan(deviceId) {
    atomicState.triggeredFans = atomicState.triggeredFans.findIndexOf{ it.key == deviceId }.with { idx ->
		if( idx > -1 ) {
            new ArrayList( atomicState.triggeredFans ).with { a ->
          		a.remove( idx )
          		a
        	}
      	} else {
            atomicState.triggeredFans
      	}
    }
}

def fanSpeedChangedHandler(evt) {    
    def isExternallyTriggered = true
    atomicState.triggeredFans.findIndexOf{ it.key == evt.deviceId }.with { idx ->
		if( idx > -1 ) {
        	isExternallyTriggered = false
		}
    }
    
    log.trace "Fan speed changed. Externally triggered? $isExternallyTriggered"
    
    removeTriggeredFan(evt.deviceId)
    
    if(isExternallyTriggered && doNotModifyForMinutesAfterExternallyChanged > 0) {
        def deviceId = evt.deviceId
        
        atomicState.externallyTriggeredFans = atomicState.externallyTriggeredFans.findIndexOf{ it.deviceId == evt.deviceId }.with { idx ->
            if( idx > -1 ) {
                new ArrayList( atomicState.externallyTriggeredFans ).with { a ->
                    a.remove( idx )
                    a
                }
            }
        }
        
		if(atomicState.externallyTriggeredFans == null) atomicState.externallyTriggeredFans = []
        
        def externallyTriggeredFans = atomicState.externallyTriggeredFans
        externallyTriggeredFans << [deviceId: deviceId, doNotModifyTime: now() + (doNotModifyForMinutesAfterExternallyChanged * 60 * 1000)]
        atomicState.externallyTriggeredFans = externallyTriggeredFans
    }
}

def testMode() {
	if(onlyForModes == null || onlyForModes.contains(location.currentMode)) {
    	return true
    }
    
    return false
}

def getIsLockedByExternalTrigger(item) {
    def externallyTriggeredFans = atomicState.externallyTriggeredFans
    externallyTriggeredFans = externallyTriggeredFans.findIndexOf { it.deviceId == item.id }.with { idx ->
        if( idx > -1) {
        	def doNotModifyTime = externallyTriggeredFans[idx].doNotModifyTime
            def rightNow = now()
            if(doNotModifyTime < rightNow) {
                new ArrayList( externallyTriggeredFans ).with { a ->
                    a.remove( idx )
                    a
                }
            } else {
            	externallyTriggeredFans
            }
        }
    }
    
    def isLocked = false
    atomicState.externallyTriggeredFans = externallyTriggeredFans
    externallyTriggeredFans.findIndexOf{ it.deviceId == item.id }.with { idx ->
        if( idx > -1 ) {
        	isLocked = true
        }
    }
    
    log.trace "is $item.displayName locked because it was changed externally? $isLocked"
    return isLocked
}