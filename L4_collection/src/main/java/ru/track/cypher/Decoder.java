package ru.track.cypher;

import java.net.SocketPermission;
import java.util.*;

import org.jetbrains.annotations.NotNull;


public class Decoder {

    // Расстояние между A-Z -> a-z
    public static final int SYMBOL_DIST = 32;
    public static final String SYMBOLS = "abcdefghijklmnopqrstuvwxyz";
    private Map<Character, Character> cypher;

    /**
     * Конструктор строит гистограммы открытого домена и зашифрованного домена
     * Сортирует буквы в соответствие с их частотой и создает обратный шифр Map<Character, Character>
     *
     * @param domain - текст по кторому строим гистограмму языка
     */
    public Decoder(@NotNull String domain, @NotNull String encryptedDomain) {
        Map<Character, Integer> domainHist = createHist(domain);
        Map<Character, Integer> encryptedDomainHist = createHist(encryptedDomain);

        cypher = new LinkedHashMap<>();
        for (int i = 0; i < SYMBOLS.length(); i++) {
            cypher.put(encryptedDomainHist.keySet().toString().charAt(i), domainHist.keySet().toString().charAt(i));
        }
    }

    public Map<Character, Character> getCypher() {
        return cypher;
    }

    /**
     * Применяет построенный шифр для расшифровки текста
     *
     * @param encoded зашифрованный текст
     * @return расшифровка
     */
    @NotNull
    public String decode(@NotNull String encoded) {

        List<Character> letters = new ArrayList<>();
        for (int i = 0; i < SYMBOLS.length(); i++) {
            letters.add(SYMBOLS.charAt(i));
        }
        Set<Character> lettersSet = new HashSet<>(letters);
        StringBuilder decoded = new StringBuilder();
        for (int i = 0; i < encoded.length(); i++) {
            if (lettersSet.contains(encoded.toLowerCase().charAt(i))) {
                decoded.append(cypher.get(encoded.toLowerCase().charAt(i)));
            } else {
                decoded.append(encoded.charAt(i));
            }
        }
        return decoded.toString();
    }

    /**
     * Считывает входной текст посимвольно, буквы сохраняет в мапу.
     * Большие буквы приводит к маленьким
     *
     *
     * @param text - входной текст
     * @return - мапа с частотой вхождения каждой буквы (Ключ - буква в нижнем регистре)
     * Мапа отсортирована по частоте. При итерировании на первой позиции наиболее частая буква
     */
    @NotNull
    Map<Character, Integer> createHist(@NotNull String text) {

        List<Character> letters = new ArrayList<>();
        for (int i = 0; i < SYMBOLS.length(); i++) {
            letters.add(SYMBOLS.charAt(i));
        }
        Set<Character> lettersSet = new HashSet<>(letters);
        Map<Character, Integer> hist = new HashMap<>();
        for (int i = 0; i < SYMBOLS.length(); i++) {
            hist.put(SYMBOLS.charAt(i), 0);
        }

        for (int i = 0; i < text.length(); i++) {
            Character c = text.toLowerCase().charAt(i);
            if (lettersSet.contains(c)) hist.put(c, hist.get(c) + 1);
        }
        List<Map.Entry<Character, Integer>> entries = new ArrayList<>(hist.entrySet());
        Collections.sort(entries, new Comparator<Map.Entry<Character, Integer>>() {
            @Override
            public int compare(Map.Entry<Character, Integer> o1, Map.Entry<Character, Integer> o2) {
                return o2.getValue() - o1.getValue();
            }
        });
        Map<Character, Integer> histSorted = new LinkedHashMap<>();
        for (int i = 0; i < SYMBOLS.length(); i++) {
            histSorted.put(entries.get(i).getKey(), entries.get(i).getValue());
        }
        return histSorted;
    }

}
