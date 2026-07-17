<#import "common.ftl" as common>
<#-- ==================== ROUTINES INDEX ==================== -->
# ${msg.navRoutines()}

<#list catalog.schemas as schema>
<#assign schemaName = schema.fullName!msg.valueNa()>
<#assign hasRoutinesInSchema = false>
<#list routines as routine>
<#if (routine.schema.fullName!msg.valueNa()) == schemaName>
<#if !hasRoutinesInSchema>
## ${schemaName}
<#assign hasRoutinesInSchema = true>
</#if>
<#assign routineTypeLabel = msg.labelRoutine()>
<#assign routineType = routine.routineType?string?lower_case>
<#if routineType?contains("function")>
<#assign routineTypeLabel = msg.labelFunction()>
<#elseif routineType?contains("procedure")>
<#assign routineTypeLabel = msg.labelStoredProcedure()>
</#if>
- ${routineTypeLabel}: <@common.routineLink routine=routine pathPrefix="./"/>
</#if>
</#list>
</#list>
