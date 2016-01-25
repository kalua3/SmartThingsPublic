/**
 *  Echo Master
 *
 *  Copyright 2015 Jeremy Huckeba
 *  Version 1.00 1/18/16
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 *  Ties a Hello, Home phrase to a switch's (virtual or real) on/off state. Perfect for use with IFTTT.
 *  Simple define a switch to be used, then tie the on/off state of the switch to a specific Hello, Home phrases.
 *  Connect the switch to an IFTTT action, and the Hello, Home phrase will fire with the switch state change.
 *
 *
 */
definition(
    name: "Echo Master",
    namespace: "LunkwillAndFook",
    author: "Jeremy Huckeba",
    description: "Allows deep, custom integration with Amazon Echo.",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/App-AmazonEcho.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/App-AmazonEcho@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/App-AmazonEcho@3x.png")

preferences(oauthPage: "deviceAuthorization") {
    page(name: "deviceAuthorization", title: "", nextPage: "routinesPage", install: false, uninstall: true) {
        section ("Allow Alexa to control these switches...") {
            input "selectedSwitches", "capability.switch", multiple: true, required: false
        }
        section ("Allow Alexa to control these thermostats...") {
            input "selectedThermostats", "capability.thermostat", multiple: true, required: false
        }
        section ("Allow Alexa to control these colored bulbs...") {
            input "selectedColorControls", "capability.colorControl", multiple: true, required: false
        }
        section ("Allow Alexa to read these contact sensors...") {
            input "selectedContactSensors", "capability.contactSensor", multiple: true, required: false
        }
        section ("Allow Alexa to read these temperature sensors...") {
            input "selectedTemperatureSensors", "capability.temperatureMeasurement", multiple: true, required: false
        }
        section ("Allow Alexa to read these water sensors...") {
            input "selectedWaterSensors", "capability.waterSensor", multiple: true, required: false
        }
       section ("Allow Alexa to read these smoke detectors...") {
            input "selectedSmokeDetectors", "capability.smokeDetector", multiple: true, required: false
        }
       section ("Allow Alexa to read these battery powered devices...") {
            input "selectedBatteries", "capability.battery", multiple: true, required: false
        }
    }
    page(name: "routinesPage")
}

// page def must include a parameter for the params map!
def routinesPage() {
	def actions = location.helloHome?.getPhrases()*.label;

    dynamicPage(name: "routinesPage", uninstall: true, install: true) {
        section {
            input "selectedRoutines", "enum", title: "Allow Alexa to run these routines...", options: actions, required: false, multiple: true
        }
    }
}

mappings {
  path("/temperatureSensors/:name") {
    action: [
      GET: "listTemperatureSensors"
    ]
  }
  path("/temperatureSensors") {
    action: [
      GET: "listTemperatureSensors"
    ]
  }
  path("/switches") {
    action: [
      GET: "listSwitches"
    ]
  }
  path("/switches/:name/:command") {
    action: [
      PUT: "updateSwitch"
    ]
  }
  path("/contactSensors/:name") {
    action: [
      GET: "listContactSensors"
    ]
  }
  path("/contactSensors") {
    action: [
      GET: "listContactSensors"
    ]
  }
  path("/lowBatteries/:level") {
    action: [
      GET: "listLowBatteries"
    ]
  }
  path("/lowBatteries") {
    action: [
      GET: "listLowBatteries"
    ]
  }
  path("/routines") {
    action: [
      GET: "listRoutines"
    ]
  }
  path("/routines/:name") {
    action: [
      PUT: "executeRoutine"
    ]
  }
}

def installed() {
	log.debug "Installed with settings: ${settings}"
	subscribe(controlSwitch, "switch", "switchHandler")
}

def updated() {
	log.debug "Updated with settings: ${settings}"
	unsubscribe()
	subscribe(controlSwitch, "switch", "switchHandler")
}

// returns a list like
// [[name: "front door", value: "65"], [name: "smoke detector", value: "30"]]
def listLowBatteries() {
    def level = 20;
	if(params.level != null) {
    	level = new BigDecimal(params.level.replaceAll(",", ""))
    }
	
    def resp = []
    selectedBatteries.each {
    	def batteryLevel = it.currentValue("battery")
        if(it.currentValue("battery") != null && it.currentValue("battery") < level) {
            resp << [name: it.displayName, value: it.currentValue("battery")]
        }
    }
    return resp
}

// returns a list like
// [[name: "kitchen lamp", value: "off"], [name: "bathroom", value: "on"]]
def listSwitches() {
    def resp = []
    selectedSwitches.each {
      resp << [name: it.displayName, value: it.currentValue("switch")]
    }
    return resp
}

// returns a list like
// [[name: "front door", value: "closed"], [name: "back door", value: "opened"]]
def listContactSensors() {
    def resp = []
    if(params.name == null) {
        selectedContactSensors.each {
          resp << [name: it.displayName, value: it.currentValue("contact")]
        }
    } else {
    	selectedContactSensors.each {
        	if(it.displayName.toLowerCase() == params.name.toLowerCase()) {
            	resp << [name: it.displayName, value: it.currentValue("contact")]
            }
        }
    }
    return resp
}

// returns a list like
// [[name: "front door", value: "74", scale: "F"], [name: "back door", value: "76", scale: "F"]]
def listTemperatureSensors() {
    def resp = []
    if(params.name == null) {
        selectedTemperatureSensors.each {
          resp << [name: it.displayName, value: it.currentValue("temperature"), scale: location.temperatureScale]
        }
    } else {
    	selectedTemperatureSensors.each {
        	if(it.displayName.toLowerCase() == params.name.toLowerCase()) {
            	resp << [name: it.displayName, value: it.currentValue("temperature"), scale: location.temperatureScale]
            }
        }
    }
    return resp
}

// returns a list like
// [[name: "goodbye"], [name: "good morning"]]
def listRoutines() {
    log.debug "listTemperatureSensors: $listRoutines"
    def resp = []
    selectedRoutines.each {
      resp << [name: it]
    }
    return resp
}

void updateSwitches() {
    // use the built-in request object to get the command parameter
    def name = params.name
    def command = params.command

    if (command && name) {

        // check that the switch supports the specified command
        // If not, return an error using httpError, providing a HTTP status code.
        selectedSwitches.each {
            if (it.displayName == name && !it.hasCommand(command)) {
                httpError(501, "$command is not a valid command for the specified switch")
            } else if (it.displayName == name) {
            	it."$command"()
            }
        }
    }
}

def executeRoutine() {
    // use the built-in request object to get the command parameter
    def name = params.name
    def executeName = name
    if (name) {
		def canExecute = false
        // find the routine to execute
        selectedRoutines.each {
        	if(it.toLowerCase() == name.toLowerCase()) {
            	canExecute = true
                executeName = it
            }
        }
        
        if(canExecute) {
            location.helloHome?.execute(executeName)
            httpSuccess
        } else {
            httpError(501, "$name is not a valid routine")
        }
    }
}