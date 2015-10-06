/**
 *  Intermatic CA3750 Device-Type 1.0
 *
 *  Copyright 2014 Cooper Lee
 *
 */



metadata {
	// Automatically generated. Make future change here.
	definition (name: "Intermatic CA3750", namespace: "research2", author: "Cooper Lee") {
		capability "Actuator"
		capability "Indicator"
		capability "Switch"
		capability "Polling"
		capability "Refresh"
		capability "Sensor"


		fingerprint inClusters: "0x91 0x73 0x72 0x86 0x60 0x25 0x27"
        
		attribute "switch1", "string"
		attribute "switch2", "string"
		attribute "switch3", "string"

		attribute "Mode", "string"


		command "on"
		command "off"

		command "on1"
		command "off1"
		command "on2"
		command "off2"

}
	simulator {
		// TODO: define status and reply messages here
	}


	// tile definitions
	tiles {
		standardTile("switch", "device.switch", width: 2, height: 2, canChangeIcon: true) {
			state "on", label: '${name}', action: "off", icon: "st.switches.switch.on", backgroundColor: "#79b821"
			state "off", label: '${name}', action: "on", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
		}
		standardTile("switch1", "device.switch1",canChangeIcon: false) {
                        state "on", label: "switch1", action: "off1", icon: "st.switches.switch.on", backgroundColor: "#79b821"
                        state "off", label: "switch1", action: "on1", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
                }
        standardTile("switch2", "device.switch2",canChangeIcon: false) {
                        state "on", label: "switch2", action: "off2", icon: "st.switches.switch.on", backgroundColor: "#79b821"
                        state "off", label: "switch2", action: "on2", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
                }

        standardTile("refresh", "device.switch", inactiveLabel: false, decoration: "flat") {
                        state "default", label:"", action:"refresh", icon:"st.secondary.refresh"
                }



		main "switch"
		details(["switch","switch1","switch2","refresh"])
	}
}

//0 0 0x1001 0 0 0 7 0x91 0x73 0x72 0x86 0x60 0x25 0x27
//Intermatic CA3750

//0x72 
//0x91 
// 0x25: switch binary
// 0x32: meter
// 0x27: switch all
// 0x60: multi-channel
// 0x70: configuration
// 0x72 Manufacturer Specific
// 0x73 Power Level
// 0x85: association
// 0x86: version
// 0x91 Manufacturer Proprietary
// 0xEF: mark
// 0x82: hail


def parse(String description) {
    def results = []
    def cmd = zwave.parse(description, [0x60:1, 0x25:1, 0x32:1, 0x70:1 , 0x72:1, 0x73:1, 0x91:1 ])
    if (cmd) { results = createEvent(zwaveEvent(cmd)) }
    return results
}

def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicReport cmd) {
//    log.debug "Strip (or sw1) Basic - $cmd ${cmd?.value}"
    def map = []; def value;
    if(cmd.value==255) { value="on" } else { value="off" }
    map = [name: "switch1", value:value, type: "digital"]
	map
}

def zwaveEvent(physicalgraph.zwave.commands.switchbinaryv1.SwitchBinaryReport cmd) {
    log.debug "Switch BINARY (button) - $cmd ${cmd?.value} "
    def map = []; def value
    if(cmd.value==255) { value="on" } else { value="off" }
    map = [name: "switch1", value:value, type: "digital"]
	map
}


def zwaveEvent(physicalgraph.zwave.commands.switchallv1.SwitchAllReport cmd) {
//    log.debug "Switch All - $cmd ${cmd?.mode}"
    def value
    if(cmd.mode==255) { value="on" } else { value="off" }
    return [name:"switch", value:value]
}


def zwaveEvent(int endPoint, physicalgraph.zwave.commands.meterv1.MeterReport cmd) {
	 log.debug "V1 Report EndPoint $endPoint, MeterReport $cmd  scale ${cmd?.scale}"
    def map = []

    if (cmd?.scale == 0) {
    	map = [ name: "energy" + endPoint, value: cmd.scaledMeterValue, unit: "kWh"]
        }
    else if (cmd?.scale == 2) {
    	map = [  name: "power" + endPoint, value: Math.round(cmd.scaledMeterValue), unit: "W" ]
    }

    map
}

