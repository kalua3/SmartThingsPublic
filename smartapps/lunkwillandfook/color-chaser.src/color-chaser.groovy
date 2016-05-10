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
    	section() {
        	input(name: "selectedColorControls", type: "capability.colorControl, capability.colorTemperature", title: "Chase these color controls", multiple: true, required: true)
            input(name: "firstColor", type: "enum", title: "Color 1", options: ["Warm White - Relax","Cool White - Concentrate","Daylight - Energize","Red","Brick Red","Safety Orange","Dark Orange","Amber","Gold","Yellow","Electric Lime","Lawn Green","Bright Green","Lime","Spring Green","Turquoise","Aqua","Sky Blue","Dodger Blue","Navy Blue","Blue","Han Purple","Electric Indigo","Electric Purple","Orchid Purple","Magenta","Hot Pink","Deep Pink","Raspberry","Crimson","Red"], multiple: false, required: true)
            //input(name: "firstColorSaturation", type: "number", title: "Saturation 1", range:"0..100", defaultValue: 100, multiple: false, required: true)
            input(name: "secondColor", type: "enum", title: "Color 2", options: ["Warm White - Relax","Cool White - Concentrate","Daylight - Energize","Red","Brick Red","Safety Orange","Dark Orange","Amber","Gold","Yellow","Electric Lime","Lawn Green","Bright Green","Lime","Spring Green","Turquoise","Aqua","Sky Blue","Dodger Blue","Navy Blue","Blue","Han Purple","Electric Indigo","Electric Purple","Orchid Purple","Magenta","Hot Pink","Deep Pink","Raspberry","Crimson","Red"], multiple: false, required: true)
            //input(name: "secondColorSaturation", type: "number", title: "Saturation 2", range:"0..100", defaultValue: 100, multiple: false, required: true)
            input(name: "thirdColor", type: "enum", title: "Color", options: ["Warm White - Relax","Cool White - Concentrate","Daylight - Energize","Red","Brick Red","Safety Orange","Dark Orange","Amber","Gold","Yellow","Electric Lime","Lawn Green","Bright Green","Lime","Spring Green","Turquoise","Aqua","Sky Blue","Dodger Blue","Navy Blue","Blue","Han Purple","Electric Indigo","Electric Purple","Orchid Purple","Magenta","Hot Pink","Deep Pink","Raspberry","Crimson","Red"], multiple: false, required: false)
            //input(name: "thirdColorSaturation", type: "number", title: "Saturation 3", range:"0..100", defaultValue: 100, multiple: false, required: true)
            input(name: "fourthColor", type: "enum", title: "Color", options: ["Warm White - Relax","Cool White - Concentrate","Daylight - Energize","Red","Brick Red","Safety Orange","Dark Orange","Amber","Gold","Yellow","Electric Lime","Lawn Green","Bright Green","Lime","Spring Green","Turquoise","Aqua","Sky Blue","Dodger Blue","Navy Blue","Blue","Han Purple","Electric Indigo","Electric Purple","Orchid Purple","Magenta","Hot Pink","Deep Pink","Raspberry","Crimson","Red"], multiple: false, required: false)
            //input(name: "fourthColorSaturation", type: "number", title: "Saturation 4", range:"0..100", defaultValue: 100, multiple: false, required: true)
        }
    }
	page(name: "page2", title: "Timing", uninstall: true, install: true) {
    	section() {
        	input(name: "pauseOnColor", type: "number", title: "Stay on a color for (minutes)", range:"1..60", multiple: false, required: true)
            input(name: "transitionTime", type: "decimal", title: "Transition between colors for (seconds)", range:"0..3", multiple: false, required: true)
	    }
	}
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
    //def satValue = 100.doubleValue()
    def colorName = ""
    switch(atomicState.currentColorIndex) {
    	case 1:
            //satValue = firstColorSaturation.doubleValue()
            colorName = firstColor
            break
        case 2:
            //satValue = secondColorSaturation.doubleValue()
            colorName = secondColor
            break
        case 3:
            //satValue = thirdColorSaturation.doubleValue()
            colorName = thirdColor
            break
        case 4:
            //satValue = fourthColorSaturation.doubleValue()
            colorName = fourthColor
            break
    }
    
    if(colorName == "Soft White - Default" || colorName == "White - Concentrate" || colorName == "Daylight - Energize" || colorName == "Warm White - Relax") {
    	def colorTemp = 2700
        switch(colorName) {
        	case "Warm White - Relax":
            	colorTemp = 2700
                break;
            case "Cool White - Concentrate":
            	colorTemp = 4100
                break;
            case "Daylight - Energize":
            	colorTemp = 6500
                break;
        }
        selectedColorControls.setColorTemperature(colorTemp, transitionTime)
    } else {
    	def colorValue = [level:null, saturation:100.0, hue:getHue(colorName), alpha:1.0]
    	log.trace "Changing color to $colorName with a saturation of $satValue over $transitionTime seconds."
    	selectedColorControls.setColor(colorValue, transitionTime)
    }
}