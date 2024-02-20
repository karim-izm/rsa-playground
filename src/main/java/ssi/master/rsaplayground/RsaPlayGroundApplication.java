package ssi.master.rsaplayground;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class RsaPlayGroundApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(RsaPlayGroundApplication.class.getResource("/ssi/master/rsaplayground/views/home-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("RSA PLAYGROUND!");
        stage.initStyle(StageStyle.UNDECORATED);

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}