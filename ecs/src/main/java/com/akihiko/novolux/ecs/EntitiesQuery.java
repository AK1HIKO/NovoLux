package com.akihiko.novolux.ecs;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author AK1HIKO
 * @project NovoLux
 * @created 16/11/22
 */
public record EntitiesQuery(List<Integer> requiredComponents) {

    public EntitiesQuery {
        requiredComponents.sort(Comparator.naturalOrder());
    }

    public EntitiesQuery(Integer... requiredComponents) {
        this(Arrays.asList(requiredComponents));
    }
}
