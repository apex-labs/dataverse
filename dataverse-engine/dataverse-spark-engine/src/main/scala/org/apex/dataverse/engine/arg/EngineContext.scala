package org.apex.dataverse.engine.arg

import org.apex.dataverse.core.context.{ClientContext, ServerContext}

/**
 * Engine Context build before start engine
 *
 * @author : Danny.Huo
 * @date : 2023/4/26 18:44
 * @version : v1.0
 */
case class EngineContext(master : String,
                         enableHive : Boolean,
                         engineId : Int,
                         engineName : String,
                         tickInterval : Int,
                         storageCode: String,
                         clientContext: ClientContext,
                         serverContext: ServerContext)
