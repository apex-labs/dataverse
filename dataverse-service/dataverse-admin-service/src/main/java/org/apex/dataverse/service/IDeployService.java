package org.apex.dataverse.service;

import org.apex.dataverse.entity.Deploy;
import org.apex.dataverse.model.PageResult;
import org.apex.dataverse.param.PageDeployParam;
import org.apex.dataverse.vo.DeployVO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author danny
 * @since 2023-05-31
 */
public interface IDeployService extends IService<Deploy> {

    /**
     * 调度任务列表查询
     * @param pageDeployParam
     * @return
     */
    PageResult<DeployVO> pageDeployVO(PageDeployParam pageDeployParam);
}
