package org.apex.dataverse.storage.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apex.dataverse.storage.entity.OdpcStorage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 自定义ODPC存储区（用自行开发的Spark/Flink Engine)，如基于HDFS的存储区+SparkEngine Mapper 接口
 * </p>
 *
 * @author danny
 * @since 2023-12-20
 */
@Mapper
public interface OdpcStorageMapper extends BaseMapper<OdpcStorage> {

}
