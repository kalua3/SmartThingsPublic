/**
 *	Button Controller App for ZWN-SC7
 *
 *	Author: Matt Frank based on VRCS Button Controller by Brian Dahlem, based on SmartThings Button Controller
 *	Date Created: 2014-12-18
 *  Last Updated: 2015-10-05
 *
 */
definition(
    name: "ZWN-SC7	 Button Controller",
    namespace: "mattjfrank",
    author: "Matt Frank, using code from Brian Dahlem",
    description: "ZWN-SC7	 7-Button	 Scene	 Controller	Button Assignment App",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/MyApps/Cat-MyApps.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/MyApps/Cat-MyApps@2x.png"
)

preferences {
  page(name: "selectButton")
  page(name: "configureButton1")
  page(name: "configureButton2")
  page(name: "configureButton3")
  page(name: "configureButton4")
  page(name: "configureButton5")
  page(name: "configureButton6")
  page(name: "configureButton7")
}

def selectButton() {
  dynamicPage(name: "selectButton", title: "First, select which ZWN-SC7", nextPage: "configureButton1", uninstall: configured()) {
    section {
      input "buttonDevice", "capability.button", title: "Controller", multiple: false, required: true
    }
  }
}

def configureButton1() {
  dynamicPage(name: "configureButton1", title: "1st button, what do you want it to do?",
    nextPage: "configureButton2", uninstall: configured(), getButtonSections(1))
}

def configureButton2() {
  dynamicPage(name: "configureButton2", title: "2nd button, what do you want it to do?",
    nextPage: "configureButton3", uninstall: configured(), getButtonSections(2))
}

def configureButton3() {
  dynamicPage(name: "configureButton3", title: "3rd button, what do you want it to do?",
    nextPage: "configureButton4", uninstall: configured(), getButtonSections(3))
}
def configureButton4() {
  dynamicPage(name: "configureButton4", title: "4th button, what do you want it to do?",
    nextPage: "configureButton5", uninstall: configured(), getButtonSections(4))
}
def configureButton5() {
  dynamicPage(name: "configureButton5", title: "5th button, what do you want it to do?",
    nextPage: "configureButton6", uninstall: configured(), getButtonSections(5))
}
def configureButton6() {
  dynamicPage(name: "configureButton6", title: "6th button, what do you want it to do?",
    nextPage: "configureButton7", uninstall: configured(), getButtonSections(6))
}
def configureButton7() {
  dynamicPage(name: "configureButton7", title: "7th  button, what do you want it to do?",
    install: true, uninstall: true, getButtonSections(7))
}

