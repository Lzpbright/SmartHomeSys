package com.lzp.generatortest.service.impl;

import com.lzp.generatortest.entity.Lock;
import com.lzp.generatortest.mapper.LockMapper;
import com.lzp.generatortest.service.ILockService;
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
public class LockServiceImpl extends ServiceImpl<LockMapper, Lock> implements ILockService {

}
