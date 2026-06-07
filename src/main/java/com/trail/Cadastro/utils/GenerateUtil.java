package com.trail.Cadastro.utils;

import lombok.experimental.UtilityClass;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

@UtilityClass
public class GenerateUtil {

    public static String makeCode(String nome, Long sequencia) {
        String nomeFormatado = nome.toLowerCase().replaceAll("\\s+", "");
        return nomeFormatado + "#" + sequencia;
    }
}