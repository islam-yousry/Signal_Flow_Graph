package eg.edu.alexu.csd.filestructure;

import com.brunomnsilva.smartgraph.containers.SmartGraphDemoContainer;
import com.brunomnsilva.smartgraph.graph.DigraphEdgeList;
import com.brunomnsilva.smartgraph.graph.Graph;
import com.brunomnsilva.smartgraph.graphview.SmartCircularSortedPlacementStrategy;
import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel;
import com.brunomnsilva.smartgraph.graphview.SmartPlacementStrategy;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class GUI extends Application {

    private SignalFlowGraph signalFlowGraph = new SignalFlowGraph();
    private Graph<String, String> graph = new DigraphEdgeList<>();
    SmartPlacementStrategy strategy = new SmartCircularSortedPlacementStrategy();
    SmartGraphPanel<String, String> graphView = new SmartGraphPanel(graph, strategy);

    @Override
    public void start(Stage primaryStage) throws Exception {
        Pane rootPane = new HBox();
        Pane pane = getInputPane();
        pane.setPrefSize(300,800);

        rootPane.getChildren().add(pane);
        SmartGraphDemoContainer graphPane = new SmartGraphDemoContainer(graphView);
        graphPane.setPrefSize(1200, 800);
        rootPane.getChildren().add(graphPane);
        Scene scene = new Scene(rootPane);//, 1024.0D, 768.0D);
        Stage stage = new Stage(StageStyle.DECORATED);
        stage.setTitle("JavaFX SmartGraph Visualization");
        stage.setMinHeight(500.0D);
        stage.setMinWidth(800.0D);
        stage.setScene(scene);
        stage.show();
        graphView.init();
        graphView.setVertexDoubleClickAction((graphVertex) -> {
            System.out.println("Vertex contains element: " + (String) graphVertex.getUnderlyingVertex().element());
            graphVertex.setStyleClass("myVertex");
        });
        graphView.setEdgeDoubleClickAction((graphEdge) -> {
            System.out.println("Edge contains element: " + (String) graphEdge.getUnderlyingEdge().element());
            graphEdge.setStyle("-fx-stroke: black; -fx-stroke-width: 2;");
        });


    }

    private VBox getInputPane() {
        VBox pane = new VBox();
        pane.setPrefSize(150, 400);
        pane.setSpacing(10);

        ToggleGroup group = new ToggleGroup();

        //add node.
        RadioButton addNodeOption = new RadioButton("add Node");
        addNodeOption.setToggleGroup(group);
        addNodeOption.setSelected(true);
        pane.setMargin(addNodeOption, new Insets(20, 20, 20, 20));
        Label nodeNameLabel = new Label("enter Node name:");
        pane.setMargin(nodeNameLabel, new Insets(0, 20, 0, 20));
        TextField nodeName = new TextField();
        pane.setMargin(nodeName, new Insets(0, 20, 20, 20));
        Button addNode = new Button("add Node");
        pane.setMargin(addNode, new Insets(0, 20, 20, 20));

        //add edge.
        RadioButton addEdgeOption = new RadioButton("add Edge");
        addEdgeOption.setToggleGroup(group);
        pane.setMargin(addEdgeOption, new Insets(20, 20, 0, 20));
        Label fromNodeLabel = new Label("From :");
        pane.setMargin(fromNodeLabel, new Insets(20, 20, 0, 20));
        TextField fromNode = new TextField();
        fromNode.setDisable(true);
        pane.setMargin(fromNode, new Insets(0, 20, 0, 20));
        Label toNodeLabel = new Label("To :");
        pane.setMargin(toNodeLabel, new Insets(20, 20, 0, 20));
        TextField toNode = new TextField();
        toNode.setDisable(true);
        pane.setMargin(toNode, new Insets(0, 20, 0, 20));
        Label edgeValueLabel = new Label("edge value :");
        pane.setMargin(edgeValueLabel, new Insets(20, 20, 0, 20));
        TextField edgeValue = new TextField();
        edgeValue.setDisable(true);
        pane.setMargin(edgeValue, new Insets(0, 20, 20, 20));
        Button addEdge = new Button("add Edge");
        addEdge.setDisable(true);
        pane.setMargin(addEdge, new Insets(0, 20, 20, 20));

        Label startNodeLabel = new Label("start Node:");
        pane.setMargin(startNodeLabel, new Insets(20, 20, 0, 20));
        TextField startNodeTextField = new TextField();
        pane.setMargin(startNodeTextField, new Insets(0, 20, 20, 20));
        Label endNodeLabel = new Label("end Node:");
        pane.setMargin(endNodeLabel, new Insets(0, 20, 0, 20));
        TextField endNodeTextField = new TextField();
        pane.setMargin(endNodeTextField, new Insets(0, 20, 20, 20));
        Button calculate = new Button("calculate TTF");
        pane.setMargin(calculate, new Insets(20, 20, 20, 20));


        Label ttfLabel = new Label();

        pane.setMargin(ttfLabel,new Insets(0,20,20,20));
        ttfLabel.setStyle("-fx-background-color: white;");
        pane.getChildren().addAll(addNodeOption, nodeNameLabel, nodeName, addNode, addEdgeOption, fromNodeLabel, fromNode, toNodeLabel, toNode, edgeValueLabel, edgeValue, addEdge,startNodeLabel,startNodeTextField,endNodeLabel,endNodeTextField,calculate,ttfLabel);


        // Radio button listener.
        group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if (group.getSelectedToggle().equals(addNodeOption)) {
                    addNode.setDisable(false);
                    nodeName.setDisable(false);
                    fromNode.setDisable(true);
                    toNode.setDisable(true);
                    edgeValue.setDisable(true);
                    addEdge.setDisable(true);
                } else if (group.getSelectedToggle().equals(addEdgeOption)) {
                    addNode.setDisable(true);
                    nodeName.setDisable(true);
                    fromNode.setDisable(false);
                    toNode.setDisable(false);
                    edgeValue.setDisable(false);
                    addEdge.setDisable(false);
                }
            }
        });


        // add node listener.
        addNode.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (!nodeName.getText().trim().isEmpty()) {
                    graph.insertVertex(nodeName.getText());
                    if (signalFlowGraph.addNode(nodeName.getText())) {
                        nodeName.setText("");
                        graphView.update();
                    }
                }
            }
        });

        // add edge listener.
        addEdge.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (!fromNode.getText().trim().isEmpty() && !toNode.getText().trim().isEmpty() && !edgeValue.getText().trim().isEmpty()) {
                    graph.insertEdge(fromNode.getText(), toNode.getText(), edgeValue.getText());
                    if (signalFlowGraph.addEdge(fromNode.getText(), toNode.getText(), Float.parseFloat(edgeValue.getText()))) {
                        fromNode.setText("");
                        toNode.setText("");
                        edgeValue.setText("");
                        graphView.update();
                    }

                }
            }
        });


        calculate.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ttfLabel.setText(String.valueOf(signalFlowGraph.calculateTransferFunction(startNodeTextField.getText(),endNodeTextField.getText())));

            }
        });


        return pane;
    }



    public static void main(String args[]) {
        launch(args);
    }
}
