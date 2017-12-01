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
            input(name: "fourthSaturation", type: "number", title: "Saturation", range:"0..100", defaultValue: 100, multiple: false, required: false)
            input(name: "fourthLevel", type: "number", title: "Dimmer Level", range:"1..100", defaultValue: 100, multiple: false, required: false)
        }
        section("fifth color") {
            input(name: "fifthColor", type: "enum", title: "Color", options: ["Warm White - Relax","Cool White - Concentrate","Daylight - Energize","Red","Brick Red","Safety Orange","Dark Orange","Amber","Gold","Yellow","Electric Lime","Lawn Green","Bright Green","Lime","Spring Green","Turquoise","Aqua","Sky Blue","Dodger Blue","Navy Blue","Blue","Han Purple","Electric Indigo","Electric Purple","Orchid Purple","Magenta","Hot Pink","Deep Pink","Raspberry","Crimson","Red"], multiple: false, required: false)
            input(name: "fifthSaturation", type: "number", title: "Saturation", range:"0..100", defaultValue: 100, multiple: false, required: false)
            input(name: "fifthLevel", type: "number", title: "Dimmer Level", range:"1..100", defaultValue: 100, multiple: false, required: false)
        }
    }
    page(name: "page2", title: "Options", uninstall: true, install: false, nextPage: "page3")   
	page(name: "page3", title: "Timing", uninstall: true, install: true)
}

def page3() {
  dynamicPage(name: "page3") {
  	def actions = location.helloHome?.getPhrases()*.label
    if (actions) {
    	// sort them alphabetically
        actions.sort()
		section() {
        	input(name: "pauseOnColor", type: "number", title: "Stay on a color for (minutes)...", range:"1..60", multiple: false, required: true)
            input(name: "onlyForModes", type: "mode", title: "Only for mode(s)...", multiple: true, required: true)
            input "triggerRoutine", "enum", title: "Set for specific routine", multiple: false, required: true, options: actions
            label title: "Assign a name", required: false
        }
    }
  }
}

def page2() {
  dynamicPage(name: "page2") {
		section("color control indicies") {
        	if(selectedColorControls != null) {
                def i = 0
                selectedColorControls.each { selectedControl ->
                    if(i < 20) {
                    	def inputName = "colorIndex$i"
                        def inputTitle = "${selectedControl.label} start index"
						input inputName, "number", title: inputTitle, range: getColorControlStartIndexRange(), multiple: false, required: true, defaultValue: 1
                    }
                    i++
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
        option = 3
    }
    if(fourthColor != null) {
        option = 4
    }
    
    return "1..$option"
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

def installed() {
	log.debug "Installed with settings: ${settings}"

	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unschedule()
	unsubscribe()
	initialize()
}

def initialize() {
	atomicState.colorIndicies = null
    subscribe(location, "routineExecuted", scheduleHandler)
    subscribe(location, "mode", modeChangedHandler)
}

def modeChangedHandler(evt) {
	if(!onlyForModes.contains(evt.value)){
    	unschedule(changeColor)
    }
}

def shouldRun() {
	def currentMode = location.mode
	if(onlyForModes.contains(currentMode) && atomicState.lastRoutineDisplayName != null && triggerRoutine == atomicState.lastRoutineDisplayName) {
    	return true
    }
    
    return false
}

def scheduleHandler(evt) {
	atomicState.lastRoutineDisplayName = evt.displayName
    def chronExpression = "0 0/${pauseOnColor} * 1/1 * ? *"
	if(shouldRun()) {
    	changeColor()
    	schedule(chronExpression, changeColor)
    } else {
    	unschedule(changeColor)
        log.trace "unscheduled"
    }
}

def changeColor() {
	def currentMode = location.mode
    log.trace "Entering color handler"
    
	if(shouldRun()) {
    	def colorIndicies = [:]
        log.trace "atomicState: ${atomicState.colorIndicies}"
    	if(atomicState.colorIndicies != null) {
        	colorIndicies = atomicState.colorIndicies
            log.trace "retrieved colorIndicies from atomicState"
        }
        
    	def i = 0
    	selectedColorControls.each { colorControl ->

			def colorType = "RGB"
            if(colorControl.name.toLowerCase().contains("hue")) {
            	colorType = "CIE"
            }
            
			def indexName = "currentColorIndex$i"
            def indexControlName = "colorIndex$i"
        	def oldColorIndex = colorIndicies["$indexName"]
            
            if(oldColorIndex == null) {
                colorIndicies.put((indexName), settings[indexControlName])
                log.trace "added ${settings[indexControlName] - 1} to colorIndicies[$indexName]"
            } else if(oldColorIndex == 3 && thirdColor == null) {
            	colorIndicies["$indexName"] = 1
                log.trace "reset colorIndicies 3"
            } else if(oldColorIndex == 4 && fourthColor == null) {
            	colorIndicies["$indexName"] = 1
                log.trace "reset colorIndicies 4"
            } else if(oldColorIndex == 5 && fifthColor == null) {
            	colorIndicies["$indexName"] = 1
                log.trace "reset colorIndicies 5"
            } else if(oldColorIndex == 6) {
            	colorIndicies["$indexName"] = 1
                log.trace "reset colorIndicies > 5"
            }            

			log.trace "colorIndicies: $colorIndicies"
			def currentColorIndex = colorIndicies.get(indexName)
            if(currentColorIndex == null) {
            	currentColorIndex = colorIndicies["$indexName"]
            }
            log.trace "currentColorIndex: $currentColorIndex"
            colorIndicies.remove("$indexName")
            colorIndicies["$indexName"] = currentColorIndex + 1

            def satValue = 100
            def switchLevel = 100
            def colorName = ""
            switch(currentColorIndex) {
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
                case 5:
                    satValue = fifthSaturation.doubleValue()
                    switchLevel = fifthLevel
                    colorName = fifthColor
                    break
            }
		
            def hasColorTemperature = false
            colorControl.capabilities.each{ capability ->
                if(capability == "Color Temperature") {
                    hasColorTemperature = true
                }
            }
        
            if(colorName == "Warm White - Relax" || colorName == "Cool White - Concentrate" || colorName == "Daylight - Energize") {
            	if(hasColorTemperature) {
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
                    colorControl.setColorTemperature(colorTemp)
                } else {
                    def hueValue = getHue("Red", colorType)
                    switch(colorName) {
                        case "Warm White - Relax":
                            hueValue = getHue("Red", colorType)
                            break;
                        case "Cool White - Concentrate":
                            hueValue = getHue("Green", colorType)
                            break;
                        case "Daylight - Energize":
                            hueValue = getHue("Blue", colorType)
                            break;
                    }
                    // set the color
                    def colorValue = [level:null, saturation:2.0, hue:hueValue, alpha:1.0]
                    log.trace "Changing color to $colorName with a saturation of $satValue."
                    colorControl.setColor(colorValue)
                }
            } else {
            	// set the color
                def colorValue = [level:null, saturation:satValue, hue:getHue(colorName, colorType), alpha:1.0]
                log.trace "Changing color to $colorName with a saturation of $satValue."
                colorControl.setColor(colorValue)
            }

            // set the levels
            colorControl.each{control->
                def controlCaps = control.capabilities
                log.trace "caps: $controlCaps"
                controlCaps.each{cap ->
                    if(cap.name == "Switch Level") {
                        log.trace "setLevel to $switchLevel"
                        control.setLevel(switchLevel)
                    }
                }
            }
            
            i = i + 1
		}
        
        atomicState.colorIndicies = colorIndicies
        log.trace "added colorIndicies to atomicState"
	}
}