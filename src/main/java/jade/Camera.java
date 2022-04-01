package jade;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Camera {
    private Matrix4f projectionMatrix, viewMatrix, inverseProjection, inverseView;
    public Vector2f position;

    private float projectionWidth = 6;
    private float projectionHeight = 3;
    private Vector2f projectionSize = new Vector2f(projectionWidth, projectionHeight);
    private float zoom = 1.0f; // Hold the zoom ratio of the screen

    public Camera(Vector2f position) {
        this.position = position;
        this.projectionMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
        this.inverseProjection = new Matrix4f();
        this.inverseView = new Matrix4f();
        adjustProjection();
    }

    /**
     * Changing zoom ratio would change the projection value.
     * Adjust each frame
     */
    public void adjustProjection() {
        // set the matrix as identity matrix
        projectionMatrix.identity();
        // set the matrix as orthogonal projection matrix
        projectionMatrix.ortho(0.0f, projectionSize.x * this.zoom,
                0.0f, projectionSize.y * this.zoom, 0.0f, 100.0f);
        // Set the value from inverse Projection matrix to calculate the actual coordinates from NDC
        inverseProjection = new Matrix4f(projectionMatrix).invert();
    }

    public Matrix4f getViewMatrix() {
        Vector3f cameraFront = new Vector3f(0.0f, 0.0f, -1.0f);
        Vector3f cameraUp = new Vector3f(0.0f, 1.0f, 0.0f);
        viewMatrix.identity();
        // set camera location (eye location)
        // set the camera position the camera looking at
        // set the up direction of the camera
        viewMatrix.lookAt(new Vector3f(position, 20.0f), cameraFront.add(position.x, position.y, 0.0f), cameraUp);
        inverseView = new Matrix4f(this.viewMatrix).invert();
        return this.viewMatrix;
    }

    public Matrix4f getProjectionMatrix() {
        return this.projectionMatrix;
    }

    public Matrix4f getInverseProjection() {
        return this.inverseProjection;
    }

    public Matrix4f getInverseView() {
        return this.inverseView;
    }

    public Vector2f getProjectionSize() {
        return this.projectionSize;
    }

    public float getZoom() {
        return zoom;
    }

    public void setZoom(float zoom) {
        this.zoom = zoom;
    }

    public void addZoom(float value) {
        this.zoom += value;
    }
}
