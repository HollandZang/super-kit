package com.holland.kit.base;

import com.holland.kit.base.file.FileKit;
import com.holland.kit.base.functional.Either;
import com.holland.kit.base.log.ILog;
import com.holland.kit.base.log.LogFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Trie<Key, Data> {
    private static final ILog log           = LogFactory.create(Trie.class);
    private static final int  DEFAULT_LIMIT = 10;

    private       int                  limit;
    private final Key                  key;
    private       Set<Data>            dataSet;
    private       Set<Trie<Key, Data>> children;

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

        Trie<Character, String> trie     = Trie.create(either.t);
        List<Character>         prefixes = new ArrayList<>();
        prefixes.add('a');
        prefixes.add('z');
        prefixes.add('a');
        prefixes.add('d');
        Set<String> match = trie.matchPrefixes(prefixes);
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
                Trie<Key, Data> child = currNode.children.stream().filter(it -> it.key == k).findFirst().orElse(null);
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

    public Set<Data> matchPrefixes(List<Key> prefixes) {
        return matchPrefixes(prefixes, limit);
    }

    public Set<Data> matchPrefixes(List<Key> prefixes, int limit) {
        if (limit < 1) limit = 1;
        Trie<Key, Data> currNode = this;
        for (Key k : prefixes) {
            if (currNode.children == null) {
                return new HashSet<>();
            } else {
                Trie<Key, Data> child = currNode.children.stream().filter(it -> it.key == k).findFirst().orElse(null);
                if (child == null)
                    return new HashSet<>();
                else
                    currNode = child;
            }
        }

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
