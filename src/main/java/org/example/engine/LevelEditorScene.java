package org.example.engine;


import org.example.components.Sprite;
import org.example.components.SpriteRenderer;
import org.example.components.SpriteSheet;
import org.example.util.AssetPool;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class LevelEditorScene extends Scene {
    private GameObject obj1;
    private SpriteSheet sprites;

    private boolean firstTime = false;

    public LevelEditorScene() {

    }

    @Override
    public void init() {
        loadResources();


        this.camera = new Camera(new Vector2f(-150, 0));

        sprites = AssetPool.getSpriteSheet("assets/images/spritesheet.png");

        obj1 = new GameObject("Object 1", new Transform(new Vector2f(200, 100), new Vector2f(256, 256)),
                -1
        );
        obj1.addComponent(new SpriteRenderer(
                new Sprite(
                        AssetPool.getTexture("assets/images/blendImage1.png")
                )
        ));
        this.addGameObjectToScene(obj1);

        GameObject obj2 = new GameObject("Object 1", new Transform(new Vector2f(400, 100), new Vector2f(256, 256)),
        2
        );
        obj2.addComponent(new SpriteRenderer(
                new Sprite(
                        AssetPool.getTexture("assets/images/blendImage2.png")
                )
        ));
        this.addGameObjectToScene(obj2);

    }

    private void loadResources() {
        AssetPool.getShader("assets/shaders/default.glsl");

        AssetPool.addSpriteSheet("assets/images/spritesheet.png",
                new SpriteSheet(AssetPool.getTexture("assets/images/spritesheet.png"),
                        16, 16, 26, 0
                        )
                );
    }

    @Override
    public void update(float dt) {


        for (GameObject go : this.gameObjects) {
            go.update(dt);
        }
        this.renderer.render();
    }
}
