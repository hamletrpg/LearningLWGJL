package org.example.engine;

import org.example.components.FontRenderer;
import org.example.components.SpriteRenderer;
import org.example.renderer.Shader;
import org.example.renderer.Texture;
import org.example.util.Time;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;

import java.awt.event.KeyEvent;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class LevelEditorScene extends Scene {
    private String vertexShaderSrc = "#version 330 core\n" +
            "layout (location=0) in vec3 aPos;\n" +
            "layout (location=1) in vec4 aColor;\n" +
            "\n" +
            "out vec4 fColor;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    fColor = aColor;\n" +
            "    gl_Position = vec4(aPos, 1.0);\n" +
            "}";
    private String fragmentShaderSrc = "#version 330 core\n" +
            "\n" +
            "in vec4 fColor;\n" +
            "out vec4 color;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    color = fColor;\n" +
            "}";

    private int vertexID, fragmentID, shaderProgram;

    private float[] vertexArray = {
        // position                           // color                      // UV Coordinates
            100.5f, 0f, 0.0f,              1.0f, 0.0f, 0.0f, 1.0f,      1, 1,  // Bottom right
            00, 100.5f, 0.0f,                  0.0f, 1.0f, 0.0f, 1.0f,      0, 0,  // Top left
            100f, 100f, 0.0f,              0.0f, 0.0f, 1.0f, 1.0f,      1, 0, // Top right
            0f, 0, 0.0f,                       1.0f, 1.0f, 0.0f, 1.0f,      0, 1, // Bottom left
    };

    // Must be in counter-clockwise order
    private int[] elementArray = {
            2, 1, 0, // Top right triangle
            0, 1, 3 // bottom left triangle
    };

    private int vaoID, vboID, eboID;

    private Shader defaultShader;
    private Texture testTexture;

    GameObject testObj;
    private boolean firstTime = false;

    public LevelEditorScene() {

    }

    @Override
    public void init() {
        System.out.println("test object");
        this.testObj = new GameObject("test object");
        this.testObj.addComponent(new SpriteRenderer());
        this.testObj.addComponent(new FontRenderer());
        this.addGameObjectToScene(this.testObj);

        this.camera = new Camera(new Vector2f());
        defaultShader = new Shader("assets/shaders/default.glsl");
        defaultShader.compile();
        this.testTexture = new Texture("assets/images/miner_basic.png");

        // Generate VAO, VBO,  and EBO buffer objects, and send to GPU
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();

        // Create VBO upload the vertex buffer
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        // create the nidices and upload

        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        // Add the vertex attribute pointers
        int positionSize = 3;
        int colorSize = 4;
        int uvSize = 2;
        int vertexSizeBytes = (positionSize + colorSize + uvSize) * Float.BYTES;
        glVertexAttribPointer(0, positionSize, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionSize * Float.BYTES);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, uvSize, GL_FLOAT, false, vertexSizeBytes, (positionSize + colorSize ) * Float.BYTES);
        glEnableVertexAttribArray(2);
    }

    @Override
    public void update(float dt) {
        defaultShader.use();

        // Upload texture to shader
        defaultShader.uploadTexture("TEX_SAMPLER", 0);
        glActiveTexture(GL_TEXTURE0);
        testTexture.bind();

        defaultShader.uploadMat4f("uProjection", camera.getProjectionMatrix());
        defaultShader.uploadMat4f("uView", camera.getViewMatrix());
        defaultShader.uploadFloat("uTime", Time.getTime());

        glBindVertexArray(vaoID);

        // enable vertex attribute pointers
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

        // unbind everything

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        glBindVertexArray(0);

        defaultShader.detach();

        if (!firstTime) {
            System.out.println("Creating gameObject!");
            GameObject go = new GameObject("Game Test 2");
            go.addComponent(new SpriteRenderer());
            this.addGameObjectToScene(go);
            firstTime = true;
        }

        for (GameObject go : this.gameObjects) {
            go.update(dt);
        }
    }
}
