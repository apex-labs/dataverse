package org.apex.dataverse.engine.util

import org.apache.hadoop.fs.Path
import org.apex.dataverse.core.exception.OutputException
import org.apex.dataverse.core.msg.packet.info.output.HdfsOutput
import org.apex.dataverse.core.util.StringUtil

/**
 * @author Danny.Huo
 * @date 2023/12/15 19:56
 * @since 0.1.0
 */
object HdfsUtil {

    /**
     * Move temp and target if need
     * @param out HdfsOutput
     */
    def move (out : HdfsOutput): Unit = {
        if(StringUtil.isBlank(out.getTmpTarget) ||
                out.getTmpTarget.equalsIgnoreCase(out.getTarget)) {
            return ;
        }

        val path1 = new Path(out.getTmpTarget)
        val path2 = new Path(out.getTarget)

        if(SparkUtil.fs.exists(path2) && !out.getOverWrite) {
            throw OutputException.pathExistException(out.getTarget)
        } else {
            SparkUtil.fs.delete(path2, true)
            SparkUtil.fs.rename(path1, path2)
        }
    }

}
