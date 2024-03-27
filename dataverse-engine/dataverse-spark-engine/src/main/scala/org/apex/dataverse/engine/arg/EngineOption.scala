package org.apex.dataverse.engine.arg

/**
 * Engine Arguments
 *  --engine-id 0
 *  --state-server-address 127.0.0.1
 *  --state-server-port 20001
 *  --state-client-threads 1
 *  --engine-server-port
 *  --engine-server-boss-threads
 *  --engine-server-worker-threads
 *  --engine-storage-code default
 *
 * @author Danny.Huo
 * @date 2023/4/26 16:32
 * @since 0.1.0
 */
object EngineOption {

  /**
   * MASTER
   */
  val MASTER: EngineKV = EngineKV("--master", "local")

  /**
   * Enable hive
   */
  val ENABLE_HIVE: EngineKV = EngineKV("--enable-hive", "false")

  /**
   * 引擎ID
   */
  val ENGINE_ID: EngineKV = EngineKV("--engine-id", "0")

  /**
   * 引擎状态服务器所在地址
   */
  val TICK_SERVER_ADDRESS: EngineKV = EngineKV("--tick-server-address", "127.0.0.1")

  /**
   * 引擎状态服务器端口号
   */
  val TICK_SERVER_PORT: EngineKV = EngineKV("--tick-server-port", "20001")

  /**
   * 引擎状态客户端线程数
   */
  val TICK_CLIENT_THREADS: EngineKV = EngineKV("--tick-client-threads", "1")

  /**
   * 心跳间隔
   */
  val TICK_INTERVAL: EngineKV = EngineKV("--tick-interval-ms", "5000")

  /**
   * 引擎端口
   */
  val ENGINE_SERVER_PORT: EngineKV = EngineKV("--engine-server-port", null)

  /**
   * BOSS端线程数
   */
  val ENGINE_SERVER_BOSS_THREADS: EngineKV = EngineKV("--engine-server-boss-threads", null)

  /**
   * Worker端线程数
   */
  val ENGINE_SERVER_WORKER_THREADS: EngineKV = EngineKV("--engine-server-worker-threads", null)

  /**
   * 引擎所属存储区编码
   * storage code
   */
  val ENGINE_STORAGE_CODE: EngineKV = EngineKV("--engine-storage-code", "default")

  case class EngineKV(key: String, default: String)

}
