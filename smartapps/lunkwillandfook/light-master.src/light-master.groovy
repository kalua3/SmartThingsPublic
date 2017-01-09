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
	page(name: "page2", title: "Mode and Controls", nextPage: "page3", uninstall: true) {
    	section() {
        	input "triggerMode", "mode", title: "Set for specific mode", multiple: false, required: true
            input "selectedSwitches", "capability.switch", title: "Set these switches", multiple: true, required: false
            input "selectedColorControls", "capability.colorControl", title: "Set these color controls", multiple: true, required: false
            input "selectedColorTemperatureControls", "capability.colorTemperature", title: "Set these color temperature controls", multiple: true, required: false
	    }
	}
    page(name: "page3", title: "Control Configuration", uninstall: true, nextPage: "page4")
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
                    //if(i < 20) {
                        def inputName = "switchLevel$i"
                        if(selectedSwitch.hasCommand("setLevel")) {
                        	input inputName, "number", title: selectedSwitch.label, range: "0..100", multiple: false, required: true, defaultValue: "100"
                        } else {
                        	input inputName, "enum", title: selectedSwitch.label, multiple: false, required: true, options: ["On", "Off"], defaultValue: "On"
                        }
                        i++
                    //}
                }
            } else {
             	paragraph "There are no switches selected."
            }
        }
		section("colors") {
        	if(selectedColorControls != null) {
                def i = 0
                selectedColorControls.each { selectedControl ->
                    //if(i < 20) {
                        def colorInputName = "color$i"
                        def saturationInputName = "saturation$i"
                        def colorTitle = "${selectedControl.label} color"
                        def saturationTitle = "${selectedControl.label} saturation"
                        input colorInputName, "enum", title: colorTitle, options: ["Red","Brick Red","Safety Orange","Dark Orange","Amber","Gold","Yellow","Electric Lime","Lawn Green","Bright Green","Lime","Spring Green","Turquoise","Aqua","Sky Blue","Dodger Blue","Navy Blue","Blue","Han Purple","Electric Indigo","Electric Purple","Orchid Purple","Magenta","Hot Pink","Deep Pink","Raspberry","Crimson","Red"], multiple: false, required: false
                        input saturationInputName, "number", title: saturationTitle, range: "0..100", defaultValue: 100, multiple: false, required: true
                        i++
                    //}
                }
             } else {
             	paragraph "There are no color controls selected."
             }
        }
        def i = 0
        section("color temperatures") {
        	if(selectedColorTemperatureControls != null) {
                selectedColorTemperatureControls.each { selectedControl ->
                    //if(i < 20) {
                        def inputName = "colorTemperature$i"
                        input inputName, "number", title: selectedControl.label, range:"(2700..6500)", multiple: false, required: false
                        i++
                    //}
                }
          } else {
             	paragraph "There are no color temperature controls selected."
             }
        }
    }
}

//private getSwitchLevelOptions(selectedSwitch) {
//	if(selectedSwitch.hasCommand("setLevel")) {
//    	// dimmable switch options
//        return ["Off", "5%", "10%", "15%", "20%", "25%", "30%", "35%", "40%", "45%", "50%", "55%", "60%", "65%", "70%", "75%", "80%", "85%", "90%", "95%", "100%" ]
//    } else {
//    	// relay switch options
//        return ["Off", "On" ]
//    }
//}

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
    	selectedColorControls.each { selectedControl -> 
        	//if(i < 20) {
        		setColor(selectedControl, i)
            //}
            i++
        }
        
        i = 0
    	selectedColorTemperatureControls.each { selectedControl -> 
        	//if(i < 20) {
        		setColorTemperature(selectedControl, i)
            //}
            i++
        }
        
		i = 0
    	selectedSwitches.each { selectedSwitch -> 
        	//if(i < 20) {
        		setSwitchLevel(selectedSwitch, i)
            //}
            i++
        }
        
        if(setMode != null) {
			log.trace "setMode: $setMode"
			setLocationMode(setMode)
    	}
    }
}

private setSwitchLevel(selectedSwitch, levelIndex) {
    def desiredLevel = settings["switchLevel$levelIndex"]
    log.trace "setting switch $selectedSwitch.label to $desiredLevel"
	if(selectedSwitch.hasCommand("setLevel")) {
        selectedSwitch.setLevel(desiredLevel)
    } else {
    	if(desiredLevel == "On") {
        	selectedSwitch.on()
        } else {
        	selectedSwitch.off()
        }
    }
}

