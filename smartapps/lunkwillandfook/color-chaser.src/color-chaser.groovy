/**
 *  Color Chaser
 *
 *  Copyright 2015 Jeremy Huckeba
 *
 */
definition(
    name: "Color Chaser",
    namespace: "LunkwillAndFook",
    author: "Jeremy Huckeba",
    description: "Enables advanced color chasing with custom color device types.",
    category: "My Apps",
    iconUrl: "http://cdn.device-icons.smartthings.com/Lighting/light14-icn.png",
    iconX2Url: "http://cdn.device-icons.smartthings.com/Lighting/light14-icn@2x.png",
    iconX3Url: "http://cdn.device-icons.smartthings.com/Lighting/light14-icn@3x.png")

preferences {
	page(name: "page1", title: "Welcome", nextPage: "page2", uninstall: true) {
    	section("controls") {
        	input(name: "selectedColorControls", type: "capability.colorControl", title: "Chase these color controls", multiple: true, required: true)
            input(name: "selectedColorTemperatureControls", type: "capability.colorTemperature", title: "Chase these color temperature controls", multiple: true, required: true)
        }
    	section("first color") {
            input(name: "firstColor", type: "enum", title: "Color", options: ["Warm White - Relax","Cool White - Concentrate","Daylight - Energize","Red","Brick Red","Safety Orange","Dark Orange","Amber","Gold","Yellow","Electric Lime","Lawn Green","Bright Green","Lime","Spring Green","Turquoise","Aqua","Sky Blue","Dodger Blue","Navy Blue","Blue","Han Purple","Electric Indigo","Electric Purple","Orchid Purple","Magenta","Hot Pink","Deep Pink","Raspberry","Crimson","Red"], multiple: false, required: true)
            input(name: "firstSaturation", type: "number", title: "Saturation", range:"0..100", defaultValue: 100, multiple: false, required: true)
            input(name: "firstLevel", type: "number", title: "Dimmer Level", range:"1..100", defaultValue: 100, multiple: false, required: true)
        }
        section("second color") {
            input(name: "secondColor", type: "enum", title: "Color", options: ["Warm White - Relax","Cool White - Concentrate","Daylight - Energize","Red","Brick Red","Safety Orange","Dark Orange","Amber","Gold","Yellow","Electric Lime","Lawn Green","Bright Green","Lime","Spring Green","Turquoise","Aqua","Sky Blue","Dodger Blue","Navy Blue","Blue","Han Purple","Electric Indigo","Electric Purple","Orchid Purple","Magenta","Hot Pink","Deep Pink","Raspberry","Crimson","Red"], multiple: false, required: true)
            input(name: "secondSaturation", type: "number", title: "Saturation", range:"0..100", defaultValue: 100, multiple: false, required: true)
            input(name: "secondLevel", type: "number", title: "Dimmer Level", range:"1..100", defaultValue: 100, multiple: false, required: true)
        }
		section("third color") {
            input(name: "thirdColor", type: "enum", title: "Color", options: ["Warm White - Relax","Cool White - Concentrate","Daylight - Energize","Red","Brick Red","Safety Orange","Dark Orange","Amber","Gold","Yellow","Electric Lime","Lawn Green","Bright Green","Lime","Spring Green","Turquoise","Aqua","Sky Blue","Dodger Blue","Navy Blue","Blue","Han Purple","Electric Indigo","Electric Purple","Orchid Purple","Magenta","Hot Pink","Deep Pink","Raspberry","Crimson","Red"], multiple: false, required: false)
            input(name: "thirdSaturation", type: "number", title: "Saturation", range:"0..100", defaultValue: 100, multiple: false, required: false)
            input(name: "thirdLevel", type: "number", title: "Dimmer Level", range:"1..100", defaultValue: 100, multiple: false, required: false)
        }
        section("fourth color") {
            input(name: "fourthColor", type: "enum", title: "Color", options: ["Warm White - Relax","Cool White - Concentrate","Daylight - Energize","Red","Brick Red","Safety Orange","Dark Orange","Amber","Gold","Yellow","Electric Lime","Lawn Green","Bright Green","Lime","Spring Green","Turquoise","Aqua","Sky Blue","Dodger Blue","Navy Blue","Blue","Han Purple","Electric Indigo","Electric Purple","Orchid Purple","Magenta","Hot Pink","Deep Pink","Raspberry","Crimson","Red"], multiple: false, required: false)
            input(name: "fourthColorSaturation", type: "number", title: "Saturation", range:"0..100", defaultValue: 100, multiple: false, required: false)
            input(name: "fourthLevel", type: "number", title: "Dimmer Level", range:"1..100", defaultValue: 100, multiple: false, required: false)
        }
    }
    page(name: "page2", title: "Options", uninstall: true, nextPage: "page3")   
	page(name: "page3", title: "Timing", uninstall: true, install: true) {
    	section() {
        	input(name: "pauseOnColor", type: "number", title: "Stay on a color for (minutes)...", range:"1..60", multiple: false, required: true)
            input(name: "onlyForModes", type: "mode", title: "Only for mode(s)...", multiple: true, required: true)
            label title: "Assign a name", required: false
	    }
	}
}

