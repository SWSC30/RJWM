package com.itzq.reggie.dto;

import com.itzq.reggie.entity.Setmeal;
import com.itzq.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
