package com.itzq.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itzq.reggie.common.R;
import com.itzq.reggie.dto.DishDto;
import com.itzq.reggie.dto.SetmealDto;
import com.itzq.reggie.entity.Category;
import com.itzq.reggie.entity.Setmeal;
import com.itzq.reggie.entity.SetmealDish;
import com.itzq.reggie.service.CategoryService;
import com.itzq.reggie.service.SetmealDishService;
import com.itzq.reggie.service.SetmealService;
import com.itzq.reggie.service.impl.CategoryServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
@Controller
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public R<String> save (@RequestBody SetmealDto setmealDto){
        log.info("套餐信息:{}",setmealDto);

        setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功");
    }

    //套餐分页查询
    @GetMapping("/page")
     public R<Page> page(int page,int pageSize, String name){
        //构造分页构造器对象
        Page<Setmeal> pageInfo = new Page<>(page,pageSize);
        //构造dtoPage用来接收和编写records
        Page<SetmealDto> dtoPage = new Page<>();

        //条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();

        //添加查询条件，根据name进行模糊查询
        queryWrapper.like(name!= null,Setmeal::getName,name);

        //添加排序条件
        queryWrapper.orderByDesc(Setmeal::getName);

        //执行分页查询
        setmealService.page(pageInfo,queryWrapper);

        //对象拷贝
        //此时上面的分页和查询条件也加入dtoPage
        BeanUtils.copyProperties(pageInfo,dtoPage,"records");
        //因为忽略了records，所以需要自己写
        //records是page方法自带的，但因为Setmeal和SetmealDto泛型不一样需要重写编写(再次赋值)
        List<Setmeal>records =pageInfo.getRecords();

        /*
        * 自己改成for循环的方式
        * */
        List<SetmealDto> list = new ArrayList<>();
        for (Setmeal setmeal:records) {
            Category category = new Category();
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(setmeal,setmealDto);
            category=categoryService.getById(setmeal.getCategoryId());
            if (category!=null){
                setmealDto.setCategoryName(category.getName());
                list.add(setmealDto);
            }
        }

        /*
        * 数据流的方式
        * */
//        List<SetmealDto> list = records.stream().map(item->{
//            //因为返回的是SetmealDto，所以需要写一个用来返回
//            SetmealDto setmealDto = new SetmealDto();
//
//            BeanUtils.copyProperties(item,setmealDto);
//            //分类
//            Long getCategoryid = item.getCategoryId();
//            //根据id查询分类
//            Category category =categoryService.getById(getCategoryid);
//            if(category!=null){
//                setmealDto.setCategoryName(category.getName());
//            }
//            return  setmealDto;
//        }).collect(Collectors.toList());
        dtoPage.setRecords(list);
        return R.success(dtoPage);
     }

    /**
     * 回显套餐数据：根据套餐id查询套餐
     */
    @GetMapping("/{id}")
    public R<SetmealDto> getData(@PathVariable Long id){
        SetmealDto setmealDto = setmealService.getByIdWithDto(id);

        return R.success(setmealDto);
    }


//    保存修改菜品信息
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto) {
        log.info(setmealDto.toString());

        if (setmealDto==null){
            return R.error("不能输入空id");
        }

        if (setmealDto.getSetmealDishes()==null){
            return R.error("请添加菜品");
        }

        setmealService.updateWithDto(setmealDto);

        return R.success("修改套餐成功");
    }

    /*
    * 删除套餐
    * */
    @DeleteMapping
    public R<String> delete(@RequestParam("id") List<Long> ids){
        log.info("ids:{}",ids);
        //删除setmeal表的信息
        setmealService.removeWithDish(ids);

        return R.success("删除成功");
    }

    /*
    * 套餐停售
    * */
    //{status}为0时，想要停售。为1时想要起售
    @PostMapping("/status/{status}")
    public R<String>stop(@RequestParam Long id ,@PathVariable Long status){
        if(status ==1) {
            LambdaUpdateWrapper<Setmeal> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(Setmeal::getId,id);
            updateWrapper.set(Setmeal::getStatus, 1);
            setmealService.update(null, updateWrapper);
            return R.success("起售成功");
        }
        else {
            LambdaUpdateWrapper<Setmeal> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(Setmeal::getId,id);
            updateWrapper.set(Setmeal::getStatus, 0);
            setmealService.update(null, updateWrapper);
            return R.success("启售成功");
        }
    }
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(Setmeal::getStatus,setmeal.getStatus());
        List<Setmeal> setmeals = setmealService.list(queryWrapper);

        return R.success(setmeals);
    }

}
