package org.example.engine;

public abstract class Component {
    public GameObject gameObject = null;

    public void start() {

    }

    public abstract void update(float dt);
}
