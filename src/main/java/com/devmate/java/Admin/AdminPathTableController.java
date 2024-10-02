package com.devmate.java.Admin;
import com.devmate.java.Path_Groups_Subjects.Path;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.legacy.MFXLegacyComboBox;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Effect;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import org.controlsfx.control.CheckComboBox;
import java.lang.reflect.Method;
import java.sql.SQLException;

public class AdminPathTableController {

    @FXML
    private Pane Panel;
    @FXML
    private TreeTableView<Path> Table;
    @FXML
    private TreeTableColumn<Path, String> PathColumn;
    @FXML
    private TreeTableColumn<Path, String> TotalSubject;
    @FXML
    private TreeTableColumn<Path, String> TotalStudent;
    @FXML
    private TreeTableColumn<Path, String> TotalGroup;
    @FXML
    private TextField PathField;
    @FXML
    private MFXButton UpdateButton;
    @FXML
    private MFXButton DeleteButton;
    @FXML
    private MFXButton AddButton;
    @FXML
    private Label errorMessageLabel;

    private Admin Admin_X;
    private Boolean Theme;

    public void initialize(Admin Admin_X, Boolean Theme) {

        Extra_Option();
        this.Admin_X = Admin_X;
        this.Theme = Theme;

        PathColumn.setCellValueFactory(param -> {
            Path path = param.getValue().getValue();
            return new SimpleStringProperty(path != null && path.getPathname() != null ? path.getPathname() : "");
        });

        TotalStudent.setCellValueFactory(param -> {
            Path path = param.getValue().getValue();
            return new SimpleStringProperty(path != null ? String.valueOf(path.getStudentslist().size()) : "");
        });

        TotalSubject.setCellValueFactory(param -> {
            Path path = param.getValue().getValue();
            return new SimpleStringProperty(path != null ? String.valueOf(path.getSubjectsList().size()) : "");
        });

        TotalGroup.setCellValueFactory(param -> {
            Path path = param.getValue().getValue();
            return new SimpleStringProperty(path != null ? String.valueOf(path.getGroupslist().size()) : "");
        });

        AddButton.setOnAction(event -> {
            errorMessageLabel.setText("");
            try {
                if (!PathField.getText().isEmpty()) {
                    Admin_X.CREATE_PATH(PathField.getText());
                    initialize(this.Admin_X, Theme);
                } else {
                    errorMessageLabel.setText("Please enter the pathname");
                    PathField.requestFocus();
                }
            } catch (SQLException e) {
                if (e.getMessage().contains("Duplicate")) {
                    errorMessageLabel.setText("The pathname already exists.");
                } else {
                    errorMessageLabel.setText("An error occurred.");
                }
            }
        });

        TreeItem<Path> rootItem = new TreeItem<>(new Path());
        rootItem.setExpanded(true);

        for (Path path : Admin_X.SELECT_ALL_PATHS(null)) {
            TreeItem<Path> item = new TreeItem<>(path);
            rootItem.getChildren().add(item);
        }

        Table.setRoot(rootItem);
        Table.setShowRoot(false);

        Specific_Changes(Table, Theme, Panel, this);
    }


