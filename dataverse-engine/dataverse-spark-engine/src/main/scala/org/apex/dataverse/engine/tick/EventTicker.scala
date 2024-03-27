package org.apex.dataverse.engine.tick

import org.apex.dataverse.core.context.ClientContext
import org.apex.dataverse.core.enums.{CmdSet, EngineEvent, EngineType}
import org.apex.dataverse.core.msg.{Header, Request}
import org.apex.dataverse.core.msg.packet.Packet
import org.apex.dataverse.core.msg.packet.engine.EngineEventPacket
import org.apex.dataverse.core.netty.client.TickClient
import org.apex.dataverse.core.util.{AddressUtil, UCodeUtil}
import org.apex.dataverse.engine.EngineApp
import org.apex.dataverse.engine.arg.EngineContext

/**
 *
 * @author : Danny.Huo
 * @date : 2023/4/25 13:45
 * @version : v1.0
 */
object EventTicker {

  /**
   * Ticker context
   */
  private var ticker: ClientContext = _

  /**
   * Tick interval
   */
  private var tickInterval : Int = 5000

  /**
   * 连接至状态服务器
   *
   * @param context ClientContext
   * @return
   */
  def connectTickServer(context: EngineContext): TickClient = {
    ticker = context.clientContext

    tickInterval = context.tickInterval

    // 启动Ticker线程
    val tickClient = new TickClient(ticker)
    val thread = new Thread(tickClient)
    thread.start()

    // 启动心跳汇报线程
    new Thread(() => {
      while (tickClient.isActive) {
        Thread.sleep(tickInterval)

        tickEvent(s"Engine[${context.engineId}] heart beat", EngineEvent.HEART_BEAT)
      }

      // 链接断开
      thread.interrupt()

      System.exit(0)

    }).start()

    tickClient
  }

  /**
   * Tick engine event
   *
   * @param msg String
   * @param event EngineEvent
   */
  def tickEvent(msg: String, event: EngineEvent): Unit = {
    val eventPacket = new EngineEventPacket
    eventPacket.setEngineId(EngineApp.engineContext.engineId)
    eventPacket.setMessage(msg)
    eventPacket.setState(event.getCode)
    eventPacket.setCommandId(UCodeUtil.produce());
    eventPacket.setEngineType(EngineType.SPARK_ENGINE.getName)

    // 注册engine时
    if (EngineEvent.ENGINE_REGISTER == event) {
      // 引擎所在地址
      eventPacket.setIp(AddressUtil.getLocalAddress)
      // 引擎Server端口
      eventPacket.setPort(EngineApp.engineContext.serverContext.getEnv.getServerPort)
      // 设置Engine名称
      eventPacket.setEngineName(EngineApp.engineContext.engineName)
      // 设置Hostname
      eventPacket.setHostname(AddressUtil.getLocalHostname)
      // 设置汇报心跳间隔
      eventPacket.setHeartbeatInterval(tickInterval)
    }

    val request = new Request[Packet]
    request.setHeader(Header.newHeader(CmdSet.ENGINE_EVENT.getReq))

    request.setPacket(eventPacket)

    ticker.pushRequest(request)
  }
}
