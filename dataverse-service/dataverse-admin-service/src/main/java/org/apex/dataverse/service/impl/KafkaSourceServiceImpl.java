package org.apex.dataverse.service.impl;

import org.apex.dataverse.entity.KafkaSource;
import org.apex.dataverse.mapper.KafkaSourceMapper;
import org.apex.dataverse.service.IKafkaSourceService;
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
public class KafkaSourceServiceImpl extends ServiceImpl<KafkaSourceMapper, KafkaSource> implements IKafkaSourceService {

}
