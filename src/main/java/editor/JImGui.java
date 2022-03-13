package editor;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class JImGui {

    private static float defaultColumnWidth = 220.0f;

    public static void drawVec2Control(String label, Vector2f values) {
        drawVec2Control(label, values, 0.0f, defaultColumnWidth);
    }

    public static void drawVec2Control(String label, Vector2f values, float resetValue) {
        drawVec2Control(label, values, resetValue, defaultColumnWidth);
    }

    /**
     * TODO: watch explain of this function
     * @param label
     * @param values
     * @param resetValue
     * @param columnWidth
     */
    public static void drawVec2Control(String label, Vector2f values, float resetValue, float columnWidth) {
        // Create an ImGui control with an unique ID (create ImGui namespace/class name with pushID)
        // The labels just need to be unique inside of the namespace
        ImGui.pushID(label); // ends with popId()

        // Create the control with 2 columns: 1 for label, 1 for vector control
        ImGui.columns(2);

        // Create label for the property vector2
        ImGui.setColumnWidth(0, columnWidth);
        ImGui.text(label);
        ImGui.nextColumn(); // move to the next column and draw the values

        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0, 0); // ends with popStyleVar()

        float lineHeight = ImGui.getFontSize() + ImGui.getStyle().getFramePaddingY() * 2.0f; // height of the cell
        Vector2f buttonSize = new Vector2f(lineHeight + 3.0f, lineHeight); // size of the button
        // i.e:
        // col[0]   | col[1]        |
        // Position |X:val.x Y:val.y|
        //   + ImGui.calcItemWidth(): Get the width of the current column (here is col[1])
        //   + split the col[1] into 2 parts, which displays X and Y values
        float widthEach = (ImGui.calcItemWidth() - buttonSize.x * 2.0f) / 2.0f; // just the width of val.x and val.y

        /*------Config the X axis properties------*/
        // Set the width of the val.x
        ImGui.pushItemWidth(widthEach); // end with popItemWidth()
        // Set the color of the button in different statuses
        ImGui.pushStyleColor(ImGuiCol.Button, 0.8f, 0.1f, 0.15f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.9f, 0.2f, 0.2f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.8f, 0.1f, 0.15f, 1.0f);
        // Create a button and check if the button is pressed
        if (ImGui.button("X", buttonSize.x, buttonSize.y))
        {
            // If the button is clicked, set the values.x to the resetValue
            values.x = resetValue;
        }
        ImGui.popStyleColor(3); // popup the style to create new element with new style

        ImGui.sameLine(); // layout the latter element horizontally with the former one
        float[] vecValuesX = {values.x}; // set the value of X displaying
        ImGui.dragFloat("##x", vecValuesX, 0.1f); // ##x to get a unique identifier (why use ##)
        ImGui.popItemWidth();
        ImGui.sameLine();

        /*------Config the Y axis properties------*/
        ImGui.pushItemWidth(widthEach);
        ImGui.pushStyleColor(ImGuiCol.Button, 0.2f, 0.7f, 0.2f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.3f, 0.8f, 0.3f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.2f, 0.7f, 0.2f, 1.0f);
        if (ImGui.button("Y", buttonSize.x, buttonSize.y)) {
            values.y = resetValue;
        }
        ImGui.popStyleColor(3); // popup the style to create new element with new style

        ImGui.sameLine();
        float[] vecValuesY = {values.y}; // set the value of Y displaying
        ImGui.dragFloat("##y", vecValuesY, 0.1f);
        ImGui.popItemWidth();
        //ImGui.sameLine();

        ImGui.nextColumn();

        // re-assign the value of X and Y after being changed
        values.x = vecValuesX[0];
        values.y = vecValuesY[0];

//        values.x = 64;
//        values.y = 64;

        ImGui.popStyleVar();
        ImGui.columns(1);
        ImGui.popID();
    }

    public static void drawVec3Control(String label, Vector3f values) {
        drawVec3Control(label, values, 0.0f, defaultColumnWidth);
    }

    public static void drawVec3Control(String label, Vector3f values, float resetValue) {
        drawVec3Control(label, values, resetValue, defaultColumnWidth);
    }

    public static void drawVec3Control(String label, Vector3f values, float resetValue, float columnWidth) {
        // Create an ImGui control with an unique ID
        ImGui.pushID(label);

        ImGui.columns(2);
        ImGui.setColumnWidth(0, columnWidth);
        ImGui.text(label);
        ImGui.nextColumn();

        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0, 0);

        float lineHeight = ImGui.getFontSize() + ImGui.getStyle().getFramePaddingY() * 2.0f;
        Vector2f buttonSize = new Vector2f(lineHeight + 3.0f, lineHeight);
        float widthEach = (ImGui.calcItemWidth() - buttonSize.x * 3.0f) / 3.0f;

        // Config the X axis properties
        ImGui.pushItemWidth(widthEach);
        ImGui.pushStyleColor(ImGuiCol.Button, 0.8f, 0.1f, 0.15f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.9f, 0.2f, 0.2f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.8f, 0.1f, 0.15f, 1.0f);
        if (ImGui.button("X", buttonSize.x, buttonSize.y)) {
            values.x = resetValue;
        }
        ImGui.popStyleColor(3);

        ImGui.sameLine();
        float[] vecValuesX = {values.x};
        ImGui.dragFloat("##X", vecValuesX, 01.f);
        ImGui.popItemWidth();
        ImGui.sameLine();

        // Config the Y axis properties
        ImGui.pushItemWidth(widthEach);
        ImGui.pushStyleColor(ImGuiCol.Button, 0.2f, 0.7f, 0.2f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.3f, 0.8f, 0.3f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.2f, 0.7f, 0.2f, 1.0f);
        if (ImGui.button("Y", buttonSize.x, buttonSize.y)) {
            values.y = resetValue;
        }
        ImGui.popStyleColor(3);

        ImGui.sameLine();
        float[] vecValuesY = {values.y};
        ImGui.dragFloat("##Y", vecValuesY, 0.1f);
        ImGui.popItemWidth();
        ImGui.columns(1);
        ImGui.sameLine();

        // Config the Z axis properties
        ImGui.pushItemWidth(widthEach);
        ImGui.pushStyleColor(ImGuiCol.Button, 0.1f, 0.25f, 0.8f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.2f, 0.35f, 0.9f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.1f, 0.25f, 0.8f, 1.0f);
        if (ImGui.button("Z", buttonSize.x, buttonSize.y)) {
            values.z = resetValue;
        }
        ImGui.popStyleColor(3);

        ImGui.sameLine();
        float[] vecValuesZ = {values.z};
        ImGui.dragFloat("##Z", vecValuesZ, 0.1f);
        ImGui.popItemWidth();
        ImGui.columns(1);

        // re-assign the value of X, Y and Z after being changed
        values.x = vecValuesX[0];
        values.y = vecValuesY[0];
        values.z = vecValuesZ[0];

        ImGui.popStyleVar();
        ImGui.popID();
    }

    public static float dragFloat(String label, float value) {
        ImGui.pushID(label);

        ImGui.columns(2);
        ImGui.setColumnWidth(0, defaultColumnWidth);
        ImGui.text(label);
        ImGui.nextColumn();

        float[] valArr = {value};
        ImGui.dragFloat("##dragFloat", valArr, 0.1f);
        ImGui.columns(1);
        value = valArr[0];
        ImGui.popID();

        return value;//valArr[0];
    }

    public static int dragInt(String label, int value) {
        ImGui.pushID(label);

        ImGui.columns(2);
        ImGui.setColumnWidth(0, defaultColumnWidth);
        ImGui.text(label);
        ImGui.nextColumn();

        int[] valArr = {value};
        ImGui.dragInt("##dragInt", valArr, 0.1f);
        ImGui.columns(1);
        value = valArr[0];
        ImGui.popID();

        return value;
    }

    public static boolean colorPicker4(String label, Vector4f color) {
        boolean res = false;
        ImGui.pushID(label);

        ImGui.columns(2);
        ImGui.setColumnWidth(0, defaultColumnWidth);
        ImGui.text(label);
        ImGui.nextColumn();

        float[] imColor = {color.x, color.y, color.z, color.w};
        if (ImGui.colorEdit4("##colorPicker", imColor)) {
            color.set(imColor[0], imColor[1], imColor[2], imColor[3]);
            res = true;
        }

        ImGui.columns(1);
        ImGui.popID();

        return res;
    }
}











































































