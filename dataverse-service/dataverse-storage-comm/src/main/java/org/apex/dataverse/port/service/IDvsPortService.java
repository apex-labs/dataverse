package org.apex.dataverse.port.service;

import org.apex.dataverse.port.entity.DvsPort;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * Dataverse port 服务类
 * </p>
 *
 * @author danny
 * @since 2023-12-20
 */
public interface IDvsPortService extends IService<DvsPort> {

    /**
     * Get by dataverse code
     * @param dvsCode dataverse code
     * @return DvsPort
     */
    DvsPort getByCode(String dvsCode);

}
