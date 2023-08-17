package com.itzq.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itzq.reggie.dto.SetmealDto;
import com.itzq.reggie.entity.Setmeal;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SetmealService extends IService<Setmeal> {
    //新增套餐，同时保存套餐和菜品的关联关系
    public  void saveWithDish(SetmealDto setmealDto);

    //根据id查询套餐信息和菜品信息
    SetmealDto getByIdWithDto(Long id);

    //更新套餐信息，同时更新对应的菜品
    void updateWithDto(SetmealDto setmealDto);

    //删除套餐，同时删除套餐和菜品的关联数据
    void removeWithDish(List<Long> list);

}
