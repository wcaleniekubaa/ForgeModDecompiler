package com.github.wcaleniekubaa.fmd.asm

import com.github.wcaleniekubaa.fmd.FMDMain
import com.github.wcaleniekubaa.fmd.MCPMappings
import org.objectweb.asm.commons.Remapper

/**
 * Maps Searge -> MCP
 * Example:
 * func_71410_x -> getMinecraft
 */
class SeargeMapper(private val map : MCPMappings) : Remapper() {
    /**
     * As it says maps field name
     */
    override fun mapFieldName(owner: String, name: String, descriptor: String): String {
        for(field in map.fields) {
            if(field.seargeName == name) {
                FMDMain.logger.info("Mapping field: $name -> ${field.mcpName}")
                return field.mcpName
            }
        }
        return name
    }

    /**
     * As it says maps method name
     */
    override fun mapMethodName(owner: String, name: String, descriptor: String): String {
        for(method in map.methods) {
            if(method.seargeName == name) {
                FMDMain.logger.info("Mapping method: $name -> ${method.mcpName}")
                return method.mcpName
            }
        }
        return name
    }
}