    public static <T> void Specific_Changes(Control table, Boolean theme, Pane panel, Object controller) {
        Class<?> controllerClass = controller.getClass();

        String themeCss = theme ? "/com/devmate/java/Admin/DarkTheme.css" : "/com/devmate/java/Admin/LightTheme.css";
        panel.getStylesheets().clear();
        panel.getStylesheets().add(controller.getClass().getResource(themeCss).toExternalForm());

        if (panel.lookup("#EditingPanel") != null) {
            Pane editingPanel = (Pane) panel.lookup("#EditingPanel");
            MFXButton addButton = (MFXButton) editingPanel.lookup("#AddButton");
            MFXButton updateButton = (MFXButton) editingPanel.lookup("#UpdateButton");
            MFXButton deleteButton = (MFXButton) editingPanel.lookup("#DeleteButton");
            Label error = (Label) editingPanel.lookup("#errorMessageLabel");

            if (addButton!=null) {
                addButton.hoverProperty().addListener((observable, oldValue, newValue) -> {
                    Tooltip tooltip = new Tooltip("Add");
                    addButton.setTooltip(tooltip);
                });
            }
            if (updateButton!=null) {

                updateButton.hoverProperty().addListener((observable, oldValue, newValue) -> {
                    Tooltip tooltip = new Tooltip("Update");
                    updateButton.setTooltip(tooltip);
                });
            }
            if (deleteButton!=null) {
                deleteButton.hoverProperty().addListener((observable, oldValue, newValue) -> {
                    Tooltip tooltip = new Tooltip("Delete");
                    deleteButton.setTooltip(tooltip);
                });
            }
           if (theme) {
               for (Node node : editingPanel.getChildren()) {
                   if (node instanceof MFXButton) {
                       ImageView imageView = (ImageView) ((MFXButton) node).getGraphic();
                       if (imageView != null) {
                           Effect effect = imageView.getEffect();
                           if (effect instanceof ColorAdjust) {
                               ((ColorAdjust) effect).setBrightness(1); // Adjust brightness
                           }
                       }
                   }
                   if (node instanceof MFXLegacyComboBox) {
                       if (node.getEffect() instanceof ColorAdjust) {
                           ColorAdjust colorAdjust = (ColorAdjust) node.getEffect();
                           colorAdjust.setBrightness(0.63); // Adjust brightness
                       }
                   }

                   if (node instanceof MFXDatePicker) {
                       if (node.getEffect() instanceof ColorAdjust) {
                           ColorAdjust colorAdjust = (ColorAdjust) node.getEffect();
                           colorAdjust.setBrightness(0.63); // Adjust brightness
                       }
                   }
               }
           }
                TreeTableView<T> tableView = (TreeTableView<T>) table;

                tableView.setRowFactory(tv -> {
                    TreeTableRow<T> row = new TreeTableRow<>();

                    if (!theme) {
                        row.getStyleClass().add("light-theme-row");
                    } else if (theme) {
                        row.getStyleClass().add("dark-theme-row");
                    }

                    row.selectedProperty().addListener((selectionObs, wasSelected, isSelected) -> {
                        if (isSelected) {
                            row.getStyleClass().add("selected-row");
                            row.setOnMouseClicked(event -> {
                                if (event.getClickCount() == 1) {
                                    if (row.isSelected()) {
                                        row.setOnMouseClicked(events -> {
                                            if (editingPanel != null) {
                                                for (Node node : editingPanel.getChildren()) {
                                                    if (node instanceof TextField || node instanceof CheckComboBox || node instanceof ComboBox || node instanceof DatePicker) {
                                                        if (node instanceof TextField) {
                                                            ((TextField) node).clear();
                                                            ((TextField) node).setText("");
                                                        } else if (node instanceof CheckComboBox) {
                                                            ((CheckComboBox) node).getCheckModel().clearChecks();
                                                        } else if (node instanceof ComboBox) {
                                                            ((ComboBox) node).getSelectionModel().clearSelection();
                                                        } else if (node instanceof DatePicker) {
                                                            ((DatePicker) node).setValue(null);
                                                        }
                                                    }
                                                }
                                            }
                                            if (error!=null){error.setText("");}

                                            row.getStyleClass().remove("selected-row");
                                            row.getTreeTableView().getSelectionModel().clearSelection(row.getIndex());
                                            if (addButton != null && updateButton != null && deleteButton != null) {
                                                addButton.setDisable(false);
                                                addButton.setVisible(true);
                                                updateButton.setDisable(true);
                                                updateButton.setVisible(false);
                                                deleteButton.setDisable(true);
                                                deleteButton.setVisible(false);
                                            }

                                            try {
                                                Method specificMethod;
                                                try {
                                                    specificMethod = controllerClass.getMethod("Extra_Option");
                                                } catch (NoSuchMethodException e) {
                                                    specificMethod = null;
                                                }
                                                if (specificMethod != null) {
                                                    specificMethod.invoke(controller);
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                        });
                                    }
                                }
                            });

                            if (addButton != null && updateButton != null && deleteButton != null) {
                                addButton.setDisable(true);
                                addButton.setVisible(false);
                                updateButton.setDisable(false);
                                updateButton.setVisible(true);
                                deleteButton.setDisable(false);
                                deleteButton.setVisible(true);

                            }

                            if (error!=null) {error.setText("");}

                            try {
                                Method specificMethod = controllerClass.getMethod("Specific", TreeTableRow.class);
                                specificMethod.invoke(controller, row);
                            } catch (NoSuchMethodException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else {
                            row.getStyleClass().remove("selected-row");
                        }
                    });
                    return row;
                });
          }
    }

    public <T extends Path> void Specific(TreeTableRow<T> row) {

            PathField.setText(row.getItem().getPathname());
            String initializedUsername = PathField.getText();

            UpdateButton.setOnAction(event -> {
                try {
                    Admin_X.UPDATE_PATH(initializedUsername, PathField.getText());

                } catch (SQLException e) {
                    if (e.getMessage().contains("Duplicate")) { // SQLState code for integrity constraint violation
                        errorMessageLabel.setText("The pathname already exists.");

                    } else {
                        errorMessageLabel.setText("An error occurred.");
                    }
                }
                initialize(Admin_X, Theme);
            });

            DeleteButton.setOnAction(event -> {
                try {
                    Admin_X.DELETE_PATH(initializedUsername);

                } catch (SQLException e) {
                    if (e.getMessage().contains("Cannot delete")) { // SQLState code for integrity constraint violation
                        errorMessageLabel.setText("The path includes currently active groups.");
                    } else {
                        errorMessageLabel.setText("An error occurred.");
                    }
                }
                initialize(Admin_X, Theme);
            });
    }

    public void Extra_Option() {
        PathField.clear();
        UpdateButton.setDisable(true);
        UpdateButton.setVisible(false);
        DeleteButton.setDisable(true);
        DeleteButton.setVisible(false);
        AddButton.setDisable(false);
        AddButton.setVisible(true);
    }

}
