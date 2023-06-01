package org.apache.dolphinscheduler.dao.mapper.parameter;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.dolphinscheduler.dao.entity.parameter.WalmartParameter;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface WalmartParameterMapper extends BaseMapper<WalmartParameter> {

    Map<String, String> selectByExecuteTime(String executeTime);
}
