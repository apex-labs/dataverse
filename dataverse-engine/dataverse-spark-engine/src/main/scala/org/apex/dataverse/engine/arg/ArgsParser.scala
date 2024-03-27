package org.apex.dataverse.engine.arg

import org.apex.dataverse.core.context.env.{ClientEnv, ServerEnv}
import org.apex.dataverse.core.context.{ClientContext, ServerContext}
import org.apex.dataverse.core.util.{AddressUtil, StringUtil}

/**
 * @author : Danny.Huo
 * @date : 2023/4/26 14:08
 * @since : 0.1.0
 */
object ArgsParser {

    /**
     * 解析参数
     *
     * @param args Array[String]
     * @return
     */
    def parseArgs(args: Array[String]): EngineContext = {
        //参数列表成对传递， key, value
        val clientContext = ClientContext.newContext(new ClientEnv())
        val serverContext = ServerContext.newContext(new ServerEnv())
        var storageCode = EngineOption.ENGINE_STORAGE_CODE.default
        var engineId = 0;
        var tickInterval = Integer.parseInt(EngineOption.TICK_INTERVAL.default)
        var master = "local"
        var enableHive = false
        for (i <- args.indices by 2) {
            val key = args(i).trim
            val value = args(i + 1)
            key match {
                // Master config
                case EngineOption.MASTER.key =>
                    master = value

                // Enable hive config
                case EngineOption.ENABLE_HIVE.key =>
                    enableHive = value.equalsIgnoreCase("true")

                // Engine config
                case EngineOption.ENGINE_ID.key =>
                    engineId = Integer.parseInt(value)

                // State client config
                case EngineOption.TICK_SERVER_ADDRESS.key =>
                    clientContext.getEnv.setServerAddress(if (null == value) EngineOption.TICK_SERVER_ADDRESS.default else value)
                case EngineOption.TICK_SERVER_PORT.key =>
                    clientContext.getEnv.setServerPort(Integer.parseInt(value))
                case EngineOption.TICK_CLIENT_THREADS.key =>
                    clientContext.getEnv.setThreads(Integer.parseInt(value))
                case EngineOption.TICK_INTERVAL.key =>
                    tickInterval = Integer.parseInt(value)

                // Engine Server config
                case EngineOption.ENGINE_SERVER_PORT.key =>
                    serverContext.getEnv.setServerPort(Integer.parseInt(value))
                case EngineOption.ENGINE_SERVER_BOSS_THREADS.key =>
                    serverContext.getEnv.setBossThreads(Integer.parseInt(value))
                case EngineOption.ENGINE_SERVER_WORKER_THREADS.key =>
                    serverContext.getEnv.setWorkerThreads(Integer.parseInt(value))

                // storage info
                case EngineOption.ENGINE_STORAGE_CODE.key =>
                    if (StringUtil.isNotBlank(value)) storageCode = value

                case _ =>
                    // else
            }
        }

        // 设置空闲端口
        if (null == serverContext.getEnv.getServerPort) {
            serverContext.getEnv.setServerPort(AddressUtil.getFreePort)
        }

        val engineName = AddressUtil.getLocalHostname + "(" +
                AddressUtil.getLocalAddress + "):" +
                serverContext.getEnv.getServerPort

        EngineContext(master, enableHive, engineId, engineName, tickInterval, storageCode, clientContext, serverContext)
    }

}
