package com.mofang.check;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * CheckManager
 *
 * @author doob  fudali113@gmail.com
 * @date 2017/4/24
 */
public class CheckManager {

    private static Lock lock = new ReentrantLock();

    private final static PathMatcher pathMatcher = new AntPathMatcher();

    private CheckProperties oldCP;
    private CheckProperties newCP;
    private CheckPropertiesFetcher checkPropertiesFetcher;
    private List<BusinessChecker> checkers;

    public CheckManager(CheckProperties oldCP,
                        CheckPropertiesFetcher checkPropertiesFetcher,
                        List<BusinessChecker> checkers) {
        if (oldCP.isEmpty()) {
            oldCP = checkPropertiesFetcher.getCheckProperties();
        }
        this.oldCP = oldCP;
        this.newCP = oldCP;
        this.checkPropertiesFetcher = checkPropertiesFetcher;
        this.checkers = checkers;
    }

    /**
     * according to serviceId and path get checkers
     *
     * @param serviceId
     * @param path
     * @return
     */
    public List<BusinessChecker> matchCheckers(String serviceId, String path) {
        Collection<String> types;
        CheckProperties checkProperties = this.getUsableCP();
        CheckProperties.CheckRule checkRule = checkProperties.getRoutes().get(serviceId).getCheck();
        try {
            types = this.getCheckerTypes(checkRule,
                    CheckContext.getCurrentContext().getRequest().getMethod() + "_" + path);
            if (types == checkRule.getDefault()) {
                types = this.getCheckerTypes(checkRule, path);
            }
        } catch (NullPointerException e) {
            return checkers;
        }
        if (!(types instanceof Set)) {
            types = new HashSet<>(types);
        }
        Set<String> _types = (Set)types;
        return checkers.stream()
                .filter(checker -> _types.contains(checker.type()))
                .sorted()
                .collect(Collectors.toList());
    }

    private CheckProperties getUsableCP() {
        if (lock.tryLock()) {
            return oldCP;
        }
        return newCP;
    }

    public void refresh() {
        lock.lock();
        try {
            oldCP = newCP;
            newCP = checkPropertiesFetcher.getCheckProperties();
        } finally {
            lock.unlock();
        }
    }

    /**
     * according to serviceId and path get checker types
     *
     * @param checkRule
     * @param path
     * @return
     */
    public Collection<String> getCheckerTypes(CheckProperties.CheckRule checkRule, String path) {
        boolean isMatch = false;
        Set<String> checkerTypes = new HashSet<>(checkRule.getDefault());
        for (Map.Entry<String, String> entry : checkRule.getRules().entrySet()) {
            if (pathMatcher.match(entry.getKey(), path)) {
                isMatch = true;
                Arrays.stream(entry.getValue().split(",")).forEach( pair -> {
                    CheckPair checkPair = CheckPair.getCheckPair(pair);
                    if (checkPair == null) return;
                    if (checkPair.isMinus()) {
                        if ("*".equals(checkPair.getCheckerType()))
                            checkerTypes.clear();
                        else
                            checkerTypes.remove(checkPair.checkerType);
                    }else {
                        checkerTypes.add(checkPair.getCheckerType());
                    }
                });
            }
        }
        if (!isMatch) {
            return checkRule.getDefault();
        }
        return checkerTypes;
    }

    @Data
    @AllArgsConstructor
    public static class CheckPair {
        private boolean minus;
        private String checkerType;

        public static CheckPair getCheckPair(String pair) {
            boolean minus = false;
            String checkerType;
            if (pair == null || pair.isEmpty()) {
                return null;
            }
            pair = pair.trim();
            if (pair.startsWith("-")) {
                minus = true;
                checkerType = pair.substring(1);
            }else if (pair.startsWith("+")) {
                checkerType = pair.substring(1);
            }else {
                checkerType = pair;
            }
            return new CheckPair(minus, checkerType);
        }
    }

}
