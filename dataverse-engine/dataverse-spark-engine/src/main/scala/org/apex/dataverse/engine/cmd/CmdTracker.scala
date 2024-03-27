package org.apex.dataverse.engine.cmd

import org.apex.dataverse.core.context.ClientContext
import org.apex.dataverse.core.msg.Request
import org.apex.dataverse.core.msg.packet.Packet


/**
 *
 * @author : Danny.Huo
 * @date : 2023/4/27 17:55
 * @version : v1.0
 */
object CmdTracker {

  private var tracker: ClientContext = null

  /**
   * 初始化tracker对象
   * @param tracker ClientContext
   */
  def initTracker(tracker: ClientContext): Unit = {
    this.tracker = tracker
  }

  /**
   * 追踪/汇报命令执行过程
   * @param log String
   * @param cmd Request[Packet]
   */
  def tracking(log : String, cmd : Request[Packet]): Unit = {
    // 生成tracker数据包对象
    val trackingReq = cmd.newTracking(log)

    // 打印日志
    println(trackingReq.getPacket.toString)

    // 汇报过程
    tracker.pushRequest(trackingReq)
  }
}
