package com.itzq.reggie.common;

/*
* 基于ThreadLocal封装工具类，用户保存和获取当前登录用户id
* */
public class BaseContext {
     private static  ThreadLocal<Long> threadLocal = new ThreadLocal<>();

     public static void setCurrentId(Long id){
         threadLocal.set(id);
     }

     public static long getCurrentId(){
         return threadLocal.get();
     }

}
