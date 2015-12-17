/**
 *  Light-Master
 *
 *  Copyright 2015 Jeremy Huckeba
 *
 */
definition(
    name: "Light Master",
    namespace: "LunkwillAndFook",
    author: "Jeremy Huckeba",
    description: "Enables advanced automatic dimming and switching when the mode changes for up to 20 devices.",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")

preferences {
	page(name: "page1", title: "Welcome", nextPage: "page2", uninstall: true) {
    	section() {
        	paragraph "Welcome. This app will let you configure dimming levels per-switch that will be set when the location changes to a specific mode. Just name this configuration, select a mode, select your switches, and select the levels you want to set. You can then create a routine in the SmartThings app to change the location mode." 
    		label title: "Assign a name", required: false
        }
    }
	page(name: "page2", title: "Mode and Switches", nextPage: "page3", uninstall: true) {
    	section() {
        	input(name: "triggerMode", type: "mode", title: "Set for specific mode", multiple: false, required: true)
            input(name: "selectedSwitches", type: "capability.switch", title: "Set these switches", multiple: true, required: true)
	    }
	}
    page(name: "page3", title: "Switch Levels", uninstall: true, nextPage: "page4")
	page(name: "page4", title: "Set Mode", uninstall: true, install: true) {
    	section() {
        	input(name: "setMode", type: "mode", title: "Then set this mode", multiple: false, required: false)
	    }
	}
}

def page3() {
	dynamicPage(name: "page3") {
    	section() {
        	def i = 0
            selectedSwitches.each { selectedSwitch ->
            	if(i < 20) {
                	def inputName = "switchLevel$i"
                    input(name: inputName, type: "enum", title: selectedSwitch.label, multiple: false, required: true, options: getSwitchLevelOptions(selectedSwitch))
                    i++
                }
            }
        }
    }
}

private getSwitchLevelOptions(selectedSwitch) {
	if(selectedSwitch.hasCommand("setLevel")) {
    	// dimmable switch options
        return ["Off", "5%", "10%", "15%", "20%", "25%", "30%", "35%", "40%", "45%", "50%", "55%", "60%", "65%", "70%", "75%", "80%", "85%", "90%", "95%", "On" ]
    } else {
    	// relay switch options
        return ["Off", "On" ]
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
	subscribe(location, "mode", modeChangeHandler)
}

def modeChangeHandler(evt){
	log.debug "Updated with mode: ${evt}"
    if(evt.value == triggerMode) {
    	def i = 0;
    	selectedSwitches.each { selectedSwitch -> 
        	if(i < 20) {
        		setSwitchLevel(selectedSwitch, i)
            }
            i++
        }
    }
}

private setSwitchLevel(selectedSwitch, levelIndex) {
	def desiredLevel = getLevel(levelIndex)
	if(selectedSwitch.hasCommand("setLevel")) {	
        selectedSwitch.setLevel(desiredLevel)
    } else {
    	if(desiredLevel > 0) {
        	selectedSwitch.on()
        } else {
        	selectedSwitch.off()
        }
    }
    
    if(setMode != null) {
    	location.setMode(setMode)
    }
}

private getLevel(levelIndex) {
	def result = 0;
    switch(levelIndex) {
    	case 0:
        	return parseLevel(switchLevel0)
    	case 1:
        	return parseLevel(switchLevel1)
    	case 2:
        	return parseLevel(switchLevel2)
    	case 3:
        	return parseLevel(switchLevel3)
    	case 4:
        	return parseLevel(switchLevel4)
    	case 5:
        	return parseLevel(switchLevel5)
    	case 6:
        	return parseLevel(switchLevel6)
    	case 7:
        	return parseLevel(switchLevel7)
    	case 8:
        	return parseLevel(switchLevel8)
    	case 9:
        	return parseLevel(switchLevel9)
    	case 10:
        	return parseLevel(switchLevel10)
    	case 11:
        	return parseLevel(switchLevel11)
    	case 12:
        	return parseLevel(switchLevel12)
    	case 13:
        	return parseLevel(switchLevel13)
    	case 14:
        	return parseLevel(switchLevel14)
    	case 15:
        	return parseLevel(switchLevel15)
    	case 16:
        	return parseLevel(switchLevel16)
    	case 17:
        	return parseLevel(switchLevel17)
    	case 18:
        	return parseLevel(switchLevel18)
    	case 19:
        	return parseLevel(switchLevel19)
    	case 20:
        	return parseLevel(switchLevel20)
    }
}

private parseLevel(selectedLevel) {
	switch(selectedLevel) {
    	case "On":
        	return 100
        case "95%":
        	return 95
        case "90%":
        	return 90
        case "85%":
        	return 85
        case "80%":
        	return 80
        case "75%":
        	return 75
        case "70%":
        	return 70
        case "65%":
        	return 65
        case "60%":
        	return 60
        case "55%":
        	return 55
        case "50%":
        	return 50
        case "45%":
        	return 45
        case "40%":
        	return 40
        case "35%":
        	return 35
        case "30%":
        	return 30
        case "25%":
        	return 25
        case "20%":
        	return 20
        case "15%":
        	return 15
        case "10%":
        	return 10
        case "5%":
        	return 5
        case "Off":
        	return 0
    }
}