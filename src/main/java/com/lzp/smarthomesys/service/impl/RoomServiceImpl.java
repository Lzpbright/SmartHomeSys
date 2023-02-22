package com.lzp.generatortest.service.impl;

import com.lzp.generatortest.entity.Room;
import com.lzp.generatortest.mapper.RoomMapper;
import com.lzp.generatortest.service.IRoomService;
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
public class RoomServiceImpl extends ServiceImpl<RoomMapper, Room> implements IRoomService {

}
