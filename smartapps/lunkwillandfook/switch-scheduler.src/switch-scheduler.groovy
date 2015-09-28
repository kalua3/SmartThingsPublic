/**
 *  Schedule Manager
 *
 *  Copyright 2015 Jeremy Huckeba
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
 */
definition(
    name: "Switch Scheduler",
    namespace: "LunkwillAndFook",
    author: "Jeremy Huckeba",
    description: "Schedule a switch to turn on and off at a specific time or allow the switch to run for 24 hours before enforcing the schedule.",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	page(name: "page1", title: "Configuration...", uninstall: true, install: false, nextPage: "page2") {
    	section("24 Hour Mode") {
        	input(name: "isRunForNext24Hours", type: "bool", title: "Run for next 24 hours?")
        }
		section("Switch settings") {
        	input(name: "selectedSwitch", type: "capability:switch", title: "Select the switches to trigger...", required: true, multiple: true)
			def timeLabel = timeIntervalLabel()
			href(name: "timeIntervalInput", title: "Only during a certain time:", description: timeLabel ?: "Tap to set", state: timeLabel ? "complete" : null)
			input(name: "days", type: "enum", title: "Only on certain days of the week:", multiple: true, required: false, options: ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"])
			input(name: "modes", type: "mode", title: "Only when mode is:", multiple: true, required: false)
        	input(name: "isTriggerOnModeChange", type: "bool", title: "Always trigger when mode changes?")
		}
	}
    page(name: "page2")
	page(name: "timeIntervalInput", title: "Only during a certain time...")
    {
		section
        {
			input "startingTime", "time", title: "Starting", required: false
			input "endingTime", "time", title: "Ending", required: false
		}
	}
}

def page2() {
	dynamicPage(name: "page2", title: "Switch Levels", uninstall: true, install: true) {
    	section() {
        	def i = 0
            selectedSwitches.each { selectedSwitch ->
            	if(i < 20) {
                	def inputName = "switchLevel$i"
                    input(name: inputName, type: "enum", title: selectedSwitch.label, multiple: false, required: true, options: getSwitchLevelOptions(selectedSwitch))
                    i++
                }
            }
        }
    }
}

private getSwitchLevelOptions(selectedSwitch) {
	if(selectedSwitch.hasCommand("setLevel")) {
    	// dimmable switch options
        return ["Off", "5%", "10%", "15%", "20%", "25%", "30%", "35%", "40%", "45%", "50%", "55%", "60%", "65%", "70%", "75%", "80%", "85%", "90%", "95%", "On" ]
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
	// TODO: subscribe to attributes, devices, locations, etc.
    
}

private timeIntervalLabel()
{
	(startingTime && endingTime) ? hhmm(startingTime) + "-" + hhmm(endingTime, "h:mm a z") : ""
}