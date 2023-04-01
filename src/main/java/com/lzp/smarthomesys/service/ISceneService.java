package com.lzp.smarthomesys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lzp.smarthomesys.entity.Scene;
import com.lzp.smarthomesys.utils.Result;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Bright J
 * @since 2023-02-22
 */
public interface ISceneService extends IService<Scene> {

    Result on(String id);

    Result off(String id);
}
