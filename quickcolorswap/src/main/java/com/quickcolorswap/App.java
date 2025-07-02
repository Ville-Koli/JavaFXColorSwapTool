package com.quickcolorswap;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;


/**
 * JavaFX App
 */
public class App extends Application {

    private File chosen_image;
    private Image previewPicture;
    private boolean[][] mask;
    private boolean isMaskOn = false;
    private boolean isEditingMask = false;
    private boolean isMaskHidden = false;
    private double maskOpacity = 0.2f;

    private Brush brush = new DefaultBrush();

    private Image previewSnapshot;
    /*
     * Function that adds HBox with a label into a pane
     * @param pane - pane to add label
     */
    public void CreateInputFileInterface(GridPane pane){
        Label input_file_name_label = new Label("Find Png file ");
        HBox box = new HBox();
        box.getChildren().add(input_file_name_label);
        pane.add(box, 0, 0, 1, 1);
    }
    /*
     * Function that calculates the manhattan distance of two colors.
     * Manhattan distance is used for optimization in perfomance.
     * @param Color a - first color
     * @param Color b - second color
     * @return double - manhattan distance between colors a and b
     * using colors as 3D points.
     */
    public double DistanceColor(Color a, Color b){
        return Math.abs(a.getRed() - b.getRed())
         + Math.abs(a.getBlue() - b.getBlue()) 
         + Math.abs(a.getGreen() - b.getGreen());
    }
    /*
     * Function that updates the color mask
     * @param ImageView maskView - ui node that contains mask image
     * @param double opacity - the opacity of the mask
     */
    public void UpdateMask(ImageView maskView, double opacity){
        PixelReader pxr = previewPicture.getPixelReader();
        WritableImage wImage = new WritableImage((int) previewPicture.getWidth(), (int) previewPicture.getHeight());
        PixelWriter pixelWriter = wImage.getPixelWriter();
        Color mask_color = new Color(0, 0, 1, opacity);
        
        for (int readY = 0; readY < previewPicture.getHeight(); readY++) {
            for (int readX = 0; readX < previewPicture.getWidth(); readX++) {
                Color color = pxr.getColor(readX, readY);

                if(mask[readX][readY]){
                    pixelWriter.setColor(readX, readY, mask_color);
                    
                }else{
                    pixelWriter.setColor(readX, readY, color);
                }
            }
        }
        maskView.setImage(wImage);
        maskView.toFront();  
    }
    /*
     * Function that updates the color of the image
     * @param double distance - the range of pixels transformed to color to from the color from
     * @param ImageView previewImage - ui node that contains image
     * @param double opacity - the opacity of the mask
     * @param Color from - color transformed to color to
     * @param Color to - color which replaces color from in image in previewImage
     */
    public void UpdateImage(double distance, ImageView previewImage, Color from, Color to){
        PixelReader pxr = previewPicture.getPixelReader();
        WritableImage wImage = new WritableImage((int) previewPicture.getWidth(), (int) previewPicture.getHeight());
        PixelWriter pixelWriter = wImage.getPixelWriter();
        
        for (int readY = 0; readY < previewPicture.getHeight(); readY++) {
            for (int readX = 0; readX < previewPicture.getWidth(); readX++) {
                Color color = pxr.getColor(readX, readY);

                if(mask[readX][readY] && DistanceColor(from, color) < distance){
                    pixelWriter.setColor(readX, readY, to.deriveColor(0, to.getSaturation(), color.getBrightness(), color.getOpacity()));
                    
                }else{
                    pixelWriter.setColor(readX, readY, color);
                }
            }
        }
        previewImage.setImage(wImage);
    }

