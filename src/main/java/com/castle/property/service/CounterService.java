package com.castle.property.service;


import com.castle.property.datatype.CounterType;

public interface CounterService {

    Integer getNextCounter(CounterType counterType);
}
