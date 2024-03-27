package org.apex.dataverse.service.impl;

import org.apex.dataverse.entity.DeployJob;
import org.apex.dataverse.mapper.DeployJobMapper;
import org.apex.dataverse.service.IDeployJobService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author danny
 * @since 2023-05-31
 */
@Service
public class DeployJobServiceImpl extends ServiceImpl<DeployJobMapper, DeployJob> implements IDeployJobService {

}