    /* Function that creates color swap interface
     * @return Scene - returns color swap interface
     */
    public Scene CreateSwapColorInterface(Stage primaryStage){
        // create gridpanes
        GridPane pane = new GridPane();
        GridPane imagePane = new GridPane();

        // box section
        VBox colorsBox = new VBox();
        VBox previewImageBox = new VBox();
        VBox maskBox = new VBox(10);
        HBox buttonsBox = new HBox(10);

        Scene scene = new Scene(pane, 750, 390);

        // create labels
        Label previewImageText = new Label("Preview Image");
        Label pcfl = new Label("Color From");
        Label pctl = new Label("Color To");
        Label distanceLabel = new Label("Distance: 0");
        Label maskLabel = new Label("Mask Settings");
        Label brushSizeLabel = new Label("Brush size: 0");
        previewPicture = new Image("file:" + chosen_image.toPath());

        // create images with certain resolution
        float heightResolution = 150;
        float widthResolution = 150;

        ImageView previewImage = new ImageView(previewPicture);
        ImageView maskView = new ImageView();

        previewImage.setImage(previewPicture);
        previewImage.setFitHeight(heightResolution);
        previewImage.setFitWidth(widthResolution);

        maskView.setFitHeight(heightResolution);
        maskView.setFitWidth(widthResolution);
        
        // create color pickers
        ColorPicker pickColorToChange = new ColorPicker();
        ColorPicker newColor = new ColorPicker();

        // create buttons
        Button swapColors = new Button("Swap Colors");
        Button saveImage = new Button("Save");
        Button keepChanges = new Button("Keep changes");
        Button showMask = new Button("Show mask");
        Button editMask = new Button("Edit mask");
        Button hideMask = new Button("Hide mask");

        hideMask.setDisable(true);

        // create sliders
        Slider brushSize = new Slider();
        Slider distanceSlider = new Slider();
        distanceSlider.setMax(1);
        distanceSlider.setMin(0);
        distanceSlider.setMinWidth(200);
        distanceSlider.setMaxWidth(200);

        // create checkboxes
        CheckBox eraser = new CheckBox("Eraser");
        eraser.setSelected(true);
        
        // set stylesheets
        scene.getStylesheets().add("stylesheet.css");
        colorsBox.getStyleClass().add("box");
        buttonsBox.getStyleClass().add("box");
        previewImageBox.getStyleClass().add("box");
        maskBox.getStyleClass().add("box");
        showMask.getStyleClass().add("maskbutton");
        editMask.getStyleClass().add("maskbutton");
        hideMask.getStyleClass().add("maskbutton");

        // set up mask
        mask = new boolean[(int)previewPicture.getWidth()][(int)previewPicture.getHeight()];

        for(int x = 0; x < previewPicture.getWidth(); ++x){
            for(int y = 0; y < previewPicture.getHeight(); ++y){
                mask[x][y] = true;
            }
        }

        // add events to ui elements

        // allow user to reverse one color change with "z"
        pane.setOnKeyPressed(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent arg0) {
                if(arg0.getCode() == KeyCode.Z){
                    if(previewSnapshot != null){
                        previewPicture = previewSnapshot;
                        previewImage.setImage(previewPicture);
                    }
                }
            }
        });

        // allow user to keep swapped colors changes
        keepChanges.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {
                previewSnapshot = previewPicture;
                previewPicture = previewImage.getImage();
            }
            
        });

        // allow user to hide color mask while editing it
        hideMask.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                if(isMaskOn){
                    isMaskHidden = !isMaskHidden;
                    if(isMaskHidden){
                        maskOpacity = 0.01f;
                        UpdateMask(maskView, maskOpacity);
                        hideMask.setText("Unhide mask");
                    }else{
                        maskOpacity = 0.2f;
                        UpdateMask(maskView, maskOpacity);
                        hideMask.setText("Hide mask");
                    }
                }
            }
        });
        
        // allow user to select a color from preview picture by clicking on a pixel
        previewImage.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {
                if(!isEditingMask){
                    PixelReader pxr = previewPicture.getPixelReader();
                    pickColorToChange.setValue(pxr.getColor(
                    (int)(arg0.getX() * previewPicture.getWidth() / widthResolution),
                    (int)(arg0.getY() * previewPicture.getHeight() / heightResolution)));
                }
            }
        });

        // allow user to edit color mask with a singular click
        maskView.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {
                if(isEditingMask){
                    int x = (int)(arg0.getX() * previewPicture.getWidth() / widthResolution);
                    int y = (int)(arg0.getY() * previewPicture.getHeight() / heightResolution);
                    brush.applyBrushToMask(new Point(x, y), mask, !eraser.isSelected());
                    UpdateMask(maskView, maskOpacity);

                    Color from = pickColorToChange.getValue();
                    Color to = newColor.getValue();
                    UpdateImage(distanceSlider.getValue(), previewImage, from, to);
                }
            }
        });

        // allow user to edit color mask while dragging the mouse on the color mask
        maskView.setOnMouseDragged(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {
                if(isEditingMask){
                    //int brushSizeValue = (int)brushSize.getValue();
                    int x = (int)(arg0.getX() * previewPicture.getWidth() / widthResolution);
                    int y = (int)(arg0.getY() * previewPicture.getHeight() / heightResolution);

                    Color from = pickColorToChange.getValue();
                    Color to = newColor.getValue();

                    brush.applyBrushToMask(new Point(x, y), mask, !eraser.isSelected());
                    UpdateImage(distanceSlider.getValue(), previewImage, from, to);
                    UpdateMask(maskView, maskOpacity);
                }
            }
        });

        // allow user to change color distance, which changes the area that is affected by the color change
        distanceSlider.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
                distanceLabel.setText(String.format("Distance: %.2f", arg0.getValue()));
                swapColors.fire();
            }
        });

        // allow user to change brush size
        brushSize.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
                brushSizeLabel.setText(String.format("Brush size: %.2f", arg0.getValue()));
                brush.setBrushSize((int)brushSize.getValue());
            }
        });

        // allow user to swap colors by clicking on swap colors button
        swapColors.setOnAction((event) -> {
            Color from = pickColorToChange.getValue();
            Color to = newColor.getValue();
            
            UpdateImage(distanceSlider.getValue(), previewImage, from, to);
        });

        // allow user to save the preview image
        saveImage.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try{
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Image Files", "*.png", "*.jpg"));
                    File previewImageFile = fileChooser.showSaveDialog(primaryStage);
                    BufferedImage toWrite = SwingFXUtils.fromFXImage(previewImage.getImage(), null);
                    ImageIO.write(toWrite, "png", previewImageFile);
                    System.out.println("Picture saved at: " + previewImageFile.getAbsolutePath());
                }catch(Exception e){

                }
            }
        });

        // show mask by editing over the image
        showMask.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {
                showMask.setText("Clear mask");
                if(isMaskHidden&&isMaskOn){hideMask.fire();}
                isMaskOn = !isMaskOn;
                if(isMaskOn){
                    UpdateMask(maskView, 0.2f); 
                    hideMask.setDisable(false);           
                }else{
                    showMask.setText("Show mask");
                    maskView.setImage(null);
                    if(isEditingMask){editMask.fire();}
                    hideMask.setDisable(true);
                }
            }
        });

        // allow editing mask
        editMask.setOnAction(new EventHandler<ActionEvent>(){

            @Override
            public void handle(ActionEvent arg0) {
                isEditingMask = !isEditingMask;
                if(isEditingMask){
                    editMask.setText("Stop editing mask");
                    if(!isMaskOn){
                        showMask.fire();
                    }
                }else{
                    editMask.setText("Edit mask");
                }
                hideMask.setDisable(false);
            }
        });
        
        // add elements to pane
        imagePane.add(maskView, 0, 0);
        imagePane.add(previewImage, 0, 0);

        maskBox.getChildren().addAll(maskLabel, showMask, editMask, hideMask, eraser, brushSizeLabel, brushSize);
        previewImageBox.getChildren().addAll(previewImageText, imagePane);
        colorsBox.getChildren().addAll(pcfl, pickColorToChange, pctl, newColor, distanceLabel, distanceSlider);    
        buttonsBox.getChildren().addAll(swapColors, keepChanges, saveImage);
        pane.add(previewImageBox, 0, 0);
        pane.add(colorsBox, 1, 0);
        pane.add(buttonsBox, 1, 1);
        pane.add(maskBox, 2, 0);
        return scene;
    }
    /*
     * Function that creates the starting interface where user is prompted to find
     * an image file they wish to change colors on.
     * @param Stage primaryStage - primary stage
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        Button btn = new Button();
        btn.setText("Find");
        btn.setOnAction((EventHandler<ActionEvent>) new EventHandler<ActionEvent>() {
 
            @Override
            public void handle(ActionEvent event) {
                FileChooser fc = new FileChooser();
                fc.setTitle("Choose png file that you wish to edit");
                fc.getExtensionFilters().addAll(new ExtensionFilter("Image Files", "*.png", "*.jpg"));
                chosen_image = fc.showOpenDialog(primaryStage);

                if(chosen_image != null){
                    primaryStage.setScene(CreateSwapColorInterface(primaryStage));
                }
            }
        });
        
        GridPane root = new GridPane();
        CreateInputFileInterface(root);
        root.add(btn, 0, 1, 1, 1);

        Scene scene = new Scene(root, 300, 250);
        scene.getStylesheets().add("stylesheet.css");

        primaryStage.setTitle("Quick Color Swap");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}