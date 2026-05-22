package com.trail.Cadastro.utils;

import lombok.experimental.UtilityClass;
import java.util.concurrent.atomic.AtomicLong;

@UtilityClass
public class GenerateUtil {

    private final AtomicLong sequencia = new AtomicLong(0);

    public static long generate() {
        return sequencia.incrementAndGet();
    }

    public static String makeCode(String nome){
        return nome + "#"  + generate();
    }
}