package com.lzp.smarthomesys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzp.smarthomesys.entity.Log;
import com.lzp.smarthomesys.mapper.LogMapper;
import com.lzp.smarthomesys.service.ILogService;
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
