package com.github.wcaleniekubaa.fmd

class MCPMappings(
    val methods: List<MCPMember> = listOf(), val fields: List<MCPMember> = listOf()
)

class MCPMember(val seargeName: String, val mcpName: String)
