definition(
    name: "Breezy for Amazon Echo",
    namespace: "LunkwillAndFook",
    author: "Jeremy Huckeba",
    description: "Allows the Amazon Echo to control SmartThings devices that support the Fan Speed capability. Modes are available for fans that also support the getAvailableModes command, setMode command, and fanMode attribute.",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/MyApps/Cat-MyApps.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/MyApps/Cat-MyApps@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/MyApps/Cat-MyApps@3x.png")
    
preferences(oauthPage: "deviceAuthorization") {
    page(name: "deviceAuthorization", title: "", install: true, uninstall: true) {
        section ("Allow Alexa to control these fans with Breezy...") {
        	input "selectedFans", "capability.fanSpeed", title: "Set these fans", multiple: true, required: false
        }
    }
}

mappings {
  path("/fans") {
  	action: [
      GET: "listFans"
    ]
  }
  path("/fans/:name") {
  	action: [
      GET: "getFanDetails"
    ]
  }
  path("/fans/:name/:value") {
  	action: [
      PUT: "setFanValue"
    ]
  }
}

// runs when the smartapp is installed
def installed() {
	def immediatelocks = state.immediateLocks ?: []
    	log.debug "Installed with settings: ${settings}"
}

// runs when the smartapp is updated
def updated() {
	def immediatelocks = state.immediateLocks ?: []
	log.debug "Updated with settings: ${settings}"
	unsubscribe()
}

def getModeName(mode) {
	switch(mode) {
    	case "fanOff":
        	return "Turn Off"
            break;
        case "fanOne":
        	return "Low"
        	break;
        case "fanTwo":
        	return "Medium"
        	break;
        case "fanThree":
        	return "Medium-High"
        	break;
        case "fanFour":
        	return "High"
        	break;
        case "fanAuto":
       		return "Breeze"
        	break;
        default:
        	return "Unknown Mode"
            break;
    }
}

// returns a list like
// [[name: "front door", value: "65"], [name: "smoke detector", value: "30"]]
def listFans() {
    log.debug "listing selected fans"
	
    def resp = []
    selectedFans.each { selectedFan ->
        def hasFanMode = false
        def currentSpeed = selectedFan.currentValue("fanSpeed")
        def currentMode = getModeName(selectedFan.currentValue("fanMode"))
        def hasSetFanMode = selectedFan.hasCommand("setFanMode")
        def hasSetFanSpeed = selectedFan.hasCommand("setFanSpeed")
        selectedFan.supportedAttributes.each {
            if(it.name == "fanMode") {
                hasFanMode = true
            }
        }

    	resp << [displayName: selectedFan.displayName, id: selectedFan.id, hasFanMode: hasFanMode, currentMode: currentMode, currentSpeed: currentSpeed, hasSetFanMode: hasSetFanMode, hasSetFanSpeed: hasSetFanSpeed]
    }
    
    return resp
}

def getFanDetails() {
    def name = params.name
	log.debug "getFanDetails, name: $name"
	def resp = []
    selectedFans.each { selectedFan ->
        if(selectedFan.displayName.toLowerCase() == name.toLowerCase()) {
            def hasFanMode = false
            def currentSpeed = selectedFan.currentValue("fanSpeed")
            def currentMode = getModeName(selectedFan.currentValue("fanMode"))
            def hasSetFanMode = selectedFan.hasCommand("setFanMode")
            def hasSetFanSpeed = selectedFan.hasCommand("setFanSpeed")
            selectedFan.supportedAttributes.each {
                if(it.name == "fanMode") {
                    hasFanMode = true
                }
            }

            resp << [displayName: selectedFan.displayName, id: selectedFan.id, hasFanMode: hasFanMode, currentMode: currentMode, currentSpeed: currentSpeed, hasSetFanMode: hasSetFanMode, hasSetFanSpeed: hasSetFanSpeed]
        }
    }
    
    return resp
}

def setFanValue() {
    def name = params.name
    def value = params.value
	log.debug "setFanValue, name: $name, value: $value"
	def isSuccess = false;
    selectedFans.each { selectedFan ->
        if(selectedFan.displayName.toLowerCase() == name.toLowerCase()) {
        	log.debug "fan ${selectedFan.displayName} found"
            def hasFanMode = false
            def currentSpeed = selectedFan.currentValue("fanSpeed")
            def currentMode = selectedFan.currentValue("fanMode")
            def hasSetFanCommand = selectedFan.hasCommand("setFanMode")
            def hasSetFanSpeed = selectedFan.hasCommand("setFanSpeed")
            selectedFan.supportedAttributes.each {
                if(it.name == "fanMode") {
                    hasFanMode = true
                }
            }
            
            if(hasFanMode && hasSetFanCommand) {
                def mode = getMode(value)
                if(mode != currentMode) {
                    selectedFan.setFanMode(mode)
                }
            } else {
            	if(currentSpeed != value) {
            		selectedFan.setFanSpeed(value)
                }
            }
            
            isSuccess = true;
        }
    }
    
    if(isSuccess) {
   		httpSuccess
    } else {
    	httpError(501, "$name could not be set to $value")
    }
}

def getMode(modeName) {
	log.debug "getMode: $modeName"
	switch(modeName) {
    	case "off":
        	return "fanOff"
            break;
        case "low":
        	return "fanOne"
        	break;
        case "medium":
        	return "fanTwo"
        	break;
        case "medium high":
        	return "fanThree"
        	break;
        case "high":
        	return "fanFour"
        	break;
        case "breeze":
       		return "fanAuto"
        	break;
        default:
        	return null
    }
}