def getButtonSections(buttonNumber) {
  return {
    section(title: "Toggle these...", hidden: hideSection(buttonNumber, "toggle"), hideable: true) {
      input "lights_${buttonNumber}_toggle", "capability.switch", title: "switches:", multiple: true, required: false
      input "locks_${buttonNumber}_toggle", "capability.lock", title: "locks:", multiple: true, required: false
      input "sonos_${buttonNumber}_toggle", "capability.musicPlayer", title: "music players:", multiple: true, required: false
    }
    section(title: "Turn on these...", hidden: hideSection(buttonNumber, "on"), hideable: true) {
      input "lights_${buttonNumber}_on", "capability.switch", title: "switches:", multiple: true, required: false
      input "lights_${buttonNumber}_level", "number", title: "set level to:", multiple: false, required: false, range:"(1..100)"
      input "sonos_${buttonNumber}_on", "capability.musicPlayer", title: "music players:", multiple: true, required: false
    }
    section(title: "Turn off these...", hidden: hideSection(buttonNumber, "off"), hideable: true) {
      input "lights_${buttonNumber}_off", "capability.switch", title: "switches:", multiple: true, required: false
      input "sonos_${buttonNumber}_off", "capability.musicPlayer", title: "music players:", multiple: true, required: false
    }
    section(title: "Set these colors...", hidden: hideSection(buttonNumber, "off"), hideable: true) {
      input "colors1_${buttonNumber}_lights", "capability.colorControl", title: "first lights:", multiple: true, required: false
      input "colors1_${buttonNumber}_color", "enum", title: "first color:", options: ["Red","Brick Red","Safety Orange","Dark Orange","Amber","Gold","Yellow","Electric Lime","Lawn Green","Bright Green","Lime","Spring Green","Turquoise","Aqua","Sky Blue","Dodger Blue","Navy Blue","Blue","Han Purple","Electric Indigo","Electric Purple","Orchid Purple","Magenta","Hot Pink","Deep Pink","Raspberry","Crimson","Red"], multiple: false, required: false
      input "colors1_${buttonNumber}_saturation", "number", title: "first saturation:", range: "0..100", defaultValue: 100, multiple: false, required: true
	  input "colors2_${buttonNumber}_lights", "capability.colorControl", title: "second lights:", multiple: true, required: false
      input "colors2_${buttonNumber}_color", "enum", title: "second color:", options: ["Red","Brick Red","Safety Orange","Dark Orange","Amber","Gold","Yellow","Electric Lime","Lawn Green","Bright Green","Lime","Spring Green","Turquoise","Aqua","Sky Blue","Dodger Blue","Navy Blue","Blue","Han Purple","Electric Indigo","Electric Purple","Orchid Purple","Magenta","Hot Pink","Deep Pink","Raspberry","Crimson","Red"], multiple: false, required: false
      input "colors2_${buttonNumber}_saturation", "number", title: "second saturation:", range: "0..100", defaultValue: 100, multiple: false, required: true
	  input "colors3_${buttonNumber}_lights", "capability.colorControl", title: "third lights:", multiple: true, required: false
      input "colors3_${buttonNumber}_color", "enum", title: "third color:", options: ["Red","Brick Red","Safety Orange","Dark Orange","Amber","Gold","Yellow","Electric Lime","Lawn Green","Bright Green","Lime","Spring Green","Turquoise","Aqua","Sky Blue","Dodger Blue","Navy Blue","Blue","Han Purple","Electric Indigo","Electric Purple","Orchid Purple","Magenta","Hot Pink","Deep Pink","Raspberry","Crimson","Red"], multiple: false, required: false
      input "colors3_${buttonNumber}_saturation", "number", title: "third saturation:", range: "0..100", defaultValue: 100, multiple: false, required: true
    }
    section(title: "Set these color temperatures...", hidden: hideSection(buttonNumber, "off"), hideable: true) {
      input "colorTemp_${buttonNumber}_lights", "capability.colorControl", title: "lights:", multiple: true, required: false
      input "colorTemp_${buttonNumber}_color", "number", title: "color temp:", range:"(2700..6500)", multiple: false, required: false
    }
    section(title: "Locks:", hidden: hideLocksSection(buttonNumber), hideable: true) {
      input "locks_${buttonNumber}_unlock", "capability.lock", title: "Unlock these locks:", multiple: true, required: false
      input "locks_${buttonNumber}_lock", "capability.lock", title: "Lock these locks:", multiple: true, required: false
    }
    section("Modes") {
      input "mode_${buttonNumber}_on", "mode", title: "Activate these modes:", required: false
    }
    if (getPhrases()) {
      section("Hello Home Actions") {
        input "phrase_${buttonNumber}_on", "enum", title: "Activate these phrases:", required: false, options: getPhrases()
      }
    }
  }
}

def getPhrases() {
	def phrases = []
    
	location.helloHome?.getPhrases()*.label.each{phraseName ->
    	if(phraseName != null ) { // wtf SmartThings? How is there even the possibility of a null routine?
    		phrases << phraseName
        }
    }
    
    log.trace phrases
    return phrases
}

def installed() {
  initialize()
}

def updated() {
  unsubscribe()
  initialize()
}

