package intermediate;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import javafx.scene.input.MouseButton;

import static com.almasb.fxgl.dsl.FXGL.onBtnBuilder;

public class MouseInputFluentSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) { }

    @Override
    protected void initInput() {

        onBtnBuilder(MouseButton.PRIMARY)
                .onActionBegin(() -> System.out.println("Primary Begin"))
                .onAction(() -> System.out.println("Primary Action"))
                .onActionEnd(() -> System.out.println("Primary End"));

        onBtnBuilder(MouseButton.SECONDARY)
                .onActionBegin(() -> System.out.println("Secondary Begin"))
                .onActionEnd(() -> System.out.println("Secondary End"));
    }

    public static void main(String[] args) {
        launch(args);
    }
}