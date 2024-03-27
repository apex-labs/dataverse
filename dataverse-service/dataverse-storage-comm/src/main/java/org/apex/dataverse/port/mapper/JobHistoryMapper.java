package org.apex.dataverse.port.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apex.dataverse.port.entity.JobHistory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 作业运行历史记录表 Mapper 接口
 * </p>
 *
 * @author danny
 * @since 2023-12-20
 */
@Mapper
public interface JobHistoryMapper extends BaseMapper<JobHistory> {

}
