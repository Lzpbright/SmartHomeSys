package com.lzp.generatortest.service.impl;

import com.lzp.generatortest.entity.Light;
import com.lzp.generatortest.mapper.LightMapper;
import com.lzp.generatortest.service.ILightService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