def setColor(selectedLight, controlIndex) {
	def colorType = "RGB"
    if(selectedLight.name.toLowerCase().contains("hue")) {
        colorType = "CIE"
    }

    def hueValue = getHue(settings["color$controlIndex"], colorType)
    def satValue = settings["saturation$controlIndex"]
    def colorValue = [level:null, saturation:satValue.toInteger(), hue:hueValue, alpha:1.0]
    selectedLight.setColor(colorValue)
}

def setColorTemperature(selectedLight, controlIndex) {
	def colorTemp = settings["colorTemperature$controlIndex"]
    selectedLight.setColorTemperature(colorTemp)
}

def getHue(colorName, colorType) {
    def hueValue = 0.0
    
	if(colorName == "Red") {
    	hueValue = 0
    }
    else if(colorName == "Brick Red") {
    	hueValue = 2
    }
    else if(colorName == "Safety Orange") {
    	hueValue = 4
    }
    else if(colorName == "Dark Orange") {
    	hueValue = 13
    }
    else if(colorName == "Amber") {
    	hueValue = 17
    }
    else if(colorName == "Gold") {
    	hueValue = 24
    }
    else if(colorName == "Yellow") {
    	hueValue = 36
    }
    else if(colorName == "Electric Lime") {
    	hueValue = 70
    }
    else if(colorName == "Lawn Green") {
    	hueValue = 120
    }
    else if(colorName == "Bright Green") {
    	hueValue = 110
    }
    else if(colorName == "Lime") {
    	hueValue = 90
    }
    else if(colorName == "Spring Green") {
    	hueValue = 100
    }
    else if(colorName == "Turquoise") {
    	hueValue = 140
    }
    else if(colorName == "Aqua") {
        hueValue = 160
    }
    else if(colorName == "Sky Blue") {
    	hueValue = 190
    }
    else if(colorName == "Dodger Blue") {
    	hueValue = 210
    }
    else if(colorName == "Navy Blue") {
    	hueValue = 240
    }
    else if(colorName == "Blue") {
    	hueValue = 230
    }
    else if(colorName == "Han Purple") {
        hueValue = 243
    }
    else if(colorName == "Electric Indigo") {
    	hueValue = 248
    }
    else if(colorName == "Electric Purple") {
    	hueValue = 252
    }
    else if(colorName == "Orchid Purple") {
    	hueValue = 263
    }
	else if(colorName == "Magenta") {
    	hueValue = 270
    }
    else if(colorName == "Hot Pink") {
    	hueValue = 318
    }
    else if(colorName == "Deep Pink") {
    	hueValue = 331
    }
    else if(colorName == "Raspberry") {
    	hueValue = 346
    }
    else if(colorName == "Crimson") {
    	hueValue = 0
    }
    
    if(colorType == "CIE") {
    	if(hueValue > 0 && hueValue <= 6) {
        	hueValue = hueValue + 15
        } else if(hueValue > 6 && hueValue <= 18) {
        	hueValue = hueValue + 25
        } else if(hueValue > 18 && hueValue <= 35) {
        	hueValue = hueValue + 45
        } else if(hueValue > 35 && hueValue <= 55) {
        	hueValue = hueValue + 60
        } else if(hueValue > 55 && hueValue <= 115) {
        	hueValue = hueValue + 50
        } else if(hueValue > 115 && hueValue <= 125) {
    		hueValue = hueValue + 20
        } else if(hueValue > 125 && hueValue <= 168) {
        	hueValue = hueValue + 45
        } else if(hueValue > 168 && hueValue <= 195) {
        	hueValue = hueValue + 30
        } else if(hueValue > 195 && hueValue <= 240) {
        	hueValue = hueValue + 15
        } else if(hueValue > 240 && hueValue <= 280) {
        	hueValue = hueValue + 18
        } else if(hueValue > 280 && hueValue <= 300) {
        	hueValue = hueValue + 8
        } else if(hueValue > 300 && hueValue <= 330) {
        	hueValue = hueValue - 15
        } else if(hueValue > 330) {
        	hueValue = hueValue - 30
        }
    }
    
    hueValue = Math.round(hueValue * 100 / 360)
    
    hueValue
}