def zwaveEvent(physicalgraph.zwave.commands.multichannelv3.MultiInstanceCmdEncap cmd) {
    log.debug "Mv3 $cmd"

    def map = [ name: "switch$cmd.sourceEndPoint" ]
    if (cmd.commandClass == 37){
    	if (cmd.parameter == [0]) {
        	map.value = "off"
        }
        if (cmd.parameter == [255]) {
            map.value = "on"
        	sendEvent(name:"switch", value:"on", displayed:false)
        }
        map
    }
    else if (cmd.commandClass == 50) {
        def hex1 = { n -> String.format("%02X", n) }
        def desc = "command: ${hex1(cmd.commandClass)}${hex1(cmd.command)}, payload: " + cmd.parameter.collect{hex1(it)}.join(" ")
        //log.debug "ReParse command as specifc endpoint"
        zwaveEvent(cmd.sourceEndPoint, zwave.parse(desc, [ 0x25:1, 0x32:1, 0x70:1 , 0x72:2, 0x73:1 ]))
    }
}

def zwaveEvent(physicalgraph.zwave.commands.multiinstancev1.MultiInstanceCmdEncap cmd) {
    log.debug "Miv1 $cmd - $cmd?.instance - $cmd?.commandClass"

    def map = [ name: "switch$cmd.instance" ]
    if (cmd.commandClass == 37){
    	if (cmd.parameter == [0]) {
        	map.value = "off"
        }
        if (cmd.parameter == [255]) {
            map.value = "on"
        	sendEvent(name:"switch", value:"on", displayed:false)
        }
        map
    }
    else if (cmd.commandClass == 50) {
        def hex1 = { n -> String.format("%02X", n) }
        def desc = "command: ${hex1(cmd.commandClass)}${hex1(cmd.command)}, payload: " + cmd.parameter.collect{hex1(it)}.join(" ")
        //log.debug "ReParse command as specifc endpoint"
        zwaveEvent(cmd.sourceEndPoint, zwave.parse(desc, [ 0x25:1, 0x32:1, 0x70:1 , 0x72:2, 0x73:1 ]))
    }
}

def zwaveEvent(physicalgraph.zwave.commands.multichannelv3.MultiInstanceReport cmd) {
    log.debug "mi v3 report $cmd - $cmd?.instance - $cmd?.commandClass"
}

def zwaveEvent(physicalgraph.zwave.commands.manufacturerspecificv1.ManufacturerSpecificReport cmd) {
    log.debug "mi v1 specific report $cmd - $cmd?.instance - $cmd?.commandClass"
}

def zwaveEvent(physicalgraph.zwave.commands.multichannelv3.MultiChannelCapabilityReport cmd) {
    log.debug "mc v3 report $cmd"
}

def zwaveEvent(physicalgraph.zwave.commands.multichannelv3.MultiChannelEndPointGet cmd) {
	log.debug "mc v3 endpoint $cmd"
}

def zwaveEvent(physicalgraph.zwave.commands.configurationv1.ConfigurationReport cmd) {
	log.debug "Configuration Report for parameter ${cmd.parameterNumber}: Value is ${cmd.configurationValue} Size is ${cmd.size}"
}

def zwaveEvent(physicalgraph.zwave.Command cmd) {
        // Handles all Z-Wave commands we arent interested in 
        [:]
    log.debug "Capture All $cmd"
}



def poll() {
	log.debug "<FONT COLOR=RED>Polling Switch - $device.label</FONT>"
	delayBetween([
		zwave.switchBinaryV1.switchBinaryGet().format(),
		zwave.manufacturerSpecificV1.manufacturerSpecificGet().format(),
        zwave.multiChannelV3.multiInstanceReport(),
   		//zwave.multiInstanceV3.multiInstanceGet().format(),
        //zwave.commands.multichannelv3.MultiChannelCapabilityGet(),
	])
}

