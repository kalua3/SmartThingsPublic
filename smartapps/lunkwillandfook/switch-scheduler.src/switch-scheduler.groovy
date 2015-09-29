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

def isInstalled = false

preferences {
	page(name: "page1", title: "Configuration...", uninstall: true, install: false, nextPage: "page2") {

	}
    page(name: "page2")
	page(name: "pageIntervalOptions1")
    page(name: "pageIntervalOptions2")
}

def page1() {
	dynamicPage(name: "page1", title: "Schedule...", nextPage: getFirstPageNextPage(), install: true, uninstall: true) {
    	if(isInstalled) {
            section("24 Hour Mode") {
                input(name: "isRunForNext24Hours", type: "bool", title: "Run for next 24 hours?")
            }
        } else {
            section("Switch settings") {
                input(name: "selectedSwitch", type: "capability.switch", title: "Select the switches to trigger...", required: true, multiple: true)
                def timeLabel = timeIntervalLabel()
                href(name: "timeIntervalInput", page: "pageIntervalOptions1", title: "Only during a certain time:", description: timeLabel ?: "Tap to set", state: timeLabel ? "complete" : null)
                input(name: "days", type: "enum", title: "Only on certain days of the week:", multiple: true, required: false, options: ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"])
                input(name: "modes", type: "mode", title: "Only when mode is:", multiple: true, required: false)
                input(name: "isTriggerOnModeChange", type: "bool", title: "Always trigger when mode changes?")
            }
        }
    }
}

def page1a() {
	dynamicPage(name: "page1a", title: "Schedule...", nextPage: "page2", install: true, uninstall: true) {
		section("Switch settings") {
        	input(name: "selectedSwitch", type: "capability.switch", title: "Select the switches to trigger...", required: true, multiple: true)
			def timeLabel = timeIntervalLabel()
			href(name: "timeIntervalInput", page: "pageIntervalOptions1", title: "Only during a certain time:", description: timeLabel ?: "Tap to set", state: timeLabel ? "complete" : null)
			input(name: "days", type: "enum", title: "Only on certain days of the week:", multiple: true, required: false, options: ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"])
			input(name: "modes", type: "mode", title: "Only when mode is:", multiple: true, required: false)
        	input(name: "isTriggerOnModeChange", type: "bool", title: "Always trigger when mode changes?")
		}
    }
}

def pageIntervalOptions1() {
	dynamicPage(name: "pageIntervalOptions1", title: "Only during a certain time...", nextPage: "pageIntervalOptions2", install: false, uninstall: false) {
    	section() {
        	input(name: "startAt", type: "enum", title: "Start at...", required: true, options: getStartAtOptions(), submitOnChange: true)
        	input(name: "endAt", type: "enum", title: "End at...", required: true, options: getEndAtOptions(), submitOnChange: true)            
        }
    }
}

def pageIntervalOptions2() {
	dynamicPage(name: "pageIntervalOptions2", title: "Only during a certain time...", install: false, uninstall: false) {
    	section() {
            log.trace "Start Type: ${startAt}"
            
            switch(startAt) {
                case "Sunrise":
                	input(name: "startAtSunriseOffsetDir", type: "enum", title: "Before or after?", required: false, metadata: [values: ["Before","After"]])
                	input(name: "startAtSunriseOffsetValue", type: "int", title: "Sunrise offset?", required: false)
                	break;
                case "Sunset":
					input(name: "startAtSunsetOffsetDir", type: "enum", title: "Before or after?", required: false, metadata: [values: ["Before","After"]])
               		input(name: "startAtSunsetOffsetValue", type: "int", title: "Sunset offset?", required: false)
                	break;
                case "Specific Time":
                	input(name: "startAtTime", type: "time", title: "Start at time?", required: isStartAtTimeRequired())
                	break;
            }
            
            log.trace "End Type: ${endAt}"
            switch(endAt) {
                case "Sunrise":
					input(name: "endAtSunriseOffsetDir", type: "enum", title: "Before or after?", required: false, metadata: [values: ["Before","After"]])
                	input(name: "endAtSunriseOffsetValue", type: "int", title: "Sunrise offset?", required: false)
                	break;
                case "Sunset":
					input(name: "endAtSunsetOffsetDir", type: "enum", title: "Before or after?", required: false, metadata: [values: ["Before","After"]])
                	input(name: "endAtSunsetOffsetValue", type: "int", title: "Sunset offset?", required: false)
                	break;
                case "Specific Time":
                	input(name: "endAtTime", type: "time", title: "End at time?", required: isEndAtTimeRequired())
                	break;
            }
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
	
    isInstalled = true
	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unsubscribe()
	unschedule()
	initialize()
}

def initialize() {  
    subscribe(location, "sunsetTime", sunsetTimeHandler)
    subscribe(location, "sunriseTime", sunriseTimeHandler)
    
  	if(isTriggerOnModeChange) {
    	
    }
    
    updateSchedule()
}

def sunriseTimeHandler(evt) {
	updateSchedule()
}

def sunsetTimeHandler(evt) {
	updateSchedule()
}

def startHandler(evt) {

}

def endHandler(evt) {
	if(!isInstalled) {
    
    }
}

def modeHandler(evt) {

}

private getFirstPageNextPage() {
	isInstalled == true ? "page1a" : "page2"
}

private updateSchedule() {
	astroCheck()
    
    if(startAt == "Sunrise") {
    	schedule(state.riseTime, startHandler)
    } else if(startAt == "Sunset") {
    	schedule(state.setTime, startHandler)
    } else if(startAt == "Specific Time") {
    	schedule(startAtTime, startHandler)
    }
    
    if(endAt == "Sunrise") {
    	schedule(state.riseTime, endHandler)
    } else if(endAt == "Sunset") {
    	schedule(state.setTime, endHandler)
    } else if(endAt == "Specific Time") {
    	schedule(endAtTime, endHandler)
    }
}

private timeIntervalLabel()
{
	(startAtTime && endAtTime) ? hhmm(startAtTime) + "-" + hhmm(endAtTime, "h:mm a z") : ""
}

private isStartAtTimeRequired() {
	return startAt == "Specific Time"
}

private isEndAtTimeRequired() {
	return endAt == "Specific Time"
}

private getStartAtOptions() {
	if(endAt == "Sunrise") {
    	return ["Sunset", "Specific Time"]
    } else if (endAt == "Sunset") {
    	return ["Sunrise", "Specific Time"]
    }
    
    return ["Sunrise", "Sunset", "Specific Time"]
}

private getEndAtOptions() {
	if(startAt == "Sunrise") {
    	return ["Sunset", "Specific Time"]
    } else if (startAt == "Sunset") {
    	return ["Sunrise", "Specific Time"]
    }
    
    return ["Sunrise", "Sunset", "Specific Time"]
}

def astroCheck()
{
	def sunriseOffset = null
    def sunsetOffset = null
    
    if(startAt == "Sunrise") {    	
    	sunriseOffset = startAtSunriseOffsetValue ? (startAtSunriseOffsetDir == "Before" ? "-$startAtSunriseOffsetValue" : startAtSunriseOffsetValue) : null
    } else if(startAt == "Sunset") {
    	sunsetOffset = startAtSunsetOffsetValue ? (startAtSunsetOffsetDir == "Before" ? "-$startAtSunsetOffsetValue" : startAtSunsetOffsetValue) : null
    }
    
    if(endAt == "Sunrise") {    	
    	sunriseOffset = endAtSunriseOffsetValue ? (endAtSunriseOffsetDir == "Before" ? "-$endAtSunriseOffsetValue" : endAtSunriseOffsetValue) : null
    } else if(startAt == "Sunset") {
    	sunsetOffset = endAtSunsetOffsetValue ? (endAtSunsetOffsetDir == "Before" ? "-$endAtSunsetOffsetValue" : endAtSunsetOffsetValue) : null
    }

	def s = getSunriseAndSunset(zipCode: location.zipCode, sunriseOffset: sunriseOffset, sunsetOffset: sunsetOffset)
	state.riseTime = s.sunrise.time
	state.setTime = s.sunset.time
	log.debug "Sunrise with offset: ${new Date(state.riseTime)}($state.riseTime), Sunset with offset: ${new Date(state.setTime)}($state.setTime)"
}

