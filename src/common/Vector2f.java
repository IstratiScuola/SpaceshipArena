package common;

import java.io.Serializable;
//classe che si occupa della matematica a 2 vettori, trovata online poichè è universale a ogni game engine 
public class Vector2f implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public float x, y;

    public Vector2f() {
        this.x = 0;
        this.y = 0;
    }

    public Vector2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2f(Vector2f other) {
        this.x = other.x;
        this.y = other.y;
    }

    public Vector2f add(Vector2f other) {
        return new Vector2f(this.x + other.x, this.y + other.y);
    }

    public Vector2f scale(float scalar) {
        return new Vector2f(this.x * scalar, this.y * scalar);
    }

    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void set(Vector2f other) {
        this.x = other.x;
        this.y = other.y;
    }

    public void addLocal(Vector2f other) {
        this.x += other.x;
        this.y += other.y;
    }

    public void scaleLocal(float scalar) {
        this.x *= scalar;
        this.y *= scalar;
    }

    public float length() {
        return (float) Math.sqrt(x * x + y * y);
    }

    public void clampLength(float maxLength) {
        float len = length();
        if (len > maxLength) {
            x = (x / len) * maxLength;
            y = (y / len) * maxLength;
        }
    }
}