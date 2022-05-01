import java.io.IOException;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class App extends Application {

    WebView webView = new WebView();
    WebEngine engine = webView.getEngine();
    WebHistory history;

    Button btBack = new Button("Back");
    Button btForward = new Button("Forward");
    Button btReload = new Button("Reload");
    Button btSourceCode = new Button("SourceCode");
    Button btHistory = new Button("History");
    Button btZoomIn = new Button("+");
    Button btZoomOut = new Button("-");

    TextField tfAddressBar = new TextField("https://www.google.com");
    TextArea taSourceCode = new TextArea();
    TextArea taHistory = new TextArea();

    SplitPane splitPane = new SplitPane();

    double webZoom = 1;

    @Override
    public void start(Stage stage) throws IOException {
        tfAddressBar.setMaxWidth(1500);

        HBox hBox = new HBox(10);
        HBox.setHgrow(tfAddressBar, Priority.ALWAYS);
        hBox.getChildren().addAll(btBack, btForward, btReload, tfAddressBar, btZoomIn, btZoomOut, btSourceCode,
                btHistory);
        hBox.setPadding(new Insets(20));
        hBox.setAlignment(Pos.CENTER);
        tfAddressBar.widthProperty().addListener(((observable, oldValue, newValue) -> {
            HBox.setMargin(tfAddressBar, new Insets(0, tfAddressBar.getWidth() / 5, 0, tfAddressBar.getWidth() / 5));
        }));
        engine.locationProperty().addListener((observable, oldValue, newValue) -> {
            tfAddressBar.setText(newValue);
            try {
                loadSourceCode(newValue);
            } catch (IOException e1) {
            }
            displayHistory();
        });
        engine.load(tfAddressBar.getText());
        tfAddressBar.setOnAction(e -> {
            engine.load(tfAddressBar.getText());
        });

        btBack.setOnAction(e -> {
            back();
        });
        btForward.setOnAction(e -> {
            forward();
        });
        btReload.setOnAction(e -> {
            refreshPage();
        });
        btHistory.setOnAction(e -> {
            displayHistory();
        });
        btZoomIn.setOnAction(e -> {
            zoomIn();
        });
        btZoomOut.setOnAction(e -> {
            zoomOut();
        });

        splitPane.getItems().addAll(webView);

        btSourceCode.setOnAction(e -> {
            setSplitPane(taSourceCode);
        });
        btHistory.setOnAction(e -> {
            setSplitPane(taHistory);
        });

        BorderPane root = new BorderPane();
        root.setTop(hBox);
        root.setCenter(splitPane);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void loadSourceCode(String s) throws IOException {
        SocketConnection socketConnection = new SocketConnection();
        taSourceCode.setText(socketConnection.getURLSource(s));
    }

    public void setSplitPane(Node node) {
        if (splitPane.getItems().size() == 1) {
            splitPane.getItems().addAll(node);
        } else {
            if (splitPane.getItems().get(1) != node) {
                splitPane.getItems().addAll(node);
            }
            splitPane.getItems().remove(1);
        }
    }

    public void refreshPage() {
        engine.reload();
    }

    public void zoomIn() {
        if (webZoom + 0.1 <= 2)
            webZoom += 0.1;
        webView.setZoom(webZoom);
    }

    public void zoomOut() {
        if (webZoom - 0.1 >= 0.5)
            webZoom -= 0.1;
        webView.setZoom(webZoom);
    }

    public void back() {
        history = engine.getHistory();
        ObservableList<WebHistory.Entry> entries = history.getEntries();
        history.go(-1);

        tfAddressBar.setText(entries.get(history.getCurrentIndex()).getUrl());
    }

    public void forward() {
        history = engine.getHistory();
        ObservableList<WebHistory.Entry> entries = history.getEntries();
        history.go(1);

        tfAddressBar.setText(entries.get(history.getCurrentIndex()).getUrl());
    }

    public void displayHistory() {
        history = engine.getHistory();
        ObservableList<WebHistory.Entry> entries = history.getEntries();
        taHistory.clear();
        for (WebHistory.Entry entry : entries) {
            taHistory.appendText(entry.getUrl() + " " + entry.getLastVisitedDate() + "\n");
        }
    }

    public static void main(String[] args) {

        launch(args);
    }

}