package org.apex.dataverse.service.impl;

import org.apex.dataverse.entity.TableStatistics;
import org.apex.dataverse.mapper.TableStatisticsMapper;
import org.apex.dataverse.service.ITableStatisticsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author danny
 * @since 2023-02-13
 */
@Service
public class TableStatisticsServiceImpl extends ServiceImpl<TableStatisticsMapper, TableStatistics> implements ITableStatisticsService {

}
