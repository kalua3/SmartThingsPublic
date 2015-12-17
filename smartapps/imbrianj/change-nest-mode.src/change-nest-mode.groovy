/**
 *  Change Nest Mode
 *
 *  Author: brian@bevey.org
 *  Date: 5/5/14
 *
 *  Simply marks any thermostat "away" if able (primarily focused on the Nest
 *  thermostat).  This is intended to be used with an "Away" or "Home" mode.
 */

definition(
    name:        "Change Nest Mode",
    namespace:   "imbrianj",
    author:      "brian@bevey.org",
    description: "Simply marks any thermostat 'away' if able (primarily focused on the Nest thermostat).  This is intended to be used with an 'Away' or 'Home' mode.",
    category:    "Green Living",
    iconUrl:     "http://cdn.device-icons.smartthings.com/Home/home1-icn.png",
    iconX2Url:   "http://cdn.device-icons.smartthings.com/Home/home1-icn@2x.png",
    iconX3Url:   "http://cdn.device-icons.smartthings.com/Home/home1-icn@3x.png"
)

preferences {
  section("Change these thermostats...") {
    input "thermostats", "capability.thermostat", multiple: true
  }
  section("Change the selected thermostat modes to...") {
    input "newMode", "enum", options:["Away", "Home"]
  }
}

def installed() {
  subscribe(location, changeMode)
  subscribe(app, changeMode)
}

def updated() {
  unsubscribe()
  subscribe(location, changeMode)
  subscribe(app, changeMode)
}

def changeMode(evt) {
  if(newMode == "Away") {
    log.info("Marking Away")
    thermostats?.away()
  }

  else {
    log.info("Marking Present")
    thermostats?.present()
  }
}