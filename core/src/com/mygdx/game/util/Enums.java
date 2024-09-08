package com.mygdx.game.util;

public class Enums {
    public enum Direction {
        LEFT, RIGHT, UP, DOWN, UP_LEFT, UP_RIGHT, DOWN_LEFT, DOWN_RIGHT
    }

    public enum PlayerState {
        WALKING, IDLE
    }

    public enum AttackState {
        NOT_ATTACKING, ATTACKING
    }
}
