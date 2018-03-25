package ru.track.cypher;

import java.util.*;
import org.jetbrains.annotations.NotNull;

/**
 * Вспомогательные методы шифрования/дешифрования
 */
public class CypherUtil {

    public static final String SYMBOLS = "abcdefghijklmnopqrstuvwxyz";

    /**
     * Генерирует таблицу подстановки - то есть каждой буква алфавита ставится в соответствие другая буква
     * Не должно быть пересечений (a -> x, b -> x). Маппинг уникальный
     *
     * @return таблицу подстановки шифра
     */
    @NotNull
    public static Map<Character, Character> generateCypher() {

        Map<Character, Character> cypherTable = new HashMap<>();
        List<Character> letters = new ArrayList<>();
        for (int i = 0; i < SYMBOLS.length(); i++) {
            letters.add(SYMBOLS.charAt(i));
        }
        List<Character> cypher = new ArrayList<>(letters);
        Collections.shuffle(cypher);
        for (int i = 0; i < SYMBOLS.length(); i++){
            cypherTable.put(letters.get(i), cypher.get(i));
        }
        return cypherTable;
    }

}