def page2() {
  dynamicPage(name: "page2", title: "What color do you want each light to start on?",
    install: true, uninstall: true) {
		section("color control indicies") {
        	if(selectedColorControls != null) {
                def i = 0
                selectedColorControls.each { control ->
                    if(i < 20) {
						input inputName, "number", title: control.label, range: getColorControlStartIndexRange(), multiple: false, required: true, defaultValue: "1"
                    }
                }
            } else {
             	paragraph "There are no color controls selected."
            }
        }
		section("color temperature control indicies") {
        	if(selectedColorTemperatureControls != null) {
                def i = 0
                selectedColorTemperatureControls.each { control ->
                    if(i < 20) {
						input inputName, "number", title: control.label, range: getColorControlStartIndexRange(), multiple: false, required: true, defaultValue: "1"
                    }
                }
            } else {
             	paragraph "There are no color controls selected."
            }
        }
	}
}

def getColorControlStartIndexRange() {
    def option = 2
    if(thirdColor != null) {
        range = 3
    }
    if(fourthColor != null) {
        range = 4
    }
    
    return range
}

def getHue(colorName) {
	
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

def installed() {
	log.debug "Installed with settings: ${settings}"

	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unschedule(chronExpression)
	unsubscribe()
	initialize()
}

def initialize() {
	def chronExpression = "0 0/${pauseOnColor} * 1/1 * ? *"
	schedule(chronExpression, changeColor)
}

def changeColor() {
	def currentMode = location.mode
    
	if(onlyForModes.contains(currentMode)) {
        if(atomicState.currentColorIndex == null) {
            atomicState.currentColorIndex = 0
        } else if(atomicState.currentColorIndex == 2 && thirdColor == null) {
            atomicState.currentColorIndex = 0
        } else if(atomicState.currentColorIndex == 3 && fourthColor == null) {
            atomicState.currentColorIndex = 0
        } else if(atomicState.currentColorIndex == 4) {
            atomicState.currentColorIndex = 0
        }

        atomicState.currentColorIndex = atomicState.currentColorIndex + 1

        def hueValue = 0
        def satValue = 100
        def switchLevel = 100
        def colorName = ""
        switch(atomicState.currentColorIndex) {
            case 1:
                satValue = firstSaturation.doubleValue()
                switchLevel = firstLevel
                colorName = firstColor
                break
            case 2:
                satValue = secondSaturation.doubleValue()
                switchLevel = secondLevel
                colorName = secondColor
                break
            case 3:
                satValue = thirdSaturation.doubleValue()
                switchLevel = thirdLevel
                colorName = thirdColor
                break
            case 4:
                satValue = fourthSaturation.doubleValue()
                switchLevel = fourthLevel
                colorName = fourthColor
                break
        }

        if(colorName == "Warm White - Relax" || colorName == "Cool White - Concentrate" || colorName == "Daylight - Energize") {
            // set the color temperature
            def colorTemp = 2700
            switch(colorName) {
                case "Warm White - Relax":
                    colorTemp = 0
                    break;
                case "Cool White - Concentrate":
                    colorTemp = 50
                    break;
                case "Daylight - Energize":
                    colorTemp = 100
                    break;
            }
            selectedColorTemperatureControls.setColorTemperature(colorTemp)
        } else {
            // set the color
            def colorValue = [level:null, saturation:satValue, hue:getHue(colorName), alpha:1.0]
            log.trace "Changing color to $colorName with a saturation of $satValue."
            selectedColorControls.setColor(colorValue)
        }

        // set the levels
        selectedColorTemperatureControls.each{control->
        	def controlCaps = control.capabilities
			log.trace "caps: $controlCaps"
            controlCaps.each{cap ->
                if(cap.name == "Switch Level") {
                    log.trace "setLevel to $switchLevel"
                    control.setLevel(switchLevel)
                }
            }
        }
	}
}