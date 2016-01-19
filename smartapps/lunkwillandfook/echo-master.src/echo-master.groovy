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
    page(title: "Switches", name: "Switches", nextPage: "routinesPage") {
        section ("Allow external service to control these things...") {
            input "selectedSwitches", "capability.switch", multiple: true, required: true
        }
    }
    page(name: "routinesPage", install: true)
}

def routinesPage() {    
    dynamicPage(title: "Routines", uninstall: true) {
        def phrases = location.helloHome?.getPhrases()*.label
        section("Allow external service to control these routines...") {
            input "selectedRoutines", "enum", title: "Phrase", required: false, options: phrases
	    }
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

def switchHandler(evt) {
    if (evt.value == "on") {
    	location.helloHome.execute(settings.phrase_on)
    } else if(settings.phrase_off){
    	location.helloHome.execute(settings.phrase_off)
    }

    if(turn_switch_off) {
      control_switch.off()
    }
}