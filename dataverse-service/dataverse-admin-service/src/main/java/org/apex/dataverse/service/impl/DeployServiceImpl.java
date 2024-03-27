package org.apex.dataverse.service.impl;

import org.apex.dataverse.entity.Deploy;
import org.apex.dataverse.mapper.DeployMapper;
import org.apex.dataverse.model.PageResult;
import org.apex.dataverse.param.PageDeployParam;
import org.apex.dataverse.service.IDeployService;
import org.apex.dataverse.vo.DeployVO;
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
public class DeployServiceImpl extends ServiceImpl<DeployMapper, Deploy> implements IDeployService {

    @Override
    public PageResult<DeployVO> pageDeployVO(PageDeployParam pageDeployParam) {
        return null;
    }
}
