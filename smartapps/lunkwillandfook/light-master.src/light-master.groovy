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
    iconUrl: "http://cdn.device-icons.smartthings.com/Lighting/light14-icn.png",
    iconX2Url: "http://cdn.device-icons.smartthings.com/Lighting/light14-icn@2x.png",
    iconX3Url: "http://cdn.device-icons.smartthings.com/Lighting/light14-icn@3x.png")

preferences {
	page(name: "page1", title: "Welcome", nextPage: "page2", uninstall: true) {
    	section() {
        	paragraph "Welcome. This app will let you configure dimming levels per-switch that will be set when the location changes to a specific mode. Just name this configuration, select a mode, select your switches, and select the levels you want to set. You can then create a routine in the SmartThings app to change the location mode." 
    		label title: "Assign a name", required: false, defaultValue: "Light Master"
        }
    }
	page(name: "page2", title: "Mode and Switches", nextPage: "page3", uninstall: true) {
    	section() {
        	input "triggerMode", "mode", title: "Set for specific mode", multiple: false, required: true
            input "selectedSwitches", "capability.switch", title: "Set these switches", multiple: true, required: false
            input "selectedColorControls", "capability.colorControl", title: "Set these color controls", multiple: true, required: false
            input "selectedColorTemperatureControls", "capability.colorTemperature", title: "Set these color temperature controls", multiple: true, required: false
	    }
	}
    page(name: "page3", title: "Switch Levels", uninstall: true, nextPage: "page4")
	page(name: "page4", title: "Set Mode", uninstall: true, install: true) {
    	section() {
        	input "setMode", "mode", title: "Then set this mode", multiple: false, required: false
	    }
	}
}

def page3() {
	dynamicPage(name: "page3") {
    	section("dimmer levels") {
        	if(selectedSwitches != null) {
                def i = 0
                selectedSwitches.each { selectedSwitch ->
                    if(i < 20) {
                        def inputName = "switchLevel$i"
                        input inputName, "enum", title: selectedSwitch.label, multiple: false, required: true, options: getSwitchLevelOptions(selectedSwitch)
                        i++
                    }
                }
            } else {
             	paragraph "There are no switches selected."
            }
        }
		section("colors") {
        	if(selectedColorControls != null) {
                def i = 0
                selectedColorControls.each { selectedControl ->
                    if(i < 20) {
                        def colorInputName = "color$i"
                        def saturationInputName = "saturation$i"
                        def colorTitle = "${selectedControl.label} color"
                        def saturationTitle = "${selectedControl.label} saturation"
                        input colorInputName, "enum", title: colorTitle, options: ["Red","Brick Red","Safety Orange","Dark Orange","Amber","Gold","Yellow","Electric Lime","Lawn Green","Bright Green","Lime","Spring Green","Turquoise","Aqua","Sky Blue","Dodger Blue","Navy Blue","Blue","Han Purple","Electric Indigo","Electric Purple","Orchid Purple","Magenta","Hot Pink","Deep Pink","Raspberry","Crimson","Red"], multiple: false, required: false
                        input saturationInputName, "number", title: saturationTitle, range: "0..100", defaultValue: 100, multiple: false, required: true
                        i++
                    }
                }
             } else {
             	paragraph "There are no color controls selected."
             }
        }
        def i = 0
        section("color temperatures") {
        	if(selectedColorTemperatureControls != null) {
                selectedColorTemperatureControls.each { selectedControl ->
                    if(i < 20) {
                        def inputName = "colorTemperature$i"
                        input inputName, "number", title: selectedControl.label, range:"(2700..6500)", multiple: false, required: false
                        i++
                    }
                }
          } else {
             	paragraph "There are no color temperature controls selected."
             }
        }
    }
}

