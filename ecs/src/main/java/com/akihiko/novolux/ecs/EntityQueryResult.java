package com.akihiko.novolux.ecs;

import java.util.Map;

/**
 * @author AK1HIKO
 * @project NovoLux
 * @created 16/11/22
 */
public record EntityQueryResult(Map<Integer, Component> components) {

}