def initialize() {

  subscribe(buttonDevice, "button", buttonEvent)

    if (relayDevice) {
        log.debug "Associating ${relayDevice.deviceNetworkId}"
        if (relayAssociate == true) {
            buttonDevice.associateLoad(relayDevice.deviceNetworkId)
        }
        else {
            buttonDevice.associateLoad(0)
        }
    }
}

def configured() {
  return  buttonDevice || buttonConfigured(1) || buttonConfigured(2) || buttonConfigured(3) || buttonConfigured(4) || buttonConfigured(5) || buttonConfigured(6) || buttonConfigured(7)
}

def buttonConfigured(idx) {
  return settings["lights_$idx_toggle"] ||
    settings["locks_$idx_toggle"] ||
    settings["sonos_$idx_toggle"] ||
    settings["mode_$idx_on"] ||
    settings["lights_$idx_on"] ||
    settings["lights_$idx_level"] ||
    settings["locks_$idx_on"] ||
    settings["sonos_$idx_on"] ||
    settings["lights_$idx_off"] ||
    settings["locks_$idx_off"] ||
    settings["sonos_$idx_off"] ||
    settings["colors1_$idx_lights"] ||
    settings["colors1_$idx_color"] ||
    settings["colors1_$idx_saturation"] ||
	settings["colors2_$idx_lights"] ||
    settings["colors2_$idx_color"] ||
    settings["colors2_$idx_saturation"] ||
	settings["colors3_$idx_lights"] ||
    settings["colors3_$idx_color"] ||
    settings["colors3_$idx_saturation"] ||
    settings["colorTemp_$idx_lights"] ||
    settings["colorTemp_$idx_color"] ||
    settings["mode_$idx_on"] ||
    settings["phrase_$idx_on"]
}

def buttonEvent(evt){
  log.debug "buttonEvent"
  if(allOk) {
    def buttonNumber = evt.jsonData.buttonNumber
    log.debug "buttonEvent: $evt.name - ($evt.data)"
    log.debug "button: $buttonNumber"

	
    def recentEvents = buttonDevice.eventsSince(new Date(now() - 1000)).findAll{it.value == evt.value && it.data == evt.data}
    log.debug "Found ${recentEvents.size()?:0} events in past 1 seconds"

    if(recentEvents.size <= 3){
      switch(buttonNumber) {
        case ~/.*1.*/:
          executeHandlers(1)
          break
        case ~/.*2.*/:
          executeHandlers(2)
          break
        case ~/.*3.*/:
          executeHandlers(3)
          break
        case ~/.*4.*/:
          executeHandlers(4)
          break
        case ~/.*5.*/:
          executeHandlers(5)
          break
        case ~/.*6.*/:
          executeHandlers(6)
          break
        case ~/.*7.*/:
          executeHandlers(7)
          break
      }
    } else {
      log.debug "Found recent button press events for $buttonNumber with value $value"
    }
  }
    else {
      log.debug "NotOK"
    }
}

