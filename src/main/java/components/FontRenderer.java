package components;

public class FontRenderer extends Component {

    @Override
    public void start() {
        // check if it can find other class name in side of its function
        // -> testing the communication ability of the Component pattern
        if (gameObject.getComponent(SpriteRenderer.class) != null) {
            System.out.println("[start] FontRenderer Found font SpriteRenderer");
        }
    }

    @Override
    public void update(float dt) {
        //System.out.println("[update] FontRenderer I am updating");
    }
}
