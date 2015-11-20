/**
 *
 * Copyright (c) 2006-2015, Speedment, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); You may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.speedment.internal.newgui;

import com.speedment.component.UserInterfaceComponent;
import com.speedment.event.ProjectLoaded;
import com.speedment.internal.gui.config.AbstractNodeProperty;
import com.speedment.internal.gui.config.ProjectProperty;
import com.speedment.internal.gui.icon.SpeedmentIcon;
import com.speedment.internal.newgui.util.UILoader;
import com.speedment.internal.newgui.util.UISession;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import static java.util.Objects.requireNonNull;
import static javafx.application.Platform.runLater;
import javafx.beans.binding.Bindings;
import static javafx.beans.binding.Bindings.createObjectBinding;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import static javafx.scene.control.SelectionMode.MULTIPLE;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

/**
 *
 * @author Emil Forslund
 */
public final class ProjectTreeController implements Initializable {
    
    private final UISession session;
    
    private @FXML TreeView<AbstractNodeProperty> hierarchy;
    
    private ProjectTreeController(UISession session) {
        this.session = requireNonNull(session);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        runLater(() -> populateTree(session.getProject()));
    }
    
    private void populateTree(ProjectProperty project) {
        requireNonNull(project);
        
        session.getSpeedment().getEventComponent().notify(new ProjectLoaded(project));

        hierarchy.setCellFactory(v -> {
            final TreeCell<AbstractNodeProperty> cell =  new TreeCell<AbstractNodeProperty>() {
                @Override
                protected void updateItem(AbstractNodeProperty item, boolean empty) {
                    // item can be null
                    super.updateItem(item, requireNonNull(empty));

                    if (item == null || empty) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        setGraphic(SpeedmentIcon.forNode(item));
                        textProperty().bind(item.nameProperty());
                        
                        backgroundProperty().bind(
                            createObjectBinding(
                                () -> item.enabledProperty().get() ?
                                BG_ENABLED : BG_DISABLED, 
                                item.enabledProperty()
                            )
                        );
                    }
                }
            };
            
            return cell;
        });
        
        hierarchy.getSelectionModel().setSelectionMode(MULTIPLE);
        
        final UserInterfaceComponent ui = session.getSpeedment().getUserInterfaceComponent();
        Bindings.bindContent(
            ui.getCurrentSelection(), 
            hierarchy.getSelectionModel().getSelectedItems()
        );
        
        hierarchy.setRoot(branch(project));
    }
    
    private TreeItem<AbstractNodeProperty> branch(AbstractNodeProperty node) {
        requireNonNull(node);
        
        final TreeItem<AbstractNodeProperty> branch = new TreeItem<>(node);
        branch.expandedProperty().bindBidirectional(node.expandedProperty());

        node.asParent().ifPresent(p ->
            p.stream()
                .map(n -> (AbstractNodeProperty) n)
                .map(this::branch)
                .forEachOrdered(
                    branch.getChildren()::add
                )
        );

        return branch;
    }
    
    public static Node create(UISession session) {
        return UILoader.create(session, "ProjectTree", ProjectTreeController::new);
	}
    
    private final static Background 
        BG_ENABLED = new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)),
        BG_DISABLED = new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY));
    
}