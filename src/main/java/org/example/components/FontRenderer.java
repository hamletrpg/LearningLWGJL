package org.example.components;

import org.example.engine.Component;

public class FontRenderer extends Component {

    @Override
    public void start() {
        if (gameObject.getComponent(SpriteRenderer.class) != null) {
            System.out.println("fount font renderer!");
        }
    }

    @Override
    public void update(float dt) {

    }
}