def refresh() {
	log.debug "----------------- Refresh requested $device.label -----------------" 
	delayBetween([
		//zwave.switchBinaryV1.switchBinaryGet().format(),
		//zwave.manufacturerSpecificV1.manufacturerSpecificGet().format(),
		//zwave.multiChannelV3.multiInstanceGet().format(),
        //zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:0, commandClass:25, command:0, parameter:[0]).format(),
        //zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:1, commandClass:25, command:0, parameter:[0]).format(),
        //zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:2, commandClass:25, command:0, parameter:[0]).format(),
        //zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:3, commandClass:25, command:0, parameter:[0]).format(),
        zwave.multiChannelV3.multiInstanceCmdEncap(instance:1, commandClass:37, command:1, parameter:[0]).format(),
        zwave.multiChannelV3.multiInstanceCmdEncap(instance:2, commandClass:37, command:1, parameter:[0]).format(),
	])
}

def indicatorWhenOn() {
	sendEvent(name: "indicatorStatus", value: "when on", display: false)
	zwave.configurationV1.configurationSet(configurationValue: [1], parameterNumber: 3, size: 1).format()
}

def setlevel1(value) { setleveX(1, value) }; def setlevel2(value) { setlevelX(2, value) }
//def on1() { swOn(1) }; def off1() { swOff(1) }
//def on2() { swOn(1) }; def off2() { swOff(1) }

def on() {
	log.debug "<FONT COLOR=GREEN>On Digital</FONT>"
    delayBetween([
		zwave.switchAllV1.switchAllOn().format(),
		zwave.switchAllV1.switchAllGet().format(),
		zwave.basicV1.basicSet(value: 0xFF).format(),
		zwave.switchBinaryV1.switchBinaryGet().format(),
        zwave.multiInstanceV1.multiInstanceGet().format(),
	])
}

def off() {
	log.debug "<FONT COLOR=GREEN>Off Digital</FONT>"
	delayBetween([
		zwave.switchAllV1.switchAllOff().format(),
		zwave.switchAllV1.switchAllGet().format(),
		zwave.basicV1.basicSet(value: 0x00).format(),
		zwave.switchBinaryV1.switchBinaryGet().format(),
        zwave.multiInstanceV1.multiInstanceGet().format(),
	])
}


def on1() {
	zwave.multiChannelV3.multiInstanceCmdEncap(instance:1, commandClass:37, command:1, parameter:[255]).format()
}

def off1() {
	zwave.multiChannelV3.multiInstanceCmdEncap(instance:1, commandClass:37, command:1, parameter:[0]).format()
}

def on2() {
	zwave.multiChannelV3.multiInstanceCmdEncap(instance:2, commandClass:37, command:1, parameter:[255]).format()
}

def off2() {
	zwave.multiChannelV3.multiInstanceCmdEncap(instance:1, commandClass:37, command:1, parameter:[0]).format()
}

def configure() {
	log.debug "Executing configure"
    def switchAllmode
    if(switchAll=="true") { switchAllmode = 255 } else { switchAllmode=0 }
    log.debug "SW All - $switchAllmode $switchAll"
    delayBetween([
    	zwave.multiInstanceV1.multiInstanceGet().format(),
		zwave.configurationV1.configurationSet(parameterNumber:101, size:4, configurationValue: [ 0, 0, 127, 127 ]).format(),
        zwave.configurationV1.configurationSet(parameterNumber:111, size:4, scaledConfigurationValue: 15).format(),
        zwave.configurationV1.configurationSet(parameterNumber:112, size:4, scaledConfigurationValue: 15).format(),
        zwave.configurationV1.configurationSet(parameterNumber:113, size:4, scaledConfigurationValue: 15).format(),
        zwave.configurationV1.configurationSet(parameterNumber:3, configurationValue: [3]).format(),
        zwave.configurationV1.configurationSet(parameterNumber:4, configurationValue: [0]).format(),
        zwave.switchAllV1.switchAllSet(mode:switchAllmode).format(),
        zwave.configurationV1.configurationGet().format(),
    ])
}


