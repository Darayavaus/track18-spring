package ru.track.cypher;

import java.util.*;

import org.jetbrains.annotations.NotNull;

/**
 * Класс умеет кодировать сообщение используя шифр
 */
public class Encoder {
    /**
     * Метод шифрует символы текста в соответствие с таблицей
     * NOTE: Текст преводится в lower case!
     *
     * Если таблица: {a -> x, b -> y}
     * то текст aB -> xy, AB -> xy, ab -> xy
     *
     * @param cypherTable - таблица подстановки
     * @param text - исходный текст
     * @return зашифрованный текст
     */

    public static final String SYMBOLS = "abcdefghijklmnopqrstuvwxyz";

    public static String encode(@NotNull Map<Character, Character> cypherTable, @NotNull String text) {

        List<Character> letters = new ArrayList<>();
        for (int i = 0; i < SYMBOLS.length(); i++) {
            letters.add(SYMBOLS.charAt(i));
        }
        Set<Character> lettersSet = new HashSet<>(letters);
        StringBuilder encoded = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            if (lettersSet.contains(text.toLowerCase().charAt(i))) {
                encoded.append(cypherTable.get(text.toLowerCase().charAt(i)));
            } else {
                encoded.append(text.charAt(i));
            }
        }
        return encoded.toString();
    }

}
