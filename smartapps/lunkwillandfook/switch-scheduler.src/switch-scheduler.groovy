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
        	input(name: "selectedSwitch", type: "capability.switch", title: "Select the switches to trigger...", required: true, multiple: true)
			def timeLabel = timeIntervalLabel()
			href(name: "timeIntervalInput", page: "pageTimeInterval", title: "Only during a certain time:", description: timeLabel ?: "Tap to set", state: timeLabel ? "complete" : null)
			input(name: "days", type: "enum", title: "Only on certain days of the week:", multiple: true, required: false, options: ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"])
			input(name: "modes", type: "mode", title: "Only when mode is:", multiple: true, required: false)
        	input(name: "isTriggerOnModeChange", type: "bool", title: "Always trigger when mode changes?")
		}
	}
    page(name: "page2")
	page(name: "pageTimeInterval", title: "Only during a certain time...") {
    	section(title: "Start time") { }
    	section(hidden: hideStartAtSunriseSection(), hideable: true){
        	input(name: "isStartAtSunrise", type: "bool", title: "Start at sunrise", required: true, submitOnChange: true)
        }
    	section(hidden: hideStartAtSunriseOffsetSection(), hideable: true){
        	input(name: "startAtSunriseOffset", type: "int", title: "Start how many minutes after sunrise?", required: false)
        }
    	section(hidden: hideStartAtSunsetSection(), hideable: true){
        	input(name: "isStartAtSunset", type: "bool", title: "Start at sunset", required: true, submitOnChange: true)
        }
    	section(hidden: hideStartAtSunsetOffsetSection(), hideable: true){
        	input(name: "startAtSunsetOffset", type: "int", title: "Start how many minutes after sunset?", required: false)
        }
    	section() {
        	input(name: "startAtTime", type: "time", title: "Start at time", required: false)
        }
        section(title: "End time") { }
    	section(hidden: hideEndAtSunriseSection(), hideable: true){
        	input(name: "isEndAtSunrise", type: "bool", title: "End at sunrise", required: true, submitOnChange: true)
        }
    	section(hidden: hideEndAtSunriseOffsetSection(), hideable: true){
        	input(name: "endAtSunriseOffset", type: "int", title: "End how many minutes after sunrise?", required: false)
        }
    	section(hidden: hideEndAtSunsetSection(), hideable: true){
        	input(name: "isEndAtSunset", type: "bool", title: "End at sunset", required: true, submitOnChange: true)
        }
    	section(hidden: hideEndAtSunsetOffsetSection(), hideable: true){
        	input(name: "endAtSunsetOffset", type: "int", title: "End how many minutes after sunset?", required: false)
        }
    	section() {
        	input(name: "endAtTime", type: "time", title: "End at time", required: false)
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
  	if(isTriggerOnModeChange) {
    	
    }
}

private timeIntervalLabel()
{
	(startAtTime && endAtTime) ? hhmm(startAtTime) + "-" + hhmm(endAtTime, "h:mm a z") : ""
}

private hideStartAtSunriseSection() {
	def isHidden = isEndAtSunrise || isStartAtSunset
    log.trace "hideStartAtSunriseSection: $isHidden"
    return isHidden
}

private hideStartAtSunriseOffsetSection() {
	def isHidden = (isStartAtSunrise == null || isStartAtSunrise == false)
    log.trace "hideStartAtSunriseOffsetSection: $isHidden"
    return isHidden
}

private hideStartAtSunsetSection() {
	def isHidden = isEndAtSunset || isStartAtSunrise
    log.trace "hideStartAtSunsetSection: $isHidden"
    return isHidden
}

private hideStartAtSunsetOffsetSection() {
	def isHidden = (isStartAtSunset == null || isStartAtSunset == false)
    log.trace "hideStartAtSunsetOffsetSection: $isHidden"
    return isHidden
}

private hideEndAtSunriseSection() {
	def isHidden = isStartAtSunrise || isEndAtSunset
    log.trace "hideEndAtSunriseSection: $isHidden"
    return isHidden
}

private hideEndAtSunriseOffsetSection() {
	def isHidden = (isEndAtSunrise == null || isEndAtSunrise == false)
    log.trace "hideEndAtSunriseOffsetSection: $isHidden"
    return isHidden
}

private hideEndAtSunsetSection() {
	def isHidden = isStartAtSunset || isEndAtSunrise 
    log.trace "hideEndAtSunsetSection: $isHidden"
    return isHidden
}

private hideEndAtSunsetOffsetSection() {
	def isHidden = (isEndAtSunset == null || isEndAtSunset == false)
    log.trace "hideEndAtSunsetOffsetSection: $isHidden"
    return isHidden
}