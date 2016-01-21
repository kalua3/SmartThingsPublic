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


preferences {
    page(title: "Switches", name: "Switches", nextPage: "routinesPage", uninstall: true) {
        section ("Allow external service to control these things...") {
            input "selectedSwitches", "capability.switch", multiple: true, required: false
        }
    }
    page(name: "routinesPage", install: true, uninstall: true, title: "Routines")
}

def routinesPage() {    
    dynamicPage(name: "routinesPage") {
        def phrases = location.helloHome?.getPhrases()*.label
        section("Allow external service to control these routines...") {
            input "selectedRoutines", "enum", title: "Phrase", required: false, multiple: true, options: phrases
	    }
    }
}

mappings {
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
// [[name: "kitchen lamp", value: "off"], [name: "bathroom", value: "on"]]
def listSwitches() {
    def resp = []
    selectedSwitches.each {
      resp << [name: it.displayName, value: it.currentValue("switch")]
    }
    return resp
}

// returns a list like
// [[name: "goodbye"], [name: "good morning"]]
def listRoutines() {
    def resp = []
    selectedRoutines.each {
      resp << [name: it.displayName]
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

void executeRoutine() {
    // use the built-in request object to get the command parameter
    def name = params.name

    if (name) {

		def canExecute = false
        // find the routine to execute
        selectedRoutines.each {
        	if(it.displayName == name) {
            	canExecute = true
            }
        }
        
        if(canExecute) {
            location.helloHome?.execute(name)
        } else {
            httpError(501, "$name is not a valid routine")
        }
    }
}