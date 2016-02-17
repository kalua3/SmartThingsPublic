/**
 *  dim-with-me.app.groovy
 *  Dim With Me
 *
 *  Author: Jeremy Huckeba
 *  Date: 2016-02-16
 */
/**
 *  
 *  Use this program with a virtual dimmer as the master for best results.
 *
 *  This app lets the user select from a list of dimmers to act as a triggering
 *  master for other dimmers or regular switches. Regular switches come on
 *  anytime the master dimmer is on or dimmer level is set to more than 0%.
 *  of the master dimmer.
 *           
 * Other Info:	Code originally by Todd Wackford (twack@wackware.net). Rewrote it to fix a pleathora of
 *              bugs mirroring levels.
 *
 ******************************************************************************
 */


// Automatically generated. Make future change here.
definition(
    name: "Switch Mirror",
    namespace: "LunkwillAndfook",
    author: "Jeremy Huckeba",
    description: "Follows the dimmer level of another dimmer",
    category: "My Apps",
    iconUrl: "http://cdn.device-icons.smartthings.com/Lighting/light11-icn.png",
    iconX2Url: "http://cdn.device-icons.smartthings.com/Lighting/light11-icn@2x.png",
    iconX3Url: "http://cdn.device-icons.smartthings.com/Lighting/light11-icn@3x.png"
)

preferences {
	section("When this...") { 
		input "masters", "capability.switchLevel", 
			multiple: false, 
			title: "Master Dimmer Switch...", 
			required: true
	}

	section("Then these will follow with on/off...") {
		input "slaves2", "capability.switch", 
			multiple: true, 
			title: "Slave On/Off Switch(es)...", 
			required: false
	}
    
	section("And these will follow with dimming level...") {
		input "slaves", "capability.switchLevel", 
			multiple: true, 
			title: "Slave Dimmer Switch(es)...", 
			required: true
	}
}

def installed()
{
	subscribe(masters, "switch.on", switchOnHandler)
	subscribe(masters, "switch.off", switchOffHandler)
	subscribe(masters, "switch", switchLevelHandler)
    subscribe(masters, "level", switchLevelHandler)
    log.info "subscribed to all of switches events on install"
}

def updated()
{
	unsubscribe()
	subscribe(masters, "switch.on", switchOnHandler)
	subscribe(masters, "switch.off", switchOffHandler)
	subscribe(masters, "switch", switchLevelHandler)
    subscribe(masters, "level", switchLevelHandler)
	log.info "subscribed to all of switches events on update"
}

def switchLevelHandler(evt)
{	
	log.info "switchLevelHandler Event: ${evt.value}"
	if ((evt.value == "on") || (evt.value == "off" ))
		return
	def level = evt.value.toFloat()
	level = level.toInteger()
	log.info "switchSetLevelHandler Event: ${level}"
	slaves?.setLevel(level)
}

def switchOffHandler(evt) {
	log.info "switchoffHandler Event: ${evt.value}"
	slaves?.off()
	slaves2?.off()
}

def switchOnHandler(evt) {
	log.info "switchOnHandler Event: ${evt.value}"
	def dimmerValue = masters.latestValue("level") //can be turned on by setting the level
	slaves?.on()
	slaves2?.on()
}