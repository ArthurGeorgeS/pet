package br.com.petshop.util;

public final class Validador {
    private Validador() {}

    public static String digitos(String s) {
        return s == null ? "" : s.replaceAll("\\D", "");
    }

    public static boolean cpfValido(String cpf) {
        String c = digitos(cpf);
        if (c.length() != 11 || c.chars().distinct().count() == 1) return false;
        for (int t = 9; t < 11; t++) {
            int soma = 0;
            for (int i = 0; i < t; i++) soma += (c.charAt(i) - '0') * (t + 1 - i);
            int d = (soma * 10) % 11;
            if (d == 10) d = 0;
            if (d != c.charAt(t) - '0') return false;
        }
        return true;
    }

    public static boolean telefoneValido(String tel) {
        int n = digitos(tel).length();
        return n == 10 || n == 11;
    }

    public static boolean emailValido(String email) {
        return email != null && email.matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");
    }

    public static String formatarTelefone(String tel) {
        String d = digitos(tel);
        if (d.length() == 11) return "(" + d.substring(0, 2) + ") " + d.substring(2, 7) + "-" + d.substring(7);
        if (d.length() == 10) return "(" + d.substring(0, 2) + ") " + d.substring(2, 6) + "-" + d.substring(6);
        return d;
    }
}