def executeHandlers(buttonNumber) {
  log.debug "executeHandlers: $buttonNumber"

  def lights = find('lights', buttonNumber, "toggle")
  if (lights != null) toggle(lights)

  def locks = find('locks', buttonNumber, "toggle")
  if (locks != null) toggle(locks)

  def sonos = find('sonos', buttonNumber, "toggle")
  if (sonos != null) toggle(sonos)

  lights = find('lights', buttonNumber, "on")
  def level = find('lights', buttonNumber, "level")
  if (lights != null) flip(lights, "on", level)
  
  def colors1Lights = find('colors1', buttonNumber, "lights")
  def colors1Color = find('colors1', buttonNumber, "color")
  def colors1Saturation = find('colors1', buttonNumber, "saturation")
  if(colors1Lights != null && colors1Color != null && colors1Saturation != null) setColor(colors1Lights, colors1Color, colors1Saturation)
  
  def colors2Lights = find('colors2', buttonNumber, "lights")
  def colors2Color = find('colors2', buttonNumber, "color")
  def colors2Saturation = find('colors2', buttonNumber, "saturation")
  if(colors2Lights != null && colors2Color != null && colors2Saturation != null) setColor(colors2Lights, colors2Color, colors2Saturation)
  
  def colors3Lights = find('colors3', buttonNumber, "lights")
  def colors3Color = find('colors3', buttonNumber, "color")
  def colors3Saturation = find('colors3', buttonNumber, "saturation")
  if(colors3Lights != null && colors3Color != null && colors3Saturation != null) setColor(colors3Lights, colors3Color, colors3Saturation)
  
  def colorTempLights = find('colorTemp', buttonNumber, "lights")
  def colorTempColor = find('colorTemp', buttonNumber, "color")
  if(colorTempLights != null && colorTempColor != null) setColorTemperature(colorTempLights, colorTempColor)

  lights = find('lights', buttonNumber, "off")
  if (lights != null) flip(lights, "off", null)

  locks = find('locks', buttonNumber, "unlock")
  if (locks != null) flip(locks, "unlock", null)

  sonos = find('sonos', buttonNumber, "on")
  if (sonos != null) flip(sonos, "on", null)

  locks = find('locks', buttonNumber, "lock")
  if (locks != null) flip(locks, "lock", null)

  sonos = find('sonos', buttonNumber, "off")
  if (sonos != null) flip(sonos, "off", null)

  def mode = find('mode', buttonNumber, "on")
  if (mode != null) changeMode(mode)

  def phrase = find('phrase', buttonNumber, "on")
  if (phrase != null) location.helloHome.execute(phrase)
}

def find(type, buttonNumber, value) {
  def preferenceName = type + "_" + buttonNumber + "_" + value
  def pref = settings[preferenceName]
  if(pref != null) {
    log.debug "Found: $pref for $preferenceName"
  }

  return pref
}

def flip(devices, newState, level) {
  log.debug "flip: $devices = ${devices*.currentValue('switch')}"
  def cmd = []
  
  if (newState == "off") {
    cmd << devices.off()
  }
  else if (newState == "on") {
    if(level == null) {
    	cmd << devices.on()
    } else {
    	cmd << devices.setLevel(level)
    }
  }
  else if (newState == "unlock") {
    devices.unlock()
  }
  else if (newState == "lock") {
    devices.lock()
  }
  
  cmd
}

def toggle(devices) {
  log.debug "toggle: $devices = ${devices*.currentValue('switch')}"

  if (devices*.currentValue('switch').contains('on')) {
    devices.off()
  }
  else if (devices*.currentValue('switch').contains('off')) {
    devices.on()
  }
  else if (devices*.currentValue('lock').contains('locked')) {
    devices.unlock()
  }
  else if (devices*.currentValue('lock').contains('unlocked')) {
    devices.lock()
  }
  else {
    devices.on()
  }
}

def changeMode(mode) {
  log.debug "changeMode: $mode, location.mode = $location.mode, location.modes = $location.modes"

  if (location.mode != mode && location.modes?.find { it.name == mode }) {
    setLocationMode(mode)
  }
}

// execution filter methods
private getAllOk() {
  modeOk && daysOk && timeOk
}

private getModeOk() {
  def result = !modes || modes.contains(location.mode)
  log.trace "modeOk = $result"
  result
}

private getDaysOk() {
  def result = true
  if (days) {
    def df = new java.text.SimpleDateFormat("EEEE")
    if (location.timeZone) {
      df.setTimeZone(location.timeZone)
    }
    else {
      df.setTimeZone(TimeZone.getTimeZone("America/New_York"))
    }
    def day = df.format(new Date())
    result = days.contains(day)
  }
  log.trace "daysOk = $result"
  result
}

