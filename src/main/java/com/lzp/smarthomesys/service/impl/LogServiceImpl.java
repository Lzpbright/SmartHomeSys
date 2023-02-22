package com.lzp.generatortest.service.impl;

import com.lzp.generatortest.entity.Log;
import com.lzp.generatortest.mapper.LogMapper;
import com.lzp.generatortest.service.ILogService;
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
public class LogServiceImpl extends ServiceImpl<LogMapper, Log> implements ILogService {

}
