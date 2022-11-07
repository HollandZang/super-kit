package com.holland.kit.base;

import com.holland.kit.base.file.FileKit;
import com.holland.kit.base.functional.Either;

import java.util.*;

public class Trie<Key, Data> {
    private static final int DEFAULT_LIMIT = 10;

    private      int                  limit;
    public final Key                  key;
    public       Set<Data>            dataSet;
    public       Set<Trie<Key, Data>> children;

    public static <Key, Data> Trie<Key, Data> create(List<Pair<List<Key>, Data>> pairs) {
        return create(DEFAULT_LIMIT, pairs);
    }

    public static <Key, Data> Trie<Key, Data> create(int limit, List<Pair<List<Key>, Data>> pairs) {
        Trie<Key, Data> root = new Trie<>(null);
        root.limit = limit < 1 ? DEFAULT_LIMIT : limit;
        if (pairs == null || pairs.size() == 0) return root;

        for (Pair<List<Key>, Data> pair : pairs) {
            List<Key> keys = pair.left;
            Data      data = pair.right;
            root.insert(keys, data);
        }
        return root;
    }

    private Trie(Key key) {
        this.key = key;
    }

    public static void main(String[] args) throws Throwable {
        Either<Throwable, List<Pair<List<Character>, String>>> either = FileKit.read(".", "words.txt")
                .then(file -> {
                    List<Pair<List<Character>, String>> pairs = new ArrayList<>();
                    String[]                            lines = file.split("\n");
                    for (String line : lines) {
                        List<Character> list = new ArrayList<>();
                        for (char c : line.toCharArray()) {
                            list.add(c);
                        }
                        pairs.add(new Pair<>(list, line));
                    }
                    return Either.success(pairs);
                });
        if (either.e != null) throw either.e;

        Trie<Character, String> trie  = Trie.create(either.t);
        Set<String>             match = trie.matchPrefixes('a', 'z', 'a', 'd');
        System.out.println(match);
    }

    public void insert(List<Key> keys, Data keyOriginal) {
        if (keys == null || keys.size() == 0)
            return;

        Trie<Key, Data> currNode = this;
        int             endIdx   = keys.size() - 1;
        for (int i = 0; i < keys.size(); i++) {
            Key k = keys.get(i);
            if (currNode.children == null) {
                currNode.children = new HashSet<>();
                Trie<Key, Data> next = new Trie<>(k);
                currNode.children.add(next);
                currNode = next;
            } else {
                Trie<Key, Data> child = currNode.children.stream().filter(it -> Objects.equals(it.key, k)).findFirst().orElse(null);
                if (child != null) {
                    currNode = child;
                } else {
                    Trie<Key, Data> next = new Trie<>(k);
                    currNode.children.add(next);
                    currNode = next;
                }
            }
            if (i == endIdx) {
                if (currNode.dataSet == null) {
                    currNode.dataSet = new HashSet<>();
                }
                currNode.dataSet.add(keyOriginal);
            }
        }
    }

    @SafeVarargs
    public final Trie<Key, Data> findNode(Key... prefixes) {
        Trie<Key, Data> currNode = this;
        for (Key k : prefixes) {
            if (currNode.children == null) {
                return null;
            } else {
                Trie<Key, Data> child = currNode.children.stream().filter(it -> Objects.equals(it.key, k)).findFirst().orElse(null);
                if (child == null)
                    return null;
                else
                    currNode = child;
            }
        }
        return currNode;
    }

    @SafeVarargs
    public final Set<Data> matchPrefixes(Key... prefixes) {
        return matchPrefixes(limit, prefixes);
    }

    @SafeVarargs
    public final Set<Data> matchPrefixes(Integer limit, Key... prefixes) {
        if (limit == null || limit < 1) limit = 1;
        Trie<Key, Data> currNode = findNode(prefixes);
        if (currNode == null)
            return new HashSet<>();

        Set<Data> container = new HashSet<>();
        try {
            recursion(currNode, container, limit - 1);
        } catch (InterruptedException ignore) {
        }
        return container;
    }

    private static <Key, Data> void recursion(Trie<Key, Data> trie, Set<Data> container, int limit) throws InterruptedException {
        if (trie.dataSet != null) {
            container.addAll(trie.dataSet);
        }
        if (container.size() > limit)
            throw new InterruptedException();
        if (trie.children != null) {
            for (Trie<Key, Data> child : trie.children) {
                recursion(child, container, limit);
            }
        }
    }
}
