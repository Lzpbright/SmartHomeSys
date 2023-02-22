package com.lzp.smarthomesys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzp.smarthomesys.entity.Light;
import com.lzp.smarthomesys.mapper.LightMapper;
import com.lzp.smarthomesys.service.ILightService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Bright J
 * @since 2023-02-22
 */
@Service
public class LightServiceImpl extends ServiceImpl<LightMapper, Light> implements ILightService {

}
