package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {


    void cartAdd(ShoppingCartDTO shoppingCartDTO);

    List<ShoppingCart> cartSelect();

    void subCart(ShoppingCartDTO shoppingCartDTO);

    void clean();
}
