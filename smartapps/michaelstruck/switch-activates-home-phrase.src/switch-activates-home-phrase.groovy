/**
 *  Switch Activates Hello, Home Phrase
 *
 *  Copyright 2015 Michael Struck
 *  Version 1.02 3/8/15
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
    name: "Switch Activates Home Phrase",
    namespace: "MichaelStruck",
    author: "Michael Struck",
    description: "Ties a Hello, Home phrase to a switch's state. Perfect for use with IFTTT.",
    category: "Convenience",
    iconUrl: "https://raw.githubusercontent.com/MichaelStruck/SmartThings/master/IFTTT-SmartApps/App1.png",
    iconX2Url: "https://raw.githubusercontent.com/MichaelStruck/SmartThings/master/IFTTT-SmartApps/App1@2x.png",
    iconX3Url: "https://raw.githubusercontent.com/MichaelStruck/SmartThings/master/IFTTT-SmartApps/App1@2x.png")


preferences {
	page(name: "page1", title: "Welcome", nextPage: "getPref", uninstall: true) {
    	section() {
        	paragraph "Welcome. This app will let you configure an activity to run when a switch it turned on or off." 
    		label title: "Assign a name", required: false
        }
    }
	page(name: "getPref")
}
	
def getPref() {    
    dynamicPage(name: "getPref", title: "Choose Switch and Phrases", install:true, uninstall: true) {
    section("Choose a switch to use...") {
		input "controlSwitch", "capability.switch", title: "Switch", multiple: false, required: true
    }
    def phrases = location.helloHome?.getPhrases()*.label
	if (phrases) {
        	phrases.sort()
			section("Perform the following phrase when...") {
				log.trace phrases
				input "phrase_on", "enum", title: "Switch is on", required: true, options: phrases
				input "phrase_off", "enum", title: "Switch is off", required: false, options: phrases
			}
                        section("Turn the switch off when done?") {
                                input "turn_switch_off", "bool", title: "Turn switch off", required: true
                        }
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