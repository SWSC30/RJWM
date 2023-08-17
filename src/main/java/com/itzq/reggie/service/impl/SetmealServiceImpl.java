package com.itzq.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itzq.reggie.common.CustomException;
import com.itzq.reggie.dto.SetmealDto;
import com.itzq.reggie.entity.Setmeal;
import com.itzq.reggie.entity.SetmealDish;
import com.itzq.reggie.mapper.SetmealMapper;
import com.itzq.reggie.service.SetmealDishService;
import com.itzq.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper,Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐的基本信息，操作setmeal,执行insert操作
        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        //使用数据流的方式更改setmealDto中没赋值的SetmealId字段
        //使用map方法对流中的每个元素进行映射操作。item代表流中的一个元素，即DishFlavor对象
        setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return  item;
        }).collect(Collectors.toList());

        //保存套餐和菜品的关联关系，操作setmeal_dish，执行insert操作
        setmealDishService.saveBatch(setmealDishes);
    }

    /*
    * 回显套餐数据：根据套餐id查询套餐
    * */
    @Override
    public SetmealDto getByIdWithDto(Long id) {
        Setmeal setmeal = this.getById(id);
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal,setmealDto);

        //SetmealDto中的List<SetmealDish> setmealDishes需要赋值
        //利用数据库关联，setmeal中的id对应setmealdish中的setmealid
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(id !=null,SetmealDish::getSetmealId,id);
        List list = setmealDishService.list(queryWrapper);
        setmealDto.setSetmealDishes(list);

        return setmealDto;
    }


    @Override
    public void updateWithDto(SetmealDto setmealDto) {

        //更新setmeal表基本信息
        this.updateById(setmealDto);

        //清理当前菜品对应菜品数据--setmealdish表的delet操作
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishService.remove(queryWrapper);

        //添加当前提交过来的菜品数据--setmealdish表的insert操作
        List<SetmealDish> list =setmealDto.getSetmealDishes();
        list.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(list);

    }


    @Override
    @Transactional
    public void removeWithDish(List<Long> list) {
        //查询套餐状态，确定是否可用删除
        //构建sql语句select count(*) from setmeal where id in(list) and status = 1
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Setmeal::getStatus,1);
        queryWrapper.in(Setmeal::getId,list);

        int count = this.count(queryWrapper);
        if (count >0){
            //如果不能删除，抛出一个异常
            throw  new CustomException("套餐正在售卖中，不能删除");
        }

        //如果可以删除，先删除套餐表中的数据
        this.removeByIds(list);

        //删除关系表中的数据
        LambdaQueryWrapper<SetmealDish> queryWrapper1 = new LambdaQueryWrapper<>();
        //构建sql语句delet * from setmeal_dish where setmeal_id in (list);
        queryWrapper1.in(SetmealDish::getSetmealId,list);
        setmealDishService.remove(queryWrapper1);
    }

}