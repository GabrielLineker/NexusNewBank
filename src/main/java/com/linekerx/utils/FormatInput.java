package com.linekerx.utils;

import java.util.Scanner;

public class FormatInput {
    private static final Scanner sc = new Scanner(System.in);

    public static String formatarScanner(String mensagem) {
        System.out.print(mensagem);
        return sc.nextLine().trim();
    }
}
