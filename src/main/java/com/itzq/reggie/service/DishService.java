package com.itzq.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itzq.reggie.dto.DishDto;
import com.itzq.reggie.entity.Dish;
import com.itzq.reggie.entity.DishFlavor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DishService extends IService<Dish>  {
    //新增菜品，同时插入菜品对应的口味数据，需要操作两张表：dish,dish_flavor
    void saveWithFlavor(DishDto dishDto);

    //根据id查询菜品菜品信息和口味信息
    DishDto getByIdWithFlavor(Long id);

    //更新菜品信息，同时更新对应的口味信息
    void updateWithFlavor(DishDto dishDto);
}