private getSwitchLevelOptions(selectedSwitch) {
	if(selectedSwitch.hasCommand("setLevel")) {
    	// dimmable switch options
        return ["Off", "5%", "10%", "15%", "20%", "25%", "30%", "35%", "40%", "45%", "50%", "55%", "60%", "65%", "70%", "75%", "80%", "85%", "90%", "95%", "100%" ]
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
	log.debug "Initializing mode changed handler."
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
        
        i = 0
    	selectedColorControls.each { selectedControl -> 
        	if(i < 20) {
        		setColor(selectedControl, i)
            }
            i++
        }
        
        i = 0
    	selectedColorTemperatureControls.each { selectedControl -> 
        	if(i < 20) {
        		setColorTemperature(selectedControl, i)
            }
            i++
        }
        
        if(setMode != null) {
    		location.setMode(setMode)
    	}
    }
}

private setSwitchLevel(selectedSwitch, levelIndex) {
    def desiredLevel = parseLevel(settings["switchLevel$levelIndex"])
	if(selectedSwitch.hasCommand("setLevel")) {	
        selectedSwitch.setLevel(desiredLevel)
    } else {
    	if(desiredLevel > 0) {
        	selectedSwitch.on()
        } else {
        	selectedSwitch.off()
        }
    }
}

def setColor(selectedLight, controlIndex) {
    def hueValue = getHue(settings["color$controlIndex"])
    def satValue = settings["saturation$controlIndex"]
    def colorValue = [level:null, saturation:satValue.toInteger(), hue:hueValue, alpha:1.0]
    
    def cmd = []
    cmd << selectedLight.setColor(colorValue)
   	cmd << "delay 200"
    cmd << selectedLight.setColor(colorValue)
    cmd
}

def setColorTemperature(selectedLight, controlIndex) {
	def colorTemp = settings["colorTemperature$controlIndex"]
    
    def cmd = []
    cmd << selectedLight.setColorTemperature(colorTemp)
    cmd << "delay 200"
    cmd << selectedLight.setColorTemperature(colorTemp)
    cmd
}

private parseLevel(selectedLevel) {
	switch(selectedLevel) {
    	case "100%":
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

private getHue(colorName) {
    def hueValue = 0.0
    
	if(colorName == "Red") {
    	hueValue = 0
    }
    else if(colorName == "Brick Red") {
    	hueValue = 13
    }
    else if(colorName == "Safety Orange") {
    	hueValue = 26
    }
    else if(colorName == "Dark Orange") {
    	hueValue = 36
    }
    else if(colorName == "Amber") {
    	hueValue = 45
    }
    else if(colorName == "Gold") {
    	hueValue = 53
    }
    else if(colorName == "Yellow") {
    	hueValue = 61
    }
    else if(colorName == "Electric Lime") {
    	hueValue = 75
    }
    else if(colorName == "Lawn Green") {
    	hueValue = 89
    }
    else if(colorName == "Bright Green") {
    	hueValue = 103
    }
    else if(colorName == "Lime") {
    	hueValue = 124
    }
    else if(colorName == "Spring Green") {
    	hueValue = 151
    }
    else if(colorName == "Turquoise") {
    	hueValue = 169
    }
    else if(colorName == "Aqua") {
    	hueValue = 180
    }
    else if(colorName == "Sky Blue") {
    	hueValue = 196
    }
    else if(colorName == "Dodger Blue") {
    	hueValue = 211
    }
    else if(colorName == "Navy Blue") {
    	hueValue = 221
    }
    else if(colorName == "Blue") {
    	hueValue = 238
    }
    else if(colorName == "Han Purple") {
    	hueValue = 254
    }
    else if(colorName == "Electric Indigo") {
    	hueValue = 266
    }
    else if(colorName == "Electric Purple") {
    	hueValue = 282
    }
    else if(colorName == "Orchid Purple") {
    	hueValue = 295
    }
	else if(colorName == "Magenta") {
    	hueValue = 308
    }
    else if(colorName == "Hot Pink") {
    	hueValue = 642
    }
    else if(colorName == "Deep Pink") {
    	hueValue = 331
    }
    else if(colorName == "Raspberry") {
    	hueValue = 338
    }
    else if(colorName == "Crimson") {
    	hueValue = 346
    }
    
    hueValue = Math.round(hueValue * 100 / 360)
    
    hueValue
}