private getTimeOk() {
  def result = true
  if (starting && ending) {
    def currTime = now()
    def start = timeToday(starting).time
    def stop = timeToday(ending).time
    result = start < stop ? currTime >= start && currTime <= stop : currTime <= stop || currTime >= start
  }
  log.trace "timeOk = $result"
  result
}

private hhmm(time, fmt = "h:mm a")
{
  def t = timeToday(time, location.timeZone)
  def f = new java.text.SimpleDateFormat(fmt)
  f.setTimeZone(location.timeZone ?: timeZone(time))
  f.format(t)
}

private hideOptionsSection() {
  (starting || ending || days || modes) ? false : true
}

private hideSection(buttonNumber, action) {
  (find("lights", buttonNumber, action) || find("locks", buttonNumber, action) || find("sonos", buttonNumber, action)) ? false : true
}

private hideLocksSection(buttonNumber) {
  (find("lights", buttonNumber, "lock") || find("locks", buttonNumber, "unlock")) ? false : true
}

private timeIntervalLabel() {
  (starting && ending) ? hhmm(starting) + "-" + hhmm(ending, "h:mm a z") : ""
}

private integer(String s) {
  return Integer.parseInt(s)
}

def getHue(colorName) {
    def hueValue = 0.0
    
	if(colorName == "Red") {
    	hueValue = 0
    }
    else if(colorName == "Brick Red") {
    	hueValue = 13
    }
    else if(colorName == "Safety Orange") {
    	hueValue = 26
    }
    else if(colorName == "Dark Orange") {
    	hueValue = 36
    }
    else if(colorName == "Amber") {
    	hueValue = 45
    }
    else if(colorName == "Gold") {
    	hueValue = 53
    }
    else if(colorName == "Yellow") {
    	hueValue = 61
    }
    else if(colorName == "Electric Lime") {
    	hueValue = 75
    }
    else if(colorName == "Lawn Green") {
    	hueValue = 89
    }
    else if(colorName == "Bright Green") {
    	hueValue = 103
    }
    else if(colorName == "Lime") {
    	hueValue = 124
    }
    else if(colorName == "Spring Green") {
    	hueValue = 151
    }
    else if(colorName == "Turquoise") {
    	hueValue = 169
    }
    else if(colorName == "Aqua") {
    	hueValue = 180
    }
    else if(colorName == "Sky Blue") {
    	hueValue = 196
    }
    else if(colorName == "Dodger Blue") {
    	hueValue = 211
    }
    else if(colorName == "Navy Blue") {
    	hueValue = 221
    }
    else if(colorName == "Blue") {
    	hueValue = 238
    }
    else if(colorName == "Han Purple") {
    	hueValue = 254
    }
    else if(colorName == "Electric Indigo") {
    	hueValue = 266
    }
    else if(colorName == "Electric Purple") {
    	hueValue = 282
    }
    else if(colorName == "Orchid Purple") {
    	hueValue = 295
    }
	else if(colorName == "Magenta") {
    	hueValue = 308
    }
    else if(colorName == "Hot Pink") {
    	hueValue = 642
    }
    else if(colorName == "Deep Pink") {
    	hueValue = 331
    }
    else if(colorName == "Raspberry") {
    	hueValue = 338
    }
    else if(colorName == "Crimson") {
    	hueValue = 346
    }
    
    hueValue = Math.round(hueValue * 100 / 360)
    
    hueValue
}

def setColor(lights, colorName, saturation) {
    def hueValue = getHue(colorName)
    def satValue = saturation.toInteger()
    
    def colorValue = [level:null, saturation:satValue, hue:hueValue, alpha:1.0]
    
    log.trace "Changing color for lights $lights.label to $colorName with saturation of $satValue."
    lights.setColor(colorValue)
}

def setColorTemperature(lights, colorTemp) {  
    log.trace "Changing color temperature to $colorTemp."
    lights.setColorTemperature(colorTemp)
}