package components;

public class SpriteRenderer extends Component {
    private boolean firstTime = false;

    @Override
    public void start() {
        System.out.println("[start] SpriteRenderer I am starting");
    }

    @Override
    public void update(float dt) {
        if (!firstTime) {
            System.out.println("[update] SpriteRenderer I am updating");
            firstTime = true;
        }
    }
}
