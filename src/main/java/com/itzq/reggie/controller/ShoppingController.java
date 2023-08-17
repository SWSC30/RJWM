package com.itzq.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itzq.reggie.common.BaseContext;
import com.itzq.reggie.common.R;
import com.itzq.reggie.entity.ShoppingCart;
import com.itzq.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sun.nio.cs.ext.SJIS;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /*
    * 添加购物车
    * */

    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        /*
        * 逻辑错误，失败的代码，后续如果有空优化
        * */
//        log.info("购物车数据:{}",shoppingCart);
//        //设置用户id,指定当前是哪个用户的购物车数据
//        //获取当前用户id
//        long currentId = BaseContext.getCurrentId();
//        //赋指userid
//        shoppingCart.setUserId(currentId);
//
//        //查询当前菜品或套餐是否在购物车中 queryWrapper.eq(ShoppingCart::getNumber,null);
//        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
//
//        queryWrapper.eq(ShoppingCart::getId,shoppingCart.getId());
//        queryWrapper.eq(ShoppingCart::getUserId,currentId);
//
//        ShoppingCart cartServiceOne = shoppingCartService.getOne(queryWrapper);
//
//        //如果查询成功，证明，新增菜品或套餐不存在，则添加
//        if(cartServiceOne ==null){
//            //设菜品数量为1
//            shoppingCart.setNumber(1);
//            shoppingCartService.save(shoppingCart);
//        }
//        else{
//            LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
//            wrapper.eq(ShoppingCart::getId,currentId);
//            ShoppingCart serviceOne = shoppingCartService.getOne(wrapper);
//            serviceOne.setNumber(serviceOne.getNumber()+1);
//            shoppingCartService.updateById(serviceOne);
//            shoppingCart=serviceOne;
//        }
//        return R.success(shoppingCart);

        log.info("购物车数据:{}", shoppingCart);

        //设置用户id，指定当前是哪个用户的购物车数据
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);

        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, currentId);

        if (dishId != null) {
            //添加到购物车的是菜品
            queryWrapper.eq(ShoppingCart::getDishId, dishId);

        } else {
            //添加到购物车的是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }

        //查询当前菜品或者套餐是否在购物车中
        //SQL:select * from shopping_cart where user_id = ? and dish_id/setmeal_id = ?
        ShoppingCart cartServiceOne = shoppingCartService.getOne(queryWrapper);

        if (cartServiceOne != null) {
            //如果已经存在，就在原来数量基础上加一
            Integer number = cartServiceOne.getNumber();
            cartServiceOne.setNumber(number + 1);
            shoppingCartService.updateById(cartServiceOne);
        } else {
            //如果不存在，则添加到购物车，数量默认就是一
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            cartServiceOne = shoppingCart;
        }

        return R.success(cartServiceOne);
        }

    /*
    * 查看购物车
    */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        log.info("查看购物车...");

        Long currentId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);

        return R.success(list);
    }

    /*
    * 清空购物车
    * */
    @DeleteMapping("/clean")
    public R<String> delete(){
        Long currentId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);
        shoppingCartService.remove(queryWrapper);

        return R.success("删除成功");
    }
}
